package com.sauryah.graion.domain.engine.scripting

import java.util.Locale
import kotlin.math.*

object LuaEngine {

    fun execute(code: String): String {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) return ""

        val logs = mutableListOf<String>()
        val variables = mutableMapOf<String, Double>()

        // Lua default math constants
        variables["math.pi"] = PI
        variables["math.huge"] = Double.POSITIVE_INFINITY
        variables["pi"] = PI

        try {
            val lines = trimmed.lines()
            var lastResult: Any? = null

            for (rawLine in lines) {
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith("--")) {
                    continue
                }

                // Handle print(...)
                if (line.startsWith("print(") && line.endsWith(")")) {
                    val content = line.substringAfter("print(").substringBeforeLast(")")
                    val output = formatLuaPrint(content, variables)
                    logs.add(output)
                    continue
                }

                // Handle local x = ... or x = ...
                if (line.contains("=") && !line.contains("==") && !line.contains("~=") && !line.contains("<=") && !line.contains(">=")) {
                    val decl = if (line.startsWith("local ")) line.substringAfter("local ").trim() else line
                    val name = decl.substringBefore("=").trim()
                    if (name.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
                        val expr = decl.substringAfter("=").trim()
                        val evaluated = evaluateLuaArithmetic(expr, variables)
                        variables[name] = evaluated
                        lastResult = evaluated
                        continue
                    }
                }

                // Expression evaluation
                lastResult = evaluateLuaArithmetic(line, variables)
            }

            if (logs.isNotEmpty()) {
                return logs.joinToString("\n")
            } else if (lastResult != null) {
                return formatNumber(lastResult as Double)
            }
            return "(Executed successfully with no output)"
        } catch (e: Exception) {
            if (logs.isNotEmpty()) {
                return "${logs.joinToString("\n")}\n\n[Lua Runtime Notice: ${e.localizedMessage ?: "Evaluation error"}]"
            }
            return "Lua Error: ${e.localizedMessage ?: "Syntax or Evaluation error"}"
        }
    }

    private fun formatLuaPrint(content: String, vars: Map<String, Double>): String {
        // String.format in Lua: string.format("...", a, b)
        if (content.startsWith("string.format(") && content.endsWith(")")) {
            val inner = content.substringAfter("string.format(").substringBeforeLast(")")
            val parts = inner.split(",").map { it.trim() }
            if (parts.isNotEmpty()) {
                val fmt = parts[0].trim('"', '\'')
                val args = parts.drop(1).map { p ->
                    p.toDoubleOrNull() ?: vars[p] ?: evaluateLuaArithmetic(p, vars)
                }
                return try {
                    String.format(Locale.US, fmt.replace("%f", "%.4f").replace("%d", "%.0f"), *args.toTypedArray())
                } catch (e: Exception) {
                    args.joinToString(", ")
                }
            }
        }

        // Direct string literal "..." or '...'
        if ((content.startsWith("\"") && content.endsWith("\"")) || (content.startsWith("'") && content.endsWith("'"))) {
            return content.substring(1, content.length - 1)
        }

        // String concatenation with ..
        if (content.contains("..")) {
            val segments = content.split("..").map { it.trim() }
            return segments.joinToString("") { seg ->
                if ((seg.startsWith("\"") && seg.endsWith("\"")) || (seg.startsWith("'") && seg.endsWith("'"))) {
                    seg.substring(1, seg.length - 1)
                } else {
                    val evaluated = evaluateLuaArithmetic(seg, vars)
                    formatNumber(evaluated)
                }
            }
        }

        // Comma separated arguments
        val parts = content.split(",").map { it.trim() }
        return parts.joinToString("\t") { part ->
            if ((part.startsWith("\"") && part.endsWith("\"")) || (part.startsWith("'") && part.endsWith("'"))) {
                part.substring(1, part.length - 1)
            } else {
                try {
                    val v = evaluateLuaArithmetic(part, vars)
                    formatNumber(v)
                } catch (e: Exception) {
                    vars[part]?.let { formatNumber(it) } ?: part
                }
            }
        }
    }

    private fun evaluateLuaArithmetic(expr: String, vars: Map<String, Double>): Double {
        val clean = expr.trim()
        clean.toDoubleOrNull()?.let { return it }
        vars[clean]?.let { return it }

        // math.sqrt, math.sin, math.cos, math.tan, math.rad, math.deg, math.log, math.exp, math.abs
        if (clean.startsWith("math.sqrt(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("math.sqrt(").substringBeforeLast(")")
            return sqrt(evaluateLuaArithmetic(inner, vars))
        }
        if (clean.startsWith("math.sin(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("math.sin(").substringBeforeLast(")")
            return sin(evaluateLuaArithmetic(inner, vars))
        }
        if (clean.startsWith("math.cos(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("math.cos(").substringBeforeLast(")")
            return cos(evaluateLuaArithmetic(inner, vars))
        }
        if (clean.startsWith("math.tan(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("math.tan(").substringBeforeLast(")")
            return tan(evaluateLuaArithmetic(inner, vars))
        }
        if (clean.startsWith("math.rad(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("math.rad(").substringBeforeLast(")")
            return Math.toRadians(evaluateLuaArithmetic(inner, vars))
        }
        if (clean.startsWith("math.deg(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("math.deg(").substringBeforeLast(")")
            return Math.toDegrees(evaluateLuaArithmetic(inner, vars))
        }
        if (clean.startsWith("math.log(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("math.log(").substringBeforeLast(")")
            return ln(evaluateLuaArithmetic(inner, vars))
        }
        if (clean.startsWith("math.abs(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("math.abs(").substringBeforeLast(")")
            return abs(evaluateLuaArithmetic(inner, vars))
        }

        // Binary operations (+, -, *, /, ^, %)
        val tokens = clean
            .replace("+", " + ")
            .replace("-", " - ")
            .replace("*", " * ")
            .replace("/", " / ")
            .replace("^", " ^ ")
            .replace("%", " % ")
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

        if (tokens.size >= 3) {
            var acc = resolveOperand(tokens[0], vars)
            var i = 1
            while (i < tokens.size) {
                val op = tokens[i]
                val next = resolveOperand(tokens[i + 1], vars)
                acc = when (op) {
                    "+" -> acc + next
                    "-" -> acc - next
                    "*" -> acc * next
                    "/" -> if (next != 0.0) acc / next else Double.NaN
                    "^" -> acc.pow(next)
                    "%" -> acc % next
                    else -> acc
                }
                i += 2
            }
            return acc
        }

        return clean.toDoubleOrNull() ?: 0.0
    }

    private fun resolveOperand(token: String, vars: Map<String, Double>): Double {
        return token.toDoubleOrNull() ?: vars[token] ?: 0.0
    }

    private fun formatNumber(v: Double): String {
        return if (v % 1.0 == 0.0 && abs(v) < 1e9) {
            v.toLong().toString()
        } else {
            String.format(Locale.US, "%.4f", v).trimEnd('0').trimEnd('.')
        }
    }
}
