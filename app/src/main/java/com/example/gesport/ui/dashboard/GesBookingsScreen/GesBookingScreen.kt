package com.example.gesport.ui.dashboard.GesBookingsScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.models.Booking
import com.example.gesport.models.BookingType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GesBookingScreen(
    navController: NavHostController,
    currentUserId: Int = 0,
    currentUserRol: String = "ADMIN"
) {
    val context      = LocalContext.current
    val focusManager = LocalFocusManager.current
    val vm: GesBookingViewModel = viewModel(factory = GesBookingViewModelFactory(context))

    val bookings      = vm.bookings
    val showCancelled = vm.showCancelled

    // ── Búsqueda y filtros ────────────────────────────────────────────────
    var searchQuery        by remember { mutableStateOf("") }
    var filterTipo         by remember { mutableStateOf<BookingType?>(null) }

    // ── Diálogos ──────────────────────────────────────────────────────────
    var bookingToDelete by remember { mutableStateOf<Booking?>(null) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }

    // ── Filtrado ──────────────────────────────────────────────────────────
    val filtered = bookings.filter { b ->
        val q = searchQuery.trim().lowercase()
        val matchesSearch = q.isEmpty() ||
                b.pistaNombre.lowercase().contains(q) ||
                b.usuarioNombre?.lowercase()?.contains(q) == true ||
                b.equipoNombre?.lowercase()?.contains(q) == true ||
                b.equipoLocalNombre?.lowercase()?.contains(q) == true ||
                b.equipoVisitanteNombre?.lowercase()?.contains(q) == true ||
                b.arbitroNombre?.lowercase()?.contains(q) == true

        val matchesTipo   = filterTipo == null || b.tipoEnum == filterTipo
        matchesSearch && matchesTipo
    }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Reservas", color = Color(0xFFE7F1FF), fontWeight = FontWeight.SemiBold)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick        = { navController.navigate("formBooking/$currentUserId/$currentUserRol") },
                    containerColor = Color(0xFF135B90),
                    contentColor   = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir reserva")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                // ── Barra de búsqueda ─────────────────────────────────────
                TextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("Buscar por pista, usuario, equipo…", fontSize = 13.sp) },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, null, tint = Color(0x99FFFFFF))
                    },
                    trailingIcon  = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = ""; focusManager.clearFocus() }) {
                                Icon(Icons.Default.Close, null, tint = Color(0x99FFFFFF))
                            }
                        }
                    },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    shape    = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors   = TextFieldDefaults.colors(
                        focusedContainerColor     = Color(0x14FFFFFF),
                        unfocusedContainerColor   = Color(0x0AFFFFFF),
                        focusedIndicatorColor     = Color.Transparent,
                        unfocusedIndicatorColor   = Color.Transparent,
                        focusedTextColor          = Color.White,
                        unfocusedTextColor        = Color.White,
                        focusedPlaceholderColor   = Color(0x55FFFFFF),
                        unfocusedPlaceholderColor = Color(0x44FFFFFF),
                        cursorColor               = Color(0xFF135B90)
                    )
                )

                Spacer(Modifier.height(10.dp))

                // ── Chips de filtro ───────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Chip canceladas
                    FilterChip(
                        selected = showCancelled,
                        onClick  = { vm.onShowCancelledChange(!showCancelled) },
                        label    = { Text("Canceladas", fontSize = 12.sp) },
                        colors   = FilterChipDefaults.filterChipColors(
                            containerColor         = Color(0x0AFFFFFF),
                            labelColor             = Color(0xB3FFFFFF),
                            selectedContainerColor = Color(0xFF3B1010),
                            selectedLabelColor     = Color(0xFFFF5555)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled          = true,
                            selected         = showCancelled,
                            borderColor      = Color(0x1AFFFFFF),
                            selectedBorderColor = Color(0xFFFF5555)
                        )
                    )

                    // Chips por tipo
                    val tiposConfig = listOf(
                        BookingType.INDIVIDUAL to Color(0xFF5B9EE7),
                        BookingType.EQUIPO     to Color(0xFFFFCC44),
                        BookingType.PARTIDO    to Color(0xFF4ECB71)
                    )
                    tiposConfig.forEach { (tipo, color) ->
                        val selected = filterTipo == tipo
                        FilterChip(
                            selected = selected,
                            onClick  = { filterTipo = if (selected) null else tipo },
                            label    = { Text(tipo.label, fontSize = 12.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                containerColor         = Color(0x0AFFFFFF),
                                labelColor             = Color(0xB3FFFFFF),
                                selectedContainerColor = color.copy(alpha = 0.2f),
                                selectedLabelColor     = color
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled             = true,
                                selected            = selected,
                                borderColor         = Color(0x1AFFFFFF),
                                selectedBorderColor = color
                            )
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ── Contador de resultados ────────────────────────────────
                val hayFiltros = searchQuery.isNotEmpty() || filterTipo != null
                if (hayFiltros) {
                    Text(
                        "${filtered.size} resultado${if (filtered.size != 1) "s" else ""}",
                        color    = Color(0x80FFFFFF),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // ── Lista ─────────────────────────────────────────────────
                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, null,
                                tint = Color(0x33FFFFFF), modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (hayFiltros) "Sin resultados para tu búsqueda"
                                else            "No hay reservas",
                                color    = Color(0x66FFFFFF),
                                fontSize = 15.sp
                            )
                            if (hayFiltros) {
                                Spacer(Modifier.height(8.dp))
                                TextButton(onClick = { searchQuery = ""; filterTipo = null }) {
                                    Text("Limpiar filtros", color = Color(0xFF5B9EE7))
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier            = Modifier.fillMaxSize()
                    ) {
                        items(filtered, key = { it.id }) { booking ->
                            BookingListItem(
                                booking           = booking,
                                onEdit            = { navController.navigate("formBooking/$currentUserId/$currentUserRol/${booking.id}") },
                                onDelete          = { bookingToDelete = booking },
                                onToggleCancelada = { bookingToCancel = booking }
                            )
                        }
                    }
                }
            }
        }

        // ── Diálogo eliminar ──────────────────────────────────────────────
        if (bookingToDelete != null) {
            AlertDialog(
                onDismissRequest = { bookingToDelete = null },
                title   = { Text("Eliminar reserva", color = Color.White, fontWeight = FontWeight.SemiBold) },
                text    = { Text("¿Eliminar esta reserva? Esta acción no se puede deshacer.", color = Color.White) },
                confirmButton = {
                    TextButton(onClick = { vm.deleteBooking(bookingToDelete!!.id); bookingToDelete = null }) {
                        Text("Eliminar", color = Color(0xFFFF5555))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bookingToDelete = null }) { Text("Cancelar", color = Color.White) }
                },
                containerColor = Color(0xFF0B0E12)
            )
        }

        // ── Diálogo cancelar / restaurar ──────────────────────────────────
        if (bookingToCancel != null) {
            AlertDialog(
                onDismissRequest = { bookingToCancel = null },
                title = {
                    Text(
                        if (bookingToCancel!!.cancelada) "Restaurar reserva" else "Cancelar reserva",
                        color = Color.White, fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        if (bookingToCancel!!.cancelada) "¿Quieres restaurar esta reserva?"
                        else "¿Quieres cancelar esta reserva?",
                        color = Color.White
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        vm.setCancelada(bookingToCancel!!.id, !bookingToCancel!!.cancelada)
                        bookingToCancel = null
                    }) {
                        Text(
                            if (bookingToCancel!!.cancelada) "Restaurar" else "Cancelar reserva",
                            color = if (bookingToCancel!!.cancelada) Color(0xFF55CC55) else Color(0xFFFF5555)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bookingToCancel = null }) { Text("Volver", color = Color.White) }
                },
                containerColor = Color(0xFF0B0E12)
            )
        }
    }
}

