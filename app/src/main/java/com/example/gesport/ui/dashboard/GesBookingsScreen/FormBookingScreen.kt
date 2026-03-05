package com.example.gesport.ui.dashboard.GesBookingsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.models.Booking
import com.example.gesport.models.BookingType
import com.example.gesport.models.Court
import com.example.gesport.models.CourtType
import com.example.gesport.models.Team
import com.example.gesport.models.User
import java.text.SimpleDateFormat
import java.util.*

// ── Modelo auxiliar slot ──────────────────────────────────────────────────────

data class TimeSlot(
    val inicio: Long,
    val fin: Long,
    val occupied: Boolean,
    val occupiedBy: String? = null
)

fun generateSlots(dayStart: Long, existing: List<Booking>, excludeId: Int?): List<TimeSlot> {
    val slots    = mutableListOf<TimeSlot>()
    val cal      = Calendar.getInstance().apply {
        timeInMillis = dayStart
        set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
    }
    val limitCal = Calendar.getInstance().apply {
        timeInMillis = dayStart
        set(Calendar.HOUR_OF_DAY, 22); set(Calendar.MINUTE, 30); set(Calendar.SECOND, 0)
    }
    val limit = limitCal.timeInMillis
    val now   = System.currentTimeMillis()

    while (cal.timeInMillis < limit) {
        val inicio = cal.timeInMillis
        cal.add(Calendar.MINUTE, 90)
        val fin = cal.timeInMillis
        if (fin > limit) break

        var occupied   = false
        var occupiedBy: String? = null
        for (b in existing) {
            if (b.id == (excludeId ?: -1)) continue
            if (!b.cancelada && inicio < b.horaFin && fin > b.horaInicio) {
                occupied   = true
                occupiedBy = b.usuarioNombre ?: b.equipoLocalNombre
                break
            }
        }
        val isPast = inicio < now
        slots.add(TimeSlot(inicio, fin, occupied || isPast, if (isPast && !occupied) null else occupiedBy))
    }
    return slots
}

fun todayMs(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}.timeInMillis

