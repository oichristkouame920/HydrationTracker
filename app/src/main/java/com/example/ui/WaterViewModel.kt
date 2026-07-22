package com.example.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.WaterLog
import com.example.data.WaterRepository
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.AppThemeMode
import com.example.utils.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class WaterUiState(
    val logs: List<WaterLog> = emptyList(),
    val totalIntake: Int = 0,
    val dailyGoal: Int = 2000, // 2 Liters default
    val progress: Float = 0f
)

class WaterViewModel(
    private val repository: WaterRepository,
    private val notificationHelper: NotificationHelper,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _themeStyle = MutableStateFlow(
        AppThemeStyle.valueOf(
            sharedPreferences.getString("theme_style", AppThemeStyle.TURQUOISE.name) ?: AppThemeStyle.TURQUOISE.name
        )
    )
    val themeStyle = _themeStyle.asStateFlow()

    private val _themeMode = MutableStateFlow(
        AppThemeMode.valueOf(
            sharedPreferences.getString("theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        )
    )
    val themeMode = _themeMode.asStateFlow()

    fun setThemeStyle(style: AppThemeStyle) {
        _themeStyle.value = style
        sharedPreferences.edit().putString("theme_style", style.name).apply()
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        sharedPreferences.edit().putString("theme_mode", mode.name).apply()
    }

    val uiState: StateFlow<WaterUiState> = repository.allLogs
        .map { logs ->
            val todayLogs = logs.filter { isToday(it.timestamp) }
            val total = todayLogs.sumOf { it.amountMl }
            val goal = 2000
            val progress = (total.toFloat() / goal).coerceAtLeast(0f)
            WaterUiState(
                logs = todayLogs,
                totalIntake = total,
                dailyGoal = goal,
                progress = progress
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WaterUiState()
        )

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addLog(WaterLog(amountMl = amountMl))
            
            // Send system notification with the intake and remaining quantity
            val currentState = uiState.value
            val newTotal = currentState.totalIntake + amountMl
            val goal = currentState.dailyGoal
            notificationHelper.sendWaterIntakeNotification(amountMl, newTotal, goal)
        }
    }

    fun removeWaterLog(log: WaterLog) {
        viewModelScope.launch {
            repository.removeLog(log)
        }
    }

    fun resetIntake() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val logDate = Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(Calendar.YEAR) == logDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == logDate.get(Calendar.DAY_OF_YEAR)
    }
}

class WaterViewModelFactory(
    private val repository: WaterRepository,
    private val notificationHelper: NotificationHelper,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterViewModel(repository, notificationHelper, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


