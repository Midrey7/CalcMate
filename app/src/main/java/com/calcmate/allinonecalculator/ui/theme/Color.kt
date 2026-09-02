package com.calcmate.allinonecalculator.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Brand Palette
val BrandIndigo = Color(0xFF6366F1)
val BrandIndigoDark = Color(0xFF4F46E5)
val BrandIndigoLight = Color(0xFF818CF8)

val BrandCyan = Color(0xFF06B6D4)
val BrandCyanLight = Color(0xFF22D3EE)
val BrandCyanDark = Color(0xFF0891B2)

val BrandViolet = Color(0xFFA855F7)
val BrandEmerald = Color(0xFF10B981)
val BrandEmeraldLight = Color(0xFF34D399)
val BrandRose = Color(0xFFF43F5E)
val BrandAmber = Color(0xFFF59E0B)

// Dark Theme Colors (Deep Obsidian & Cyber Accents)
val DarkBackground = Color(0xFF080C15)
val DarkSurface = Color(0xFF0F1626)
val DarkSurfaceVariant = Color(0xFF172033)
val DarkCardBorder = Color(0xFF25334E)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextTertiary = Color(0xFF64748B)

val DarkNumKeyBg = Color(0xFF131C2E)
val DarkNumKeyText = Color(0xFFF8FAFC)
val DarkOpKeyBg = Color(0xFF1E2B45)
val DarkOpKeyText = Color(0xFF38BDF8)
val DarkFnKeyBg = Color(0xFF1B2337)
val DarkFnKeyText = Color(0xFF94A3B8)
val DarkClearKeyText = Color(0xFFFB7185)
val DarkEqualsKeyBg = Color(0xFF6366F1)
val DarkEqualsKeyText = Color(0xFFFFFFFF)

// Light Theme Colors (Clean Alabaster & Crisp Indigo)
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightCardBorder = Color(0xFFE2E8F0)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
val LightTextTertiary = Color(0xFF94A3B8)

val LightNumKeyBg = Color(0xFFFFFFFF)
val LightNumKeyText = Color(0xFF0F172A)
val LightOpKeyBg = Color(0xFFEEF2FF)
val LightOpKeyText = Color(0xFF4F46E5)
val LightFnKeyBg = Color(0xFFF1F5F9)
val LightFnKeyText = Color(0xFF475569)
val LightClearKeyText = Color(0xFFE11D48)
val LightEqualsKeyBg = Color(0xFF4F46E5)
val LightEqualsKeyText = Color(0xFFFFFFFF)

@Immutable
data class CalculatorPalette(
    val numKeyBg: Color,
    val numKeyText: Color,
    val opKeyBg: Color,
    val opKeyText: Color,
    val fnKeyBg: Color,
    val fnKeyText: Color,
    val clearKeyText: Color,
    val equalsKeyBg: Color,
    val equalsKeyText: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentGradient: Brush,
    val cardGradient: Brush
)

val DarkCalculatorPalette = CalculatorPalette(
    numKeyBg = DarkNumKeyBg,
    numKeyText = DarkNumKeyText,
    opKeyBg = DarkOpKeyBg,
    opKeyText = DarkOpKeyText,
    fnKeyBg = DarkFnKeyBg,
    fnKeyText = DarkFnKeyText,
    clearKeyText = DarkClearKeyText,
    equalsKeyBg = DarkEqualsKeyBg,
    equalsKeyText = DarkEqualsKeyText,
    cardBorder = DarkCardBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    accentGradient = Brush.linearGradient(listOf(BrandIndigo, BrandCyan)),
    cardGradient = Brush.verticalGradient(listOf(DarkSurface, DarkSurfaceVariant))
)

val LightCalculatorPalette = CalculatorPalette(
    numKeyBg = LightNumKeyBg,
    numKeyText = LightNumKeyText,
    opKeyBg = LightOpKeyBg,
    opKeyText = LightOpKeyText,
    fnKeyBg = LightFnKeyBg,
    fnKeyText = LightFnKeyText,
    clearKeyText = LightClearKeyText,
    equalsKeyBg = LightEqualsKeyBg,
    equalsKeyText = LightEqualsKeyText,
    cardBorder = LightCardBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    accentGradient = Brush.linearGradient(listOf(BrandIndigoDark, BrandCyanDark)),
    cardGradient = Brush.verticalGradient(listOf(LightSurface, LightSurfaceVariant))
)

val LocalCalculatorPalette = staticCompositionLocalOf { DarkCalculatorPalette }