// ── Pantalla principal ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormBookingScreen(
    navController: NavHostController,
    currentUserId: Int,
    currentUserRol: String,
    bookingId: Int? = null
) {
    val context = LocalContext.current
    val vm: GesBookingViewModel = viewModel(factory = GesBookingViewModelFactory(context))

    val currentBooking by vm.currentBooking.observeAsState()
    val saveCompleted  by vm.saveCompleted.observeAsState(false)
    val conflictError  by vm.conflictError.observeAsState()
    val allUsers   = vm.allUsers
    val allCourts  = vm.allCourts
    val allTeams   = vm.allTeams

    val isAdmin      = currentUserRol == "ADMIN"
    val isEntrenador = currentUserRol == "ENTRENADOR"
    val isJugador    = currentUserRol == "JUGADOR"

    LaunchedEffect(bookingId) { if (bookingId != null) vm.loadBookingById(bookingId) }
    LaunchedEffect(saveCompleted) {
        if (saveCompleted) { navController.popBackStack(); vm.onSaveCompletedHandled() }
    }

    var tipoReserva by rememberSaveable(currentBooking) {
        mutableStateOf(currentBooking?.tipoEnum ?: BookingType.INDIVIDUAL)
    }
    var usuarioId by rememberSaveable(currentBooking) {
        mutableStateOf(if (isJugador) currentUserId else currentBooking?.usuarioId ?: 0)
    }
    var pistaId by rememberSaveable(currentBooking) {
        mutableStateOf(currentBooking?.pistaId ?: 0)
    }
    var selectedSport by rememberSaveable(currentBooking) {
        mutableStateOf<CourtType?>(allCourts.firstOrNull { it.id == (currentBooking?.pistaId ?: 0) }?.tipoEnum)
    }
    var notas by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.notas ?: "") }
    var selectedDay by remember(currentBooking) {
        mutableStateOf<Long?>(currentBooking?.fecha ?: todayMs())
    }
    var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var slots        by remember { mutableStateOf<List<TimeSlot>>(emptyList()) }
    var loadingSlots by remember { mutableStateOf(false) }

    var equipoId            by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.equipoId) }
    var equipoNombre        by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.equipoNombre) }
    var equipoLocalId       by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.equipoLocalId) }
    var equipoLocalNombre   by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.equipoLocalNombre) }
    var equipoVisitanteId   by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.equipoVisitanteId) }
    var equipoVisitanteNombre by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.equipoVisitanteNombre) }
    var arbitroId           by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.arbitroId) }
    var arbitroNombre       by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.arbitroNombre) }
    var puntosLocalText     by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.puntosLocal?.toString() ?: "") }
    var puntosVisitanteText by rememberSaveable(currentBooking) { mutableStateOf(currentBooking?.puntosVisitante?.toString() ?: "") }

    LaunchedEffect(selectedDay, pistaId) {
        if (selectedDay != null && pistaId != 0) {
            loadingSlots = true
            selectedSlot = null
            val existing = vm.getBookingsForCourtAndDay(pistaId, selectedDay!!, selectedDay!! + 86_400_000L)
            slots = generateSlots(selectedDay!!, existing, bookingId)
            if (currentBooking != null)
                selectedSlot = slots.firstOrNull { it.inicio == currentBooking!!.horaInicio }
            loadingSlots = false
        }
    }

    val arbitros          = allUsers.filter { it.rol == "ARBITRO" }
    val equiposParaEquipo = when {
        isAdmin      -> allTeams
        isEntrenador -> allTeams.filter { it.entrenadorId == currentUserId }
        else         -> emptyList()
    }
    val maxDaysAhead = if (isAdmin) Int.MAX_VALUE else 15

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (bookingId == null) "Nueva reserva" else "Editar reserva",
                            color = Color(0xFFE7F1FF), fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Selector de tipo ──────────────────────────────────────
                if (isAdmin || isEntrenador) {
                    BookingTypeSelector(
                        current  = tipoReserva,
                        isAdmin  = isAdmin,
                        onSelect = { tipo ->
                            tipoReserva           = tipo
                            usuarioId             = if (isJugador) currentUserId else 0
                            equipoId              = null; equipoNombre          = null
                            equipoLocalId         = null; equipoLocalNombre     = null
                            equipoVisitanteId     = null; equipoVisitanteNombre = null
                            arbitroId             = null; arbitroNombre         = null
                            selectedSport         = null
                            pistaId               = 0
                            selectedDay           = todayMs()
                            selectedSlot          = null
                            slots                 = emptyList()
                        }
                    )
                    Spacer(Modifier.height(24.dp))
                }

                // ── FIX: capturamos selectedSport en val local ────────────
                // Así Kotlin puede hacer smart cast dentro del when
                val sport = selectedSport

                when (tipoReserva) {

                    // ══════════════════════════════════════════════════════
                    // INDIVIDUAL
                    // ══════════════════════════════════════════════════════
                    BookingType.INDIVIDUAL -> {

                        // Paso 1: Usuario (solo admin elige)
                        if (isAdmin) {
                            StepHeader("1", "Usuario", Icons.Default.Person,
                                Color(0xFF5B9EE7), done = usuarioId != 0)
                            Spacer(Modifier.height(10.dp))
                            UserSearchSelector(
                                users      = allUsers.filter { it.rol != "ARBITRO" },
                                selectedId = usuarioId,
                                onSelect   = { usuarioId = it }
                            )
                            Spacer(Modifier.height(24.dp))
                        }

                        // Paso 2 (o 1): Deporte
                        val stepDeporte = if (isAdmin) "2" else "1"
                        StepHeader(stepDeporte, "Deporte", Icons.Default.Sports,
                            Color(0xFFB57BFF), done = sport != null)
                        Spacer(Modifier.height(10.dp))
                        SportSelector(
                            courts        = allCourts,
                            selectedSport = sport,
                            onSelect      = { nuevoSport ->
                                selectedSport = nuevoSport
                                pistaId       = 0
                                selectedDay   = todayMs()
                                selectedSlot  = null
                                slots         = emptyList()
                            }
                        )
                        Spacer(Modifier.height(24.dp))

                        // Paso 3 (o 2): Pista — filtrada por deporte
                        val stepPista = if (isAdmin) "3" else "2"
                        StepHeader(stepPista, "Pista", Icons.Default.Stadium,
                            Color(0xFFFF6B6B), done = pistaId != 0, locked = sport == null)
                        Spacer(Modifier.height(10.dp))
                        if (sport == null) {
                            LockedHint("Selecciona un deporte primero")
                        } else {
                            CourtSelector(
                                courts     = allCourts.filter { it.activa && it.tipoEnum == sport },
                                selectedId = pistaId,
                                onSelect   = { id ->
                                    pistaId      = id
                                    selectedDay  = todayMs()
                                    selectedSlot = null
                                    slots        = emptyList()
                                }
                            )
                        }
                        Spacer(Modifier.height(24.dp))

                        // Paso 4 (o 3): Día
                        val stepDia = if (isAdmin) "4" else "3"
                        StepHeader(stepDia, "Día", Icons.Default.CalendarToday,
                            Color(0xFFFFB347), done = selectedDay != null, locked = pistaId == 0)
                        Spacer(Modifier.height(10.dp))
                        if (pistaId == 0) LockedHint("Selecciona una pista primero")
                        else DayNavigator(
                            selectedDay  = selectedDay!!,
                            maxDaysAhead = maxDaysAhead,
                            onDayChange  = { day -> selectedDay = day; selectedSlot = null; slots = emptyList() }
                        )
                        Spacer(Modifier.height(24.dp))

                        // Paso 5 (o 4): Horario
                        val stepHora = if (isAdmin) "5" else "4"
                        StepHeader(stepHora, "Horario", Icons.Default.Schedule,
                            Color(0xFF4ECB71), done = selectedSlot != null, locked = pistaId == 0)
                        Spacer(Modifier.height(10.dp))
                        if (pistaId == 0) LockedHint("Selecciona una pista primero")
                        else if (loadingSlots) SlotLoader()
                        else SlotsGrid(slots = slots, selectedSlot = selectedSlot, onSelect = { selectedSlot = it })
                    }

                    // ══════════════════════════════════════════════════════
                    // EQUIPO
                    // ══════════════════════════════════════════════════════
                    BookingType.EQUIPO -> {
                        StepHeader("1", "Deporte", Icons.Default.Sports,
                            Color(0xFFB57BFF), done = sport != null)
                        Spacer(Modifier.height(10.dp))
                        SportSelector(
                            courts        = allCourts,
                            selectedSport = sport,
                            onSelect      = { nuevoSport ->
                                selectedSport = nuevoSport
                                equipoId     = null; equipoNombre = null
                                pistaId      = 0; selectedDay = todayMs()
                                selectedSlot = null; slots = emptyList()
                            }
                        )
                        Spacer(Modifier.height(24.dp))

                        StepHeader("2", "Equipo", Icons.Default.Groups,
                            Color(0xFFFFCC44), done = equipoId != null, locked = sport == null)
                        Spacer(Modifier.height(10.dp))
                        if (sport == null) {
                            LockedHint("Selecciona un deporte primero")
                        } else {
                            // sport ya es CourtType (no nullable) gracias al val local
                            TeamSelector(
                                teams      = equiposParaEquipo.filter { it.sport == sport.name },
                                selectedId = equipoId,
                                color      = Color(0xFFFFCC44),
                                label      = "Selecciona un equipo",
                                onSelect   = { team ->
                                    equipoId     = team.id
                                    equipoNombre = team.nombre
                                    usuarioId    = team.entrenadorId ?: currentUserId
                                }
                            )
                        }
                        Spacer(Modifier.height(24.dp))

                        StepHeader("3", "Pista", Icons.Default.Stadium,
                            Color(0xFFFF6B6B), done = pistaId != 0, locked = equipoId == null)
                        Spacer(Modifier.height(10.dp))
                        if (equipoId == null) {
                            LockedHint("Selecciona un equipo primero")
                        } else {
                            CourtSelector(
                                courts     = allCourts.filter { it.activa && (sport == null || it.tipoEnum == sport) },
                                selectedId = pistaId,
                                onSelect   = { id ->
                                    pistaId = id; selectedDay = todayMs()
                                    selectedSlot = null; slots = emptyList()
                                }
                            )
                        }
                        Spacer(Modifier.height(24.dp))

                        StepHeader("4", "Día", Icons.Default.CalendarToday,
                            Color(0xFFFFB347), done = selectedDay != null, locked = pistaId == 0)
                        Spacer(Modifier.height(10.dp))
                        if (pistaId == 0) LockedHint("Selecciona una pista primero")
                        else DayNavigator(
                            selectedDay  = selectedDay!!,
                            maxDaysAhead = maxDaysAhead,
                            onDayChange  = { day -> selectedDay = day; selectedSlot = null; slots = emptyList() }
                        )
                        Spacer(Modifier.height(24.dp))

                        StepHeader("5", "Horario", Icons.Default.Schedule,
                            Color(0xFF4ECB71), done = selectedSlot != null, locked = pistaId == 0)
                        Spacer(Modifier.height(10.dp))
                        if (pistaId == 0) LockedHint("Selecciona una pista primero")
                        else if (loadingSlots) SlotLoader()
                        else SlotsGrid(slots = slots, selectedSlot = selectedSlot, onSelect = { selectedSlot = it })
                    }

                    // ══════════════════════════════════════════════════════
                    // PARTIDO
                    // ══════════════════════════════════════════════════════
                    BookingType.PARTIDO -> {
                        StepHeader("1", "Deporte", Icons.Default.Sports,
                            Color(0xFFB57BFF), done = sport != null)
                        Spacer(Modifier.height(10.dp))
                        SportSelector(
                            courts        = allCourts,
                            selectedSport = sport,
                            onSelect      = { nuevoSport ->
                                selectedSport     = nuevoSport
                                equipoLocalId     = null; equipoLocalNombre     = null
                                equipoVisitanteId = null; equipoVisitanteNombre = null
                                pistaId           = 0; selectedDay = todayMs()
                                selectedSlot      = null; slots = emptyList()
                            }
                        )
                        Spacer(Modifier.height(24.dp))

                        StepHeader("2", "Equipo Local", Icons.Default.Home,
                            Color(0xFF5B9EE7), done = equipoLocalId != null, locked = sport == null)
                        Spacer(Modifier.height(10.dp))
                        if (sport == null) {
                            LockedHint("Selecciona un deporte primero")
                        } else {
                            TeamSelector(
                                teams      = allTeams.filter { it.sport == sport.name && it.id != equipoVisitanteId },
                                selectedId = equipoLocalId,
                                color      = Color(0xFF5B9EE7),
                                label      = "Selecciona equipo local",
                                onSelect   = { team ->
                                    equipoLocalId     = team.id
                                    equipoLocalNombre = team.nombre
                                }
                            )
                        }
                        Spacer(Modifier.height(24.dp))

                        StepHeader("3", "Equipo Visitante", Icons.Default.FlightLand,
                            Color(0xFFFF6B6B), done = equipoVisitanteId != null, locked = equipoLocalId == null)
                        Spacer(Modifier.height(10.dp))
                        if (equipoLocalId == null) {
                            LockedHint("Selecciona el equipo local primero")
                        } else {
                            TeamSelector(
                                teams      = allTeams.filter { it.sport == sport?.name && it.id != equipoLocalId },
                                selectedId = equipoVisitanteId,
                                color      = Color(0xFFFF6B6B),
                                label      = "Selecciona equipo visitante",
                                onSelect   = { team ->
                                    equipoVisitanteId     = team.id
                                    equipoVisitanteNombre = team.nombre
                                }
                            )
                        }
                        Spacer(Modifier.height(24.dp))

                        StepHeader("4", "Pista", Icons.Default.Stadium,
                            Color(0xFFFFCC44), done = pistaId != 0, locked = equipoVisitanteId == null)
                        Spacer(Modifier.height(10.dp))
                        if (equipoVisitanteId == null) {
                            LockedHint("Selecciona los equipos primero")
                        } else {
                            CourtSelector(
                                courts     = allCourts.filter { it.activa && (sport == null || it.tipoEnum == sport) },
                                selectedId = pistaId,
                                onSelect   = { id ->
                                    pistaId = id; selectedDay = todayMs()
                                    selectedSlot = null; slots = emptyList()
                                }
                            )
                        }
                        Spacer(Modifier.height(24.dp))

                        StepHeader("5", "Árbitro", Icons.Default.SportsTennis,
                            Color(0xFF4ECB71), done = arbitroId != null)
                        Spacer(Modifier.height(10.dp))
                        ArbitroSelector(
                            arbitros   = arbitros,
                            selectedId = arbitroId,
                            onSelect   = { id, nombre -> arbitroId = id; arbitroNombre = nombre }
                        )
                        Spacer(Modifier.height(24.dp))

                        StepHeader("6", "Día", Icons.Default.CalendarToday,
                            Color(0xFFFFB347), done = selectedDay != null, locked = pistaId == 0)
                        Spacer(Modifier.height(10.dp))
                        if (pistaId == 0) LockedHint("Selecciona una pista primero")
                        else DayNavigator(
                            selectedDay  = selectedDay!!,
                            maxDaysAhead = maxDaysAhead,
                            onDayChange  = { day -> selectedDay = day; selectedSlot = null; slots = emptyList() }
                        )
                        Spacer(Modifier.height(24.dp))

                        StepHeader("7", "Horario", Icons.Default.Schedule,
                            Color(0xFF4ECB71), done = selectedSlot != null, locked = pistaId == 0)
                        Spacer(Modifier.height(10.dp))
                        if (pistaId == 0) LockedHint("Selecciona una pista primero")
                        else if (loadingSlots) SlotLoader()
                        else SlotsGrid(slots = slots, selectedSlot = selectedSlot, onSelect = { selectedSlot = it })
                        Spacer(Modifier.height(24.dp))

                        StepHeader("8", "Resultado (opcional)", Icons.Default.EmojiEvents,
                            Color(0xFFFFCC44), done = puntosLocalText.isNotBlank() && puntosVisitanteText.isNotBlank())
                        Spacer(Modifier.height(10.dp))
                        ResultadoInput(
                            puntosLocal       = puntosLocalText,
                            puntosVisitante   = puntosVisitanteText,
                            localNombre       = equipoLocalNombre ?: "Local",
                            visitanteNombre   = equipoVisitanteNombre ?: "Visitante",
                            onLocalChange     = { puntosLocalText    = it },
                            onVisitanteChange = { puntosVisitanteText = it }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextField(
                    value         = notas,
                    onValueChange = { notas = it },
                    label         = { Text("Notas (opcional)") },
                    leadingIcon   = { Icon(Icons.Default.Notes, null, tint = Color(0x99FFFFFF)) },
                    maxLines      = 3,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = noteFieldColors()
                )

                AnimatedVisibility(!conflictError.isNullOrBlank()) {
                    Text(conflictError ?: "", color = Color(0xFFFF5555),
                        fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(Modifier.height(24.dp))

                val canSave = when (tipoReserva) {
                    BookingType.INDIVIDUAL -> pistaId != 0 && selectedSlot != null && sport != null && (isJugador || usuarioId != 0)
                    BookingType.EQUIPO     -> equipoId != null && pistaId != 0 && selectedSlot != null
                    BookingType.PARTIDO    -> equipoLocalId != null && equipoVisitanteId != null &&
                            pistaId != 0 && selectedSlot != null
                }

                Button(
                    onClick = {
                        vm.onConflictErrorHandled()
                        val slot  = selectedSlot ?: return@Button
                        val court = allCourts.firstOrNull { it.id == pistaId } ?: return@Button

                        when (tipoReserva) {
                            BookingType.INDIVIDUAL -> {
                                val user = allUsers.firstOrNull { it.id == usuarioId } ?: return@Button
                                vm.saveBooking(
                                    bookingId     = bookingId, tipo = "INDIVIDUAL",
                                    usuarioId     = user.id,   usuarioNombre = user.nombre,
                                    pistaId       = court.id,  pistaNombre   = court.nombre,
                                    fecha         = selectedDay!!, horaInicio = slot.inicio, horaFin = slot.fin,
                                    notas         = notas.ifBlank { null }
                                )
                            }
                            BookingType.EQUIPO -> {
                                vm.saveBooking(
                                    bookingId     = bookingId, tipo = "EQUIPO",
                                    usuarioId     = usuarioId.takeIf { it != 0 },
                                    usuarioNombre = allUsers.firstOrNull { it.id == usuarioId }?.nombre,
                                    pistaId       = court.id, pistaNombre  = court.nombre,
                                    fecha         = selectedDay!!, horaInicio = slot.inicio, horaFin = slot.fin,
                                    notas         = notas.ifBlank { null },
                                    equipoId      = equipoId, equipoNombre = equipoNombre
                                )
                            }
                            BookingType.PARTIDO -> {
                                vm.saveBooking(
                                    bookingId             = bookingId, tipo = "PARTIDO",
                                    pistaId               = court.id, pistaNombre = court.nombre,
                                    fecha                 = selectedDay!!, horaInicio = slot.inicio, horaFin = slot.fin,
                                    notas                 = notas.ifBlank { null },
                                    equipoLocalId         = equipoLocalId,     equipoLocalNombre     = equipoLocalNombre,
                                    equipoVisitanteId     = equipoVisitanteId, equipoVisitanteNombre = equipoVisitanteNombre,
                                    arbitroId             = arbitroId,         arbitroNombre         = arbitroNombre,
                                    puntosLocal           = puntosLocalText.toIntOrNull(),
                                    puntosVisitante       = puntosVisitanteText.toIntOrNull()
                                )
                            }
                        }
                    },
                    enabled  = canSave,
                    shape    = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            if (canSave)
                                Brush.horizontalGradient(listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843)))
                            else
                                Brush.horizontalGradient(listOf(Color(0xFF1A1A1A), Color(0xFF2A2A2A))),
                            RoundedCornerShape(14.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = Color.Transparent,
                        contentColor           = Color.White,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor   = Color(0x66FFFFFF)
                    )
                ) {
                    Text(
                        if (bookingId == null) "Crear reserva" else "Guardar cambios",
                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// ── Selector de tipo ──────────────────────────────────────────────────────────

@Composable
private fun BookingTypeSelector(current: BookingType, isAdmin: Boolean, onSelect: (BookingType) -> Unit) {
    val tipos  = if (isAdmin) listOf(BookingType.INDIVIDUAL, BookingType.EQUIPO, BookingType.PARTIDO)
    else         listOf(BookingType.INDIVIDUAL, BookingType.EQUIPO)
    val colors = mapOf(BookingType.INDIVIDUAL to Color(0xFF5B9EE7), BookingType.EQUIPO to Color(0xFFFFCC44), BookingType.PARTIDO to Color(0xFF4ECB71))
    val icons  = mapOf(BookingType.INDIVIDUAL to Icons.Default.Person, BookingType.EQUIPO to Icons.Default.Groups, BookingType.PARTIDO to Icons.Default.SportsSoccer)

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        tipos.forEach { tipo ->
            val selected = current == tipo
            val color    = colors[tipo] ?: Color.White
            Box(
                modifier = Modifier
                    .weight(1f).clip(RoundedCornerShape(14.dp))
                    .background(if (selected) color.copy(0.15f) else Color(0x0DFFFFFF))
                    .border(if (selected) 1.5.dp else 1.dp, if (selected) color else Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onSelect(tipo) }.padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(icons[tipo] ?: Icons.Default.Event, null,
                        tint = if (selected) color else Color(0x80FFFFFF), modifier = Modifier.size(22.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(tipo.label, color = if (selected) color else Color(0x80FFFFFF),
                        fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

// ── Selector usuario con buscador ─────────────────────────────────────────────

@Composable
private fun UserSearchSelector(users: List<User>, selectedId: Int, onSelect: (Int) -> Unit) {
    var query    by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val filtered = users.filter { it.nombre.contains(query, ignoreCase = true) || it.email.contains(query, ignoreCase = true) }
    val selected = users.firstOrNull { it.id == selectedId }

    Column {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(Color(0x0DFFFFFF)).clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = Color(0xFF5B9EE7), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(selected?.let { "${it.nombre} (${it.rol})" } ?: "Selecciona un usuario",
                    color = if (selected != null) Color.White else Color(0x80FFFFFF),
                    modifier = Modifier.weight(1f), fontSize = 14.sp)
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color(0x66FFFFFF))
            }
        }
        AnimatedVisibility(expanded) {
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0D1F35)).padding(8.dp)) {
                TextField(
                    value = query, onValueChange = { query = it },
                    placeholder = { Text("Buscar usuario...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine  = true, modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x1AFFFFFF), unfocusedContainerColor = Color(0x1AFFFFFF),
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color(0x66FFFFFF), unfocusedPlaceholderColor = Color(0x66FFFFFF),
                        focusedLeadingIconColor = Color(0x99FFFFFF), unfocusedLeadingIconColor = Color(0x99FFFFFF),
                        cursorColor = Color(0xFF135B90)
                    )
                )
                Spacer(Modifier.height(4.dp))
                filtered.forEach { user ->
                    val isSel = user.id == selectedId
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color(0xFF135B90).copy(0.3f) else Color.Transparent)
                            .clickable { onSelect(user.id); expanded = false; query = "" }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(user.nombre, Color(0xFF5B9EE7), isSel)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.nombre, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(user.rol, color = Color(0x80FFFFFF), fontSize = 11.sp)
                        }
                        if (isSel) Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF5B9EE7))
                    }
                    HorizontalDivider(color = Color(0x0FFFFFFF))
                }
            }
        }
    }
}

// ── Selector de deporte ───────────────────────────────────────────────────────

@Composable
private fun SportSelector(courts: List<Court>, selectedSport: CourtType?, onSelect: (CourtType) -> Unit) {
    val available = CourtType.values().filter { sport -> courts.any { it.activa && it.tipoEnum == sport } }
    val color     = Color(0xFFB57BFF)
    FlowRow (horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        available.forEach { sport ->
            val selected = selectedSport == sport
            val icon = when (sport) {
                CourtType.PADEL      -> Icons.Default.SportsTennis
                CourtType.FUTBOL     -> Icons.Default.SportsSoccer
                CourtType.BALONCESTO -> Icons.Default.SportsBasketball
                CourtType.TENIS      -> Icons.Default.SportsTennis
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    .background(if (selected) color.copy(0.2f) else Color(0x0DFFFFFF))
                    .border(if (selected) 1.5.dp else 1.dp, if (selected) color else Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                    .clickable { onSelect(sport) }.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = if (selected) color else Color(0x80FFFFFF), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(sport.label, color = if (selected) color else Color(0xB3FFFFFF),
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                }
            }
        }
    }
}

// ── Selector de pista ─────────────────────────────────────────────────────────

@Composable
private fun CourtSelector(courts: List<Court>, selectedId: Int, onSelect: (Int) -> Unit) {
    val color = Color(0xFFFF6B6B)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        courts.forEach { court ->
            val selected = court.id == selectedId
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(if (selected) color.copy(0.12f) else Color(0x0DFFFFFF))
                    .border(if (selected) 1.5.dp else 1.dp, if (selected) color else Color(0x14FFFFFF), RoundedCornerShape(12.dp))
                    .clickable { onSelect(court.id) }.padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stadium, null, tint = if (selected) color else Color(0x66FFFFFF), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(court.nombre, color = if (selected) Color.White else Color(0xB3FFFFFF),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                        if (court.descripcion.isNotEmpty())
                            Text(court.descripcion, color = Color(0x66FFFFFF), fontSize = 11.sp,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text("${court.precioPorHora.toInt()}€/h", color = if (selected) color else Color(0x66FFFFFF),
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(6.dp))
                    if (selected) Icon(Icons.Default.CheckCircle, null, tint = color, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ── Selector de equipo (dropdown) ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSelector(
    teams: List<Team>,
    selectedId: Int?,
    color: Color = Color(0xFFFFCC44),
    label: String = "Selecciona un equipo",
    onSelect: (Team) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = teams.firstOrNull { it.id == selectedId }

    if (teams.isEmpty()) {
        LockedHint("No hay equipos disponibles para este deporte")
        return
    }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(if (selected != null) color.copy(0.1f) else Color(0x0DFFFFFF))
                .border(if (selected != null) 1.5.dp else 1.dp,
                    if (selected != null) color else Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                .menuAnchor().padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Groups, null,
                    tint = if (selected != null) color else Color(0x80FFFFFF), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(selected?.nombre ?: label,
                        color = if (selected != null) Color.White else Color(0x80FFFFFF),
                        fontSize = 14.sp, fontWeight = if (selected != null) FontWeight.SemiBold else FontWeight.Normal)
                    selected?.entrenadorNombre?.let {
                        Text("Entrenador: $it", color = color.copy(0.8f), fontSize = 11.sp)
                    }
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null, tint = Color(0x66FFFFFF))
            }
        }
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF0D1F35))) {
            teams.forEach { team ->
                val isSel = team.id == selectedId
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                                .background(color.copy(if (isSel) 0.3f else 0.1f)),
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Groups, null, tint = color, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(team.nombre, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                team.entrenadorNombre?.let { Text("Entrenador: $it", color = Color(0x80FFFFFF), fontSize = 11.sp) }
                                Text("${team.getJugadoresIdsList().size} jugadores", color = Color(0x66FFFFFF), fontSize = 11.sp)
                            }
                            if (isSel) Icon(Icons.Default.CheckCircle, null, tint = color, modifier = Modifier.size(18.dp))
                        }
                    },
                    onClick = { onSelect(team); expanded = false }
                )
                HorizontalDivider(color = Color(0x0FFFFFFF))
            }
        }
    }
}

