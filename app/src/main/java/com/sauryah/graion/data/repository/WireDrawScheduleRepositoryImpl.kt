package com.sauryah.graion.data.repository

import com.sauryah.graion.data.local.WireDrawScheduleDao
import com.sauryah.graion.data.local.WireDrawScheduleEntity
import com.sauryah.graion.data.local.toDomainModel
import com.sauryah.graion.domain.model.wiredrawing.SavedSchedule
import com.sauryah.graion.domain.repository.WireDrawScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class WireDrawScheduleRepositoryImpl(
    private val dao: WireDrawScheduleDao
) : WireDrawScheduleRepository {

    override fun getSchedules(): Flow<List<SavedSchedule>> {
        return dao.getAllSchedules()
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun saveSchedule(name: String, dies: List<Double>): Long {
        val entity = WireDrawScheduleEntity(
            name = name,
            diesCsv = dies.joinToString(",")
        )
        return dao.insertSchedule(entity)
    }

    override suspend fun deleteSchedule(id: Long) {
        dao.deleteScheduleById(id)
    }

    override suspend fun clearAllSchedules() {
        dao.clearAllSchedules()
    }
}
