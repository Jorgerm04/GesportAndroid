@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gesport.ui.auth.RegisterScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.R

@Composable
fun RegisterScreen(
    navController: NavHostController,
    onForgotPassword: () -> Unit = {}
) {
    val context  = LocalContext.current
    val vm: RegisterViewModel = viewModel(factory = RegisterViewModelFactory(context))

    var nombre      by rememberSaveable { mutableStateOf("") }
    var email       by rememberSaveable { mutableStateOf("") }
    var password    by rememberSaveable { mutableStateOf("") }
    var showPass    by rememberSaveable { mutableStateOf(false) }

    // Cuando el registro es exitoso navegamos al login
    LaunchedEffect(vm.registered) {
        if (vm.registered) {
            navController.navigate("login") { popUpTo("register") { inclusive = true } }
        }
    }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))
    val gradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))
    )
    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor     = Color.Transparent,
        unfocusedContainerColor   = Color.Transparent,
        focusedIndicatorColor     = Color(0xFF135B90),
        unfocusedIndicatorColor   = Color(0x334B5563),
        focusedPlaceholderColor   = Color(0xFF135B90),
        unfocusedPlaceholderColor = Color(0x55FFFFFF),
        focusedLeadingIconColor   = Color(0xFF135B90),
        unfocusedLeadingIconColor = Color(0x99FFFFFF),
        focusedTrailingIconColor  = Color(0xFF135B90),
        unfocusedTrailingIconColor = Color(0x99FFFFFF),
        focusedLabelColor         = Color(0xFF135B90),
        unfocusedLabelColor       = Color(0x99FFFFFF),
        cursorColor               = Color(0xFF135B90),
        focusedTextColor          = Color.White,
        unfocusedTextColor        = Color.White
    )

    Box(
        modifier = Modifier.fillMaxSize().background(bg).padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Gesport",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Gesport",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFE7F1FF),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
            )
            Spacer(Modifier.height(6.dp))
            Text("Crea tu cuenta", color = Color(0x99FFFFFF), fontSize = 14.sp)

            Spacer(Modifier.height(28.dp))

            // ── Nombre ────────────────────────────────────────────────────
            TextField(
                value         = nombre,
                onValueChange = { nombre = it; vm.clearError() },
                placeholder   = { Text("Nombre completo") },
                leadingIcon   = { Icon(Icons.Default.Person, null) },
                singleLine    = true,
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors        = fieldColors
            )

            Spacer(Modifier.height(12.dp))

            // ── Email ─────────────────────────────────────────────────────
            TextField(
                value         = email,
                onValueChange = { email = it; vm.clearError() },
                placeholder   = { Text("Email") },
                leadingIcon   = { Icon(Icons.Default.AlternateEmail, null) },
                singleLine    = true,
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors        = fieldColors
            )

            Spacer(Modifier.height(12.dp))

            // ── Contraseña ────────────────────────────────────────────────
            TextField(
                value         = password,
                onValueChange = { password = it; vm.clearError() },
                placeholder   = { Text("Contraseña") },
                leadingIcon   = { Icon(Icons.Default.Lock, null) },
                trailingIcon  = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                singleLine            = true,
                visualTransformation  = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                shape                 = RoundedCornerShape(14.dp),
                modifier              = Modifier.fillMaxWidth(),
                colors                = fieldColors
            )

            Spacer(Modifier.height(16.dp))

            // ── Error ─────────────────────────────────────────────────────
            if (!vm.errorMsg.isNullOrBlank()) {
                Text(
                    vm.errorMsg ?: "",
                    color     = Color(0xFFFF5555),
                    fontSize  = 12.sp,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
            }

            // ── Botón registrarse ─────────────────────────────────────────
            Button(
                onClick  = { vm.register(nombre, email, password) },
                enabled  = !vm.isLoading,
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(gradient, RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = Color.Transparent,
                    contentColor           = Color.White,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor   = Color(0x66FFFFFF)
                )
            ) {
                if (vm.isLoading) {
                    CircularProgressIndicator(
                        color       = Color.White,
                        strokeWidth = 2.dp,
                        modifier    = Modifier.size(18.dp)
                    )
                } else {
                    Text("Registrarse", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row {
                Text("¿Ya tienes cuenta?", color = Color(0x55FFFFFF))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Inicia sesión",
                    color      = Color(0x88FFFFFF),
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { navController.navigate("login") }
                )
            }

            Spacer(Modifier.height(24.dp))
            Text("© 2025 Gesport", color = Color(0x55FFFFFF), fontSize = 11.sp)
        }
    }
}