// ── Selector de árbitro ───────────────────────────────────────────────────────

@Composable
private fun ArbitroSelector(arbitros: List<User>, selectedId: Int?, onSelect: (Int?, String?) -> Unit) {
    val color = Color(0xFF4ECB71)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val noSel = selectedId == null
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(if (noSel) color.copy(0.08f) else Color(0x0DFFFFFF))
                .border(1.dp, if (noSel) color.copy(0.4f) else Color(0x14FFFFFF), RoundedCornerShape(12.dp))
                .clickable { onSelect(null, null) }.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonOff, null, tint = if (noSel) color else Color(0x66FFFFFF), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text("Sin árbitro", color = if (noSel) color else Color(0x80FFFFFF), fontSize = 14.sp, modifier = Modifier.weight(1f))
                if (noSel) Icon(Icons.Default.CheckCircle, null, tint = color, modifier = Modifier.size(18.dp))
            }
        }
        if (arbitros.isEmpty()) {
            LockedHint("No hay árbitros registrados")
        } else {
            arbitros.forEach { user ->
                val selected = user.id == selectedId
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(if (selected) color.copy(0.12f) else Color(0x0DFFFFFF))
                        .border(if (selected) 1.5.dp else 1.dp, if (selected) color else Color(0x14FFFFFF), RoundedCornerShape(12.dp))
                        .clickable { onSelect(user.id, user.nombre) }.padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(user.nombre, color, selected)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.nombre, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(user.email, color = Color(0x80FFFFFF), fontSize = 11.sp)
                        }
                        if (selected) Icon(Icons.Default.CheckCircle, null, tint = color, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ── Navegador de días con DatePicker ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayNavigator(selectedDay: Long, maxDaysAhead: Int, onDayChange: (Long) -> Unit) {
    val today      = todayMs()
    val dayFmt     = rememberSaveable { SimpleDateFormat("EEE d MMM", Locale("es")) }
    val color      = Color(0xFFFFB347)
    val canBack    = selectedDay > today
    val daysAhead  = ((selectedDay - today) / 86_400_000L).toInt()
    val canFwd     = maxDaysAhead == Int.MAX_VALUE || daysAhead < maxDaysAhead
    var showPicker by rememberSaveable { mutableStateOf(false) }

    if (showPicker) {
        val maxMs = if (maxDaysAhead == Int.MAX_VALUE) today + 365L * 86_400_000L
        else today + maxDaysAhead * 86_400_000L
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDay,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis >= today && utcTimeMillis <= maxMs
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { ms ->
                        val cal = Calendar.getInstance().apply {
                            timeInMillis = ms
                            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
                        }
                        onDayChange(cal.timeInMillis)
                    }
                    showPicker = false
                }) { Text("Confirmar", color = Color(0xFF135B90)) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancelar", color = Color.White) }
            },
            colors = DatePickerDefaults.colors(containerColor = Color(0xFF0B0E12))
        ) {
            DatePicker(
                state  = pickerState,
                colors = DatePickerDefaults.colors(
                    containerColor            = Color(0xFF0B0E12),
                    titleContentColor         = Color.White,
                    headlineContentColor      = Color.White,
                    weekdayContentColor       = Color(0x99FFFFFF),
                    selectedDayContainerColor = Color(0xFF135B90),
                    todayDateBorderColor      = Color(0xFF135B90),
                    todayContentColor         = Color(0xFFFFB347),
                    dayContentColor           = Color.White,
                    disabledDayContentColor   = Color(0x33FFFFFF)
                )
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.1f)).border(1.dp, color.copy(0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDayChange(selectedDay - 86_400_000L) }, enabled = canBack) {
            Icon(Icons.Default.ChevronLeft, null, tint = if (canBack) color else Color(0x33FFFFFF))
        }
        Box(
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                .clickable { showPicker = true }.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Default.CalendarToday, null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(dayFmt.format(Date(selectedDay)).replaceFirstChar { it.uppercase() },
                    color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
        IconButton(onClick = { onDayChange(selectedDay + 86_400_000L) }, enabled = canFwd) {
            Icon(Icons.Default.ChevronRight, null, tint = if (canFwd) color else Color(0x33FFFFFF))
        }
    }
}

// ── Grid de slots ─────────────────────────────────────────────────────────────

@Composable
private fun SlotsGrid(slots: List<TimeSlot>, selectedSlot: TimeSlot?, onSelect: (TimeSlot) -> Unit) {
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val now     = System.currentTimeMillis()
    val green   = Color(0xFF4ECB71)
    val red     = Color(0xFFFF5555)

    if (slots.isEmpty()) { LockedHint("No hay horarios disponibles (09:00 – 22:30)"); return }

    Column {
        Row {
            LegendDot(green, "Disponible")
            Spacer(Modifier.width(16.dp))
            LegendDot(red, "Ocupado / Pasado")
        }
        Spacer(Modifier.height(12.dp))
        slots.chunked(3).forEach { rowSlots ->
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowSlots.forEach { slot ->
                    val isSelected = selectedSlot?.inicio == slot.inicio
                    val isPast     = slot.inicio < now && !slot.occupied
                    val color      = if (slot.occupied || isPast) red else green
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(when { isSelected -> green.copy(0.25f); slot.occupied -> red.copy(0.12f); isPast -> red.copy(0.08f); else -> green.copy(0.08f) })
                            .border(if (isSelected) 2.dp else 1.dp,
                                when { isSelected -> green; slot.occupied -> red.copy(0.5f); isPast -> red.copy(0.3f); else -> green.copy(0.3f) },
                                RoundedCornerShape(10.dp))
                            .clickable(enabled = !slot.occupied && !isPast) { onSelect(slot) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(timeFmt.format(Date(slot.inicio)), color = if (isSelected) green else color,
                                fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(timeFmt.format(Date(slot.fin)), color = (if (isSelected) green else color).copy(0.7f), fontSize = 11.sp)
                            if (isSelected) Icon(Icons.Default.CheckCircle, null, tint = green, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                repeat(3 - rowSlots.size) { Box(modifier = Modifier.weight(1f)) }
            }
        }
        if (selectedSlot != null) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(green.copy(0.1f)).border(1.dp, green.copy(0.3f), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircleOutline, null, tint = green, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("${timeFmt.format(Date(selectedSlot.inicio))} — ${timeFmt.format(Date(selectedSlot.fin))}  (1h 30min)",
                        color = green, fontSize = 13.sp)
                }
            }
        }
    }
}

// ── Input resultado partido ───────────────────────────────────────────────────

@Composable
private fun ResultadoInput(
    puntosLocal: String, puntosVisitante: String,
    localNombre: String, visitanteNombre: String,
    onLocalChange: (String) -> Unit, onVisitanteChange: (String) -> Unit
) {
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0x0DFFFFFF), unfocusedContainerColor = Color(0x0DFFFFFF),
        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
        focusedLabelColor = Color(0xFFFFCC44), unfocusedLabelColor = Color(0x99FFFFFF),
        cursorColor = Color(0xFFFFCC44)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TextField(value = puntosLocal,
            onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) onLocalChange(it) },
            label = { Text(localNombre, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            singleLine = true, shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f), colors = fieldColors)
        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
            Text("VS", color = Color(0x80FFFFFF), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        TextField(value = puntosVisitante,
            onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) onVisitanteChange(it) },
            label = { Text(visitanteNombre, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            singleLine = true, shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f), colors = fieldColors)
    }
}

