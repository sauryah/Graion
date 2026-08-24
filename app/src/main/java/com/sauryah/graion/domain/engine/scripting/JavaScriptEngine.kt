package com.sauryah.graion.domain.engine.scripting

import java.util.Locale
import kotlin.math.*

object JavaScriptEngine {

    fun execute(code: String): String {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) return ""

        val logs = mutableListOf<String>()
        val variables = mutableMapOf<String, Double>()

        // Default global constants
        variables["Math.PI"] = PI
        variables["Math.E"] = E
        variables["PI"] = PI
        variables["E"] = E

        try {
            val lines = trimmed.lines()
            var lastResult: Any? = null

            for (rawLine in lines) {
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith("//") || line.startsWith("/*") || line.startsWith("*")) {
                    continue
                }

                // Handle console.log(...)
                if (line.startsWith("console.log(") && line.endsWith(");") || (line.startsWith("console.log(") && line.endsWith(")"))) {
                    val content = line.substringAfter("console.log(").substringBeforeLast(")")
                    val output = formatJsLog(content, variables)
                    logs.add(output)
                    continue
                }

                // Handle variable declarations: let x = ..., const y = ..., var z = ...
                if (line.startsWith("let ") || line.startsWith("const ") || line.startsWith("var ")) {
                    val decl = line.substringAfter(" ").trim()
                    val name = decl.substringBefore("=").trim().substringAfter("let ").substringAfter("const ").substringAfter("var ").trim()
                    val expr = decl.substringAfter("=").trim().removeSuffix(";")
                    val evaluated = evaluateArithmetic(expr, variables)
                    variables[name] = evaluated
                    lastResult = evaluated
                    continue
                }

                // Handle variable assignment: x = ...
                if (line.contains("=") && !line.contains("==") && !line.contains("!=") && !line.contains("<=") && !line.contains(">=")) {
                    val name = line.substringBefore("=").trim()
                    if (name.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
                        val expr = line.substringAfter("=").trim().removeSuffix(";")
                        val evaluated = evaluateArithmetic(expr, variables)
                        variables[name] = evaluated
                        lastResult = evaluated
                        continue
                    }
                }

                // Handle expressions
                val expr = line.removeSuffix(";")
                lastResult = evaluateArithmetic(expr, variables)
            }

            if (logs.isNotEmpty()) {
                return logs.joinToString("\n")
            } else if (lastResult != null) {
                return formatNumber(lastResult as Double)
            }
            return "(Executed successfully with no output)"
        } catch (e: Exception) {
            if (logs.isNotEmpty()) {
                return "${logs.joinToString("\n")}\n\n[JS Runtime Notice: ${e.localizedMessage ?: "Evaluation error"}]"
            }
            return "JavaScript Error: ${e.localizedMessage ?: "Syntax or Evaluation error"}"
        }
    }

    private fun formatJsLog(content: String, vars: Map<String, Double>): String {
        // String template literal `... ${x} ...` or standard "..." + x
        if (content.startsWith("`") && content.endsWith("`")) {
            var raw = content.substring(1, content.length - 1)
            val regex = Regex("\\$\\{([^}]+)\\}")
            raw = regex.replace(raw) { match ->
                val expr = match.groupValues[1].trim()
                try {
                    val value = evaluateArithmetic(expr, vars)
                    formatNumber(value)
                } catch (e: Exception) {
                    match.value
                }
            }
            return raw
        }

        // Direct string literal "..." or '...'
        if ((content.startsWith("\"") && content.endsWith("\"")) || (content.startsWith("'") && content.endsWith("'"))) {
            return content.substring(1, content.length - 1)
        }

        // Comma separated arguments
        val parts = content.split(",").map { it.trim() }
        return parts.joinToString(" ") { part ->
            if ((part.startsWith("\"") && part.endsWith("\"")) || (part.startsWith("'") && part.endsWith("'"))) {
                part.substring(1, part.length - 1)
            } else {
                try {
                    val v = evaluateArithmetic(part, vars)
                    formatNumber(v)
                } catch (e: Exception) {
                    vars[part]?.let { formatNumber(it) } ?: part
                }
            }
        }
    }

    private fun evaluateArithmetic(expr: String, vars: Map<String, Double>): Double {
        var clean = expr.trim()
        
        // Handle .toFixed(n)
        if (clean.contains(".toFixed(")) {
            val baseExpr = clean.substringBefore(".toFixed(")
            return evaluateArithmetic(baseExpr, vars)
        }

        // Direct number
        clean.toDoubleOrNull()?.let { return it }

        // Variable lookup
        vars[clean]?.let { return it }

        // Math.sqrt(x), Math.sin(x), Math.cos(x), Math.tan(x), Math.log(x), Math.pow(x, y), Math.abs(x)
        if (clean.startsWith("Math.sqrt(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("Math.sqrt(").substringBeforeLast(")")
            return sqrt(evaluateArithmetic(inner, vars))
        }
        if (clean.startsWith("Math.sin(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("Math.sin(").substringBeforeLast(")")
            return sin(evaluateArithmetic(inner, vars))
        }
        if (clean.startsWith("Math.cos(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("Math.cos(").substringBeforeLast(")")
            return cos(evaluateArithmetic(inner, vars))
        }
        if (clean.startsWith("Math.tan(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("Math.tan(").substringBeforeLast(")")
            return tan(evaluateArithmetic(inner, vars))
        }
        if (clean.startsWith("Math.log(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("Math.log(").substringBeforeLast(")")
            return ln(evaluateArithmetic(inner, vars))
        }
        if (clean.startsWith("Math.abs(") && clean.endsWith(")")) {
            val inner = clean.substringAfter("Math.abs(").substringBeforeLast(")")
            return abs(evaluateArithmetic(inner, vars))
        }
        if (clean.startsWith("Math.pow(") && clean.endsWith(")")) {
            val args = clean.substringAfter("Math.pow(").substringBeforeLast(")").split(",")
            if (args.size == 2) {
                val b = evaluateArithmetic(args[0].trim(), vars)
                val e = evaluateArithmetic(args[1].trim(), vars)
                return b.pow(e)
            }
        }

        // Binary operations (+, -, *, /, **, %)
        val tokens = clean.replace("**", " ^ ")
            .replace("+", " + ")
            .replace("-", " - ")
            .replace("*", " * ")
            .replace("/", " / ")
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
