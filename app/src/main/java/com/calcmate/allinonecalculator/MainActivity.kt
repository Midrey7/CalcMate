package com.calcmate.allinonecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.calcmate.allinonecalculator.ads.AdManager
import com.calcmate.allinonecalculator.data.AppStore
import com.calcmate.allinonecalculator.ui.screens.*
import com.calcmate.allinonecalculator.ui.theme.BrandIndigo
import com.calcmate.allinonecalculator.ui.theme.CalcMateTheme
import com.calcmate.allinonecalculator.ui.theme.LocalCalculatorPalette
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val store = remember(context) { AppStore(context) }
            val themePreference by store.themeFlow.collectAsState(initial = "system")
            val hapticsEnabled by store.hapticsFlow.collectAsState(initial = true)
            val precision by store.precisionFlow.collectAsState(initial = 8)

            CalcMateTheme(
                themePreference = themePreference,
                hapticsEnabled = hapticsEnabled,
                precision = precision
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalcMateMainApp(store)
                }
            }
        }
    }
}

@Composable
fun CalcMateMainApp(store: AppStore) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val adManager = remember(context) { AdManager(context) }

    var currentTab by remember { mutableIntStateOf(0) }
    var selectedToolId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val themePreference by store.themeFlow.collectAsState(initial = "system")
    val hapticsEnabled by store.hapticsFlow.collectAsState(initial = true)
    val precision by store.precisionFlow.collectAsState(initial = 8)
    val favorites by store.favoritesFlow.collectAsState(initial = emptySet())
    val history by store.historyFlow.collectAsState(initial = emptyList())
    val recent by store.recentFlow.collectAsState(initial = emptyList())

    // Handle system back navigation when a tool is open or on another tab
    BackHandler(enabled = selectedToolId != null || currentTab != 0) {
        if (selectedToolId != null) {
            selectedToolId = null
        } else if (currentTab != 0) {
            currentTab = 0
        }
    }

    LaunchedEffect(selectedToolId, currentTab) {
        if (selectedToolId == null && currentTab == 0 && context is ComponentActivity) {
            adManager.showIfEligible(context)
        }
    }

    Scaffold(
        bottomBar = {
            // Only show bottom navigation when on root screens, or always for quick switching
            if (selectedToolId == null) {
                val palette = LocalCalculatorPalette.current
                NavigationBar(
                    containerColor = palette.numKeyBg,
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        Triple("Home", Icons.Filled.Home, Icons.Outlined.Home),
                        Triple("Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate),
                        Triple("History", Icons.Filled.History, Icons.Outlined.History),
                        Triple("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
                    )

                    navItems.forEachIndexed { index, (label, selectedIcon, unselectedIcon) ->
                        val isSelected = currentTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentTab = index
                                selectedToolId = null
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                    contentDescription = label
                                )
                            },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandIndigo,
                                selectedTextColor = BrandIndigo,
                                indicatorColor = BrandIndigo.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when {
            // When a specific tool is opened
            selectedToolId != null -> {
                val toolId = selectedToolId!!
                val toolInfo = AppToolsList.firstOrNull { it.id == toolId }
                val title = toolInfo?.title ?: "Calculator"
                val isFav = toolId in favorites

                if (toolId == "standard" || toolId == "scientific") {
                    CalculatorScreen(
                        isScientificInitially = (toolId == "scientific"),
                        favorite = isFav,
                        onToggleFavorite = { scope.launch { store.toggleFavorite(toolId) } },
                        onCalculationComplete = { expr, res ->
                            if (res.isNotBlank()) {
                                scope.launch { store.addHistory("$expr = $res") }
                            }
                            adManager.recordSession()
                            adManager.preloadInterstitial(context)
                        },
                        historyList = history,
                        onClearHistory = { scope.launch { store.clearHistory() } },
                        onBack = { selectedToolId = null },
                        modifier = screenModifier
                    )
                } else {
                    SpecializedToolScreen(
                        toolId = toolId,
                        title = title,
                        favorite = isFav,
                        onBack = { selectedToolId = null },
                        onToggleFavorite = { scope.launch { store.toggleFavorite(toolId) } },
                        onCalculationComplete = { expr, res ->
                            if (res.isNotBlank()) {
                                scope.launch { store.addHistory("$expr = $res") }
                            }
                            adManager.recordSession()
                            adManager.preloadInterstitial(context)
                        },
                        modifier = screenModifier
                    )
                }
            }

            // Tab 0: Home Toolbox
            currentTab == 0 -> {
                HomeScreen(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    favorites = favorites,
                    recent = recent,
                    onOpenTool = { toolId ->
                        scope.launch { store.addRecent(toolId) }
                        selectedToolId = toolId
                    },
                    onToggleFavorite = { toolId ->
                        scope.launch { store.toggleFavorite(toolId) }
                    },
                    modifier = screenModifier
                )
            }

            // Tab 1: Dedicated Standard/Scientific Calculator
            currentTab == 1 -> {
                CalculatorScreen(
                    isScientificInitially = false,
                    favorite = "standard" in favorites,
                    onToggleFavorite = { scope.launch { store.toggleFavorite("standard") } },
                    onCalculationComplete = { expr, res ->
                        if (res.isNotBlank()) {
                            scope.launch { store.addHistory("$expr = $res") }
                        }
                        adManager.recordSession()
                        adManager.preloadInterstitial(context)
                    },
                    historyList = history,
                    onClearHistory = { scope.launch { store.clearHistory() } },
                    onBack = { currentTab = 0 },
                    modifier = screenModifier
                )
            }

            // Tab 2: Full History Screen
            currentTab == 2 -> {
                HistoryScreen(
                    historyList = history,
                    onDeleteItem = { scope.launch { store.deleteHistoryItem(it) } },
                    onClearHistory = { scope.launch { store.clearHistory() } },
                    onBack = { currentTab = 0 },
                    modifier = screenModifier
                )
            }

            // Tab 3: Settings Screen
            else -> {
                SettingsScreen(
                    currentTheme = themePreference,
                    onThemeChange = { scope.launch { store.setTheme(it) } },
                    hapticsEnabled = hapticsEnabled,
                    onHapticsChange = { scope.launch { store.setHaptics(it) } },
                    precision = precision,
                    onPrecisionChange = { scope.launch { store.setPrecision(it) } },
                    onClearHistory = { scope.launch { store.clearHistory() } },
                    onBack = { currentTab = 0 },
                    modifier = screenModifier
                )
            }
        }
    }
}
