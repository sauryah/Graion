package com.sauryah.graion.domain.engine.scripting

import java.util.Locale
import kotlin.math.*

object RustSimulationEngine {

    fun execute(code: String): String {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) return ""

        val logs = mutableListOf<String>()
        val variables = mutableMapOf<String, Double>()

        // Standard physics & metallurgical constants
        variables["PI"] = PI
        variables["E"] = E

        try {
            val lines = trimmed.lines()

            for (rawLine in lines) {
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith("//") || line.startsWith("/*") || line.startsWith("*")) {
                    continue
                }

                // Handle println!(...)
                if (line.startsWith("println!(") && line.endsWith(");")) {
                    val content = line.substringAfter("println!(").substringBeforeLast(");")
                    val output = formatRustPrint(content, variables)
                    logs.add(output)
                    continue
                }

                // Handle let x: f64 = ... or let mut y = ...
                if (line.startsWith("let ")) {
                    val decl = line.substringAfter("let ").removePrefix("mut ").trim()
                    val name = decl.substringBefore("=").substringBefore(":").trim()
                    val expr = decl.substringAfter("=").trim().removeSuffix(";")
                    val evaluated = evaluateRustArithmetic(expr, variables)
                    variables[name] = evaluated
                    continue
                }

                // Handle assignment: x = ...
                if (line.contains("=") && !line.contains("==") && !line.contains("!=") && !line.contains("<=") && !line.contains(">=")) {
                    val name = line.substringBefore("=").trim()
                    if (name.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
                        val expr = line.substringAfter("=").trim().removeSuffix(";")
                        val evaluated = evaluateRustArithmetic(expr, variables)
                        variables[name] = evaluated
                        continue
                    }
                }
            }

            if (logs.isNotEmpty()) {
                return logs.joinToString("\n")
            }
            return "(Rust Native Kernel Simulation executed successfully)"
        } catch (e: Exception) {
            return "Rust Kernel Error: ${e.localizedMessage ?: "Simulation error"}"
        }
    }

    private fun formatRustPrint(content: String, vars: Map<String, Double>): String {
        // println!("... {}, {:.2} ...", a, b)
        val parts = content.split(",").map { it.trim() }
        if (parts.isEmpty()) return ""

        var fmt = parts[0].trim('"', '\'')
        val args = parts.drop(1).map { p ->
            vars[p] ?: evaluateRustArithmetic(p, vars)
        }

        var argIdx = 0
        val regex = Regex("\\{([^}]*)\\}")
        fmt = regex.replace(fmt) { match ->
            if (argIdx < args.size) {
                val value = args[argIdx++]
                val spec = match.groupValues[1]
                if (spec.startsWith(":.2")) {
                    String.format(Locale.US, "%.2f", value)
                } else if (spec.startsWith(":.3")) {
                    String.format(Locale.US, "%.3f", value)
                } else if (spec.startsWith(":.4")) {
                    String.format(Locale.US, "%.4f", value)
                } else {
                    formatNumber(value)
                }
            } else {
                match.value
            }
        }
        return fmt
    }

    private fun evaluateRustArithmetic(expr: String, vars: Map<String, Double>): Double {
        val clean = expr.trim()
        clean.toDoubleOrNull()?.let { return it }
        vars[clean]?.let { return it }

        // f64::sqrt(), f64::sin(), f64::cos(), f64::ln(), f64::powf(x, y), x.sqrt(), x.powf(y)
        if (clean.contains(".sqrt()")) {
            val base = clean.substringBefore(".sqrt()")
            return sqrt(evaluateRustArithmetic(base, vars))
        }
        if (clean.contains(".sin()")) {
            val base = clean.substringBefore(".sin()")
            return sin(evaluateRustArithmetic(base, vars))
        }
        if (clean.contains(".cos()")) {
            val base = clean.substringBefore(".cos()")
            return cos(evaluateRustArithmetic(base, vars))
        }
        if (clean.contains(".ln()")) {
            val base = clean.substringBefore(".ln()")
            return ln(evaluateRustArithmetic(base, vars))
        }

        // Binary operations (+, -, *, /, %)
        val tokens = clean
            .replace("+", " + ")
            .replace("-", " - ")
            .replace("*", " * ")
            .replace("/", " / ")
            .replace("%", " % ")
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

        if (tokens.size >= 3) {
            var acc = tokens[0].toDoubleOrNull() ?: vars[tokens[0]] ?: 0.0
            var i = 1
            while (i < tokens.size) {
                val op = tokens[i]
                val next = tokens[i + 1].toDoubleOrNull() ?: vars[tokens[i + 1]] ?: 0.0
                acc = when (op) {
                    "+" -> acc + next
                    "-" -> acc - next
                    "*" -> acc * next
                    "/" -> if (next != 0.0) acc / next else Double.NaN
                    "%" -> acc % next
                    else -> acc
                }
                i += 2
            }
            return acc
        }

        return clean.toDoubleOrNull() ?: 0.0
    }

    private fun formatNumber(v: Double): String {
        return if (v % 1.0 == 0.0 && abs(v) < 1e9) {
            v.toLong().toString()
        } else {
            String.format(Locale.US, "%.4f", v).trimEnd('0').trimEnd('.')
        }
    }
}
