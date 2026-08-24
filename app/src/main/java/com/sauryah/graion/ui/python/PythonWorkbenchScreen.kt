package com.sauryah.graion.ui.python

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sauryah.graion.domain.engine.scripting.PolyglotScriptEngine
import com.sauryah.graion.domain.model.scripting.ScriptLanguage
import com.sauryah.graion.theme.LocalCalculatorColors
import com.sauryah.graion.ui.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PythonWorkbenchScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current

    var selectedLanguage by remember { mutableStateOf(ScriptLanguage.PYTHON) }
    var code by remember { mutableStateOf(PolyglotScriptEngine.PRESETS[ScriptLanguage.PYTHON]?.firstOrNull()?.code ?: "") }
    var output by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }
    var executionTimeMs by remember { mutableStateOf<Long?>(null) }

    fun selectLanguage(lang: ScriptLanguage) {
        if (lang == selectedLanguage) return
        selectedLanguage = lang
        val defaultPreset = PolyglotScriptEngine.PRESETS[lang]?.firstOrNull()
        if (defaultPreset != null) {
            code = defaultPreset.code
        }
        output = ""
        executionTimeMs = null
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Code & Script Studio",
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
                actions = {
                    IconButton(
                        onClick = {
                            if (code.isNotEmpty()) {
                                ShareHelper.shareContent(
                                    context = context,
                                    title = "Export ${selectedLanguage.displayName} Script",
                                    content = code
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share script",
                            tint = colors.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.textPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Language Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScriptLanguage.entries.forEach { lang ->
                    val isSelected = lang == selectedLanguage
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectLanguage(lang) },
                        label = {
                            Text(
                                text = "${lang.iconEmoji} ${lang.displayName}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.accentPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // 2. Preset Chips for Current Language
            val currentPresets = PolyglotScriptEngine.PRESETS[selectedLanguage] ?: emptyList()
            if (currentPresets.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentPresets.forEach { preset ->
                        FilterChip(
                            selected = code == preset.code,
                            onClick = {
                                code = preset.code
                                output = ""
                                executionTimeMs = null
                            },
                            label = { Text(preset.title, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.accentPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = colors.accentPrimary
                            )
                        )
                    }
                }
            }

            // 3. Code Editor Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedLanguage.syntaxName.uppercase()} SCRIPT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = {
                                code = ""
                                output = ""
                                executionTimeMs = null
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear code",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = colors.textPrimary,
                            lineHeight = 18.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.accentPrimary,
                            unfocusedBorderColor = colors.surfaceVariant,
                            focusedContainerColor = colors.background.copy(alpha = 0.6f),
                            unfocusedContainerColor = colors.background.copy(alpha = 0.6f)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (!isRunning) {
                                isRunning = true
                                val startTime = System.currentTimeMillis()
                                output = PolyglotScriptEngine.execute(code, selectedLanguage)
                                executionTimeMs = System.currentTimeMillis() - startTime
                                isRunning = false
                            }
                        },
                        enabled = !isRunning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.accentPrimary,
                            disabledContainerColor = colors.accentPrimary.copy(alpha = 0.6f)
                        )
                    ) {
                        if (isRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EXECUTING...", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RUN SCRIPT", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }

            // 4. Output Terminal Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TERMINAL OUTPUT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary,
                                letterSpacing = 1.sp
                            )
                            if (executionTimeMs != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = colors.accentPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${executionTimeMs}ms",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.accentPrimary
                                    )
                                }
                            }
                        }

                        if (output.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    val clip = ClipData.newPlainText("${selectedLanguage.displayName} Output", output)
                                    clipboard?.setPrimaryClip(clip)
                                    Toast.makeText(context, "Output copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy output",
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = if (output.isEmpty()) "Terminal ready. Press 'RUN SCRIPT' to execute." else output,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = if (output.startsWith("Error") || output.contains("JavaScript Error") || output.contains("Lua Error")) Color(0xFFF87171) else Color(0xFF38BDF8),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}
