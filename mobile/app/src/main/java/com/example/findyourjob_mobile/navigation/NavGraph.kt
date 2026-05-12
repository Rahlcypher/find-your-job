package com.example.findyourjob_mobile.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.findyourjob_mobile.presentation.screens.auth.LoginScreen
import com.example.findyourjob_mobile.presentation.screens.auth.RegisterScreen
import com.example.findyourjob_mobile.presentation.screens.applications.ApplicationsScreen
import com.example.findyourjob_mobile.presentation.screens.chat.ChatListScreen
import com.example.findyourjob_mobile.presentation.screens.home.HomeScreen
import com.example.findyourjob_mobile.presentation.screens.jobs.JobDetailScreen
import com.example.findyourjob_mobile.presentation.screens.profile.ProfileScreen
import com.example.findyourjob_mobile.presentation.screens.recruiter.MyJobsScreen
import com.example.findyourjob_mobile.presentation.screens.recruiter.JobFormScreen
import com.example.findyourjob_mobile.presentation.screens.recruiter.ApplicationsReceivedScreen
import com.example.findyourjob_mobile.presentation.viewmodel.AuthViewModel
import com.example.findyourjob_mobile.presentation.components.BottomNavigationBar
import com.example.findyourjob_mobile.presentation.components.RecruiterBottomNavigationBar

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userRole by authViewModel.userRole.collectAsStateWithLifecycle()

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
                userRole = userRole,
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
    userRole: String?,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

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

    val isRecruiter = userRole == "ROLE_RECRUITER"

    Scaffold(
        bottomBar = {
            if (isRecruiter) {
                RecruiterBottomNavigationBar(navController = navController)
            } else {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isRecruiter) Screen.RecruiterJobs.route else Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            if (isRecruiter) {
                composable(Screen.RecruiterJobs.route) {
                    MyJobsScreen(
                        onCreateJob = { navController.navigate(Screen.RecruiterJobForm.createRoute()) },
                        onJobClick = { jobId -> navController.navigate(Screen.RecruiterJobForm.createRoute(jobId)) }
                    )
                }

                composable(Screen.RecruiterJobForm.route) { backStackEntry ->
                    val jobIdStr = backStackEntry.arguments?.getString("jobId")
                    val jobId = jobIdStr?.toLongOrNull()
                    JobFormScreen(
                        jobId = jobId,
                        onBack = { navController.popBackStack() },
                        onSuccess = { navController.popBackStack() }
                    )
                }

                composable(Screen.RecruiterApplications.route) {
                    ApplicationsReceivedScreen()
                }

                composable(Screen.ChatList.route) {
                    ChatListScreen()
                }

                composable(Screen.Profile.route) {
                    ProfileScreen()
                }
            } else {
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
    }
}
