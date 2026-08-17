package com.example.lihascalculator.data.repository

import com.example.lihascalculator.data.local.CalculationDao
import com.example.lihascalculator.data.local.CalculationEntity
import com.example.lihascalculator.data.local.toDomainModel
import com.example.lihascalculator.domain.model.CalculationRecord
import com.example.lihascalculator.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(
    private val calculationDao: CalculationDao
) : HistoryRepository {

    override fun getHistory(): Flow<List<CalculationRecord>> {
        return calculationDao.getAllCalculations()
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun saveCalculation(expression: String, result: String): Long {
        val entity = CalculationEntity(
            expression = expression,
            result = result,
            timestamp = System.currentTimeMillis()
        )
        return calculationDao.insertCalculation(entity)
    }

    override suspend fun deleteCalculation(id: Long) {
        calculationDao.deleteById(id)
    }

    override suspend fun clearHistory() {
        calculationDao.clearAll()
    }
}
