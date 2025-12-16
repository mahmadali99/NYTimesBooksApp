package com.example.nytimesbooksapp.presentation.navigation

sealed class NavRoutes (val route: String){
// here we will now define three objects
object Splash : NavRoutes("Splash_Screen")
object Home : NavRoutes("Home_Screen")
object Details : NavRoutes("book_details/{isbn}") {

    // here we will now define the function
    fun createRoute(isbn: String) = "book_details/$isbn"
}

}