// ── Widgets comunes ───────────────────────────────────────────────────────────

@Composable
private fun StepHeader(step: String, title: String, icon: ImageVector, color: Color, done: Boolean = false, locked: Boolean = false) {
    val c = if (locked) Color(0x40FFFFFF) else color
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(28.dp).clip(CircleShape)
            .background(if (done) c.copy(0.25f) else if (locked) Color(0x1AFFFFFF) else c.copy(0.1f))
            .border(1.5.dp, if (done || !locked) c else Color(0x40FFFFFF), CircleShape),
            contentAlignment = Alignment.Center) {
            if (done) Icon(Icons.Default.Check, null, tint = c, modifier = Modifier.size(14.dp))
            else Text(step, color = c, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Icon(icon, null, tint = c, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(title, color = if (locked) Color(0x40FFFFFF) else Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable private fun LockedHint(text: String) {
    Text(text, color = Color(0x40FFFFFF), fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
}

@Composable private fun SlotLoader() {
    Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0x66FFFFFF), strokeWidth = 2.dp)
    }
}

@Composable private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color.copy(0.3f)).border(1.dp, color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(label, color = color.copy(0.8f), fontSize = 12.sp)
    }
}

@Composable private fun UserAvatar(name: String, color: Color, selected: Boolean) {
    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(if (selected) 0.3f else 0.1f)),
        contentAlignment = Alignment.Center) {
        Text(if (name.isNotEmpty()) name.first().uppercaseChar().toString() else "?",
            color = if (selected) color else Color(0xB3FFFFFF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable private fun noteFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0x0DFFFFFF), unfocusedContainerColor = Color(0x0DFFFFFF),
    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
    focusedLabelColor = Color(0xFF135B90), unfocusedLabelColor = Color(0x99FFFFFF),
    cursorColor = Color(0xFF135B90), focusedTextColor = Color.White, unfocusedTextColor = Color.White,
    focusedLeadingIconColor = Color(0x99FFFFFF), unfocusedLeadingIconColor = Color(0x99FFFFFF)
)