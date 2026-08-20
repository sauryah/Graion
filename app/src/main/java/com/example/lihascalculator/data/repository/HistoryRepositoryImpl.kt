package com.sauryah.lihas.calculator.data.repository

import com.sauryah.lihas.calculator.data.local.CalculationDao
import com.sauryah.lihas.calculator.data.local.CalculationEntity
import com.sauryah.lihas.calculator.data.local.toDomainModel
import com.sauryah.lihas.calculator.domain.model.CalculationRecord
import com.sauryah.lihas.calculator.domain.repository.HistoryRepository
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
