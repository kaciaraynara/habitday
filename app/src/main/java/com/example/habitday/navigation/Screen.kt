package com.example.habitday.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Habits : Screen("habits")
    object HabitChoices : Screen("habitChoices")
    object HabitForm : Screen("habitForm/{habitId}") {
        fun createRoute(habitId: Long) = "habitForm/$habitId"
    }
    object Progress : Screen("progress")
    object Settings : Screen("settings")
    object About : Screen("about")
    object Privacy : Screen("privacy")
}
