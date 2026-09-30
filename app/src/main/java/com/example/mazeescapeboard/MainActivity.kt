package com.example.mazeescapeboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mazeescapeboard.data.SettingsRepository
import com.example.mazeescapeboard.data.SoundAndHapticFeedback
import com.example.mazeescapeboard.ui.navigation.Screen
import com.example.mazeescapeboard.ui.screens.AboutScreen
import com.example.mazeescapeboard.ui.screens.GameScreen
import com.example.mazeescapeboard.ui.screens.HomeScreen
import com.example.mazeescapeboard.ui.screens.HowToPlayScreen
import com.example.mazeescapeboard.ui.screens.ResultScreen
import com.example.mazeescapeboard.ui.screens.SettingsScreen
import com.example.mazeescapeboard.ui.screens.SplashScreen
import com.example.mazeescapeboard.ui.theme.MazeEscapeBoardTheme
import com.example.mazeescapeboard.ui.viewmodel.GameViewModel
import com.example.mazeescapeboard.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsRepository = SettingsRepository(applicationContext)
        val soundAndHaptic = SoundAndHapticFeedback(applicationContext)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(settingsRepository)
            )
            val gameViewModel: GameViewModel = viewModel(
                factory = GameViewModel.Factory(application, settingsRepository, soundAndHaptic)
            )

            val settings by settingsViewModel.settings.collectAsState()

            MazeEscapeBoardTheme(
                themeMode = settings.themeMode,
                boardTheme = settings.boardTheme
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MazeEscapeAppNavHost(
                        settingsViewModel = settingsViewModel,
                        gameViewModel = gameViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MazeEscapeAppNavHost(
    settingsViewModel: SettingsViewModel,
    gameViewModel: GameViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                settingsViewModel = settingsViewModel,
                onPlayClicked = {
                    gameViewModel.startNewGameSession()
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClicked = {
                    navController.navigate(Screen.HowToPlay.route)
                },
                onSettingsClicked = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClicked = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                gameViewModel = gameViewModel,
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                gameViewModel = gameViewModel,
                onPlayAgainClicked = {
                    gameViewModel.startNewGameSession()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHomeClicked = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}