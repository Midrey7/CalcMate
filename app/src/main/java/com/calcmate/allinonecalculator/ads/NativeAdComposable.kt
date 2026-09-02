package com.calcmate.allinonecalculator.ads

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.calcmate.allinonecalculator.ui.theme.LocalCalculatorPalette
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * Material 3 styled Native Ad Card.
 * Fully policy-compliant, adapts to Dark/Light themes, and collapses to 0 height when unavailable.
 */
@Composable
fun NativeAdCard(modifier: Modifier = Modifier) {
    if (!AdConfig.enabled || AdConfig.nativeId.isBlank()) return

    val context = LocalContext.current
    val palette = LocalCalculatorPalette.current
    val isDark = isSystemInDarkTheme()
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isFailed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val manager = NativeAdManager()
        manager.load(
            context = context,
            onLoaded = { ad ->
                nativeAd = ad
                isFailed = false
            },
            onUnavailable = {
                isFailed = true
                nativeAd = null
            }
        )

        onDispose {
            nativeAd?.destroy()
            nativeAd = null
        }
    }

    val loadedAd = nativeAd

    AnimatedVisibility(
        visible = loadedAd != null && !isFailed,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (loadedAd != null) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
                border = CardDefaults.outlinedCardBorder(),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    factory = { ctx ->
                        val dp = ctx.resources.displayMetrics.density

                        NativeAdView(ctx).apply {
                            val rootLayout = LinearLayout(ctx).apply {
                                orientation = LinearLayout.VERTICAL
                                setPadding((16 * dp).toInt(), (14 * dp).toInt(), (16 * dp).toInt(), (14 * dp).toInt())
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            }

                            // Top Header: Ad Badge & Title
                            val topRow = LinearLayout(ctx).apply {
                                orientation = LinearLayout.HORIZONTAL
                                gravity = Gravity.CENTER_VERTICAL
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            }

                            // Ad Badge
                            val adBadge = TextView(ctx).apply {
                                text = "AD"
                                textSize = 10f
                                setTypeface(null, Typeface.BOLD)
                                setTextColor(if (isDark) Color.WHITE else Color.rgb(79, 70, 229))
                                val badgeBg = GradientDrawable().apply {
                                    cornerRadius = 6 * dp
                                    setColor(if (isDark) Color.rgb(37, 51, 78) else Color.rgb(238, 242, 255))
                                }
                                background = badgeBg
                                setPadding((6 * dp).toInt(), (2 * dp).toInt(), (6 * dp).toInt(), (2 * dp).toInt())
                            }
                            topRow.addView(adBadge)

                            // Advertiser / Subtitle
                            val advertiserView = TextView(ctx).apply {
                                textSize = 11f
                                setTextColor(if (isDark) Color.rgb(148, 163, 184) else Color.rgb(100, 116, 139))
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    leftMargin = (8 * dp).toInt()
                                }
                                layoutParams = params
                            }
                            topRow.addView(advertiserView)
                            rootLayout.addView(topRow)

                            // Middle: Icon + Headline
                            val middleRow = LinearLayout(ctx).apply {
                                orientation = LinearLayout.HORIZONTAL
                                gravity = Gravity.CENTER_VERTICAL
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    topMargin = (10 * dp).toInt()
                                    bottomMargin = (6 * dp).toInt()
                                }
                                layoutParams = params
                            }

                            val iconView = ImageView(ctx).apply {
                                val params = LinearLayout.LayoutParams((38 * dp).toInt(), (38 * dp).toInt()).apply {
                                    rightMargin = (12 * dp).toInt()
                                }
                                layoutParams = params
                            }
                            middleRow.addView(iconView)

                            val headlineView = TextView(ctx).apply {
                                textSize = 15f
                                setTypeface(null, Typeface.BOLD)
                                setTextColor(if (isDark) Color.rgb(248, 250, 252) else Color.rgb(15, 23, 42))
                                maxLines = 2
                                val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                                layoutParams = params
                            }
                            middleRow.addView(headlineView)
                            rootLayout.addView(middleRow)

                            // Body Description
                            val bodyView = TextView(ctx).apply {
                                textSize = 13f
                                setTextColor(if (isDark) Color.rgb(148, 163, 184) else Color.rgb(100, 116, 139))
                                maxLines = 3
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    bottomMargin = (12 * dp).toInt()
                                }
                                layoutParams = params
                            }
                            rootLayout.addView(bodyView)

                            // Call To Action Button
                            val ctaButton = Button(ctx).apply {
                                textSize = 13f
                                setTypeface(null, Typeface.BOLD)
                                setTextColor(Color.WHITE)
                                val ctaBg = GradientDrawable().apply {
                                    cornerRadius = 12 * dp
                                    setColor(Color.rgb(99, 102, 241)) // BrandIndigo
                                }
                                background = ctaBg
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    (42 * dp).toInt()
                                )
                                layoutParams = params
                            }
                            rootLayout.addView(ctaButton)

                            // Bind views to NativeAdView
                            this.headlineView = headlineView
                            this.bodyView = bodyView
                            this.callToActionView = ctaButton
                            this.iconView = iconView
                            this.advertiserView = advertiserView

                            // Populate data
                            headlineView.text = loadedAd.headline ?: "Sponsored"
                            bodyView.text = loadedAd.body ?: ""
                            bodyView.visibility = if (loadedAd.body.isNullOrBlank()) View.GONE else View.VISIBLE

                            ctaButton.text = loadedAd.callToAction ?: "Learn More"
                            ctaButton.visibility = if (loadedAd.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE

                            if (loadedAd.icon != null) {
                                iconView.setImageDrawable(loadedAd.icon!!.drawable)
                                iconView.visibility = View.VISIBLE
                            } else {
                                iconView.visibility = View.GONE
                            }

                            if (!loadedAd.advertiser.isNullOrBlank()) {
                                advertiserView.text = loadedAd.advertiser
                                advertiserView.visibility = View.VISIBLE
                            } else {
                                advertiserView.visibility = View.GONE
                            }

                            setNativeAd(loadedAd)
                            addView(rootLayout)
                        }
                    },
                    update = {}
                )
            }
        }
    }
}
