package com.sauryah.graion.domain.engine.wiredrawing

import com.sauryah.graion.domain.model.wiredrawing.PassResult
import com.sauryah.graion.domain.model.wiredrawing.WireDrawingStats
import org.junit.Assert.assertTrue
import org.junit.Test

class WireDrawingExportHelperTest {

    private val samplePasses = listOf(
        PassResult(
            passNumber = 1,
            fromDie = 2.0,
            toDie = 1.8,
            areaBefore = 3.14159,
            areaAfter = 2.54469,
            areaReductionPercent = 19.0,
            elongationPercent = 23.456,
            reductionRatio = 1.234
        ),
        PassResult(
            passNumber = 2,
            fromDie = 1.8,
            toDie = 1.6,
            areaBefore = 2.54469,
            areaAfter = 2.01062,
            areaReductionPercent = 20.988,
            elongationPercent = 26.562,
            reductionRatio = 1.265
        )
    )

    private val sampleStats = WireDrawingStats(
        totalPasses = 2,
        startingDie = 2.0,
        finalDie = 1.6,
        avgElongationPercent = 25.009,
        maxElongationPercent = 26.562,
        minElongationPercent = 23.456,
        avgAreaReductionPercent = 19.994,
        overallAreaReductionPercent = 36.0,
        overallReductionRatio = 1.5625
    )

    @Test
    fun `generateCsv generates valid CSV structure with headers and summary`() {
        val csv = WireDrawingExportHelper.generateCsv(samplePasses, sampleStats)

        assertTrue(csv.contains("\"Pass\",\"From (mm)\",\"To (mm)\""))
        assertTrue(csv.contains("\"1\",\"2.000\",\"1.800\""))
        assertTrue(csv.contains("\"2\",\"1.800\",\"1.600\""))
        assertTrue(csv.contains("\"--- SUMMARY STATISTICS ---\""))
        assertTrue(csv.contains("\"Total Passes\",\"2\""))
        assertTrue(csv.contains("\"Starting Die (mm)\",\"2.000\""))
        assertTrue(csv.contains("\"Final Die (mm)\",\"1.600\""))
    }

    @Test
    fun `generateTextReport generates formatted engineering report`() {
        val report = WireDrawingExportHelper.generateTextReport(samplePasses, sampleStats)

        assertTrue(report.contains("GRAION - WIRE DRAWING DIE SCHEDULE REPORT"))
        assertTrue(report.contains("PASS   FROM(mm)   TO(mm)"))
        assertTrue(report.contains("• Total Passes:            2"))
        assertTrue(report.contains("• Starting Die:            2.000 mm"))
        assertTrue(report.contains("• Final Die:               1.600 mm"))
    }
}
