package com.probasketacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.probasketacademy.presentacion.navegacion.AppNavDisplay
import com.probasketacademy.presentacion.navegacion.Screen
import com.probasketacademy.ui.theme.ProBasketAcademyTheme

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProBasketAcademyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavDisplay(
                        backStack = rememberNavBackStack(Screen.Home),
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}