package com.calcmate.allinonecalculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calcmate.allinonecalculator.ads.AdaptiveBannerAd
import com.calcmate.allinonecalculator.ads.NativeAdCard
import com.calcmate.allinonecalculator.ui.theme.*

data class ToolItem(
    val id: String,
    val title: String,
    val category: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color
)

val AppToolsList = listOf(
    // Standard & Scientific
    ToolItem("standard", "Standard Calculator", "Math", "Everyday basic math operations", Icons.Default.Calculate, BrandIndigo),
    ToolItem("scientific", "Scientific Calculator", "Math", "Trigonometry, logarithms & powers", Icons.Default.Functions, BrandViolet),
    ToolItem("percentage", "Percentage Calculator", "Math", "Quick % changes, increase & decrease", Icons.Default.Percent, BrandCyan),

    // Finance
    ToolItem("loan", "Loan & EMI Calculator", "Finance", "Monthly payments & total interest", Icons.Default.AccountBalance, BrandEmerald),
    ToolItem("compound", "Compound Interest", "Finance", "Project wealth growth over time", Icons.AutoMirrored.Filled.TrendingUp, BrandEmerald),
    ToolItem("savings", "Savings Goal", "Finance", "Track target savings & monthly deposit", Icons.Default.Savings, BrandEmerald),
    ToolItem("discount", "Discount & Tax", "Finance", "Instant sale price & amount saved", Icons.Default.LocalOffer, BrandAmber),
    ToolItem("profit", "Profit & Margin", "Finance", "Markup, profit margin & cost price", Icons.Default.MonetizationOn, BrandEmerald),
    ToolItem("tip", "Tip & Bill Split", "Finance", "Split bills fairly with custom tips", Icons.AutoMirrored.Filled.ReceiptLong, BrandAmber),

    // Converters
    ToolItem("unit", "Unit Converter", "Converters", "Length, weight, temp, volume & speed", Icons.Default.SwapHoriz, BrandCyan),
    ToolItem("storage", "Digital Storage", "Converters", "Convert KB, MB, GB, TB, PB & binary", Icons.Default.Storage, BrandCyan),

    // Daily & Life
    ToolItem("age", "Age Calculator", "Daily", "Exact age & countdown to next birthday", Icons.Default.Cake, BrandRose),
    ToolItem("date", "Date Calculator", "Daily", "Days between dates or add/subtract", Icons.Default.CalendarToday, BrandIndigoLight),
    ToolItem("time", "Time Interval", "Daily", "Duration between hours & spans", Icons.Default.Schedule, BrandCyanLight),
    ToolItem("fuel", "Fuel & Trip Cost", "Daily", "Trip fuel needed & estimated budget", Icons.Default.DirectionsCar, BrandAmber),
    ToolItem("electricity", "Electricity Usage", "Daily", "Appliance kWh usage and cost", Icons.Default.Bolt, BrandAmber)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    favorites: Set<String>,
    recent: List<String>,
    onOpenTool: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalCalculatorPalette.current
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Finance", "Math", "Converters", "Daily")

    val filteredTools = remember(searchQuery, selectedCategory) {
        AppToolsList.filter { tool ->
            val matchesCategory = selectedCategory == "All" || tool.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                    tool.title.contains(searchQuery, ignoreCase = true) ||
                    tool.subtitle.contains(searchQuery, ignoreCase = true) ||
                    tool.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val favoriteTools = remember(favorites) {
        AppToolsList.filter { it.id in favorites }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // App Header Brand
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(palette.accentGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "CALCMATE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandCyan,
                                letterSpacing = 2.sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Calculate Smarter",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "All-in-one smart calculator suite for everyday life.",
                    fontSize = 14.sp,
                    color = palette.textSecondary
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search 16+ calculators & converters...", color = palette.textTertiary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandIndigo) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = palette.textSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandIndigo,
                    unfocusedBorderColor = palette.cardBorder,
                    focusedContainerColor = palette.numKeyBg,
                    unfocusedContainerColor = palette.numKeyBg
                )
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val selected = category == selectedCategory
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                        shape = RoundedCornerShape(14.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandIndigo,
                            selectedLabelColor = Color.White,
                            containerColor = palette.numKeyBg,
                            labelColor = palette.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) BrandIndigo else palette.cardBorder
                        )
                    )
                }
            }
        }

        // Hero Quick Calculator Card
        if (searchQuery.isBlank() && selectedCategory == "All") {
            item {
                Card(
                    onClick = { onOpenTool("standard") },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(BrandIndigoDark, Color(0xFF1E1B4B))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = BrandCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "FEATURED",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandCyan
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Smart Calculator",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Live preview, history drawer & scientific mode",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Tools
        if (searchQuery.isBlank() && selectedCategory == "All" && recent.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Recently Used",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        recent.take(4).forEach { recentId ->
                            AppToolsList.firstOrNull { it.id == recentId }?.let { tool ->
                                Surface(
                                    onClick = { onOpenTool(tool.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = palette.numKeyBg,
                                    border = CardDefaults.outlinedCardBorder()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            tool.icon,
                                            contentDescription = null,
                                            tint = tool.accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = tool.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = palette.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Favorites Section
        if (searchQuery.isBlank() && selectedCategory == "All" && favoriteTools.isNotEmpty()) {
            item {
                Text(
                    text = "Favorites",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            }
            items(favoriteTools) { tool ->
                ModernToolCard(
                    tool = tool,
                    isFavorite = true,
                    onOpen = { onOpenTool(tool.id) },
                    onToggleFavorite = { onToggleFavorite(tool.id) }
                )
            }
        }

        // Native Ad Card
        if (searchQuery.isBlank() && selectedCategory == "All") {
            item {
                NativeAdCard(modifier = Modifier.fillMaxWidth())
            }
        }

        // All / Filtered Tools Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedCategory == "All") "All Tools" else "$selectedCategory Tools",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
                Text(
                    text = "${filteredTools.size} available",
                    fontSize = 13.sp,
                    color = palette.textTertiary
                )
            }
        }

        // Tools List
        items(filteredTools) { tool ->
            ModernToolCard(
                tool = tool,
                isFavorite = tool.id in favorites,
                onOpen = { onOpenTool(tool.id) },
                onToggleFavorite = { onToggleFavorite(tool.id) }
            )
        }

        item {
            AdaptiveBannerAd(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun ModernToolCard(
    tool: ToolItem,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val palette = LocalCalculatorPalette.current

    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with soft tinted background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(tool.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = tool.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            // Tool Title and Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tool.subtitle,
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Favorite star button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) BrandAmber else palette.textTertiary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
