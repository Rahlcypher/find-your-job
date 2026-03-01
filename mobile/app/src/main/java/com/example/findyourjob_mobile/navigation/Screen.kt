package com.example.findyourjob_mobile.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Home : Screen("home")
    object JobList : Screen("jobs")
    object JobDetail : Screen("jobs/{jobId}") {
        fun createRoute(jobId: Long) = "jobs/$jobId"
    }
    object Applications : Screen("applications")
    object Profile : Screen("profile")
    object EditProfile : Screen("profile/edit")
    object ChatList : Screen("chats")
    object Conversation : Screen("chats/{chatId}") {
        fun createRoute(chatId: Long) = "chats/$chatId"
    }
}
