package com.example.gesport.ui.dashboard.GesTeamsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.models.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTeamScreen(navController: NavHostController, teamId: Int? = null) {

    val context = LocalContext.current
    val vm: GesTeamViewModel = viewModel(factory = GesTeamViewModelFactory(context))

    val currentTeam   by vm.currentTeam.observeAsState()
    val saveCompleted by vm.saveCompleted.observeAsState(false)
    val allUsers       = vm.allUsers

    LaunchedEffect(teamId) {
        if (teamId != null) vm.loadTeamById(teamId)
    }
    LaunchedEffect(saveCompleted) {
        if (saveCompleted) {
            navController.popBackStack()
            vm.onSaveCompletedHandled()
        }
    }

    var nombre          by rememberSaveable(currentTeam) { mutableStateOf(currentTeam?.nombre ?: "") }
    var descripcion     by rememberSaveable(currentTeam) { mutableStateOf(currentTeam?.descripcion ?: "") }
    var entrenadorId    by rememberSaveable(currentTeam) { mutableStateOf(currentTeam?.entrenadorId) }
    var entrenadorNombre by rememberSaveable(currentTeam) { mutableStateOf(currentTeam?.entrenadorNombre) }
    var jugadoresSelIds by remember(currentTeam) {
        mutableStateOf(currentTeam?.getJugadoresIdsList()?.toSet() ?: emptySet())
    }

    val entrenadores = allUsers.filter { it.rol == "ENTRENADOR" }
    val jugadores    = allUsers.filter { it.rol == "JUGADOR" }

    val sheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope        = rememberCoroutineScope()
    var sheetType    by remember { mutableStateOf<SheetType?>(null) }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor   = Color(0x0DFFFFFF),
        unfocusedContainerColor = Color(0x0DFFFFFF),
        focusedIndicatorColor   = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedLabelColor       = Color(0xFF135B90),
        unfocusedLabelColor     = Color(0x99FFFFFF),
        cursorColor             = Color(0xFF135B90),
        focusedTextColor        = Color.White,
        unfocusedTextColor      = Color.White
    )

    // ── BottomSheet ───────────────────────────────────────────────────────
    if (sheetType != null) {
        ModalBottomSheet(
            onDismissRequest  = { sheetType = null },
            sheetState        = sheetState,
            containerColor    = Color(0xFF0D1F35),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            when (sheetType) {

                SheetType.COACH -> CoachSheet(
                    entrenadores    = entrenadores,
                    selectedId      = entrenadorId,
                    onSelect        = { id, nombre ->
                        entrenadorId     = id
                        entrenadorNombre = nombre
                        scope.launch { sheetState.hide() }.invokeOnCompletion { sheetType = null }
                    }
                )

                SheetType.PLAYERS -> PlayersSheet(
                    jugadores       = jugadores,
                    selectedIds     = jugadoresSelIds,
                    onToggle        = { id ->
                        jugadoresSelIds = if (jugadoresSelIds.contains(id))
                            jugadoresSelIds - id else jugadoresSelIds + id
                    },
                    onConfirm       = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { sheetType = null }
                    }
                )

                null -> {}
            }
        }
    }

    // ── Pantalla principal ────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (teamId == null) "Nuevo equipo" else "Editar equipo",
                            color = Color(0xFFE7F1FF),
                            fontWeight = FontWeight.SemiBold
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
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Nombre ────────────────────────────────────────────────
                TextField(
                    value         = nombre,
                    onValueChange = { nombre = it },
                    label         = { Text("Nombre del equipo") },
                    leadingIcon   = { Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0x99FFFFFF)) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = fieldColors
                )

                Spacer(Modifier.height(12.dp))

                // ── Descripción ───────────────────────────────────────────
                TextField(
                    value         = descripcion,
                    onValueChange = { descripcion = it },
                    label         = { Text("Descripción (opcional)") },
                    leadingIcon   = { Icon(Icons.Default.Notes, contentDescription = null, tint = Color(0x99FFFFFF)) },
                    maxLines      = 2,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = fieldColors
                )

                Spacer(Modifier.height(28.dp))

                // ── Sección Entrenador ────────────────────────────────────
                SectionLabel(
                    text  = "Entrenador",
                    icon  = Icons.Default.PersonPin,
                    color = Color(0xFF5B9EE7)
                )

                Spacer(Modifier.height(10.dp))

                SelectorCard(onClick = { sheetType = SheetType.COACH }) {
                    if (entrenadorId == null) {
                        PlaceholderRow(
                            icon  = Icons.Default.PersonAdd,
                            text  = "Toca para asignar entrenador",
                            color = Color(0xFF5B9EE7)
                        )
                    } else {
                        SelectedUserRow(
                            name  = entrenadorNombre ?: "",
                            color = Color(0xFF5B9EE7),
                            onRemove = {
                                entrenadorId     = null
                                entrenadorNombre = null
                            }
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // ── Sección Jugadores ─────────────────────────────────────
                SectionLabel(
                    text  = "Jugadores",
                    icon  = Icons.Default.SportsSoccer,
                    color = Color(0xFF4ECB71)
                )

                Spacer(Modifier.height(10.dp))

                SelectorCard(onClick = { sheetType = SheetType.PLAYERS }) {
                    if (jugadoresSelIds.isEmpty()) {
                        PlaceholderRow(
                            icon  = Icons.Default.GroupAdd,
                            text  = "Toca para añadir jugadores",
                            color = Color(0xFF4ECB71)
                        )
                    } else {
                        // Chips de jugadores seleccionados
                        val selectedPlayers = jugadores.filter { jugadoresSelIds.contains(it.id) }
                        Column {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                selectedPlayers.forEach { user ->
                                    InputChip(
                                        selected  = true,
                                        onClick   = { jugadoresSelIds = jugadoresSelIds - user.id },
                                        label     = { Text(user.nombre, fontSize = 12.sp, color = Color.White) },
                                        leadingIcon = {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(Color(0xFF4ECB71).copy(alpha = 0.25f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    user.nombre.first().uppercaseChar().toString(),
                                                    color    = Color(0xFF4ECB71),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            Icon(Icons.Default.Close, contentDescription = "Quitar",
                                                tint = Color(0x99FFFFFF), modifier = Modifier.size(14.dp))
                                        },
                                        colors = InputChipDefaults.inputChipColors(
                                            selectedContainerColor = Color(0xFF1A3050),
                                            selectedLeadingIconColor = Color(0xFF4ECB71)
                                        ),
                                        border = InputChipDefaults.inputChipBorder(
                                            selectedBorderColor = Color(0xFF4ECB71).copy(alpha = 0.3f),
                                            selectedBorderWidth = 1.dp,
                                            enabled = true,
                                            selected = true,
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "${jugadoresSelIds.size} jugador${if (jugadoresSelIds.size == 1) "" else "es"} · Toca para editar",
                                color    = Color(0xFF4ECB71),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                // ── Botón guardar ─────────────────────────────────────────
                Button(
                    onClick = {
                        vm.saveTeam(
                            teamId           = teamId,
                            nombre           = nombre,
                            descripcion      = descripcion,
                            entrenadorId     = entrenadorId,
                            entrenadorNombre = entrenadorNombre,
                            jugadoresIds     = jugadoresSelIds.toList()
                        )
                    },
                    shape    = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))
                            ),
                            RoundedCornerShape(14.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor   = Color.White
                    )
                ) {
                    Text(
                        if (teamId == null) "Crear equipo" else "Guardar cambios",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ── Enum tipo de sheet ────────────────────────────────────────────────────────

private enum class SheetType { COACH, PLAYERS }

// ── Sheet: Entrenador ─────────────────────────────────────────────────────────

@Composable
private fun CoachSheet(
    entrenadores: List<User>,
    selectedId: Int?,
    onSelect: (Int?, String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        SheetHandle()

        Spacer(Modifier.height(16.dp))

        Text(
            "Seleccionar entrenador",
            color      = Color.White,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(color = Color(0x1AFFFFFF))

        if (entrenadores.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay entrenadores disponibles.\nAsigna el rol \"Entrenador\" a un usuario primero.",
                    color     = Color(0x80FFFFFF),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            // Opción sin entrenador
            ListItem(
                headlineContent = {
                    Text(
                        "Sin entrenador",
                        color = if (selectedId == null) Color(0xFF5B9EE7) else Color(0x80FFFFFF)
                    )
                },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0x1AFFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonOff, contentDescription = null,
                            tint = Color(0x66FFFFFF), modifier = Modifier.size(18.dp))
                    }
                },
                modifier = Modifier.clickable { onSelect(null, null) },
                colors   = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            HorizontalDivider(color = Color(0x1AFFFFFF))

            entrenadores.forEach { user ->
                val isSelected = selectedId == user.id
                ListItem(
                    headlineContent = {
                        Text(user.nombre, color = Color.White, fontWeight = FontWeight.Medium)
                    },
                    supportingContent = {
                        Text(user.email, color = Color(0x80FFFFFF), fontSize = 12.sp)
                    },
                    leadingContent = {
                        UserAvatar(name = user.nombre, color = Color(0xFF5B9EE7), selected = isSelected)
                    },
                    trailingContent = {
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = Color(0xFF5B9EE7))
                        }
                    },
                    modifier = Modifier.clickable { onSelect(user.id, user.nombre) },
                    colors   = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider(color = Color(0x1AFFFFFF))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Sheet: Jugadores ──────────────────────────────────────────────────────────

@Composable
private fun PlayersSheet(
    jugadores: List<User>,
    selectedIds: Set<Int>,
    onToggle: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        SheetHandle()

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Seleccionar jugadores",
                color      = Color.White,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { jugadores.forEach { onToggle(it.id) } }) {
                // Limpiar todo
                Text("Limpiar todo", color = Color(0x66FFFFFF))
            }
        }

        HorizontalDivider(color = Color(0x1AFFFFFF))

        if (jugadores.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay jugadores disponibles.\nAsigna el rol \"Jugador\" a un usuario primero.",
                    color     = Color(0x80FFFFFF),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(jugadores) { user ->
                    val isSelected = selectedIds.contains(user.id)
                    ListItem(
                        headlineContent = {
                            Text(user.nombre, color = Color.White, fontWeight = FontWeight.Medium)
                        },
                        supportingContent = {
                            Text(user.email, color = Color(0x80FFFFFF), fontSize = 12.sp)
                        },
                        leadingContent = {
                            UserAvatar(name = user.nombre, color = Color(0xFF4ECB71), selected = isSelected)
                        },
                        trailingContent = {
                            Checkbox(
                                checked         = isSelected,
                                onCheckedChange = { onToggle(user.id) },
                                colors          = CheckboxDefaults.colors(
                                    checkedColor   = Color(0xFF4ECB71),
                                    uncheckedColor = Color(0x66FFFFFF),
                                    checkmarkColor = Color.Black
                                )
                            )
                        },
                        modifier = Modifier.clickable { onToggle(user.id) },
                        colors   = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                }
            }
        }

        // Botón confirmar
        Button(
            onClick  = onConfirm,
            shape    = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(48.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF135B90))
        ) {
            Text(
                "Confirmar · ${selectedIds.size} seleccionado${if (selectedIds.size == 1) "" else "s"}",
                color = Color.White
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ── Widgets reutilizables ─────────────────────────────────────────────────────

@Composable
private fun SheetHandle() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0x40FFFFFF))
        )
    }
}

@Composable
private fun SectionLabel(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = color, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SelectorCard(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x0DFFFFFF))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        content()
    }
}

@Composable
private fun PlaceholderRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null,
            tint = color.copy(alpha = 0.5f), modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, color = color.copy(alpha = 0.5f), fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0x40FFFFFF))
    }
}

@Composable
private fun SelectedUserRow(name: String, color: Color, onRemove: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        UserAvatar(name = name, color = color, selected = true)
        Spacer(Modifier.width(12.dp))
        Text(name, color = Color.White, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Cancel, contentDescription = "Quitar",
                tint = Color(0x66FFFFFF), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun UserAvatar(name: String, color: Color, selected: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = if (selected) 0.3f else 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = if (name.isNotEmpty()) name.first().uppercaseChar().toString() else "?",
            color      = if (selected) color else Color(0xB3FFFFFF),
            fontWeight = FontWeight.Bold,
            fontSize   = 13.sp
        )
    }
}