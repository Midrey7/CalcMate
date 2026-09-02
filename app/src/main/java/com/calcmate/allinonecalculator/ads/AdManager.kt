package com.calcmate.allinonecalculator.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "CalcMateAdManager"

class AdManager(private val appContext: Context) {
    private val initialized = AtomicBoolean(false)
    private var interstitial: InterstitialAd? = null
    private var isPreloading = false
    private var completedSessions = 0
    private var lastShown = 0L

    init {
        if (initialized.compareAndSet(false, true) && AdConfig.enabled) {
            try {
                MobileAds.initialize(appContext.applicationContext) { status ->
                    Log.d(TAG, "MobileAds initialized: $status")
                    preloadInterstitial(appContext)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize MobileAds", e)
            }
        }
    }

    fun recordSession() {
        completedSessions++
        Log.d(TAG, "Session recorded. Total sessions: $completedSessions / ${AdConfig.sessionsBeforeInterstitial}")
    }

    fun preloadInterstitial(context: Context) {
        if (!AdConfig.enabled || AdConfig.interstitialId.isBlank() || interstitial != null || isPreloading) {
            return
        }

        isPreloading = true
        Log.d(TAG, "Preloading interstitial ad...")

        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context.applicationContext,
                AdConfig.interstitialId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "Interstitial ad successfully preloaded and ready")
                        interstitial = ad
                        isPreloading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.w(TAG, "Interstitial ad failed to preload: ${error.message} (code: ${error.code})")
                        interstitial = null
                        isPreloading = false
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating interstitial load", e)
            isPreloading = false
        }
    }

    fun showIfEligible(activity: Activity, onFinished: () -> Unit = {}) {
        val now = System.currentTimeMillis()
        val cooldownPassed = (now - lastShown) >= AdConfig.interstitialCooldownMillis
        val sessionsPassed = completedSessions >= AdConfig.sessionsBeforeInterstitial

        Log.d(TAG, "Checking interstitial eligibility: sessions=$completedSessions/${AdConfig.sessionsBeforeInterstitial}, cooldown=${now - lastShown}ms/${AdConfig.interstitialCooldownMillis}ms, adReady=${interstitial != null}")

        if (!sessionsPassed || !cooldownPassed) {
            onFinished()
            return
        }

        val ad = interstitial
        if (ad == null) {
            Log.d(TAG, "Eligible for interstitial, but no ad is ready. Triggering preload.")
            preloadInterstitial(activity)
            onFinished()
            return
        }

        interstitial = null
        completedSessions = 0
        lastShown = now

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial dismissed by user. Preloading next one.")
                preloadInterstitial(activity)
                onFinished()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.w(TAG, "Interstitial failed to show: ${error.message}")
                preloadInterstitial(activity)
                onFinished()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial opened and displayed to user")
            }
        }

        ad.show(activity)
    }
}
