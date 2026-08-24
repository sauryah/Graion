package com.sauryah.graion.domain.engine.scripting

import com.sauryah.graion.domain.engine.python.PythonEngine
import com.sauryah.graion.domain.model.scripting.ScriptLanguage
import com.sauryah.graion.domain.model.scripting.ScriptPreset

object PolyglotScriptEngine {

    fun execute(code: String, language: ScriptLanguage): String {
        return when (language) {
            ScriptLanguage.PYTHON -> PythonEngine.executeCode(code)
            ScriptLanguage.JAVASCRIPT -> JavaScriptEngine.execute(code)
            ScriptLanguage.LUA -> LuaEngine.execute(code)
            ScriptLanguage.RUST_NATIVE -> RustSimulationEngine.execute(code)
        }
    }

    val PRESETS: Map<ScriptLanguage, List<ScriptPreset>> = mapOf(
        ScriptLanguage.PYTHON to listOf(
            ScriptPreset(
                title = "Drawing Force",
                category = "Wire Engineering",
                language = ScriptLanguage.PYTHON,
                description = "Siebel formula for die drawing force and electric power",
                code = """# Wire Drawing Force & Power (Siebel Formula)
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

sigma_draw = yield_stress * (epsilon + (4.0/3.0)*math.sin(alpha) + (mu/math.tan(alpha))*epsilon)
draw_force_N = sigma_draw * area_out
draw_power_kW = (draw_force_N * drawing_speed) / 1000.0

print(f"Area Reduction: {reduction*100:.2f}%")
print(f"Drawing Stress: {sigma_draw:.2f} MPa")
print(f"Drawing Force:  {draw_force_N:.1f} N ({draw_force_N/9.80665:.1f} kgf)")
print(f"Drawing Power:  {draw_power_kW:.2f} kW")
"""
            ),
            ScriptPreset(
                title = "Flow Stress",
                category = "Material Science",
                language = ScriptLanguage.PYTHON,
                description = "Hollomon work hardening flow stress prediction",
                code = """# Work Hardening Flow Stress (Hollomon: sigma = K * epsilon^n)
import math

d0 = 2.490  # mm inlet
d1 = 0.500  # mm final
true_strain = 2.0 * math.log(d0 / d1)

K = 480.0  # Copper strength coeff (MPa)
n = 0.35   # Strain hardening exponent
sigma_0 = 70.0  # Initial yield stress

sigma_flow = sigma_0 + K * (true_strain ** n)
est_uts = sigma_flow * 1.08

print(f"True Strain:  {true_strain:.3f}")
print(f"Flow Stress:  {sigma_flow:.1f} MPa")
print(f"Est Tensile:  {est_uts:.1f} MPa")
"""
            ),
            ScriptPreset(
                title = "Resistance & Drop",
                category = "Electrical",
                language = ScriptLanguage.PYTHON,
                description = "Ohmic conductor loop resistance and power dissipation",
                code = """# Copper Wire Resistance & Voltage Drop at 20°C
import math

diameter_mm = 1.628  # 14 AWG
length_m = 100.0     # metres
current_A = 15.0     # Amperes
rho_copper = 0.017241  # ohm * mm^2 / m

area_mm2 = math.pi * (diameter_mm / 2.0)**2
resistance = rho_copper * (length_m / area_mm2)
v_drop = current_A * (2 * resistance)
power_loss_W = (current_A ** 2) * (2 * resistance)

print(f"Area:            {area_mm2:.3f} mm²")
print(f"Loop Resistance: {resistance * 2:.4f} Ω")
print(f"Voltage Drop:    {v_drop:.2f} V")
print(f"I²R Power Loss:  {power_loss_W:.1f} W")
"""
            )
        ),

        ScriptLanguage.JAVASCRIPT to listOf(
            ScriptPreset(
                title = "Elongation & Ratio",
                category = "Workshop Script",
                language = ScriptLanguage.JAVASCRIPT,
                description = "Calculate pass elongation percentage and area reduction",
                code = """// JavaScript Pass Elongation & Area Reduction
const dIn = 2.490;
const dOut = 2.217;

const areaIn = Math.PI * (dIn / 2) * (dIn / 2);
const areaOut = Math.PI * (dOut / 2) * (dOut / 2);
const areaReductionPct = ((areaIn - areaOut) / areaIn) * 100;
const elongationPct = ((areaIn / areaOut) - 1) * 100;

console.log("Inlet Die:      " + dIn + " mm");
console.log("Outlet Die:     " + dOut + " mm");
console.log("Area Reduction: " + areaReductionPct.toFixed(2) + " %");
console.log("Elongation:     " + elongationPct.toFixed(2) + " %");
"""
            ),
            ScriptPreset(
                title = "Spool Capacity",
                category = "Logistics",
                language = ScriptLanguage.JAVASCRIPT,
                description = "Estimate wire spool weight and winding capacity",
                code = """// Wire Spool Capacity & Linear Density
const wireDiaMm = 1.200;
const lengthM = 5000;
const copperDensity = 8.96; // g/cm^3

const areaCm2 = Math.PI * (wireDiaMm / 20) * (wireDiaMm / 20);
const volumeCm3 = areaCm2 * (lengthM * 100);
const totalWeightKg = (volumeCm3 * copperDensity) / 1000;
const linearMassGPerM = (totalWeightKg * 1000) / lengthM;

console.log("Wire Diameter: " + wireDiaMm + " mm");
console.log("Coil Length:   " + lengthM + " m");
console.log("Linear Mass:   " + linearMassGPerM.toFixed(3) + " g/m");
console.log("Total Weight:  " + totalWeightKg.toFixed(2) + " kg");
"""
            )
        ),

        ScriptLanguage.LUA to listOf(
            ScriptPreset(
                title = "Avitzur Die Angle",
                category = "Die Geometry",
                language = ScriptLanguage.LUA,
                description = "Optimal die approach angle using Avitzur upper bound formula",
                code = """-- Lua Avitzur Optimal Die Approach Angle
local d_in = 2.490
local d_out = 2.217
local mu = 0.05

local r = 1.0 - (d_out * d_out) / (d_in * d_in)
local alpha_opt_rad = math.sqrt(1.5 * mu * math.log(1.0 / (1.0 - r)))
local alpha_deg = math.deg(alpha_opt_rad * 2.0)
local delta = (alpha_opt_rad / r) * (1.0 + math.sqrt(1.0 - r))^2

print("Area Reduction: " .. string.format("%.2f %%", r * 100))
print("Optimal 2*Alpha: " .. string.format("%.2f deg", alpha_deg))
print("Delta Parameter: " .. string.format("%.2f", delta))
"""
            ),
            ScriptPreset(
                title = "Kinematics & Speed",
                category = "Machine Operation",
                language = ScriptLanguage.LUA,
                description = "Continuous multi-block capstan speed ratios",
                code = """-- Lua Capstan Speed & Production Rate
local finish_speed = 25.0 -- m/s
local d_finish = 0.500    -- mm
local d_inlet = 2.490     -- mm

local inlet_speed = finish_speed * (d_finish * d_finish) / (d_inlet * d_inlet)
local speed_ratio = finish_speed / inlet_speed

print("Finish Speed: " .. string.format("%.2f m/s", finish_speed))
print("Inlet Speed:  " .. string.format("%.3f m/s", inlet_speed))
print("Speed Ratio:  " .. string.format("%.2f x", speed_ratio))
"""
            )
        ),

        ScriptLanguage.RUST_NATIVE to listOf(
            ScriptPreset(
                title = "Thermal Gradient",
                category = "Physics Simulation",
                language = ScriptLanguage.RUST_NATIVE,
                description = "Finite difference wire heat generation and die deformation zone temperature",
                code = """// Rust Native Kernel: Deformation Zone Thermal Dissipation
let rho: f64 = 8960.0;     // Copper density kg/m^3
let cp: f64 = 385.0;       // Specific heat capacity J/(kg*K)
let sigma_mean: f64 = 350.0; // Mean drawing stress MPa (1e6 N/m^2)
let true_strain: f64 = 0.42;
let beta: f64 = 0.90;      // Taylor-Quinney deformation heat factor

let delta_t: f64 = (beta * sigma_mean * 1000000.0 * true_strain) / (rho * cp);
let ambient_t: f64 = 25.0;
let die_exit_t: f64 = ambient_t + delta_t;

println!("Plastic Work Heat: {:.2} J/m^3", sigma_mean * true_strain * 1000000.0);
println!("Adiabatic Temp Rise: +{:.1} °C", delta_t);
println!("Wire Exit Temp:      {:.1} °C", die_exit_t);
"""
            ),
            ScriptPreset(
                title = "Strain Hardening",
                category = "Metallurgy",
                language = ScriptLanguage.RUST_NATIVE,
                description = "Kocks-Mecking dislocation density evolution and flow stress",
                code = """// Rust Native: Dislocation Density & Yield Strength
let g_shear: f64 = 48000.0; // Shear modulus MPa
let b_burgers: f64 = 0.000000000256; // Burgers vector m
let alpha_taylor: f64 = 0.30;
let rho_disloc: f64 = 50000000000000.0; // Dislocation density m^-2

let delta_tau: f64 = alpha_taylor * g_shear * b_burgers * 7071067.8;
let sigma_yield: f64 = 70.0 + (3.06 * delta_tau);

println!("Dislocation Forest: {:.2e} m^-2", rho_disloc);
println!("Taylor Stress Add:  +{:.1} MPa", delta_tau * 3.06);
println!("Total Flow Stress:  {:.1} MPa", sigma_yield);
"""
            )
        )
    )
}
