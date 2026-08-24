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
    fun `pressure conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.PRESSURE)
        val pa = units.first { it.id == "pa" }
        val bar = units.first { it.id == "bar" }
        val mpa = units.first { it.id == "mpa" }
        val psi = units.first { it.id == "psi" }
        val atm = units.first { it.id == "atm" }

        assertEquals(1.0, UnitConverterEngine.convert(100_000.0, pa, bar), 1e-6)
        assertEquals(1.0, UnitConverterEngine.convert(1_000_000.0, pa, mpa), 1e-6)
        assertEquals(14.50377, UnitConverterEngine.convert(1.0, bar, psi), 1e-4)
        assertEquals(101325.0, UnitConverterEngine.convert(1.0, atm, pa), 1e-3)
    }

    @Test
    fun `power conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.POWER)
        val w = units.first { it.id == "w" }
        val kw = units.first { it.id == "kw" }
        val hp = units.first { it.id == "hp" }

        assertEquals(1.0, UnitConverterEngine.convert(1000.0, w, kw), 1e-6)
        assertEquals(1.34102, UnitConverterEngine.convert(1.0, kw, hp), 1e-4)
        assertEquals(745.69987, UnitConverterEngine.convert(1.0, hp, w), 1e-3)
    }

    @Test
    fun `force conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.FORCE)
        val n = units.first { it.id == "n" }
        val kn = units.first { it.id == "kn" }
        val lbf = units.first { it.id == "lbf" }
        val kgf = units.first { it.id == "kgf" }

        assertEquals(1.0, UnitConverterEngine.convert(1000.0, n, kn), 1e-6)
        assertEquals(4.44822, UnitConverterEngine.convert(1.0, lbf, n), 1e-4)
        assertEquals(9.80665, UnitConverterEngine.convert(1.0, kgf, n), 1e-4)
    }

    @Test
    fun `torque conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.TORQUE)
        val nm = units.first { it.id == "nm" }
        val knm = units.first { it.id == "knm" }
        val lbfft = units.first { it.id == "lbfft" }

        assertEquals(1.0, UnitConverterEngine.convert(1000.0, nm, knm), 1e-6)
        assertEquals(1.355818, UnitConverterEngine.convert(1.0, lbfft, nm), 1e-4)
    }

    @Test
    fun `data storage conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.DATA)
        val b = units.first { it.id == "b" }
        val kb = units.first { it.id == "kb" }
        val mb = units.first { it.id == "mb" }
        val gb = units.first { it.id == "gb" }
        val kib = units.first { it.id == "kib" }
        val mib = units.first { it.id == "mib" }

        assertEquals(1.0, UnitConverterEngine.convert(1000.0, b, kb), 1e-6)
        assertEquals(1.0, UnitConverterEngine.convert(1000.0, kb, mb), 1e-6)
        assertEquals(1024.0, UnitConverterEngine.convert(1.0, kib, b), 1e-6)
        assertEquals(1024.0, UnitConverterEngine.convert(1.0, mib, kib), 1e-6)
    }

    @Test
    fun `angle conversions are accurate`() {
        val units = UnitConverterEngine.unitsFor(UnitCategory.ANGLE)
        val deg = units.first { it.id == "deg" }
        val rad = units.first { it.id == "rad" }
        val rev = units.first { it.id == "rev" }

        assertEquals(360.0, UnitConverterEngine.convert(1.0, rev, deg), 1e-6)
        assertEquals(Math.PI, UnitConverterEngine.convert(180.0, deg, rad), 1e-5)
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
