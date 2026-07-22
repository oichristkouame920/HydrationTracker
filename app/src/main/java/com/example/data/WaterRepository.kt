package com.example.data

import kotlinx.coroutines.flow.Flow

class WaterRepository(private val waterLogDao: WaterLogDao) {
    val allLogs: Flow<List<WaterLog>> = waterLogDao.getAllLogs()

    suspend fun addLog(log: WaterLog) {
        waterLogDao.insertLog(log)
    }

    suspend fun removeLog(log: WaterLog) {
        waterLogDao.deleteLog(log)
    }

    suspend fun clearLogs() {
        waterLogDao.clearAllLogs()
    }
}
