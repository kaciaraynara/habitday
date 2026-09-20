package com.example.habitday.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habitday.HabitApplication
import kotlinx.coroutines.launch
import com.example.habitday.ui.components.MascotStyle
import com.example.habitday.ui.screens.auth.LoginScreen
import com.example.habitday.ui.screens.auth.RegisterScreen
import com.example.habitday.ui.screens.home.HomeScreen
import com.example.habitday.ui.screens.habits.HabitChoicesScreen
import com.example.habitday.ui.screens.habits.HabitsScreen
import com.example.habitday.ui.screens.habitform.HabitFormScreen
import com.example.habitday.ui.screens.onboarding.OnboardingScreen
import com.example.habitday.ui.screens.progress.ProgressScreen
import com.example.habitday.ui.screens.settings.AboutScreen
import com.example.habitday.ui.screens.settings.PrivacyScreen
import com.example.habitday.ui.screens.settings.SettingsScreen
import com.example.habitday.ui.screens.time.TimeManagementScreen
import com.example.habitday.ui.theme.BackgroundWhite
import com.example.habitday.ui.theme.BrandBlue
import com.example.habitday.viewmodel.*

@Composable
fun AppNavigation(
    application: HabitApplication
) {
    val navController = rememberNavController()
    val repository = application.repository
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(repository))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository))
    val prefs by settingsViewModel.preferences.collectAsState()

    val startDestination = if (prefs?.isLoggedIn == true) {
        Screen.Home.route
    } else if (prefs?.isOnboardingCompleted == true) {
        Screen.Login.route
    } else {
        Screen.Onboarding.route
    }

    Scaffold(
        containerColor = BackgroundWhite,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val items = listOf(Screen.Home, Screen.Habits, Screen.Progress)
            
            val showBottomBar = items.any { it.route == currentDestination?.route }
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = BackgroundWhite,
                    contentColor = BrandBlue,
                    tonalElevation = 8.dp
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    imageVector = when(screen) {
                                        Screen.Home -> Icons.Default.Home
                                        Screen.Habits -> Icons.AutoMirrored.Filled.List
                                        Screen.Progress -> Icons.Default.Assessment
                                        else -> Icons.Default.Home
                                    }, 
                                    contentDescription = null
                                ) 
                            },
                            label = { Text(when(screen) {
                                Screen.Home -> "Hoje"
                                Screen.Habits -> "Hábitos"
                                Screen.Progress -> "Evolução"
                                else -> ""
                            }) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandBlue,
                                selectedTextColor = BrandBlue,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = BackgroundWhite
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        settingsViewModel.completeOnboarding()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
                val mascotStyle = when (prefs?.avatarStyle) {
                    "CALMO" -> MascotStyle.CALMO
                    "ENERGETICO" -> MascotStyle.ENERGETICO
                    "FOCADO" -> MascotStyle.FOCADO
                    "MINIMALISTA" -> MascotStyle.MINIMALISTA
                    else -> MascotStyle.CLASSICO
                }
                
                HomeScreen(
                    viewModel = homeViewModel,
                    application = application,
                    onAddHabit = { navController.navigate(Screen.HabitChoices.route) },
                    onSettings = { navController.navigate(Screen.Settings.route) },
                    mascotStyle = mascotStyle
                )
            }
            composable(Screen.Habits.route) {
                val habitViewModel: HabitViewModel = viewModel(factory = HabitViewModelFactory(repository))
                HabitsScreen(
                    viewModel = habitViewModel,
                    onAddHabit = { navController.navigate(Screen.HabitChoices.route) },
                    onEditHabit = { id -> navController.navigate(Screen.HabitForm.createRoute(id)) }
                )
            }
            composable(Screen.HabitChoices.route) {
                val hVM: HabitViewModel = viewModel(factory = HabitViewModelFactory(repository))
                val scope = androidx.compose.runtime.rememberCoroutineScope()
                HabitChoicesScreen(
                    onChoiceSelected = { preset ->
                        scope.launch {
                            hVM.insertHabit(
                                name = preset.name,
                                description = preset.category,
                                frequency = "Diário",
                                reminderTime = null,
                                category = preset.category,
                                icon = "check",
                                color = preset.color,
                                isHydration = preset.isHydration
                            )
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.HabitChoices.route) { inclusive = true }
                            }
                        }
                    },
                    onCustomHabit = { navController.navigate(Screen.HabitForm.createRoute(-1L)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Progress.route) {
                val progressViewModel: ProgressViewModel = viewModel(factory = ProgressViewModelFactory(repository))
                val mascotStyle = when (prefs?.avatarStyle) {
                    "CALMO" -> MascotStyle.CALMO
                    "ENERGETICO" -> MascotStyle.ENERGETICO
                    "FOCADO" -> MascotStyle.FOCADO
                    "MINIMALISTA" -> MascotStyle.MINIMALISTA
                    else -> MascotStyle.CLASSICO
                }
                ProgressScreen(
                    viewModel = progressViewModel,
                    application = application,
                    mascotStyle = mascotStyle
                )
            }
            composable(Screen.Settings.route) {
                val sVM: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(repository))
                SettingsScreen(
                    viewModel = sVM,
                    onBack = { navController.popBackStack() },
                    onAbout = { navController.navigate(Screen.About.route) },
                    onPrivacy = { navController.navigate(Screen.Privacy.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onTimeManagement = { navController.navigate(Screen.TimeManagement.route) }
                )
            }
            composable(Screen.About.route) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Privacy.route) {
                PrivacyScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.TimeManagement.route) {
                TimeManagementScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.HabitForm.route,
                arguments = listOf(navArgument("habitId") { type = NavType.LongType })
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getLong("habitId") ?: -1L
                val habitViewModel: HabitViewModel = viewModel(factory = HabitViewModelFactory(repository))
                HabitFormScreen(
                    habitId = habitId,
                    viewModel = habitViewModel,
                    notificationHelper = application.notificationHelper,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
