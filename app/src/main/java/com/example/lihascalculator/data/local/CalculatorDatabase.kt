package com.example.lihascalculator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CalculationEntity::class, WireDrawScheduleEntity::class],
    version = 3,
    exportSchema = true
)
abstract class CalculatorDatabase : RoomDatabase() {

    abstract fun calculationDao(): CalculationDao
    abstract fun wireDrawScheduleDao(): WireDrawScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: CalculatorDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `wire_draw_schedules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `diesCsv` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_calculations_timestamp` ON `calculations` (`timestamp`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_wire_draw_schedules_timestamp` ON `wire_draw_schedules` (`timestamp`)")
            }
        }

        fun getInstance(context: Context): CalculatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalculatorDatabase::class.java,
                    "calculator_history.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
