package com.sauryah.graion.domain.engine.unitconverter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UnitConverterEngineTest {

    @Test
    fun `length conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.LENGTH)
        val m = units.first { it.id == "m" }
        val km = units.first { it.id == "km" }
        val inch = units.first { it.id == "in" }
        val cm = units.first { it.id == "cm" }
        val ft = units.first { it.id == "ft" }

        assertEquals(1.0, UnitConverterEngine.convert(1000.0, m, km), 1e-6)
        assertEquals(2.54, UnitConverterEngine.convert(1.0, inch, cm), 1e-6)
        assertEquals(0.3048, UnitConverterEngine.convert(1.0, ft, m), 1e-6)
    }

    @Test
    fun `temperature conversions handle Celsius, Fahrenheit, and Kelvin`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.TEMPERATURE)
        val c = units.first { it.id == "c" }
        val f = units.first { it.id == "f" }
        val k = units.first { it.id == "k" }

        // Water freezing: 0 C = 32 F = 273.15 K
        assertEquals(32.0, UnitConverterEngine.convert(0.0, c, f), 1e-6)
        assertEquals(273.15, UnitConverterEngine.convert(0.0, c, k), 1e-6)

        // Water boiling: 100 C = 212 F
        assertEquals(212.0, UnitConverterEngine.convert(100.0, c, f), 1e-6)

        // Intersect: -40 C = -40 F
        assertEquals(-40.0, UnitConverterEngine.convert(-40.0, c, f), 1e-6)
        assertEquals(-40.0, UnitConverterEngine.convert(-40.0, f, c), 1e-6)
    }

    @Test
    fun `mass conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.MASS)
        val kg = units.first { it.id == "kg" }
        val g = units.first { it.id == "g" }
        val lb = units.first { it.id == "lb" }

        assertEquals(1000.0, UnitConverterEngine.convert(1.0, kg, g), 1e-6)
        assertEquals(2.20462262, UnitConverterEngine.convert(1.0, kg, lb), 1e-4)
    }

    @Test
    fun `speed conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.SPEED)
        val mps = units.first { it.id == "mps" }
        val kmph = units.first { it.id == "kmph" }
        val mph = units.first { it.id == "mph" }

        assertEquals(36.0, UnitConverterEngine.convert(10.0, mps, kmph), 1e-6)
        assertEquals(60.0, UnitConverterEngine.convert(96.56064, kmph, mph), 1e-3)
    }

    @Test
    fun `defaultUnitsFor returns non-null valid defaults for all categories`() {
        UnitCategory.entries.forEach { category ->
            val (from, to) = UnitConverterEngine.defaultUnitsFor(category)
            assertNotNull(from)
            assertNotNull(to)
        }
    }

    @Test
    fun `formatResult handles edge cases`() {
        assertEquals("0", UnitConverterEngine.formatResult(0.0))
        assertEquals("—", UnitConverterEngine.formatResult(Double.NaN))
        assertEquals("—", UnitConverterEngine.formatResult(Double.POSITIVE_INFINITY))
        assertEquals("42", UnitConverterEngine.formatResult(42.0))
        assertEquals("3.14159", UnitConverterEngine.formatResult(3.14159))
    }
}
