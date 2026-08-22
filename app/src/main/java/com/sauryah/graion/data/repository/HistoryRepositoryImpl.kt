package com.sauryah.graion.data.repository

import com.sauryah.graion.data.local.CalculationDao
import com.sauryah.graion.data.local.CalculationEntity
import com.sauryah.graion.data.local.toDomainModel
import com.sauryah.graion.domain.model.CalculationRecord
import com.sauryah.graion.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HistoryRepositoryImpl(
    private val calculationDao: CalculationDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HistoryRepository {

    override fun getHistory(): Flow<List<CalculationRecord>> {
        return calculationDao.getAllCalculations()
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun saveCalculation(expression: String, result: String): Long = withContext(ioDispatcher) {
        val entity = CalculationEntity(
            expression = expression,
            result = result,
            timestamp = System.currentTimeMillis()
        )
        calculationDao.insertCalculation(entity)
    }

    override suspend fun deleteCalculation(id: Long) = withContext(ioDispatcher) {
        calculationDao.deleteById(id)
    }

    override suspend fun clearHistory() = withContext(ioDispatcher) {
        calculationDao.clearAll()
    }
}

