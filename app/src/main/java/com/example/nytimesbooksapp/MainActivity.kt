package com.example.nytimesbooksapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.nytimesbooksapp.presentation.navigation.AppNavHost
import com.example.nytimesbooksapp.presentation.viewmodel.ThemeViewModel
import com.example.nytimesbooksapp.ui.theme.NYTimesBooksAppTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val apiKey = BuildConfig.API_KEY

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val navController = rememberNavController()

            NYTimesBooksAppTheme(themeViewModel = themeViewModel) {
                AppNavHost(
                    navController = navController,
                    themeViewModel = themeViewModel
                )
            }
        }

        Log.e("API_KEY", "$apiKey",)


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "Token: $token")
        }

    }
}
//        Log.e("API_KEY", "$apiKey", )
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name!",
        modifier = modifier
    )
}

