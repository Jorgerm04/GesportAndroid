package com.example.gesport.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gesport.ui.auth.LoginScreen.LoginScreen
import com.example.gesport.ui.auth.RegisterScreen.RegisterScreen
import com.example.gesport.ui.dashboard.DashboardScreen.DashboardScreen
import com.example.gesport.ui.dashboard.GesUserScreen.FormUserScreen
import com.example.gesport.ui.dashboard.GesUserScreen.GesUserScreen
import com.example.gesport.ui.dashboard.GesCourtsScreen.GesCourtScreen
import com.example.gesport.ui.dashboard.GesCourtsScreen.FormCourtScreen
import com.example.gesport.ui.dashboard.GesTeamsScreen.GesTeamScreen
import com.example.gesport.ui.dashboard.GesTeamsScreen.FormTeamScreen
import com.example.gesport.ui.dashboard.GesBookingsScreen.GesBookingScreen
import com.example.gesport.ui.dashboard.GesBookingsScreen.FormBookingScreen
import com.example.gesport.ui.front.HomeScreen.HomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // ── Auth ──────────────────────────────────────────────────────────
        composable("login")    { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        // ── Home (jugadores, entrenadores, árbitros) ──────────────────────
        composable(
            "home/{userId}/{nombre}/{rol}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("rol")    { type = NavType.StringType }
            )
        ) { back ->
            HomeScreen(
                navController = navController,
                userId        = back.arguments?.getInt("userId") ?: 0,
                nombre1       = back.arguments?.getString("nombre"),
                rol           = back.arguments?.getString("rol") ?: ""
            )
        }

        // ── Dashboard (admin) ─────────────────────────────────────────────
        composable(
            "dashboard/{userId}/{nombre}/{rol}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("rol")    { type = NavType.StringType }
            )
        ) { back ->
            DashboardScreen(
                navController = navController,
                userId        = back.arguments?.getInt("userId") ?: 0,
                nombre        = back.arguments?.getString("nombre"),
                rol           = back.arguments?.getString("rol") ?: "ADMIN"
            )
        }

        // ── Usuarios ──────────────────────────────────────────────────────
        composable("gesUser") { GesUserScreen(navController) }
        composable("formuser") { FormUserScreen(navController) }
        composable(
            "formuser/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { back -> FormUserScreen(navController, back.arguments?.getInt("userId")) }

        // ── Pistas ────────────────────────────────────────────────────────
        composable("gesCourt") { GesCourtScreen(navController) }
        composable("formCourt") { FormCourtScreen(navController) }
        composable(
            "formCourt/{courtId}",
            arguments = listOf(navArgument("courtId") { type = NavType.IntType })
        ) { back -> FormCourtScreen(navController, back.arguments?.getInt("courtId")) }

        // ── Equipos ───────────────────────────────────────────────────────
        composable("gesTeam") { GesTeamScreen(navController) }
        composable("formTeam") { FormTeamScreen(navController) }
        composable(
            "formTeam/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { back -> FormTeamScreen(navController, back.arguments?.getInt("teamId")) }

        // ── Reservas ──────────────────────────────────────────────────────
        composable(
            "gesBooking/{userId}/{rol}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("rol")    { type = NavType.StringType }
            )
        ) { back ->
            GesBookingScreen(
                navController  = navController,
                currentUserId  = back.arguments?.getInt("userId") ?: 0,
                currentUserRol = back.arguments?.getString("rol") ?: "ADMIN"
            )
        }
        composable(
            "formBooking/{userId}/{rol}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("rol")    { type = NavType.StringType }
            )
        ) { back ->
            FormBookingScreen(
                navController = navController,
                currentUserId = back.arguments?.getInt("userId") ?: 0,
                currentUserRol = back.arguments?.getString("rol") ?: "ADMIN"
            )
        }
        composable(
            "formBooking/{userId}/{rol}/{bookingId}",
            arguments = listOf(
                navArgument("userId")    { type = NavType.IntType },
                navArgument("rol")       { type = NavType.StringType },
                navArgument("bookingId") { type = NavType.IntType }
            )
        ) { back ->
            FormBookingScreen(
                navController  = navController,
                currentUserId  = back.arguments?.getInt("userId") ?: 0,
                currentUserRol = back.arguments?.getString("rol") ?: "ADMIN",
                bookingId      = back.arguments?.getInt("bookingId")
            )
        }
    }
}