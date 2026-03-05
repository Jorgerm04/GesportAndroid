package com.example.gesport.ui.dashboard.GesCourtsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.models.CourtType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormCourtScreen(navController: NavHostController, courtId: Int? = null) {

    val context = LocalContext.current
    val vm: GesCourtViewModel = viewModel(factory = GesCourtViewModelFactory(context))

    val currentCourt  by vm.currentCourt.observeAsState()
    val saveCompleted by vm.saveCompleted.observeAsState(false)

    LaunchedEffect(courtId) {
        if (courtId != null) vm.loadCourtById(courtId)
    }
    LaunchedEffect(saveCompleted) {
        if (saveCompleted) {
            navController.popBackStack()
            vm.onSaveCompletedHandled()
        }
    }

    var nombre      by rememberSaveable(currentCourt) { mutableStateOf(currentCourt?.nombre ?: "") }
    var tipo        by rememberSaveable(currentCourt) { mutableStateOf(CourtType.fromString(currentCourt?.tipo)) }
    var descripcion by rememberSaveable(currentCourt) { mutableStateOf(currentCourt?.descripcion ?: "") }
    var activa      by rememberSaveable(currentCourt) { mutableStateOf(currentCourt?.activa ?: true) }
    var precioText  by rememberSaveable(currentCourt) { mutableStateOf(currentCourt?.precioPorHora?.toString() ?: "0.0") }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))
    val fieldColors = TextFieldDefaults.colors(
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

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (courtId == null) "Nueva pista" else "Editar pista",
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                Spacer(Modifier.height(12.dp))

                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                Spacer(Modifier.height(12.dp))

                TextField(
                    value = precioText,
                    onValueChange = { precioText = it },
                    label = { Text("Precio por hora (€)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Tipo de pista",
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
                    CourtType.values().forEach { ct ->
                        FilterChip(
                            selected = tipo == ct,
                            onClick  = { tipo = ct },
                            label    = { Text(ct.label) },
                            colors   = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFF0B0E12),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF135B90),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = activa,
                        onCheckedChange = { activa = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF135B90)
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (activa) "Pista activa" else "Pista inactiva",
                        color = Color(0xCCFFFFFF)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        vm.saveCourt(
                            courtId       = courtId,
                            nombre        = nombre,
                            tipo          = tipo,
                            descripcion   = descripcion,
                            activa        = activa,
                            precioPorHora = precioText.toDoubleOrNull() ?: 0.0
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))
                            ),
                            RoundedCornerShape(14.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        if (courtId == null) "Crear pista" else "Guardar cambios",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}