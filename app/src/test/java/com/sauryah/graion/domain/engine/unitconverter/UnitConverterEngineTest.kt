package com.sauryah.graion.domain.engine.unitconverter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitConverterEngineTest {

    private fun units(category: UnitCategory) = UnitConverterEngine.unitsFor(category)

    @Test
    fun testLengthConversions() {
        val m = units(UnitCategory.LENGTH).first { it.id == "m" }
        val km = units(UnitCategory.LENGTH).first { it.id == "km" }
        val inch = units(UnitCategory.LENGTH).first { it.id == "in" }
        val ft = units(UnitCategory.LENGTH).first { it.id == "ft" }

        assertEquals(0.001, UnitConverterEngine.convert(1.0, m, km), 1e-12)
        assertEquals(1000.0, UnitConverterEngine.convert(1.0, km, m), 1e-9)
        assertEquals(39.3700787, UnitConverterEngine.convert(1.0, m, inch), 1e-6)
        assertEquals(3.2808399, UnitConverterEngine.convert(1.0, m, ft), 1e-6)
    }

    @Test
    fun testMassConversions() {
        val kg = units(UnitCategory.MASS).first { it.id == "kg" }
        val lb = units(UnitCategory.MASS).first { it.id == "lb" }
        val oz = units(UnitCategory.MASS).first { it.id == "oz" }

        assertEquals(2.2046226, UnitConverterEngine.convert(1.0, kg, lb), 1e-6)
        assertEquals(453.59237, UnitConverterEngine.convert(1.0, lb, units(UnitCategory.MASS).first { it.id == "g" }), 1e-6)
        assertEquals(16.0, UnitConverterEngine.convert(1.0, lb, oz), 1e-9)
    }

    @Test
    fun testAreaConversions() {
        val m2 = units(UnitCategory.AREA).first { it.id == "m2" }
        val ft2 = units(UnitCategory.AREA).first { it.id == "ft2" }
        val hectare = units(UnitCategory.AREA).first { it.id == "ha" }

        assertEquals(10.7639104, UnitConverterEngine.convert(1.0, m2, ft2), 1e-5)
        assertEquals(10_000.0, UnitConverterEngine.convert(1.0, hectare, m2), 1e-6)
    }

    @Test
    fun testVolumeConversions() {
        val l = units(UnitCategory.VOLUME).first { it.id == "l" }
        val gal = units(UnitCategory.VOLUME).first { it.id == "gal" }
        val ml = units(UnitCategory.VOLUME).first { it.id == "ml" }

        assertEquals(0.2641720524, UnitConverterEngine.convert(1.0, l, gal), 1e-8)
        assertEquals(1000.0, UnitConverterEngine.convert(1.0, l, ml), 1e-9)
    }

    @Test
    fun testTemperatureConversions() {
        val c = units(UnitCategory.TEMPERATURE).first { it.id == "c" }
        val f = units(UnitCategory.TEMPERATURE).first { it.id == "f" }
        val k = units(UnitCategory.TEMPERATURE).first { it.id == "k" }

        assertEquals(32.0, UnitConverterEngine.convert(0.0, c, f), 1e-9)
        assertEquals(100.0, UnitConverterEngine.convert(212.0, f, c), 1e-9)
        assertEquals(273.15, UnitConverterEngine.convert(0.0, c, k), 1e-9)
        assertEquals(-40.0, UnitConverterEngine.convert(-40.0, c, f), 1e-9)
    }

    @Test
    fun testSpeedConversions() {
        val mps = units(UnitCategory.SPEED).first { it.id == "mps" }
        val kmph = units(UnitCategory.SPEED).first { it.id == "kmph" }
        val knot = units(UnitCategory.SPEED).first { it.id == "kn" }

        assertEquals(3.6, UnitConverterEngine.convert(1.0, mps, kmph), 1e-9)
        assertEquals(1.94384449, UnitConverterEngine.convert(1.0, mps, knot), 1e-6)
    }

    @Test
    fun testRoundTrip() {
        for (category in UnitCategory.entries) {
            val list = units(category)
            for (from in list) {
                for (to in list) {
                    val value = 123.456
                    val roundTrip = UnitConverterEngine.convert(
                        UnitConverterEngine.convert(value, from, to),
                        to,
                        from
                    )
                    assertEquals(category.name + " " + from.id + "->" + to.id, value, roundTrip, 1e-6)
                }
            }
        }
    }

    @Test
    fun testFormatResult() {
        assertEquals("0", UnitConverterEngine.formatResult(0.0))
        assertEquals("1.5", UnitConverterEngine.formatResult(1.5))
        assertEquals("1000", UnitConverterEngine.formatResult(1000.0))
        assertTrue(UnitConverterEngine.formatResult(1e20).contains("e"))
    }
}