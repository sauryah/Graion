package com.sauryah.graion.data.repository

import com.sauryah.graion.data.local.WireDrawScheduleDao
import com.sauryah.graion.data.local.WireDrawScheduleEntity
import com.sauryah.graion.data.local.toDomainModel
import com.sauryah.graion.domain.model.wiredrawing.SavedSchedule
import com.sauryah.graion.domain.repository.WireDrawScheduleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WireDrawScheduleRepositoryImpl(
    private val dao: WireDrawScheduleDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WireDrawScheduleRepository {

    override fun getSchedules(): Flow<List<SavedSchedule>> {
        return dao.getAllSchedules()
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun saveSchedule(name: String, dies: List<Double>): Long = withContext(ioDispatcher) {
        val entity = WireDrawScheduleEntity(
            name = name,
            diesCsv = dies.joinToString(",")
        )
        dao.insertSchedule(entity)
    }

    override suspend fun deleteSchedule(id: Long) = withContext(ioDispatcher) {
        dao.deleteScheduleById(id)
    }

    override suspend fun clearAllSchedules() = withContext(ioDispatcher) {
        dao.clearAllSchedules()
    }
}

