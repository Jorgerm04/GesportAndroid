package com.example.gesport.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gesport.models.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    nombre: String,
    email: String,
    rol: String,
    equipos: List<Team> = emptyList(),
    navController: NavHostController? = null,   // null en dashboard (admin no navega a formTeam desde aquí)
    esEntrenador: Boolean = false,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val rolColor = when (rol) {
        "ADMIN"      -> Color(0xFFFF6B6B)
        "ENTRENADOR" -> Color(0xFFFFCC44)
        "ARBITRO"    -> Color(0xFF4ECB71)
        else         -> Color(0xFF5B9EE7)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = Color(0xFF12171E),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Cabecera perfil ───────────────────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(rolColor.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color      = rolColor,
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text(nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(email, color = Color(0x99FFFFFF), fontSize = 14.sp)
                Spacer(Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = rolColor.copy(0.15f)
                ) {
                    Text(
                        rol,
                        color      = rolColor,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                }

                Spacer(Modifier.height(28.dp))
                HorizontalDivider(color = Color(0x1AFFFFFF))
            }

            // ── Sección equipos (si tiene) ────────────────────────────────
            if (equipos.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Groups, null,
                            tint     = rolColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (esEntrenador) "Mis equipos" else "Equipos en los que estoy",
                            color      = rolColor,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                items(equipos) { equipo ->
                    TeamProfileCard(
                        team         = equipo,
                        rolColor     = rolColor,
                        esEntrenador = esEntrenador,
                        onClick      = if (esEntrenador && navController != null) {
                            { navController.navigate("formTeam/${equipo.id}"); onDismiss() }
                        } else null
                    )
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                }
            }

            // ── Botón cerrar sesión ───────────────────────────────────────
            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick  = onLogout,
                    shape    = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF3B0B0B), Color(0xFF8B1A1A), Color(0xFF3B0B0B))
                            ),
                            RoundedCornerShape(14.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor   = Color.White
                    )
                ) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Tarjeta de equipo en el perfil ────────────────────────────────────────────

@Composable
private fun TeamProfileCard(
    team: Team,
    rolColor: Color,
    esEntrenador: Boolean,
    onClick: (() -> Unit)?
) {
    val sportIcon = when (team.sport) {
        "FUTBOL"     -> Icons.Default.SportsSoccer
        "BALONCESTO" -> Icons.Default.SportsBasketball
        "TENIS"      -> Icons.Default.SportsTennis
        else         -> Icons.Default.SportsTennis // PADEL
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rolColor.copy(0.08f))
            .then(
                if (onClick != null)
                    Modifier.clickable { onClick() }
                else
                    Modifier
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Icono deporte
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(rolColor.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(sportIcon, null, tint = rolColor, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    team.nombre,
                    color      = Color.White,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    buildString {
                        append(team.sport.lowercase().replaceFirstChar { it.uppercase() })
                        val nJugadores = team.getJugadoresIdsList().size
                        append(" · $nJugadores jugador${if (nJugadores == 1) "" else "es"}")
                    },
                    color    = Color(0x99FFFFFF),
                    fontSize = 12.sp
                )
            }

            // Si es entrenador → flecha indicando que es navegable
            if (esEntrenador && onClick != null) {
                Icon(
                    Icons.Default.ChevronRight, null,
                    tint     = rolColor.copy(0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}