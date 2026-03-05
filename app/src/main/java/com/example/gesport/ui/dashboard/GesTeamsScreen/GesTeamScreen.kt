package com.example.gesport.ui.dashboard.GesTeamsScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.models.Team

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GesTeamScreen(navController: NavHostController) {

    val context = LocalContext.current
    val vm: GesTeamViewModel = viewModel(factory = GesTeamViewModelFactory(context))

    val teams       = vm.teams
    val searchQuery = vm.searchQuery

    var teamToDelete by remember { mutableStateOf<Team?>(null) }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Equipos", color = Color(0xFFE7F1FF), fontWeight = FontWeight.SemiBold)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("formTeam") },
                    containerColor = Color(0xFF135B90),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir equipo")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { vm.onSearchQueryChange(it) },
                    leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                    placeholder = { Text("Buscar equipo") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF135B90),
                        unfocusedIndicatorColor = Color(0x334B5563),
                        focusedPlaceholderColor = Color(0x55FFFFFF),
                        unfocusedPlaceholderColor = Color(0x55FFFFFF),
                        focusedLeadingIconColor = Color(0xFF135B90),
                        unfocusedLeadingIconColor = Color(0x99FFFFFF),
                        cursorColor = Color(0xFF135B90),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(teams) { team ->
                        TeamListItem(
                            team     = team,
                            onEdit   = { navController.navigate("formTeam/${team.id}") },
                            onDelete = { teamToDelete = team }
                        )
                    }
                }
            }
        }

        if (teamToDelete != null) {
            AlertDialog(
                onDismissRequest = { teamToDelete = null },
                title = {
                    Text("Eliminar equipo", color = Color.White, fontWeight = FontWeight.SemiBold)
                },
                text = {
                    Text("¿Eliminar «${teamToDelete!!.nombre}»?", color = Color.White)
                },
                confirmButton = {
                    TextButton(onClick = {
                        vm.deleteTeam(teamToDelete!!.id)
                        teamToDelete = null
                    }) {
                        Text("Eliminar", color = Color(0xFFFF5555))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { teamToDelete = null }) {
                        Text("Cancelar", color = Color.White)
                    }
                },
                containerColor = Color(0xFF0B0E12)
            )
        }
    }
}

@Composable
private fun TeamListItem(team: Team, onEdit: () -> Unit, onDelete: () -> Unit) {
    val cardGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        team.nombre,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    if (!team.entrenadorNombre.isNullOrBlank()) {
                        Text(
                            "Entrenador: ${team.entrenadorNombre}",
                            color = Color(0xCCFFFFFF),
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        "${team.getJugadoresIdsList().size} jugador(es)",
                        color = Color(0x99FFFFFF),
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF5555))
                }
            }
        }
    }
}