package com.example.lihascalculator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CalculationEntity::class, WireDrawScheduleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CalculatorDatabase : RoomDatabase() {

    abstract fun calculationDao(): CalculationDao
    abstract fun wireDrawScheduleDao(): WireDrawScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: CalculatorDatabase? = null

        fun getInstance(context: Context): CalculatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalculatorDatabase::class.java,
                    "calculator_history.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
