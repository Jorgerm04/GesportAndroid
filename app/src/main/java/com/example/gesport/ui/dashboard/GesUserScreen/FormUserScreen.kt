package com.example.gesport.ui.dashboard.GesUserScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormUserScreen(navController: NavHostController, userId: Int? = null) {

    val context = LocalContext.current
    val vm: GesUserViewModel = viewModel(factory = GesUserViewModelFactory(context))

    val currentUser    by vm.currentUser.observeAsState()
    val saveCompleted  by vm.saveCompleted.observeAsState(false)
    val equipoAsociado by vm.equipoAsociado.observeAsState()
    val loadingEquipo  by vm.loadingEquipo.observeAsState(false)

    LaunchedEffect(userId) {
        if (userId != null) vm.loadUserById(userId)
    }
    LaunchedEffect(saveCompleted) {
        if (saveCompleted) {
            navController.popBackStack()
            vm.onSaveCompletedHandled()
        }
    }

    var nombre   by rememberSaveable(currentUser) { mutableStateOf(currentUser?.nombre   ?: "") }
    var email    by rememberSaveable(currentUser) { mutableStateOf(currentUser?.email    ?: "") }
    var password by rememberSaveable(currentUser) { mutableStateOf(currentUser?.password ?: "") }
    var rol      by rememberSaveable(currentUser) { mutableStateOf(currentUser?.rol      ?: "JUGADOR") }

    LaunchedEffect(currentUser, rol) {
        if (userId != null && (rol == "JUGADOR" || rol == "ENTRENADOR")) {
            vm.loadEquipoAsociado(userId, rol)
        }
    }

    val roles = listOf("ADMIN", "ENTRENADOR", "ARBITRO", "JUGADOR")
    var rolDropdownExpanded by remember { mutableStateOf(false) }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor     = Color(0x0DFFFFFF),
        unfocusedContainerColor   = Color(0x0DFFFFFF),
        focusedIndicatorColor     = Color.Transparent,
        unfocusedIndicatorColor   = Color.Transparent,
        focusedLabelColor         = Color(0xFF135B90),
        unfocusedLabelColor       = Color(0x99FFFFFF),
        cursorColor               = Color(0xFF135B90),
        focusedTextColor          = Color.White,
        unfocusedTextColor        = Color.White,
        focusedLeadingIconColor   = Color(0x99FFFFFF),
        unfocusedLeadingIconColor = Color(0x99FFFFFF),
        disabledContainerColor    = Color(0x0DFFFFFF),
        disabledTextColor         = Color(0x66FFFFFF),
        disabledLeadingIconColor  = Color(0x44FFFFFF),
        disabledLabelColor        = Color(0x44FFFFFF),
        disabledIndicatorColor    = Color.Transparent
    )

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (userId == null) "Nuevo usuario" else "Editar usuario",
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
                    label         = { Text("Nombre completo") },
                    leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = fieldColors
                )

                Spacer(Modifier.height(16.dp))

                // ── Email ─────────────────────────────────────────────────
                TextField(
                    value           = email,
                    onValueChange   = { email = it },
                    label           = { Text("Email") },
                    leadingIcon     = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine      = true,
                    enabled         = userId == null,
                    shape           = RoundedCornerShape(12.dp),
                    modifier        = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors          = fieldColors
                )

                Spacer(Modifier.height(16.dp))

                // ── Contraseña ────────────────────────────────────────────
                TextField(
                    value         = password,
                    onValueChange = { password = it },
                    label         = { Text("Contraseña") },
                    leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = fieldColors
                )

                Spacer(Modifier.height(16.dp))

                // ── Rol ───────────────────────────────────────────────────
                Text(
                    "Rol del usuario",
                    color    = Color(0x99FFFFFF),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded         = rolDropdownExpanded,
                    onExpandedChange = { rolDropdownExpanded = !rolDropdownExpanded },
                    modifier         = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value         = rol,
                        onValueChange = {},
                        readOnly      = true,
                        leadingIcon   = { Icon(Icons.Default.Badge, contentDescription = null) },
                        trailingIcon  = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null,
                                tint = Color(0x99FFFFFF))
                        },
                        shape    = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors   = fieldColors
                    )

                    ExposedDropdownMenu(
                        expanded         = rolDropdownExpanded,
                        onDismissRequest = { rolDropdownExpanded = false },
                        modifier         = Modifier.background(Color(0xFF12171E))
                    ) {
                        roles.forEach { roleKey ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        roleKey,
                                        color = if (rol == roleKey) Color(0xFF135B90) else Color.White
                                    )
                                },
                                onClick = {
                                    rol = roleKey
                                    rolDropdownExpanded = false
                                    if (userId != null && (roleKey == "JUGADOR" || roleKey == "ENTRENADOR")) {
                                        vm.loadEquipoAsociado(userId, roleKey)
                                    }
                                }
                            )
                        }
                    }
                }

                // ── Equipo asociado ───────────────────────────────────────
                if (userId != null && (rol == "JUGADOR" || rol == "ENTRENADOR")) {
                    Spacer(Modifier.height(28.dp))

                    val isEntrenador = rol == "ENTRENADOR"
                    val colorEquipo  = if (isEntrenador) Color(0xFF5B9EE7) else Color(0xFF4ECB71)
                    val iconEquipo   = if (isEntrenador) Icons.Default.PersonPin else Icons.Default.SportsSoccer
                    val labelEquipo  = if (isEntrenador) "Equipo que entrena" else "Equipo"

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(iconEquipo, contentDescription = null,
                            tint = colorEquipo, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(labelEquipo, color = colorEquipo,
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(10.dp))

                    if (loadingEquipo) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x0DFFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color       = Color(0x66FFFFFF),
                                strokeWidth = 2.dp,
                                modifier    = Modifier.size(20.dp)
                            )
                        }
                    } else if (equipoAsociado == null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x08FFFFFF))
                                .border(1.dp, Color(0x12FFFFFF), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null,
                                tint = Color(0x40FFFFFF), modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                if (isEntrenador) "No entrena ningún equipo todavía"
                                else "No pertenece a ningún equipo todavía",
                                color    = Color(0x66FFFFFF),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        val equipo = equipoAsociado!!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorEquipo.copy(alpha = 0.07f))
                                .border(1.dp, colorEquipo.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .clickable { navController.navigate("formTeam/${equipo.id}") }
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colorEquipo.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Groups, contentDescription = null,
                                        tint = colorEquipo, modifier = Modifier.size(24.dp))
                                }

                                Spacer(Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(equipo.nombre, color = Color.White,
                                        fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (equipo.descripcion.isNotEmpty()) {
                                        Spacer(Modifier.height(2.dp))
                                        Text(equipo.descripcion, color = Color(0x80FFFFFF),
                                            fontSize = 12.sp, maxLines = 1,
                                            overflow = TextOverflow.Ellipsis)
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.SportsSoccer, contentDescription = null,
                                            tint = colorEquipo.copy(alpha = 0.7f),
                                            modifier = Modifier.size(12.dp))
                                        Spacer(Modifier.width(4.dp))
                                        val n = equipo.getJugadoresIdsList().size
                                        Text("$n jugador${if (n == 1) "" else "es"}",
                                            color = colorEquipo.copy(alpha = 0.8f), fontSize = 11.sp)
                                        if (isEntrenador) {
                                            Spacer(Modifier.width(10.dp))
                                            Icon(Icons.Default.PersonPin, contentDescription = null,
                                                tint = colorEquipo.copy(alpha = 0.7f),
                                                modifier = Modifier.size(12.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Tú eres el entrenador",
                                                color = colorEquipo.copy(alpha = 0.8f), fontSize = 11.sp)
                                        }
                                    }
                                }

                                Icon(Icons.Default.ChevronRight, contentDescription = null,
                                    tint = colorEquipo.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                // ── Botón guardar ─────────────────────────────────────────
                Button(
                    onClick = {
                        vm.saveUser(
                            userId   = userId,
                            nombre   = nombre,
                            email    = email,
                            password = password,
                            rol      = rol
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
                        if (userId == null) "Crear usuario" else "Guardar cambios",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}