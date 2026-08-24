package com.sauryah.graion.domain.engine.scripting

import com.sauryah.graion.domain.model.scripting.ScriptLanguage
import org.junit.Assert.assertTrue
import org.junit.Test

class PolyglotScriptEngineTest {

    @Test
    fun testJavaScriptExecution() {
        val jsCode = """
            const d1 = 2.0;
            const d2 = 1.0;
            const area1 = Math.PI * (d1 / 2) * (d1 / 2);
            const area2 = Math.PI * (d2 / 2) * (d2 / 2);
            console.log(`Area Ratio: ${area1 / area2}`);
        """.trimIndent()

        val output = JavaScriptEngine.execute(jsCode)
        assertTrue(output.contains("Area Ratio: 4"))
    }

    @Test
    fun testLuaExecution() {
        val luaCode = """
            local speed = 20.0
            local passes = 10
            print("Speed: " .. speed .. " m/s")
        """.trimIndent()

        val output = LuaEngine.execute(luaCode)
        assertTrue(output.contains("Speed: 20 m/s"))
    }

    @Test
    fun testRustSimulationExecution() {
        val rustCode = """
            let rho: f64 = 8960.0;
            let cp: f64 = 385.0;
            println!("Density: {:.1}", rho);
        """.trimIndent()

        val output = RustSimulationEngine.execute(rustCode)
        assertTrue(output.contains("Density: 8960.0"))
    }

    @Test
    fun testPolyglotPresetsExistForAllLanguages() {
        ScriptLanguage.entries.forEach { lang ->
            val presets = PolyglotScriptEngine.PRESETS[lang]
            assertTrue(presets != null && presets.isNotEmpty())
        }
    }
}
