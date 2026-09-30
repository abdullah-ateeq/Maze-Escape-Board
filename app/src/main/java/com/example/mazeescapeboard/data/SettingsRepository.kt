package com.example.mazeescapeboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "maze_escape_preferences")

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

enum class BoardTheme {
    CLASSIC_SLATE,
    MIDNIGHT_BRONZE
}

data class GameSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val boardTheme: BoardTheme = BoardTheme.CLASSIC_SLATE,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showLegalMoves: Boolean = true,
    val showTimer: Boolean = true
)

data class GameStats(
    val gamesPlayed: Int = 0,
    val escapesCompleted: Int = 0,
    val bestMoves: Int = 0,
    val bestTimeSeconds: Long = 0L,
    val fewestRotations: Int = 0
)

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SHOW_LEGAL_MOVES = booleanPreferencesKey("show_legal_moves")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")

        val GAMES_PLAYED = intPreferencesKey("games_played")
        val ESCAPES_COMPLETED = intPreferencesKey("escapes_completed")
        val BEST_MOVES = intPreferencesKey("best_moves")
        val BEST_TIME_SECONDS = longPreferencesKey("best_time_seconds")
        val FEWEST_ROTATIONS = intPreferencesKey("fewest_rotations")
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val themeModeStr = prefs[PreferencesKeys.THEME_MODE] ?: ThemeMode.DARK.name
            val boardThemeStr = prefs[PreferencesKeys.BOARD_THEME] ?: BoardTheme.CLASSIC_SLATE.name
            val themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.DARK)
            val boardTheme = runCatching { BoardTheme.valueOf(boardThemeStr) }.getOrDefault(BoardTheme.CLASSIC_SLATE)

            GameSettings(
                themeMode = themeMode,
                boardTheme = boardTheme,
                soundEnabled = prefs[PreferencesKeys.SOUND_ENABLED] ?: true,
                hapticsEnabled = prefs[PreferencesKeys.HAPTICS_ENABLED] ?: true,
                showLegalMoves = prefs[PreferencesKeys.SHOW_LEGAL_MOVES] ?: true,
                showTimer = prefs[PreferencesKeys.SHOW_TIMER] ?: true
            )
        }

    val statsFlow: Flow<GameStats> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            GameStats(
                gamesPlayed = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0,
                escapesCompleted = prefs[PreferencesKeys.ESCAPES_COMPLETED] ?: 0,
                bestMoves = prefs[PreferencesKeys.BEST_MOVES] ?: 0,
                bestTimeSeconds = prefs[PreferencesKeys.BEST_TIME_SECONDS] ?: 0L,
                fewestRotations = prefs[PreferencesKeys.FEWEST_ROTATIONS] ?: 0
            )
        }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun updateBoardTheme(theme: BoardTheme) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.BOARD_THEME] = theme.name
        }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun updateShowLegalMoves(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SHOW_LEGAL_MOVES] = enabled
        }
    }

    suspend fun updateShowTimer(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SHOW_TIMER] = enabled
        }
    }

    suspend fun incrementGamesPlayed() {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0
            prefs[PreferencesKeys.GAMES_PLAYED] = current + 1
        }
    }

    suspend fun recordEscapeCompleted(moves: Int, rotations: Int, timeSeconds: Long) {
        context.dataStore.edit { prefs ->
            val escapes = (prefs[PreferencesKeys.ESCAPES_COMPLETED] ?: 0) + 1
            prefs[PreferencesKeys.ESCAPES_COMPLETED] = escapes

            val currentBestMoves = prefs[PreferencesKeys.BEST_MOVES] ?: 0
            if (currentBestMoves == 0 || moves < currentBestMoves) {
                prefs[PreferencesKeys.BEST_MOVES] = moves
            }

            val currentBestTime = prefs[PreferencesKeys.BEST_TIME_SECONDS] ?: 0L
            if (currentBestTime == 0L || (timeSeconds in 1 until currentBestTime)) {
                prefs[PreferencesKeys.BEST_TIME_SECONDS] = timeSeconds
            }

            val currentFewestRotations = prefs[PreferencesKeys.FEWEST_ROTATIONS] ?: 0
            if (currentFewestRotations == 0 || rotations < currentFewestRotations) {
                prefs[PreferencesKeys.FEWEST_ROTATIONS] = rotations
            }
        }
    }

    suspend fun resetStatistics() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.GAMES_PLAYED] = 0
            prefs[PreferencesKeys.ESCAPES_COMPLETED] = 0
            prefs[PreferencesKeys.BEST_MOVES] = 0
            prefs[PreferencesKeys.BEST_TIME_SECONDS] = 0L
            prefs[PreferencesKeys.FEWEST_ROTATIONS] = 0
        }
    }
}
