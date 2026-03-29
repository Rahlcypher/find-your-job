package com.example.findyourjob_mobile.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.findyourjob_mobile.presentation.screens.auth.LoginScreen
import com.example.findyourjob_mobile.presentation.screens.auth.RegisterScreen
import com.example.findyourjob_mobile.presentation.screens.applications.ApplicationsScreen
import com.example.findyourjob_mobile.presentation.screens.chat.ChatListScreen
import com.example.findyourjob_mobile.presentation.screens.home.HomeScreen
import com.example.findyourjob_mobile.presentation.screens.jobs.JobDetailScreen
import com.example.findyourjob_mobile.presentation.screens.profile.ProfileScreen
import com.example.findyourjob_mobile.presentation.viewmodel.AuthViewModel
import com.example.findyourjob_mobile.presentation.components.BottomNavigationBar

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()

    val startDestination = Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                isLoggedIn = isLoggedIn,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(
    isLoggedIn: Boolean,
    onLogout: () -> Unit
) {
    val navController = androidx.navigation.compose.rememberNavController()

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (!isLoggedIn) {
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onJobClick = { jobId ->
                                navController.navigate(Screen.JobDetail.createRoute(jobId))
                            }
                        )
                    }

                    composable(Screen.JobList.route) {
                        ApplicationsScreen()
                    }

                    composable(
                        route = Screen.JobDetail.route,
                        arguments = listOf(navArgument("jobId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val jobId = backStackEntry.arguments?.getLong("jobId") ?: return@composable
                        JobDetailScreen(
                            jobId = jobId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.ChatList.route) {
                        ChatListScreen()
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen()
                    }
                }
            }
            BottomNavigationBar(navController = navController)
        }
    }
}
