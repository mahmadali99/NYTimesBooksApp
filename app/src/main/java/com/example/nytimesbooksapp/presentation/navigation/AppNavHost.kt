package com.example.nytimesbooksapp.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nytimesbooksapp.presentation.details.BookDetailsScreen
import com.example.nytimesbooksapp.presentation.home.HomeScreen
import com.example.nytimesbooksapp.presentation.splash.SplashScreen
import com.example.nytimesbooksapp.presentation.viewmodel.ThemeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController, themeViewModel: ThemeViewModel)
// |----------------------------------------------------------------------------------|
// |    ↓                                                                             |
// | AppNavHost defines which screens exist and how to move between them.             |
// |                                                                                  |
// |----------------------------------------------------------------------------------|
{
    NavHost(navController = navController,
        startDestination = NavRoutes.Splash.route){

        composable(NavRoutes.Splash.route) {
            SplashScreen(navController)
        }


        composable(NavRoutes.Home.route) {
            HomeScreen(navController = navController, themeViewModel = themeViewModel)
        }

        composable(
            route = "book_details/{isbn}",
            arguments = listOf(
                navArgument("isbn") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isbn = backStackEntry.arguments?.getString("isbn") ?: ""
            BookDetailsScreen(
                isbn = isbn,
                navController = navController,
                themeViewModel = themeViewModel
            )
        }
    }
}