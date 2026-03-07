@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gesport.ui.dashboard.DashboardScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.ui.components.ProfileBottomSheet

@Composable
fun DashboardScreen(
    navController: NavHostController,
    userId: Int
) {
    val context = LocalContext.current
    val vm: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(context))

    val currentUser          by vm.currentUser.observeAsState()
    val navigateToGesUser    by vm.navigateToGesUser.observeAsState(false)
    val navigateToGesCourt   by vm.navigateToGesCourt.observeAsState(false)
    val navigateToGesTeam    by vm.navigateToGesTeam.observeAsState(false)

    LaunchedEffect(userId) { vm.loadUser(userId) }

    LaunchedEffect(navigateToGesUser)  { if (navigateToGesUser)  { navController.navigate("gesUser");  vm.onNavigationDone() } }
    LaunchedEffect(navigateToGesCourt) { if (navigateToGesCourt) { navController.navigate("gesCourt"); vm.onNavigationDone() } }
    LaunchedEffect(navigateToGesTeam)  { if (navigateToGesTeam)  { navController.navigate("gesTeam");  vm.onNavigationDone() } }

    val userName = currentUser?.nombre ?: ""
    val rol      = currentUser?.rol    ?: "ADMIN"
    val email    = currentUser?.email  ?: ""

    var showProfile by remember { mutableStateOf(false) }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))

    Box(modifier = Modifier.fillMaxSize().background(bg)) {

        // ── TopBar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Dashboard",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFFE7F1FF),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp
                    )
                )
                Text(
                    if (userName.isNotEmpty()) "Hola, $userName 👋" else "Bienvenido a Gesport 👋",
                    color = Color(0x99FFFFFF), fontSize = 14.sp
                )
            }

            IconButton(onClick = { showProfile = true }) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF135B90).copy(0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color(0xFFE7F1FF))
                }
            }
        }

        // ── Cards centro ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Panel principal", color = Color(0xCCFFFFFF),
                fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardItemCard("Equipos",  Icons.Default.People, Modifier.weight(1f)) { vm.onGesTeamCardClicked() }
                DashboardItemCard("Usuarios", Icons.Default.Person, Modifier.weight(1f)) { vm.onGesUserCardClicked() }
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardItemCard("Pistas",   Icons.Default.Place,  Modifier.weight(1f)) { vm.onGesCourtCardClicked() }
                DashboardItemCard("Reservas", Icons.Default.Event,  Modifier.weight(1f)) {
                    navController.navigate("gesBooking/$userId")
                }
            }
        }

        // ── Bottom Sheet perfil ───────────────────────────────────────────
        if (showProfile) {
            ProfileBottomSheet(
                nombre    = userName,
                email     = email,
                rol       = rol,
                onDismiss = { showProfile = false },
                onLogout  = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}

// ── Card del dashboard ────────────────────────────────────────────────────────

@Composable
private fun DashboardItemCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cardGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))
    )
    Card(
        modifier = modifier.height(130.dp).clickable { onClick() },
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape    = RoundedCornerShape(50),
                    color    = Color(0x33135B90),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(icon, contentDescription = title, tint = Color(0xFFE7F1FF))
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    title, color = Color.White, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
                )
            }
        }
    }
}