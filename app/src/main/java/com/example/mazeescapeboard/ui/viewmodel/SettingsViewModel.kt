package com.example.mazeescapeboard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mazeescapeboard.data.BoardTheme
import com.example.mazeescapeboard.data.GameSettings
import com.example.mazeescapeboard.data.GameStats
import com.example.mazeescapeboard.data.SettingsRepository
import com.example.mazeescapeboard.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<GameSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = GameSettings()
        )

    val stats: StateFlow<GameStats> = settingsRepository.statsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = GameStats()
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(mode)
        }
    }

    fun setBoardTheme(theme: BoardTheme) {
        viewModelScope.launch {
            settingsRepository.updateBoardTheme(theme)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSoundEnabled(enabled)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateHapticsEnabled(enabled)
        }
    }

    fun setShowLegalMoves(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateShowLegalMoves(enabled)
        }
    }

    fun setShowTimer(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateShowTimer(enabled)
        }
    }

    fun resetStatistics() {
        viewModelScope.launch {
            settingsRepository.resetStatistics()
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository) as T
        }
    }
}
