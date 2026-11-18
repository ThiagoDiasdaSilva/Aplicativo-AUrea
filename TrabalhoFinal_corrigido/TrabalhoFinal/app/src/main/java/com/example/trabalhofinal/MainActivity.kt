package com.example.trabalhofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trabalhofinal.view.AnimalTela
import com.example.trabalhofinal.view.SessoesTela

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "calculadora"
                ) {
                    composable(route = "calculadora") {
                        AnimalTela(
                            viewModel = mainViewModel,
                            onAbrirSessoes = {
                                navController.navigate("sessoes_salvas")
                            }
                        )
                    }

                    composable(route = "sessoes_salvas") {
                        SessoesTela(
                            viewModel = mainViewModel,
                            onVoltar = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}