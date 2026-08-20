package com.sauryah.graion.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy

class DatabaseMigrationTest {

    @Test
    fun testMigration1To2ExecutedSql() {
        val executedSql = mutableListOf<String>()
        val fakeDb = createFakeDatabase(executedSql)

        assertEquals(1, CalculatorDatabase.MIGRATION_1_2.startVersion)
        assertEquals(2, CalculatorDatabase.MIGRATION_1_2.endVersion)

        CalculatorDatabase.MIGRATION_1_2.migrate(fakeDb)

        assertEquals(1, executedSql.size)
        assertTrue(executedSql[0].contains("CREATE TABLE IF NOT EXISTS `wire_draw_schedules`"))
    }

    @Test
    fun testMigration2To3ExecutedSql() {
        val executedSql = mutableListOf<String>()
        val fakeDb = createFakeDatabase(executedSql)

        assertEquals(2, CalculatorDatabase.MIGRATION_2_3.startVersion)
        assertEquals(3, CalculatorDatabase.MIGRATION_2_3.endVersion)

        CalculatorDatabase.MIGRATION_2_3.migrate(fakeDb)

        assertEquals(2, executedSql.size)
        assertTrue(executedSql[0].contains("CREATE INDEX IF NOT EXISTS `index_calculations_timestamp`"))
        assertTrue(executedSql[1].contains("CREATE INDEX IF NOT EXISTS `index_wire_draw_schedules_timestamp`"))
    }

    private fun createFakeDatabase(executedSql: MutableList<String>): SupportSQLiteDatabase {
        return Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedSql.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase
    }
}
