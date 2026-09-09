package com.calcmate.allinonecalculator.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calcmate.allinonecalculator.ads.AdaptiveBannerAd
import com.calcmate.allinonecalculator.ui.theme.BrandCyan
import com.calcmate.allinonecalculator.ui.theme.BrandIndigo
import com.calcmate.allinonecalculator.ui.theme.LocalCalculatorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    precision: Int,
    onPrecisionChange: (Int) -> Unit,
    onClearHistory: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalCalculatorPalette.current
    val context = LocalContext.current
    var showClearConfirm by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardGradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = palette.textPrimary
                    )
                }
            }
            Text(
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary
            )
        }

        // Section: Appearance
        Text(
            text = "APPEARANCE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandIndigo,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Theme Mode",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("system" to "System", "dark" to "Dark", "light" to "Light").forEach { (key, label) ->
                        val selected = currentTheme == key
                        FilterChip(
                            selected = selected,
                            onClick = { onThemeChange(key) },
                            label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandIndigo,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Section: Calculation & Feedback
        Text(
            text = "CALCULATION & HAPTICS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandIndigo,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Haptics switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Vibration & Haptic Feedback",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = "Gentle tactile feedback when tapping keys",
                            fontSize = 12.sp,
                            color = palette.textSecondary
                        )
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = onHapticsChange
                    )
                }

                HorizontalDivider(color = palette.cardBorder)

                // Precision selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Decimal Precision",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = "$precision digits",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandCyan
                        )
                    }
                    Slider(
                        value = precision.toFloat(),
                        onValueChange = { onPrecisionChange(it.toInt()) },
                        valueRange = 0f..12f,
                        steps = 11
                    )
                }
            }
        }

        // Section: Data & Storage
        Text(
            text = "DATA MANAGEMENT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandIndigo,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Calculation History",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                    Text(
                        text = "Permanently clear stored calculations",
                        fontSize = 12.sp,
                        color = palette.textSecondary
                    )
                }
                FilledTonalButton(
                    onClick = { showClearConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(contentColor = palette.clearKeyText)
                ) {
                    Text("Clear")
                }
            }
        }

        // Section: About App
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(listOf(BrandIndigo, BrandCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Calculate,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "CalcMate",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
                Text(
                    text = "Version 1.0.0 (Pro Edition)",
                    fontSize = 12.sp,
                    color = palette.textSecondary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Engineered for speed, precision, and everyday simplicity.",
                    fontSize = 13.sp,
                    color = palette.textTertiary
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Check out CalcMate!")
                                putExtra(Intent.EXTRA_TEXT, "I'm using CalcMate for all my calculations and conversions! Get it on Google Play Store.")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share CalcMate"))
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.calcmate.allinonecalculator"))
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.calcmate.allinonecalculator")))
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo)
                    ) {
                        Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Rate App")
                    }
                }

                Spacer(Modifier.height(8.dp))

                TextButton(onClick = { showPrivacyPolicy = true }) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandIndigo)
                    Spacer(Modifier.width(6.dp))
                    Text("Privacy Policy", fontSize = 13.sp, color = BrandIndigo, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        AdaptiveBannerAd()
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear History?") },
            text = { Text("Are you sure you want to delete all saved calculations?") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.clearKeyText)
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPrivacyPolicy) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicy = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = BrandIndigo)
                    Spacer(Modifier.width(8.dp))
                    Text("Privacy Policy", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Last updated: September 2026",
                        fontSize = 12.sp,
                        color = palette.textTertiary
                    )
                    Text(
                        text = "1. Local Calculations\nCalcMate does not require an account. All your calculations, notes, formula inputs, and custom settings (theme, haptics, precision) remain strictly on your local device.",
                        fontSize = 13.sp,
                        color = palette.textSecondary
                    )
                    Text(
                        text = "2. Advertisements\nWe display ads through Google AdMob to keep CalcMate free. AdMob may collect device identifiers (Android Advertising ID) and non-sensitive diagnostics to serve ads in compliance with Google's policies.",
                        fontSize = 13.sp,
                        color = palette.textSecondary
                    )
                    Text(
                        text = "3. Permissions\nCalcMate only uses INTERNET (for AdMob) and VIBRATE (for tactile haptic feedback on key presses). We never request Camera, Contacts, Microphone, or Storage permissions.",
                        fontSize = 13.sp,
                        color = palette.textSecondary
                    )
                    Text(
                        text = "4. Your Control\nYou can clear your entire calculation history at any time from the History or Settings screen.",
                        fontSize = 13.sp,
                        color = palette.textSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyPolicy = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}
