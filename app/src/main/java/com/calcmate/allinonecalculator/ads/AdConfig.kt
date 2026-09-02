package com.calcmate.allinonecalculator.ads

import com.calcmate.allinonecalculator.BuildConfig

/** Single source of truth for monetization. */
object AdConfig {
    // Official Google AdMob sample test IDs (used in debug builds for safe development)
    const val debugAppId = "ca-app-pub-3940256099942544~3347511713"
    const val debugBannerId = "ca-app-pub-3940256099942544/9214589741"
    const val debugNativeId = "ca-app-pub-3940256099942544/2247696110"
    const val debugInterstitialId = "ca-app-pub-3940256099942544/1033173712"

    // Production AdMob Unit IDs
    const val productionAppId = "ca-app-pub-6007105834228266~1709877884"
    const val productionBannerId = "ca-app-pub-6007105834228266/1953420467"
    const val productionNativeId = "ca-app-pub-6007105834228266/3553547795"
    const val productionInterstitialId = "ca-app-pub-6007105834228266/4388012113"

    // Interstitial pacing: show interstitial after N calculation sessions, with cooldown
    const val sessionsBeforeInterstitial = 4
    const val interstitialCooldownMillis = 3 * 60 * 1000L // 3 minutes cooldown
    const val adsEnabled = true

    val appId: String get() = when {
        productionAppId.isNotBlank() -> productionAppId
        else -> debugAppId
    }

    val bannerId: String get() = when {
        productionBannerId.isNotBlank() -> productionBannerId
        BuildConfig.DEBUG -> debugBannerId
        else -> ""
    }

    val nativeId: String get() = when {
        productionNativeId.isNotBlank() -> productionNativeId
        BuildConfig.DEBUG -> debugNativeId
        else -> ""
    }

    val interstitialId: String get() = when {
        productionInterstitialId.isNotBlank() -> productionInterstitialId
        BuildConfig.DEBUG -> debugInterstitialId
        else -> ""
    }

    val enabled: Boolean get() = adsEnabled && (bannerId.isNotBlank() || nativeId.isNotBlank() || interstitialId.isNotBlank())
}
