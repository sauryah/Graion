package com.sauryah.lihas.calculator.domain.engine.unitconverter

import java.math.BigDecimal
import java.math.RoundingMode

enum class UnitCategory(val displayName: String) {
    LENGTH("Length"),
    MASS("Mass"),
    AREA("Area"),
    VOLUME("Volume"),
    TEMPERATURE("Temperature"),
    SPEED("Speed")
}

data class UnitDefinition(
    val id: String,
    val symbol: String,
    val name: String,
    val toBase: (Double) -> Double,
    val fromBase: (Double) -> Double
)

object UnitConverterEngine {

    private val lengthUnits = listOf(
        UnitDefinition("mm", "mm", "Millimetre", { it / 1000.0 }, { it * 1000.0 }),
        UnitDefinition("cm", "cm", "Centimetre", { it / 100.0 }, { it * 100.0 }),
        UnitDefinition("m", "m", "Metre", { it }, { it }),
        UnitDefinition("km", "km", "Kilometre", { it * 1000.0 }, { it / 1000.0 }),
        UnitDefinition("in", "in", "Inch", { it * 0.0254 }, { it / 0.0254 }),
        UnitDefinition("ft", "ft", "Foot", { it * 0.3048 }, { it / 0.3048 }),
        UnitDefinition("yd", "yd", "Yard", { it * 0.9144 }, { it / 0.9144 }),
        UnitDefinition("mi", "mi", "Mile", { it * 1609.344 }, { it / 1609.344 })
    )

    private val massUnits = listOf(
        UnitDefinition("mg", "mg", "Milligram", { it / 1_000_000.0 }, { it * 1_000_000.0 }),
        UnitDefinition("g", "g", "Gram", { it / 1000.0 }, { it * 1000.0 }),
        UnitDefinition("kg", "kg", "Kilogram", { it }, { it }),
        UnitDefinition("t", "t", "Tonne", { it * 1000.0 }, { it / 1000.0 }),
        UnitDefinition("oz", "oz", "Ounce", { it * 0.028349523125 }, { it / 0.028349523125 }),
        UnitDefinition("lb", "lb", "Pound", { it * 0.45359237 }, { it / 0.45359237 }),
        UnitDefinition("st", "st", "Stone", { it * 6.35029318 }, { it / 6.35029318 })
    )

