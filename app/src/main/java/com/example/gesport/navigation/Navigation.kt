package com.example.gesport.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gesport.ui.front.HomeScreen.HomeScreen
import com.example.gesport.ui.auth.LoginScreen.LoginScreen
import com.example.gesport.ui.auth.RegisterScreen.RegisterScreen
import com.example.gesport.ui.dashboard.DashboardScreen.DashboardScreen
import com.example.gesport.ui.dashboard.GesUserScreen.FormUserScreen
import com.example.gesport.ui.dashboard.GesUserScreen.GesUserScreen // ajusta el paquete si es distinto

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        composable(
            "home/{nombre}",
            listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")
            HomeScreen(navController, nombre)
        }

        composable(
            "dashboard/{nombre}",
            listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")
            DashboardScreen(navController, nombre)
        }

        // Listado de usuarios
        composable("gesUser") {
            GesUserScreen(navController)
        }

        // Form crear usuario
        composable("formuser") {
            FormUserScreen(navController)
        }

        // Form editar usuario
        composable(
            "formuser/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }   // 👈 IntType
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            FormUserScreen(navController, userId)
        }
    }
}