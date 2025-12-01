package com.example.gesport.ui.dashboard.GesUserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.data.DataUserRepository
import com.example.gesport.models.UserRoles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormUserScreen(
    navController: NavHostController,
    userId: Int? = null        // 👈 ahora Int?
) {
    val viewModel: GesUserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = DataUserRepository
                return GesUserViewModel(repo) as T
            }
        }
    )

    val currentUser by viewModel.currentUser.observeAsState()
    val saveCompleted by viewModel.saveCompleted.observeAsState(false)

    // Cargar datos si venimos a EDITAR
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUserById(userId)
        }
    }

    // Volver atrás cuando se complete el guardado
    LaunchedEffect(saveCompleted) {
        if (saveCompleted) {
            navController.popBackStack()
            viewModel.onSaveCompletedHandled()
        }
    }

    // Estados de los campos, se rellenan si currentUser tiene valor
    var nombre by remember(currentUser) { mutableStateOf(currentUser?.nombre ?: "") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var password by remember(currentUser) { mutableStateOf(currentUser?.password ?: "") }
    var rol by remember(currentUser) { mutableStateOf(currentUser?.rol ?: "JUGADOR") }

    val bg = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (userId == null) "Nuevo usuario" else "Editar usuario",
                            color = Color(0xFFE7F1FF),
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                // Nombre
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColorsLikeLogin()
                )

                Spacer(Modifier.height(12.dp))

                // Email
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColorsLikeLogin()
                )

                Spacer(Modifier.height(12.dp))

                // Password
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColorsLikeLogin()
                )

                Spacer(Modifier.height(16.dp))

                // Rol
                Text(
                    text = "Rol",
                    color = Color(0xCCFFFFFF),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserRoles.allRoles.forEach { (roleKey, roleLabel) ->
                        FilterChip(
                            selected = rol == roleKey,
                            onClick = { rol = roleKey },
                            label = { Text(roleLabel) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFF0B0E12),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF135B90),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                val gradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0B2843),
                        Color(0xFF135B90),
                        Color(0xFF0B2843)
                    )
                )

                Button(
                    onClick = {
                        viewModel.saveUser(
                            userId = userId,
                            nombre = nombre,
                            email = email,
                            password = password,
                            rol = rol
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(gradient, RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (userId == null) "Crear usuario" else "Guardar cambios",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// helper para no repetir colores
@Composable
private fun textFieldColorsLikeLogin() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color(0xFF135B90),
    unfocusedIndicatorColor = Color(0x334B5563),
    focusedLabelColor = Color(0xFF135B90),
    unfocusedLabelColor = Color(0x99FFFFFF),
    cursorColor = Color(0xFF135B90),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)