@Composable
private fun BookingListItem(
    booking: Booking,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleCancelada: () -> Unit
) {
    val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val hourFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    val cardGradient = Brush.horizontalGradient(
        colors = if (booking.cancelada)
            listOf(Color(0xFF1A0B0B), Color(0xFF3B1010), Color(0xFF1A0B0B))
        else when (booking.tipoEnum) {
            BookingType.PARTIDO -> listOf(Color(0xFF0B2A1A), Color(0xFF0E6B35), Color(0xFF0B2A1A))
            BookingType.EQUIPO  -> listOf(Color(0xFF1A2B0B), Color(0xFF4A7A10), Color(0xFF1A2B0B))
            else                -> listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))
        }
    )

    val badgeColor = when (booking.tipoEnum) {
        BookingType.PARTIDO -> Color(0xFF4ECB71)
        BookingType.EQUIPO  -> Color(0xFFFFCC44)
        else                -> Color(0xFF5B9EE7)
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {

                    // ── Cabecera ──────────────────────────────────────────
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(booking.pistaNombre, color = Color.White,
                            fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = badgeColor.copy(alpha = 0.2f)) {
                            Text(booking.tipoEnum.label, color = badgeColor, fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        if (booking.cancelada) {
                            Spacer(Modifier.width(6.dp))
                            Text("Cancelada", color = Color(0xFFFF5555), fontSize = 11.sp)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // ── Contenido según tipo ──────────────────────────────
                    when (booking.tipoEnum) {
                        BookingType.PARTIDO -> {
                            Text("${booking.equipoLocalNombre ?: "?"} vs ${booking.equipoVisitanteNombre ?: "?"}",
                                color = Color(0xCCFFFFFF), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            booking.arbitroNombre?.let {
                                Text("Árbitro: $it", color = Color(0x99FFFFFF), fontSize = 12.sp)
                            }
                            if (booking.puntosLocal != null && booking.puntosVisitante != null) {
                                Spacer(Modifier.height(2.dp))
                                Text("${booking.puntosLocal} - ${booking.puntosVisitante}",
                                    color = Color(0xFF4ECB71), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        BookingType.EQUIPO -> {
                            booking.equipoNombre?.let {
                                Text(it, color = Color(0xCCFFFFFF), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            booking.usuarioNombre?.let {
                                Text("Entrenador: $it", color = Color(0x99FFFFFF), fontSize = 12.sp)
                            }
                        }
                        BookingType.INDIVIDUAL -> {
                            booking.usuarioNombre?.let {
                                Text(it, color = Color(0xCCFFFFFF), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // ── Fecha y hora ──────────────────────────────────────
                    Text(
                        "${dateFmt.format(Date(booking.fecha))}  " +
                                "${hourFmt.format(Date(booking.horaInicio))} → ${hourFmt.format(Date(booking.horaFin))}",
                        color = Color(0x99FFFFFF), fontSize = 12.sp
                    )

                    booking.notas?.let {
                        Text(it, color = Color(0x88FFFFFF), fontSize = 11.sp)
                    }
                }

                // ── Acciones ──────────────────────────────────────────────
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onToggleCancelada) {
                        Icon(
                            if (booking.cancelada) Icons.Default.Check else Icons.Default.Cancel,
                            contentDescription = if (booking.cancelada) "Restaurar" else "Cancelar",
                            tint = if (booking.cancelada) Color(0xFF55CC55) else Color(0xFFFF5555)
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
}