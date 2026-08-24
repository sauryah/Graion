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
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.sauryah.graion.domain.engine.python.PythonEngine
import com.sauryah.graion.theme.LocalCalculatorColors

import androidx.compose.material3.CircularProgressIndicator

private val samplePresets = listOf(
    "Wire Drawing" to """# Calculate Constant Elongation Die Passes
d_in = 2.490
d_out = 0.309
passes = 18

ratio = (d_out / d_in) ** (1.0 / passes)
print(f"Required Ratio: {ratio:.4f}")
print("--- PASS SCHEDULE ---")
d = d_in
for i in range(1, passes + 1):
    next_d = d * ratio
    elong = ((d/next_d)**2 - 1) * 100
    print(f"Pass {i:02d}: {d:.3f} mm -> {next_d:.3f} mm | Elong: {elong:.2f}%")
    d = next_d
""",
    "Drawing Force" to """# Wire Drawing Force & Power (Siebel Formula)
import math

d_in = 2.490   # mm
d_out = 2.050  # mm
yield_stress = 450.0  # MPa (N/mm^2)
die_semi_angle_deg = 8.0  # degrees
mu = 0.08  # friction coefficient
drawing_speed = 12.0  # m/s

alpha = math.radians(die_semi_angle_deg)
area_in = math.pi * (d_in / 2)**2
area_out = math.pi * (d_out / 2)**2
reduction = (area_in - area_out) / area_in
epsilon = math.log(area_in / area_out)

# Siebel drawing stress formula: sigma_d = k_mean * (epsilon + (4/3)*alpha + mu*epsilon/alpha)
sigma_draw = yield_stress * (epsilon + (4.0/3.0)*math.sin(alpha) + (mu/math.tan(alpha))*epsilon)
draw_force_N = sigma_draw * area_out
draw_power_kW = (draw_force_N * drawing_speed) / 1000.0

print(f"Area Reduction: {reduction*100:.2f}%")
print(f"Drawing Stress: {sigma_draw:.2f} MPa")
print(f"Drawing Force:  {draw_force_N:.1f} N ({draw_force_N/9.80665:.1f} kgf)")
print(f"Drawing Power:  {draw_power_kW:.2f} kW")
""",
    "Math & Trig" to """import math

# Vector magnitude and angle
x, y = 12.5, 7.8
mag = math.sqrt(x**2 + y**2)
deg = math.degrees(math.atan2(y, x))

print(f"Vector: ({x}, {y})")
print(f"Magnitude: {mag:.4f}")
print(f"Angle: {deg:.2f}°")
""",
    "Flow Stress" to """# Work Hardening Flow Stress (Hollomon: sigma = K * epsilon^n)
import math

# Material Hollomon parameters
materials = {
    "Copper (ETP annealed)": {"K": 480.0, "n": 0.35, "sigma_0": 70.0},
    "Aluminum 1100-O":       {"K": 180.0, "n": 0.20, "sigma_0": 35.0},
    "Stainless Steel 304":   {"K": 1200.0, "n": 0.45, "sigma_0": 240.0},
    "High Carbon Steel":     {"K": 1100.0, "n": 0.15, "sigma_0": 550.0}
}

d0 = 2.490  # mm inlet
d1 = 0.500  # mm final

true_strain = 2.0 * math.log(d0 / d1)
print(f"Total True Strain: {true_strain:.3f}")
print("=" * 45)

for mat, params in materials.items():
    sigma = params["sigma_0"] + params["K"] * (true_strain ** params["n"])
    uts_approx = sigma * 1.08
    print(f"{mat:22s} -> Flow Stress: {sigma:6.1f} MPa | Est UTS: {uts_approx:6.1f} MPa")
""",
    "Resistance & Drop" to """# Copper Wire Resistance & Voltage Drop at 20°C
import math

diameter_mm = 1.628  # 14 AWG
length_m = 100.0     # metres
current_A = 15.0     # Amperes
rho_copper_20C = 0.017241  # ohm * mm^2 / m (IACS 100%)

area_mm2 = math.pi * (diameter_mm / 2.0)**2
resistance_20C = rho_copper_20C * (length_m / area_mm2)
v_drop = current_A * (2 * resistance_20C)  # Loop (send + return)
power_loss_W = (current_A ** 2) * (2 * resistance_20C)

print(f"Wire Diameter: {diameter_mm:.3f} mm (Area: {area_mm2:.3f} mm²)")
print(f"One-way Resistance: {resistance_20C:.4f} Ω")
print(f"Loop Voltage Drop:  {v_drop:.2f} V ({v_drop/240.0*100:.2f}% on 240V)")
print(f"I²R Power Loss:     {power_loss_W:.1f} Watts")
""",
    "Fibonacci" to """# Fibonacci Series Generator
def fib(n):
    a, b = 0, 1
    res = []
    for _ in range(n):
        res.append(a)
        a, b = b, a + b
    return res

print("First 15 Fibonacci numbers:")
print(fib(15))
"""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PythonWorkbenchScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current

    var code by remember { mutableStateOf(samplePresets[0].second) }
    var output by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Python Workbench",
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
            // Preset Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                samplePresets.forEach { (name, snippet) ->
                    FilterChip(
                        selected = code == snippet,
                        onClick = { code = snippet },
                        label = { Text(name, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.accentPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = colors.accentPrimary
                        )
                    )
                }
            }

            // Code Editor Card
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
                            text = "PYTHON SCRIPT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = { code = "" },
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
                            .height(220.dp),
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
                                output = PythonEngine.executeCode(code)
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

            // Output Terminal Card
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
                        }

                        if (output.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    val clip = ClipData.newPlainText("Python Output", output)
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
                            color = if (output.startsWith("Error")) Color(0xFFF87171) else Color(0xFF38BDF8),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}
