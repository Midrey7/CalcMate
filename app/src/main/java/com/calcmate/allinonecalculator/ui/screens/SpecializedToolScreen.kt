package com.calcmate.allinonecalculator.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calcmate.allinonecalculator.ads.AdaptiveBannerAd
import com.calcmate.allinonecalculator.domain.CalculatorFormulas
import com.calcmate.allinonecalculator.domain.DateCalculations
import com.calcmate.allinonecalculator.domain.Numbers
import com.calcmate.allinonecalculator.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecializedToolScreen(
    toolId: String,
    title: String,
    favorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCalculationComplete: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalCalculatorPalette.current
    val tool = AppToolsList.firstOrNull { it.id == toolId }
    val accentColor = tool?.accentColor ?: BrandIndigo

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                        tool?.let {
                            Text(
                                text = it.category.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = palette.textPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (favorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (favorite) BrandAmber else palette.textTertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.numKeyBg
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(palette.cardGradient)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (toolId) {
                "unit" -> UnitConverterTool(onCalculationComplete)
                "storage" -> StorageConverterTool(onCalculationComplete)
                "tip" -> TipCalculatorTool(onCalculationComplete)
                "loan" -> LoanCalculatorTool(onCalculationComplete)
                "compound" -> CompoundInterestTool(onCalculationComplete)
                "savings" -> SavingsGoalTool(onCalculationComplete)
                "discount" -> DiscountTool(onCalculationComplete)
                "profit" -> ProfitMarginTool(onCalculationComplete)
                "percentage" -> PercentageTool(onCalculationComplete)
                "age" -> AgeCalculatorTool(onCalculationComplete)
                "date" -> DateCalculatorTool(onCalculationComplete)
                "time" -> TimeIntervalTool(onCalculationComplete)
                "fuel" -> FuelCostTool(onCalculationComplete)
                "electricity" -> ElectricityUsageTool(onCalculationComplete)
                else -> DefaultGenericTool(toolId, title, onCalculationComplete)
            }

            Spacer(Modifier.height(8.dp))
            AdaptiveBannerAd()
        }
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Unit Converter
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitConverterTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    val categories = listOf("Length", "Mass", "Temperature", "Area", "Volume", "Speed", "Time")
    var selectedCategory by remember { mutableStateOf("Length") }

    val unitsMap = mapOf(
        "Length" to listOf("m", "cm", "mm", "km", "in", "ft", "mi"),
        "Mass" to listOf("kg", "g", "lb", "oz"),
        "Temperature" to listOf("C", "F", "K"),
        "Area" to listOf("m²", "ft²", "km²"),
        "Volume" to listOf("L", "mL", "gal"),
        "Speed" to listOf("m/s", "km/h", "mph"),
        "Time" to listOf("s", "min", "h", "day")
    )

    var fromUnit by remember(selectedCategory) { mutableStateOf(unitsMap[selectedCategory]?.first() ?: "m") }
    var toUnit by remember(selectedCategory) { mutableStateOf(unitsMap[selectedCategory]?.getOrNull(1) ?: unitsMap[selectedCategory]?.first() ?: "m") }
    var inputVal by remember { mutableStateOf("1") }

    val result = remember(inputVal, fromUnit, toUnit, selectedCategory) {
        val num = inputVal.toDoubleOrNull() ?: 0.0
        val res = CalculatorFormulas.convert(num, selectedCategory, fromUnit, toUnit)
        Numbers.format(res)
    }

    LaunchedEffect(result) {
        onComplete("$inputVal $fromUnit to $toUnit", result)
    }

    // Category Tabs
    PrimaryScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        edgePadding = 0.dp,
        containerColor = palette.numKeyBg,
        contentColor = BrandCyan
    ) {
        categories.forEach { cat ->
            Tab(
                selected = selectedCategory == cat,
                onClick = { selectedCategory = cat },
                text = { Text(cat, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) }
            )
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = inputVal,
                onValueChange = { inputVal = it },
                label = { Text("Value to convert") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // From Unit Dropdown
                UnitDropdown("From", fromUnit, unitsMap[selectedCategory] ?: emptyList(), Modifier.weight(1f)) {
                    fromUnit = it
                }

                IconButton(
                    onClick = {
                        val temp = fromUnit
                        fromUnit = toUnit
                        toUnit = temp
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.SwapHoriz, "Swap", tint = BrandCyan)
                }

                // To Unit Dropdown
                UnitDropdown("To", toUnit, unitsMap[selectedCategory] ?: emptyList(), Modifier.weight(1f)) {
                    toUnit = it
                }
            }
        }
    }

    ResultCard(
        title = "Converted Result",
        primaryValue = "$result $toUnit",
        subtitle = "$inputVal $fromUnit = $result $toUnit",
        accentBrush = Brush.linearGradient(listOf(BrandCyan, BrandIndigo))
    )
}

// -------------------------------------------------------------
// Specialized Implementation: Digital Storage Converter
// -------------------------------------------------------------
@Composable
private fun StorageConverterTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var fromUnit by remember { mutableStateOf("GB") }
    var toUnit by remember { mutableStateOf("MB") }
    var inputVal by remember { mutableStateOf("1") }
    var isBinary by remember { mutableStateOf(true) }

    val result = remember(inputVal, fromUnit, toUnit, isBinary) {
        val num = inputVal.toDoubleOrNull() ?: 0.0
        val res = CalculatorFormulas.storage(num, fromUnit, toUnit, isBinary)
        Numbers.format(res)
    }

    LaunchedEffect(result) {
        onComplete("$inputVal $fromUnit to $toUnit", result)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = inputVal,
                onValueChange = { inputVal = it },
                label = { Text("Storage Size") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UnitDropdown("From", fromUnit, units, Modifier.weight(1f)) { fromUnit = it }
                IconButton(
                    onClick = {
                        val temp = fromUnit
                        fromUnit = toUnit
                        toUnit = temp
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.SwapHoriz, "Swap", tint = BrandCyan)
                }
                UnitDropdown("To", toUnit, units, Modifier.weight(1f)) { toUnit = it }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBinary) "Binary (1024 - RAM / OS)" else "Decimal (1000 - Hard Drives)",
                    fontSize = 13.sp,
                    color = palette.textSecondary
                )
                Switch(
                    checked = isBinary,
                    onCheckedChange = { isBinary = it }
                )
            }
        }
    }

    ResultCard(
        title = "Storage Result",
        primaryValue = "$result $toUnit",
        subtitle = "$inputVal $fromUnit = $result $toUnit (${if (isBinary) "Base 1024" else "Base 1000"})",
        accentBrush = Brush.linearGradient(listOf(BrandCyan, BrandIndigo))
    )
}

