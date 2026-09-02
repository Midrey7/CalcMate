package com.calcmate.allinonecalculator

import android.app.Application
import com.calcmate.allinonecalculator.ads.AdConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class CalcMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (AdConfig.enabled) {
            if (BuildConfig.DEBUG) MobileAds.setRequestConfiguration(RequestConfiguration.Builder().build())
            MobileAds.initialize(this)
        }
    }
}
