package com.calcmate.allinonecalculator.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

private const val TAG = "CalcMateNativeAd"

class NativeAdManager {
    fun load(
        context: Context,
        onLoaded: (NativeAd) -> Unit,
        onUnavailable: () -> Unit = {}
    ) {
        if (!AdConfig.enabled || AdConfig.nativeId.isBlank()) {
            onUnavailable()
            return
        }

        try {
            val adLoader = AdLoader.Builder(context, AdConfig.nativeId)
                .forNativeAd { nativeAd ->
                    Log.d(TAG, "Native ad loaded: ${nativeAd.headline}")
                    onLoaded(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.w(TAG, "Native ad failed to load: ${error.message} (code ${error.code})")
                        onUnavailable()
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Native ad clicked")
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .build()
                )
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            Log.e(TAG, "Error building AdLoader", e)
            onUnavailable()
        }
    }
}