// -------------------------------------------------------------
// Specialized Implementation: Tip & Bill Split
// -------------------------------------------------------------
@Composable
private fun TipCalculatorTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var billText by remember { mutableStateOf("100") }
    var tipPercent by remember { mutableStateOf(15.0) }
    var peopleCount by remember { mutableIntStateOf(2) }

    val bill = billText.toDoubleOrNull() ?: 0.0
    val tipResult = remember(bill, tipPercent, peopleCount) {
        if (bill > 0 && peopleCount > 0) {
            CalculatorFormulas.tip(bill, tipPercent, peopleCount)
        } else null
    }

    LaunchedEffect(tipResult) {
        tipResult?.let {
            onComplete("Bill $bill, Tip ${tipPercent.toInt()}%, $peopleCount people", "Each: ${Numbers.format(it.perPerson)}")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = billText,
                onValueChange = { billText = it },
                label = { Text("Bill Amount ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tip Percentage: ${tipPercent.toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10.0, 15.0, 18.0, 20.0, 25.0).forEach { pct ->
                    val selected = tipPercent == pct
                    FilterChip(
                        selected = selected,
                        onClick = { tipPercent = pct },
                        label = { Text("${pct.toInt()}%") },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandAmber,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Split Between: $peopleCount people", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalIconButton(
                        onClick = { if (peopleCount > 1) peopleCount-- },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("$peopleCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Spacer(Modifier.width(12.dp))
                    FilledTonalIconButton(
                        onClick = { peopleCount++ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    tipResult?.let { res ->
        ResultCard(
            title = "Per Person Share",
            primaryValue = "$${Numbers.format(res.perPerson)}",
            subtitle = "Total Tip: $${Numbers.format(res.tip)}  •  Grand Total: $${Numbers.format(res.total)}",
            accentBrush = Brush.linearGradient(listOf(BrandAmber, BrandRose))
        )
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Loan & EMI
// -------------------------------------------------------------
@Composable
private fun LoanCalculatorTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var principalText by remember { mutableStateOf("10000") }
    var rateText by remember { mutableStateOf("6.5") }
    var monthsText by remember { mutableStateOf("24") }

    val principal = principalText.toDoubleOrNull() ?: 0.0
    val rate = rateText.toDoubleOrNull() ?: 0.0
    val months = monthsText.toIntOrNull() ?: 1

    val loanResult = remember(principal, rate, months) {
        if (principal > 0 && rate >= 0 && months > 0) {
            runCatching { CalculatorFormulas.loan(principal, rate, months) }.getOrNull()
        } else null
    }

    LaunchedEffect(loanResult) {
        loanResult?.let {
            onComplete("Loan $principal @ $rate% for $months mo", "Monthly $${Numbers.format(it.monthly)}")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = principalText,
                onValueChange = { principalText = it },
                label = { Text("Loan Amount ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = rateText,
                onValueChange = { rateText = it },
                label = { Text("Annual Interest Rate (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = monthsText,
                onValueChange = { monthsText = it },
                label = { Text("Term in Months") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    loanResult?.let { res ->
        ResultCard(
            title = "Monthly Payment (EMI)",
            primaryValue = "$${Numbers.format(res.monthly)}",
            subtitle = "Total Repayment: $${Numbers.format(res.total)}  •  Total Interest: $${Numbers.format(res.interest)}",
            accentBrush = Brush.linearGradient(listOf(BrandEmerald, BrandCyan))
        )
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Compound Interest
// -------------------------------------------------------------
@Composable
private fun CompoundInterestTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var principalText by remember { mutableStateOf("5000") }
    var rateText by remember { mutableStateOf("7.0") }
    var yearsText by remember { mutableStateOf("10") }
    var contribText by remember { mutableStateOf("200") }

    val principal = principalText.toDoubleOrNull() ?: 0.0
    val rate = rateText.toDoubleOrNull() ?: 0.0
    val years = yearsText.toDoubleOrNull() ?: 0.0
    val contrib = contribText.toDoubleOrNull() ?: 0.0

    val finalBalance = remember(principal, rate, years, contrib) {
        if (principal >= 0 && rate >= 0 && years > 0) {
            runCatching { CalculatorFormulas.compound(principal, rate, years, 12, contrib) }.getOrNull()
        } else null
    }

    val totalContributions = contrib * years * 12

    LaunchedEffect(finalBalance) {
        finalBalance?.let {
            onComplete("Compound $principal @ $rate% for $years yrs", "$${Numbers.format(it)}")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = principalText,
                onValueChange = { principalText = it },
                label = { Text("Initial Principal ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = rateText,
                onValueChange = { rateText = it },
                label = { Text("Annual Interest Rate (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = yearsText,
                onValueChange = { yearsText = it },
                label = { Text("Investment Period (Years)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = contribText,
                onValueChange = { contribText = it },
                label = { Text("Monthly Contribution ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    finalBalance?.let { balance ->
        val interestEarned = (balance - principal - totalContributions).coerceAtLeast(0.0)
        ResultCard(
            title = "Final Projected Balance",
            primaryValue = "$${Numbers.format(balance)}",
            subtitle = "Contributions: $${Numbers.format(totalContributions)}  •  Interest Earned: $${Numbers.format(interestEarned)}",
            accentBrush = Brush.linearGradient(listOf(BrandEmerald, BrandCyan))
        )
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Savings Goal
// -------------------------------------------------------------
@Composable
private fun SavingsGoalTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var targetText by remember { mutableStateOf("20000") }
    var currentText by remember { mutableStateOf("2000") }
    var monthlyText by remember { mutableStateOf("500") }

    val target = targetText.toDoubleOrNull() ?: 0.0
    val current = currentText.toDoubleOrNull() ?: 0.0
    val monthly = monthlyText.toDoubleOrNull() ?: 1.0

    val savingsResult = remember(target, current, monthly) {
        if (target > current && monthly > 0) {
            runCatching { CalculatorFormulas.savings(target, current, monthly) }.getOrNull()
        } else null
    }

    LaunchedEffect(savingsResult) {
        savingsResult?.let {
            onComplete("Goal $target from $current with $monthly/mo", "${it.months} months")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = targetText,
                onValueChange = { targetText = it },
                label = { Text("Target Savings ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = currentText,
                onValueChange = { currentText = it },
                label = { Text("Current Savings ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = monthlyText,
                onValueChange = { monthlyText = it },
                label = { Text("Monthly Deposit ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    savingsResult?.let { res ->
        ResultCard(
            title = "Time to Reach Goal",
            primaryValue = "${res.months} Months (${(res.months / 12)}y ${res.months % 12}m)",
            subtitle = "Final Balance: $${Numbers.format(res.total)}",
            accentBrush = Brush.linearGradient(listOf(BrandEmerald, BrandCyan))
        )
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Discount & Tax
// -------------------------------------------------------------
@Composable
private fun DiscountTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var priceText by remember { mutableStateOf("120") }
    var discountText by remember { mutableStateOf("20") }
    var taxText by remember { mutableStateOf("8") }

    val price = priceText.toDoubleOrNull() ?: 0.0
    val discount = discountText.toDoubleOrNull() ?: 0.0
    val tax = taxText.toDoubleOrNull() ?: 0.0

    val discountResult = remember(price, discount, tax) {
        if (price >= 0 && discount in 0.0..100.0 && tax >= 0) {
            runCatching { CalculatorFormulas.discountSummary(price, discount, tax) }.getOrNull()
        } else null
    }

    LaunchedEffect(discountResult) {
        discountResult?.let {
            onComplete("Price $price, ${discount}% off, ${tax}% tax", "Final: $${Numbers.format(it.finalPrice)}")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Original Price ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = discountText,
                onValueChange = { discountText = it },
                label = { Text("Discount (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = taxText,
                onValueChange = { taxText = it },
                label = { Text("Sales Tax (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    discountResult?.let { res ->
        ResultCard(
            title = "Final Price",
            primaryValue = "$${Numbers.format(res.finalPrice)}",
            subtitle = "You Save: $${Numbers.format(res.saved)}",
            accentBrush = Brush.linearGradient(listOf(BrandAmber, BrandRose))
        )
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Profit & Margin
// -------------------------------------------------------------
@Composable
private fun ProfitMarginTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var costText by remember { mutableStateOf("50") }
    var saleText by remember { mutableStateOf("85") }

    val cost = costText.toDoubleOrNull() ?: 0.0
    val sale = saleText.toDoubleOrNull() ?: 0.0

    val profit = sale - cost
    val margin = if (sale > 0) (profit / sale) * 100 else 0.0
    val markup = if (cost > 0) (profit / cost) * 100 else 0.0

    LaunchedEffect(profit, margin, markup) {
        onComplete("Cost $cost, Sale $sale", "Profit $${Numbers.format(profit)} (${Numbers.format(margin)}%)")
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = costText,
                onValueChange = { costText = it },
                label = { Text("Cost Price ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = saleText,
                onValueChange = { saleText = it },
                label = { Text("Selling Price ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    ResultCard(
        title = "Net Profit",
        primaryValue = "$${Numbers.format(profit)}",
        subtitle = "Profit Margin: ${Numbers.format(margin)}%  •  Markup: ${Numbers.format(markup)}%",
        accentBrush = Brush.linearGradient(listOf(BrandEmerald, BrandCyan))
    )
}

// -------------------------------------------------------------
// Specialized Implementation: Percentage Calculator
// -------------------------------------------------------------
@Composable
private fun PercentageTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var percentText by remember { mutableStateOf("15") }
    var valueText by remember { mutableStateOf("250") }

    val p = percentText.toDoubleOrNull() ?: 0.0
    val v = valueText.toDoubleOrNull() ?: 0.0

    val result = CalculatorFormulas.percentOf(p, v)
    val increased = v * (1 + p / 100)
    val decreased = v * (1 - p / 100)

    LaunchedEffect(result) {
        onComplete("${p}% of $v", Numbers.format(result))
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = percentText,
                onValueChange = { percentText = it },
                label = { Text("Percentage (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = valueText,
                onValueChange = { valueText = it },
                label = { Text("Of Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    ResultCard(
        title = "${p}% of $v",
        primaryValue = Numbers.format(result),
        subtitle = "Increase: ${Numbers.format(increased)}  •  Decrease: ${Numbers.format(decreased)}",
        accentBrush = Brush.linearGradient(listOf(BrandCyan, BrandIndigo))
    )
}

// -------------------------------------------------------------
// Specialized Implementation: Age Calculator
// -------------------------------------------------------------
@Composable
private fun AgeCalculatorTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    var birthDate by remember { mutableStateOf(today.minusYears(25)) }

    val isFuture = birthDate.isAfter(today)
    val diff = remember(birthDate, today, isFuture) {
        if (!isFuture) runCatching { DateCalculations.difference(birthDate, today) }.getOrNull() else null
    }

    val daysUntilNextBday = remember(birthDate, today, isFuture) {
        if (!isFuture) runCatching { DateCalculations.daysUntilBirthday(birthDate, today) }.getOrElse { 0L } else 0L
    }

    LaunchedEffect(diff) {
        diff?.let {
            onComplete("Birth: $birthDate", "${it.years} years, ${it.months} months, ${it.days} days")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Date of Birth", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)

            OutlinedButton(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            birthDate = LocalDate.of(year, month + 1, day)
                        },
                        birthDate.year,
                        birthDate.monthValue - 1,
                        birthDate.dayOfMonth
                    ).apply {
                        datePicker.maxDate = System.currentTimeMillis()
                    }.show()
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Cake, contentDescription = null, tint = BrandRose)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = birthDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (diff != null) {
        ResultCard(
            title = "Exact Age",
            primaryValue = "${diff.years} Years",
            subtitle = "${diff.months} Months, ${diff.days} Days (${diff.totalDays} total days)\nNext birthday in $daysUntilNextBday days! 🎉",
            accentBrush = Brush.linearGradient(listOf(BrandRose, BrandViolet))
        )
    } else {
        ResultCard(
            title = "Invalid Date",
            primaryValue = "Future Date Selected",
            subtitle = "Please choose today or a date in the past.",
            accentBrush = Brush.linearGradient(listOf(BrandRose, BrandAmber))
        )
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Date Calculator
// -------------------------------------------------------------
@Composable
private fun DateCalculatorTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    val context = LocalContext.current
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusMonths(3)) }

    val from = if (endDate.isBefore(startDate)) endDate else startDate
    val to = if (endDate.isBefore(startDate)) startDate else endDate

    val diff = remember(from, to) {
        runCatching { DateCalculations.difference(from, to) }.getOrNull()
    }

    LaunchedEffect(diff) {
        diff?.let {
            onComplete("$startDate to $endDate", "${it.totalDays} days")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Start Date", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
            OutlinedButton(
                onClick = {
                    DatePickerDialog(context, { _, y, m, d -> startDate = LocalDate.of(y, m + 1, d) }, startDate.year, startDate.monthValue - 1, startDate.dayOfMonth).show()
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(startDate.toString(), fontWeight = FontWeight.SemiBold)
            }

            Text("End Date", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
            OutlinedButton(
                onClick = {
                    DatePickerDialog(context, { _, y, m, d -> endDate = LocalDate.of(y, m + 1, d) }, endDate.year, endDate.monthValue - 1, endDate.dayOfMonth).show()
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(endDate.toString(), fontWeight = FontWeight.SemiBold)
            }
        }
    }

    diff?.let { res ->
        ResultCard(
            title = "Total Difference",
            primaryValue = "${res.totalDays} Days",
            subtitle = "${res.years} Years, ${res.months} Months, ${res.days} Days",
            accentBrush = Brush.linearGradient(listOf(BrandIndigoLight, BrandCyan))
        )
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Time Interval Tool
// -------------------------------------------------------------
@Composable
private fun TimeIntervalTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    val context = LocalContext.current
    var startTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(17, 30)) }

    val duration = remember(startTime, endTime) {
        val minutes = java.time.Duration.between(startTime, endTime).toMinutes().let {
            if (it < 0) it + 1440 else it
        }
        val hours = minutes / 60
        val mins = minutes % 60
        "$hours h $mins m ($minutes total min)"
    }

    LaunchedEffect(duration) {
        onComplete("$startTime to $endTime", duration)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Start Time", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
            OutlinedButton(
                onClick = {
                    TimePickerDialog(context, { _, h, m -> startTime = LocalTime.of(h, m) }, startTime.hour, startTime.minute, true).show()
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(startTime.format(DateTimeFormatter.ofPattern("HH:mm")), fontWeight = FontWeight.SemiBold)
            }

            Text("End Time", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
            OutlinedButton(
                onClick = {
                    TimePickerDialog(context, { _, h, m -> endTime = LocalTime.of(h, m) }, endTime.hour, endTime.minute, true).show()
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(endTime.format(DateTimeFormatter.ofPattern("HH:mm")), fontWeight = FontWeight.SemiBold)
            }
        }
    }

    ResultCard(
        title = "Time Duration",
        primaryValue = duration,
        subtitle = "From ${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} to ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
        accentBrush = Brush.linearGradient(listOf(BrandCyanLight, BrandIndigo))
    )
}

// -------------------------------------------------------------
// Specialized Implementation: Fuel Cost
// -------------------------------------------------------------
@Composable
private fun FuelCostTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var distanceText by remember { mutableStateOf("350") }
    var economyText by remember { mutableStateOf("14") }
    var priceText by remember { mutableStateOf("1.65") }

    val distance = distanceText.toDoubleOrNull() ?: 0.0
    val economy = economyText.toDoubleOrNull() ?: 1.0
    val price = priceText.toDoubleOrNull() ?: 0.0

    val fuelResult = remember(distance, economy, price) {
        if (distance > 0 && economy > 0 && price >= 0) {
            runCatching { CalculatorFormulas.fuel(distance, economy, price) }.getOrNull()
        } else null
    }

    LaunchedEffect(fuelResult) {
        fuelResult?.let {
            onComplete("Trip $distance km @ $price/L", "$${Numbers.format(it.cost)}")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = distanceText,
                onValueChange = { distanceText = it },
                label = { Text("Trip Distance (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = economyText,
                onValueChange = { economyText = it },
                label = { Text("Fuel Economy (km per Litre)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Fuel Price per Litre ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    fuelResult?.let { res ->
        ResultCard(
            title = "Estimated Trip Cost",
            primaryValue = "$${Numbers.format(res.cost)}",
            subtitle = "Fuel Needed: ${Numbers.format(res.litres)} Litres",
            accentBrush = Brush.linearGradient(listOf(BrandAmber, BrandIndigo))
        )
    }
}

// -------------------------------------------------------------
// Specialized Implementation: Electricity Usage
// -------------------------------------------------------------
@Composable
private fun ElectricityUsageTool(onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var wattsText by remember { mutableStateOf("1500") }
    var hoursText by remember { mutableStateOf("4") }
    var daysText by remember { mutableStateOf("30") }
    var priceText by remember { mutableStateOf("0.22") }

    val watts = wattsText.toDoubleOrNull() ?: 0.0
    val hours = hoursText.toDoubleOrNull() ?: 0.0
    val days = daysText.toDoubleOrNull() ?: 0.0
    val price = priceText.toDoubleOrNull() ?: 0.0

    val electResult = remember(watts, hours, days, price) {
        if (watts > 0 && hours >= 0 && days >= 0 && price >= 0) {
            runCatching { CalculatorFormulas.electricity(watts, hours, days, price) }.getOrNull()
        } else null
    }

    LaunchedEffect(electResult) {
        electResult?.let {
            onComplete("${watts}W, ${hours}h/d, $days days", "$${Numbers.format(it.cost)}")
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = wattsText,
                onValueChange = { wattsText = it },
                label = { Text("Power Rating (Watts)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = hoursText,
                onValueChange = { hoursText = it },
                label = { Text("Hours Used Per Day") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = daysText,
                onValueChange = { daysText = it },
                label = { Text("Number of Days") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Electricity Cost per kWh ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    electResult?.let { res ->
        ResultCard(
            title = "Estimated Power Cost",
            primaryValue = "$${Numbers.format(res.cost)}",
            subtitle = "Total Consumption: ${Numbers.format(res.kwh)} kWh",
            accentBrush = Brush.linearGradient(listOf(BrandAmber, BrandCyan))
        )
    }
}

// -------------------------------------------------------------
// Generic Fallback
// -------------------------------------------------------------
@Composable
private fun DefaultGenericTool(id: String, title: String, onComplete: (String, String) -> Unit) {
    val palette = LocalCalculatorPalette.current
    var inputVal1 by remember { mutableStateOf("") }
    var inputVal2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = inputVal1,
                onValueChange = { inputVal1 = it },
                label = { Text("First Value") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = inputVal2,
                onValueChange = { inputVal2 = it },
                label = { Text("Second Value") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val x = inputVal1.toDoubleOrNull() ?: 0.0
                    val y = inputVal2.toDoubleOrNull() ?: 0.0
                    val res = Numbers.format(x + y)
                    result = res
                    onComplete("$inputVal1 + $inputVal2", res)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Calculate")
            }
        }
    }

    if (result.isNotEmpty()) {
        ResultCard(
            title = "Result",
            primaryValue = result,
            subtitle = "Calculation complete",
            accentBrush = Brush.linearGradient(listOf(BrandIndigo, BrandCyan))
        )
    }
}

// -------------------------------------------------------------
// Reusable UI Component: ResultCard
// -------------------------------------------------------------
@Composable
fun ResultCard(
    title: String,
    primaryValue: String,
    subtitle: String,
    accentBrush: Brush
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(accentBrush)
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Result", primaryValue))
                            Toast.makeText(context, "Copied: $primaryValue", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, "Copy", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = primaryValue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Reusable UI Component: UnitDropdown
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    label: String,
    selected: String,
    items: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, fontWeight = if (item == selected) FontWeight.Bold else FontWeight.Normal) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
