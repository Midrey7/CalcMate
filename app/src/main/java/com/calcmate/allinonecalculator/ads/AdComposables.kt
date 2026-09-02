package com.calcmate.allinonecalculator.ads

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

private const val TAG = "CalcMateBannerAd"

/**
 * Modern adaptive banner ad that sizes dynamically and leaves 0 empty space if failed or loading.
 */
@Composable
fun AdaptiveBannerAd(
    modifier: Modifier = Modifier
) {
    if (!AdConfig.enabled || AdConfig.bannerId.isBlank()) return

    val context = LocalContext.current
    val widthDp = LocalConfiguration.current.screenWidthDp.coerceAtLeast(320)
    var isAdLoaded by remember { mutableStateOf(false) }
    var isAdFailed by remember { mutableStateOf(false) }

    val adView = remember(context, widthDp, AdConfig.bannerId) {
        AdView(context).apply {
            adUnitId = AdConfig.bannerId
            @Suppress("DEPRECATION")
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp))
        }
    }

    DisposableEffect(adView) {
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Banner ad loaded successfully")
                isAdLoaded = true
                isAdFailed = false
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.w(TAG, "Banner ad failed to load: ${error.message} (code ${error.code})")
                isAdLoaded = false
                isAdFailed = true
            }

            override fun onAdClicked() {
                Log.d(TAG, "Banner ad clicked")
            }
        }

        try {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting banner ad", e)
            isAdFailed = true
        }

        onDispose {
            try {
                adView.destroy()
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying adView", e)
            }
        }
    }

    // Only render space when the ad is actually loaded
    AnimatedVisibility(
        visible = isAdLoaded && !isAdFailed,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                factory = { adView },
                update = {}
            )
        }
    }
}
