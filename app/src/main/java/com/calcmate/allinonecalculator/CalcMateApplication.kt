package com.calcmate.allinonecalculator

import android.app.Application
import com.calcmate.allinonecalculator.ads.AdConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class CalcMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (AdConfig.enabled) {
            // Register local test devices so ads load reliably during testing without policy violations
            val testDeviceIds = listOf(
                "43F3A709F363C03924EF289C10D30691" // Waydroid local device
            )
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
            MobileAds.setRequestConfiguration(requestConfig)
            MobileAds.initialize(this)
        }
    }
}
