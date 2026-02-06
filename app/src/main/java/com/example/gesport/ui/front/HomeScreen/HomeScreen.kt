package com.example.gesport.ui.front.HomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.R

@Composable
fun HomeScreen(
    navController: NavHostController,
    nombre1: String?
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val vm: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = HomeViewModelFactory(context)
    )

    // Observamos el usuario del ViewModel
    val user by vm.user.observeAsState()

    // Cargar datos cuando se inicia la pantalla
    LaunchedEffect(nombre1) {
        nombre1?.let { vm.loadUser(it) }
    }

    val bg = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(24.dp)
    ) {
        // --- CABECERA ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            var expanded by remember { mutableStateOf(false) }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Menu",
                        tint = Color(0xFFE7F1FF)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Usamos los datos reales del usuario de Room
                    user?.let {
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(it.nombre, fontWeight = FontWeight.Bold)
                                    Text(it.email, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = { }
                        )
                        HorizontalDivider()
                    }

                    DropdownMenuItem(
                        text = { Text("Cerrar sesión", color = Color.Red) },
                        onClick = {
                            expanded = false
                            vm.logout {
                                // Navega al login y borra todo el historial anterior
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }

        // --- CONTENIDO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido/a,",
                color = Color(0x99FFFFFF),
                fontSize = 16.sp
            )
            Text(
                text = user?.nombre ?: nombre1 ?: "Usuario",
                color = Color(0xFFE7F1FF),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )

            // Aquí puedes mostrar más datos de Room
            user?.let {
                Text(
                    text = "Rol: ${it.rol}",
                    color = Color(0xFF135B90),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}