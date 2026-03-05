package com.example.gesport.ui.front.HomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.R
import com.example.gesport.models.Booking
import com.example.gesport.models.BookingType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    userId: Int,
    nombre1: String?,
    rol: String
) {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))

    val user     by vm.user.observeAsState()
    val bookings = vm.bookings

    LaunchedEffect(nombre1) { nombre1?.let { vm.loadUser(it) } }
    LaunchedEffect(userId, rol) { vm.loadBookings(userId, rol) }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Gesport", color = Color(0xFFE7F1FF), fontWeight = FontWeight.SemiBold)
                        }
                    },
                    actions = {
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.Person, null, tint = Color(0xFFE7F1FF))
                            }
                            DropdownMenu(
                                expanded         = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                user?.let {
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(it.nombre, fontWeight = FontWeight.Bold)
                                                Text(it.email, style = MaterialTheme.typography.bodySmall)
                                                Text(it.rol, color = Color(0xFF135B90),
                                                    style = MaterialTheme.typography.bodySmall)
                                            }
                                        },
                                        onClick = {}
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    text    = { Text("Cerrar sesión", color = Color.Red) },
                                    onClick = {
                                        expanded = false
                                        vm.logout {
                                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                        }
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                if (rol != "ARBITRO") {
                    FloatingActionButton(
                        onClick        = { navController.navigate("formBooking/$userId/$rol") },
                        containerColor = Color(0xFF135B90),
                        contentColor   = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva reserva")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // ── Saludo ────────────────────────────────────────────────
                Text(
                    "Hola, ${user?.nombre ?: nombre1 ?: ""} 👋",
                    color      = Color(0xFFE7F1FF),
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    when (rol) {
                        "JUGADOR"    -> "Aquí tienes tus reservas activas"
                        "ENTRENADOR" -> "Aquí tienes las reservas de tus equipos"
                        "ARBITRO"    -> "Aquí tienes los partidos asignados"
                        else         -> ""
                    },
                    color    = Color(0x99FFFFFF),
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(20.dp))

                // ── Lista de reservas ─────────────────────────────────────
                if (bookings.isEmpty()) {
                    Box(
                        modifier        = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EventBusy, null,
                                tint     = Color(0x33FFFFFF),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "No tienes reservas próximas",
                                color    = Color(0x66FFFFFF),
                                fontSize = 15.sp
                            )
                            if (rol != "ARBITRO") {
                                Spacer(Modifier.height(8.dp))
                                TextButton(
                                    onClick = { navController.navigate("formBooking/$userId/$rol") }
                                ) {
                                    Text("Crear una reserva", color = Color(0xFF5B9EE7))
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(bookings, key = { it.id }) { booking ->
                            HomeBookingCard(booking = booking)
                        }
                    }
                }
            }
        }
    }
}

// ── Tarjeta de reserva para el Home ──────────────────────────────────────────

@Composable
private fun HomeBookingCard(booking: Booking) {
    val dateFmt = SimpleDateFormat("EEE d MMM", Locale("es"))
    val hourFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    val badgeColor = when (booking.tipoEnum) {
        BookingType.PARTIDO -> Color(0xFF4ECB71)
        BookingType.EQUIPO  -> Color(0xFFFFCC44)
        else                -> Color(0xFF5B9EE7)
    }
    val cardGradient = Brush.horizontalGradient(
        colors = when (booking.tipoEnum) {
            BookingType.PARTIDO -> listOf(Color(0xFF0B2A1A), Color(0xFF0E6B35), Color(0xFF0B2A1A))
            BookingType.EQUIPO  -> listOf(Color(0xFF1A2B0B), Color(0xFF4A7A10), Color(0xFF1A2B0B))
            else                -> listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))
        }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Icono lateral
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(badgeColor.copy(0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (booking.tipoEnum) {
                            BookingType.PARTIDO -> Icons.Default.SportsSoccer
                            BookingType.EQUIPO  -> Icons.Default.Groups
                            else                -> Icons.Default.Person
                        },
                        null, tint = badgeColor, modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Pista + badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(booking.pistaNombre, color = Color.White,
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = badgeColor.copy(0.2f)) {
                            Text(booking.tipoEnum.label, color = badgeColor, fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(Modifier.height(3.dp))

                    // Detalle según tipo
                    when (booking.tipoEnum) {
                        BookingType.PARTIDO ->
                            Text("${booking.equipoLocalNombre} vs ${booking.equipoVisitanteNombre}",
                                color = Color(0xCCFFFFFF), fontSize = 12.sp)
                        BookingType.EQUIPO ->
                            Text(booking.equipoNombre ?: "", color = Color(0xCCFFFFFF), fontSize = 12.sp)
                        BookingType.INDIVIDUAL ->
                            Text(booking.usuarioNombre ?: "", color = Color(0xCCFFFFFF), fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(3.dp))

                    // Fecha y hora
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null,
                            tint = Color(0x80FFFFFF), modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${dateFmt.format(Date(booking.fecha)).replaceFirstChar { it.uppercase() }}  " +
                                    "${hourFmt.format(Date(booking.horaInicio))} – ${hourFmt.format(Date(booking.horaFin))}",
                            color = Color(0x99FFFFFF), fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}