package com.example.lihascalculator.domain.engine.wiredrawing

import android.content.Context
import android.content.Intent
import com.example.lihascalculator.domain.model.wiredrawing.PassResult
import com.example.lihascalculator.domain.model.wiredrawing.WireDrawingStats
import java.util.Locale

object WireDrawingExportHelper {

    /**
     * Generates standard CSV with quoted values, headers, and 3-decimal formatted values.
     */
    fun generateCsv(passes: List<PassResult>, stats: WireDrawingStats): String {
        val sb = StringBuilder()
        sb.append("\"Pass\",\"From (mm)\",\"To (mm)\",\"Area Before (mm²)\",\"Area After (mm²)\",\"Area Reduction (%)\",\"Elongation (%)\",\"Ratio\"\n")

        for (p in passes) {
            sb.append(
                String.format(
                    Locale.US,
                    "\"%d\",\"%.3f\",\"%.3f\",\"%.3f\",\"%.3f\",\"%.3f\",\"%.3f\",\"%.3f\"\n",
                    p.passNumber,
                    p.fromDie,
                    p.toDie,
                    p.areaBefore,
                    p.areaAfter,
                    p.areaReductionPercent,
                    p.elongationPercent,
                    p.reductionRatio
                )
            )
        }

        sb.append("\n\"--- SUMMARY STATISTICS ---\"\n")
        sb.append(String.format(Locale.US, "\"Total Passes\",\"%d\"\n", stats.totalPasses))
        sb.append(String.format(Locale.US, "\"Starting Die (mm)\",\"%.3f\"\n", stats.startingDie))
        sb.append(String.format(Locale.US, "\"Final Die (mm)\",\"%.3f\"\n", stats.finalDie))
        sb.append(String.format(Locale.US, "\"Average Elongation (%%)\",\"%.3f\"\n", stats.avgElongationPercent))
        sb.append(String.format(Locale.US, "\"Maximum Elongation (%%)\",\"%.3f\"\n", stats.maxElongationPercent))
        sb.append(String.format(Locale.US, "\"Minimum Elongation (%%)\",\"%.3f\"\n", stats.minElongationPercent))
        sb.append(String.format(Locale.US, "\"Average Area Reduction (%%)\",\"%.3f\"\n", stats.avgAreaReductionPercent))
        sb.append(String.format(Locale.US, "\"Overall Area Reduction (%%)\",\"%.3f\"\n", stats.overallAreaReductionPercent))
        sb.append(String.format(Locale.US, "\"Overall Reduction Ratio\",\"%.3f\"\n", stats.overallReductionRatio))

        return sb.toString()
    }

    /**
     * Generates a plain text formatted engineering table report.
     */
    fun generateTextReport(passes: List<PassResult>, stats: WireDrawingStats): String {
        val sb = StringBuilder()
        sb.append("========================================================================================\n")
        sb.append("                      LICAL - WIRE DRAWING DIE SCHEDULE REPORT                          \n")
        sb.append("========================================================================================\n\n")

        sb.append(
            String.format(
                Locale.US,
                "%-6s %-10s %-10s %-14s %-14s %-14s %-14s %-8s\n",
                "PASS", "FROM(mm)", "TO(mm)", "AREA_IN(mm²)", "AREA_OUT(mm²)", "REDUCTION(%)", "ELONGATION(%)", "RATIO"
            )
        )
        sb.append("----------------------------------------------------------------------------------------\n")

        for (p in passes) {
            sb.append(
                String.format(
                    Locale.US,
                    "%-6d %-10.3f %-10.3f %-14.3f %-14.3f %-14.3f %-14.3f %-8.3f\n",
                    p.passNumber,
                    p.fromDie,
                    p.toDie,
                    p.areaBefore,
                    p.areaAfter,
                    p.areaReductionPercent,
                    p.elongationPercent,
                    p.reductionRatio
                )
            )
        }
        sb.append("----------------------------------------------------------------------------------------\n\n")

        sb.append("--- SUMMARY STATISTICS ---\n")
        sb.append(String.format(Locale.US, "• Total Passes:            %d\n", stats.totalPasses))
        sb.append(String.format(Locale.US, "• Starting Die:            %.3f mm\n", stats.startingDie))
        sb.append(String.format(Locale.US, "• Final Die:               %.3f mm\n", stats.finalDie))
        sb.append(String.format(Locale.US, "• Average Elongation:      %.3f %%\n", stats.avgElongationPercent))
        sb.append(String.format(Locale.US, "• Max Elongation:          %.3f %%\n", stats.maxElongationPercent))
        sb.append(String.format(Locale.US, "• Min Elongation:          %.3f %%\n", stats.minElongationPercent))
        sb.append(String.format(Locale.US, "• Average Area Reduction:  %.3f %%\n", stats.avgAreaReductionPercent))
        sb.append(String.format(Locale.US, "• Overall Area Reduction:  %.3f %%\n", stats.overallAreaReductionPercent))
        sb.append(String.format(Locale.US, "• Overall Reduction Ratio: %.3f\n", stats.overallReductionRatio))
        sb.append("========================================================================================\n")

        return sb.toString()
    }

    /**
     * Shares content via Android system share sheet.
     */
    fun shareContent(context: Context, title: String, content: String, mimeType: String = "text/plain") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            type = mimeType
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}
