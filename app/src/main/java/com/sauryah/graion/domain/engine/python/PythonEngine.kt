package com.sauryah.graion.domain.engine.python

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

object PythonEngine {

    @Volatile
    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    try {
                        if (!Python.isStarted()) {
                            Python.start(AndroidPlatform(context.applicationContext))
                        }
                        isInitialized = true
                    } catch (e: Exception) {
                        isInitialized = false
                    }
                }
            }
        }
    }

    fun isAvailable(): Boolean = isInitialized && Python.isStarted()

    fun executeCode(code: String): String {
        return try {
            if (!Python.isStarted()) return "Python engine is not initialized."
            val py = Python.getInstance()
            val module = py.getModule("graion_engine")
            val pyResult = module.callAttr("execute_script", code)
            pyResult.toString()
        } catch (e: Exception) {
            "Execution Error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    fun generateDieSeries(startingDie: Double, finalDie: Double, numPasses: Int): List<Double> {
        return try {
            if (!Python.isStarted()) return emptyList()
            val py = Python.getInstance()
            val module = py.getModule("graion_engine")
            val pyResult = module.callAttr("calculate_die_series", startingDie, finalDie, numPasses)
            pyResult.asList().mapNotNull { it.toString().toDoubleOrNull() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
