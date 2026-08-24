package com.sauryah.graion.ui.unitconverter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.graion.domain.engine.unitconverter.UnitCategory
import com.sauryah.graion.domain.engine.unitconverter.UnitConverterEngine
import com.sauryah.graion.domain.engine.unitconverter.UnitDefinition
import com.sauryah.graion.theme.LocalCalculatorColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current

    var category by remember { mutableStateOf(UnitCategory.LENGTH) }
    var fromUnit by remember { mutableStateOf(UnitConverterEngine.unitsFor(UnitCategory.LENGTH).first()) }
    var toUnit by remember { mutableStateOf(UnitConverterEngine.unitsFor(UnitCategory.LENGTH)[1]) }
    var input by remember { mutableStateOf("1") }
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val inputValue = input.toDoubleOrNull() ?: 0.0
    val converted = UnitConverterEngine.convert(inputValue, fromUnit, toUnit)

    val context = LocalContext.current
    var isSwapped by remember { mutableStateOf(false) }
    val swapRotation by animateFloatAsState(
        targetValue = if (isSwapped) 180f else 0f,
        animationSpec = tween(300),
        label = "swap_rotation"
    )

    fun switchCategory(newCategory: UnitCategory) {
        if (newCategory == category) return
        category = newCategory
        val defaults = UnitConverterEngine.defaultUnitsFor(newCategory)
        fromUnit = defaults.first
        toUnit = defaults.second
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Unit Converter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                    titleContentColor = colors.textPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            CategoryChips(
                selected = category,
                onSelect = ::switchCategory,
                colors = colors
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FROM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { newValue ->
                                if (newValue.length <= 18 && newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                                    input = newValue
                                }
                            },
                            trailingIcon = {
                                if (input.isNotEmpty() && input != "0") {
                                    IconButton(onClick = { input = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear input",
                                            tint = colors.textSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accentPrimary,
                                unfocusedBorderColor = colors.surfaceVariant,
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary,
                                cursorColor = colors.accentPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        UnitDropdown(
                            units = UnitConverterEngine.unitsFor(category),
                            selected = fromUnit,
                            expanded = fromExpanded,
                            onExpandedChange = { fromExpanded = it },
                            onSelect = { fromUnit = it },
                            colors = colors
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        val tmp = fromUnit
                        fromUnit = toUnit
                        toUnit = tmp
                        isSwapped = !isSwapped
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .rotate(swapRotation)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SwapHoriz,
                        contentDescription = "Swap units",
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val formattedResult = UnitConverterEngine.formatResult(converted)
                        Text(
                            text = formattedResult,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.accentPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val clip = ClipData.newPlainText("Converted Value", "$formattedResult ${toUnit.symbol}")
                                clipboard?.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied $formattedResult ${toUnit.symbol}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy converted value",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        UnitDropdown(
                            units = UnitConverterEngine.unitsFor(category),
                            selected = toUnit,
                            expanded = toExpanded,
                            onExpandedChange = { toExpanded = it },
                            onSelect = { toUnit = it },
                            colors = colors
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "1 ${fromUnit.symbol} = ${UnitConverterEngine.formatResult(UnitConverterEngine.convert(1.0, fromUnit, toUnit))} ${toUnit.symbol}",
                fontSize = 13.sp,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategoryChips(
    selected: UnitCategory,
    onSelect: (UnitCategory) -> Unit,
    colors: com.sauryah.graion.theme.CalculatorColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UnitCategory.entries.forEach { cat ->
            val isSelected = cat == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = if (isSelected) colors.accentPrimary else colors.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .semantics {
                        contentDescription = "${cat.displayName} category, ${if (isSelected) "selected" else "not selected"}"
                    }
            ) {
                Text(
                    text = cat.displayName,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) androidx.compose.ui.graphics.Color.White else colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun UnitDropdown(
    units: List<UnitDefinition>,
    selected: UnitDefinition,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (UnitDefinition) -> Unit,
    colors: com.sauryah.graion.theme.CalculatorColors
) {
    Box {
        Row(
            modifier = Modifier
                .background(colors.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                .clickable { onExpandedChange(true) }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected.symbol,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            containerColor = colors.surface
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${unit.symbol} — ${unit.name}",
                            color = colors.textPrimary,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onSelect(unit)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}