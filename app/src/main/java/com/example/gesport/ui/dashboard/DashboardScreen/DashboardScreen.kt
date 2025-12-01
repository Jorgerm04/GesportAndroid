@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gesport.ui.dashboard.DashboardScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    nombre: String? = null,
    vm: DashboardViewModel = viewModel()
) {
    val userName by vm.userName.observeAsState("")

    LaunchedEffect(nombre) {
        vm.setUserName(nombre.orEmpty())
    }

    val navigateToGesUser by vm.navigateToGesUser.observeAsState(false)

    LaunchedEffect(navigateToGesUser) {
        if (navigateToGesUser) {
            navController.navigate("gesUser")
            vm.onNavigationToGesUserDone()
        }
    }

    val bg = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 24.dp)
    ) {
        // Cabecera
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFE7F1FF),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (userName.isNotEmpty())
                    "Hola, $userName 👋"
                else
                    "Bienvenido a Gesport 👋",
                color = Color(0x99FFFFFF),
                fontSize = 14.sp
            )
        }

        // Grid central de cards
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Panel principal",
                color = Color(0xCCFFFFFF),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // 1ª fila: Equipos - Usuarios
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardItemCard(
                    title = "Equipos",
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f)
                    // de momento sin navegación
                )

                DashboardItemCard(
                    title = "Usuarios",
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f),
                    enabled = true
                ) {
                    vm.onGesUserCardClicked()
                }
            }

            Spacer(Modifier.height(16.dp))

            // 2ª fila: Pistas - Reservas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardItemCard(
                    title = "Pistas",
                    icon = Icons.Default.Place,
                    modifier = Modifier.weight(1f)
                    // de momento sin navegación
                )

                DashboardItemCard(
                    title = "Reservas",
                    icon = Icons.Default.Event,
                    modifier = Modifier.weight(1f)
                    // de momento sin navegación
                )
            }
        }
    }
}

@Composable
private fun DashboardItemCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val cardGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0B2843),
            Color(0xFF135B90),
            Color(0xFF0B2843)
        )
    )

    val clickableModifier = if (enabled && onClick != null) {
        modifier
            .height(130.dp)
            .clickable { onClick() }
    } else {
        modifier.height(130.dp)
    }

    Card(
        modifier = clickableModifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0x33135B90),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color(0xFFE7F1FF)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDashboard() {
    DashboardScreen(rememberNavController())
}