    private val areaUnits = listOf(
        UnitDefinition("mm2", "mm²", "Square Millimetre", { it / 1_000_000.0 }, { it * 1_000_000.0 }),
        UnitDefinition("cm2", "cm²", "Square Centimetre", { it / 10_000.0 }, { it * 10_000.0 }),
        UnitDefinition("m2", "m²", "Square Metre", { it }, { it }),
        UnitDefinition("ha", "ha", "Hectare", { it * 10_000.0 }, { it / 10_000.0 }),
        UnitDefinition("km2", "km²", "Square Kilometre", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
        UnitDefinition("in2", "in²", "Square Inch", { it * 0.00064516 }, { it / 0.00064516 }),
        UnitDefinition("ft2", "ft²", "Square Foot", { it * 0.09290304 }, { it / 0.09290304 }),
        UnitDefinition("acre", "ac", "Acre", { it * 4046.8564224 }, { it / 4046.8564224 })
    )

    private val volumeUnits = listOf(
        UnitDefinition("ml", "mL", "Millilitre", { it / 1000.0 }, { it * 1000.0 }),
        UnitDefinition("l", "L", "Litre", { it }, { it }),
        UnitDefinition("m3", "m³", "Cubic Metre", { it * 1000.0 }, { it / 1000.0 }),
        UnitDefinition("tsp", "tsp", "Teaspoon (US)", { it * 0.00492892159375 }, { it / 0.00492892159375 }),
        UnitDefinition("tbsp", "tbsp", "Tablespoon (US)", { it * 0.01478676478125 }, { it / 0.01478676478125 }),
        UnitDefinition("cup", "cup", "Cup (US)", { it * 0.2365882365 }, { it / 0.2365882365 }),
        UnitDefinition("floz", "fl oz", "Fluid Ounce (US)", { it * 0.0295735295625 }, { it / 0.0295735295625 }),
        UnitDefinition("gal", "gal", "Gallon (US)", { it * 3.785411784 }, { it / 3.785411784 })
    )

    private val temperatureUnits = listOf(
        UnitDefinition("c", "°C", "Celsius", { it + 273.15 }, { it - 273.15 }),
        UnitDefinition("f", "°F", "Fahrenheit", { (it + 459.67) * 5.0 / 9.0 }, { it * 9.0 / 5.0 - 459.67 }),
        UnitDefinition("k", "K", "Kelvin", { it }, { it })
    )

    private val speedUnits = listOf(
        UnitDefinition("mps", "m/s", "Metre per second", { it }, { it }),
        UnitDefinition("kmph", "km/h", "Kilometre per hour", { it / 3.6 }, { it * 3.6 }),
        UnitDefinition("mph", "mph", "Mile per hour", { it * 0.44704 }, { it / 0.44704 }),
        UnitDefinition("kn", "kn", "Knot", { it * 0.5144444444444445 }, { it / 0.5144444444444445 }),
        UnitDefinition("fps", "ft/s", "Foot per second", { it * 0.3048 }, { it / 0.3048 })
    )

    private val unitsByCategory: Map<UnitCategory, List<UnitDefinition>> = mapOf(
        UnitCategory.LENGTH to lengthUnits,
        UnitCategory.MASS to massUnits,
        UnitCategory.AREA to areaUnits,
        UnitCategory.VOLUME to volumeUnits,
        UnitCategory.TEMPERATURE to temperatureUnits,
        UnitCategory.SPEED to speedUnits
    )

    fun unitsFor(category: UnitCategory): List<UnitDefinition> = unitsByCategory.getValue(category)

    fun defaultUnitsFor(category: UnitCategory): Pair<UnitDefinition, UnitDefinition> {
        val units = unitsFor(category)
        val from = when (category) {
            UnitCategory.LENGTH -> units.first { it.id == "m" }
            UnitCategory.MASS -> units.first { it.id == "kg" }
            UnitCategory.AREA -> units.first { it.id == "m2" }
            UnitCategory.VOLUME -> units.first { it.id == "l" }
            UnitCategory.TEMPERATURE -> units.first { it.id == "c" }
            UnitCategory.SPEED -> units.first { it.id == "mps" }
        }
        val to = when (category) {
            UnitCategory.LENGTH -> units.first { it.id == "km" }
            UnitCategory.MASS -> units.first { it.id == "g" }
            UnitCategory.AREA -> units.first { it.id == "ft2" }
            UnitCategory.VOLUME -> units.first { it.id == "gal" }
            UnitCategory.TEMPERATURE -> units.first { it.id == "f" }
            UnitCategory.SPEED -> units.first { it.id == "kmph" }
        }
        return from to to
    }

    fun convert(value: Double, from: UnitDefinition, to: UnitDefinition): Double {
        if (value.isNaN() || value.isInfinite()) return 0.0
        val inBase = from.toBase(value)
        return to.fromBase(inBase)
    }

    fun formatResult(value: Double): String {
        if (value == 0.0) return "0"
        if (value.isNaN() || value.isInfinite()) return "—"

        val abs = kotlin.math.abs(value)
        if (abs >= 1e15 || (abs > 0 && abs < 1e-9)) {
            return String.format(java.util.Locale.US, "%.6E", value).replace("E", "e")
        }

        val big = BigDecimal(value.toString())
        val scale = when {
            abs >= 1e6 -> 3
            abs >= 1e3 -> 4
            abs >= 1.0 -> 6
            else -> 10
        }
        return big.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
    }
}