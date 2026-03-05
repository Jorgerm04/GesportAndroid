@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gesport.ui.auth.LoginScreen

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
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gesport.R

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(context))

    val email           by vm.email.observeAsState("")
    val password        by vm.password.observeAsState("")
    val showPassword    by vm.showPassword.observeAsState(false)
    val error           by vm.error.observeAsState("")
    val navigateToHome  by vm.navigateToHome.observeAsState(null)
    val navigateToDash  by vm.navigateToDashboard.observeAsState(null)

    LaunchedEffect(navigateToHome) {
        navigateToHome?.let { dest ->
            navController.navigate("home/${dest.userId}/${dest.nombre}/${dest.rol}") {
                popUpTo("login") { inclusive = true }
            }
            vm.onNavigationDone()
        }
    }
    LaunchedEffect(navigateToDash) {
        navigateToDash?.let { dest ->
            navController.navigate("dashboard/${dest.userId}/${dest.nombre}/${dest.rol}") {
                popUpTo("login") { inclusive = true }
            }
            vm.onNavigationDone()
        }
    }

    val bg = Brush.verticalGradient(colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E)))

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
                    contentDescription = "Logo",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("Gesport",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFE7F1FF), fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp))
            Spacer(Modifier.height(6.dp))
            Text("Entrena, gestiona, mejora.", color = Color(0x99FFFFFF), fontSize = 14.sp)
            Spacer(Modifier.height(28.dp))

            val fieldColors = TextFieldDefaults.colors(
                focusedContainerColor     = Color.Transparent,
                unfocusedContainerColor   = Color.Transparent,
                focusedIndicatorColor     = Color(0xFF135B90),
                unfocusedIndicatorColor   = Color(0x334B5563),
                focusedPlaceholderColor   = Color(0xFF135B90),
                unfocusedPlaceholderColor = Color(0x55FFFFFF),
                focusedLeadingIconColor   = Color(0xFF135B90),
                unfocusedLeadingIconColor = Color(0x99FFFFFF),
                cursorColor               = Color(0xFF135B90),
                focusedTextColor          = Color.White,
                unfocusedTextColor        = Color.White
            )

            TextField(
                value = email, onValueChange = { vm.setEmail(it) },
                placeholder = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, null) },
                singleLine = true, shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(), colors = fieldColors
            )

            Spacer(Modifier.height(12.dp))

            TextField(
                value = password, onValueChange = { vm.setPassword(it) },
                placeholder = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { vm.toggleShowPassword() }) {
                        Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(), colors = fieldColors
            )

            Spacer(Modifier.height(8.dp))
            Text("¿Has olvidado tu contraseña?", color = Color(0x55FFFFFF),
                fontSize = 12.sp, modifier = Modifier.align(Alignment.End))
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.login() },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF0B2843), Color(0xFF135B90), Color(0xFF0B2843))),
                        RoundedCornerShape(14.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
            ) {
                Text("Iniciar sesión", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(10.dp))

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(16.dp))

            Row {
                Text("¿No tienes cuenta?", color = Color(0x55FFFFFF))
                Spacer(Modifier.width(8.dp))
                Text("Regístrate", color = Color(0x88FFFFFF), fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { navController.navigate("register") })
            }

            Spacer(Modifier.height(24.dp))
            Text("© 2025 Gesport", color = Color(0x55FFFFFF), fontSize = 11.sp)
        }
    }
}

@Preview
@Composable
private fun PreviewLogin() { LoginScreen(rememberNavController()) }