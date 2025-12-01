@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gesport.ui.auth.LoginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ges_sports.domain.LogicLogin
import com.example.gesport.R
import com.example.gesport.ui.auth.LoginScreen.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    vm: LoginViewModel = viewModel()
) {
    val email by vm.email.observeAsState("")
    val password by vm.password.observeAsState("")
    val showPassword by vm.showPassword.observeAsState(false)
    val error by vm.error.observeAsState("")

    val navigateToHome by vm.navigateToHome.observeAsState(null)
    val navigateToDashboard by vm.navigateToDashboard.observeAsState(null)

    // 👉 Navegar a HOME si no es ADMIN_DEPORTIVO
    LaunchedEffect(navigateToHome) {
        navigateToHome?.let { userName ->
            navController.navigate("home/$userName") {
                popUpTo("login") { inclusive = true }
            }
            vm.onNavigationDone()
        }
    }

    // 👉 Navegar a DASHBOARD si es ADMIN_DEPORTIVO
    LaunchedEffect(navigateToDashboard) {
        navigateToDashboard?.let { userName ->
            navController.navigate("dashboard/$userName") {
                popUpTo("login") { inclusive = true }
            }
            vm.onNavigationDone()
        }
    }

    val bg = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
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
                text = "Gesport",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFFE7F1FF),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Entrena, gestiona, mejora.",
                color = Color(0x99FFFFFF),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(28.dp))

            TextField(
                value = email,
                onValueChange = { vm.setEmail(it) },
                placeholder = { Text("Email o usuario") },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    focusedIndicatorColor = Color(0xFF135B90),
                    unfocusedIndicatorColor = Color(0x334B5563),

                    focusedPlaceholderColor = Color(0xFF135B90),
                    unfocusedPlaceholderColor = Color(0x55FFFFFF),

                    focusedLeadingIconColor = Color(0xFF135B90),
                    unfocusedLeadingIconColor = Color(0x99FFFFFF),

                    focusedLabelColor = Color(0xFF135B90),
                    unfocusedLabelColor = Color(0x99FFFFFF),

                    cursorColor = Color(0xFF135B90),
                    focusedTextColor = Color(0xFFFFFFFF),
                    unfocusedTextColor = Color(0xFFFFFFFF)
                )
            )

            Spacer(Modifier.height(12.dp))

            TextField(
                value = password,
                onValueChange = { vm.setPassword(it) },
                placeholder = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { vm.toggleShowPassword() }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Ocultar" else "Mostrar"
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    focusedIndicatorColor = Color(0xFF135B90),
                    unfocusedIndicatorColor = Color(0x334B5563),

                    focusedPlaceholderColor = Color(0xFF135B90),
                    unfocusedPlaceholderColor = Color(0x55FFFFFF),

                    focusedLeadingIconColor = Color(0xFF135B90),
                    unfocusedLeadingIconColor = Color(0x99FFFFFF),
                    focusedTrailingIconColor = Color(0xFF135B90),
                    unfocusedTrailingIconColor = Color(0x99FFFFFF),

                    focusedLabelColor = Color(0xFF135B90),
                    unfocusedLabelColor = Color(0x99FFFFFF),

                    cursorColor = Color(0xFF135B90),
                    focusedTextColor = Color(0xFFFFFFFF),
                    unfocusedTextColor = Color(0xFFFFFFFF)
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "¿Has olvidado tu contraseña?",
                color = Color(0x55FFFFFF),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.End)
            )

            Spacer(Modifier.height(16.dp))

            val gradient = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF0B2843),
                    Color(0xFF135B90),
                    Color(0xFF0B2843)
                )
            )

            Button(
                onClick = { vm.login() },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(gradient, RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Text("Iniciar sesión", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(10.dp))

            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(16.dp))

            Row {
                Text(
                    text = "¿No tienes cuenta?",
                    color = Color(0x55FFFFFF)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Registrate",
                    color = Color(0x88FFFFFF),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("register")
                        }
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "© 2025 Gesport",
                color = Color(0x55FFFFFF),
                fontSize = 11.sp
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLogin() {
    LoginScreen(rememberNavController())
}
