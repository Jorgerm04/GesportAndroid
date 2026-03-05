@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gesport.ui.dashboard.DashboardScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun DashboardScreen(
    navController: NavHostController,
    userId: Int = 0,
    nombre: String? = null,
    rol: String = "ADMIN",
    vm: DashboardViewModel = viewModel()
) {
    LaunchedEffect(nombre) { vm.setUserName(nombre.orEmpty()) }

    val userName             by vm.userName.observeAsState("")
    val navigateToGesUser    by vm.navigateToGesUser.observeAsState(false)
    val navigateToGesCourt   by vm.navigateToGesCourt.observeAsState(false)
    val navigateToGesTeam    by vm.navigateToGesTeam.observeAsState(false)
    val navigateToGesBooking by vm.navigateToGesBooking.observeAsState(false)

    LaunchedEffect(navigateToGesUser)    { if (navigateToGesUser)    { navController.navigate("gesUser");    vm.onNavigationDone() } }
    LaunchedEffect(navigateToGesCourt)   { if (navigateToGesCourt)   { navController.navigate("gesCourt");   vm.onNavigationDone() } }
    LaunchedEffect(navigateToGesTeam)    { if (navigateToGesTeam)    { navController.navigate("gesTeam");    vm.onNavigationDone() } }
    LaunchedEffect(navigateToGesBooking) { if (navigateToGesBooking) { navController.navigate("gesBooking"); vm.onNavigationDone() } }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))

    Box(modifier = Modifier.fillMaxSize().background(bg).padding(horizontal = 24.dp)) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Dashboard",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFE7F1FF), fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp))
            Spacer(Modifier.height(4.dp))
            Text(if (userName.isNotEmpty()) "Hola, $userName 👋" else "Bienvenido a Gesport 👋",
                color = Color(0x99FFFFFF), fontSize = 14.sp)
        }

        Column(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Panel principal", color = Color(0xCCFFFFFF),
                fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardItemCard("Equipos",   Icons.Default.People,  Modifier.weight(1f)) { vm.onGesTeamCardClicked() }
                DashboardItemCard("Usuarios",  Icons.Default.Person,  Modifier.weight(1f)) { vm.onGesUserCardClicked() }
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardItemCard("Pistas",    Icons.Default.Place,   Modifier.weight(1f)) { vm.onGesCourtCardClicked() }
                DashboardItemCard("Reservas",  Icons.Default.Event,   Modifier.weight(1f)) {
                    navController.navigate("gesBooking/$userId/$rol")
                }
            }
        }
    }
}

@Composable
private fun DashboardItemCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val cardGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))
    )
    Card(
        modifier = modifier.height(130.dp).clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(cardGradient, RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(shape = RoundedCornerShape(50), color = Color(0x33135B90),
                    modifier = Modifier.size(52.dp)) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(icon, contentDescription = title, tint = Color(0xFFE7F1FF))
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(title, color = Color.White, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDashboard() { DashboardScreen(rememberNavController()) }