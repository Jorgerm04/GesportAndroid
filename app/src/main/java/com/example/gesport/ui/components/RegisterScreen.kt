@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gesport.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ges_sports.domain.LogicLogin
import com.example.gesport.R

@Composable
fun RegisterScreen(
    navController: NavHostController,
    onForgotPassword: () -> Unit = {}
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }

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
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Nombre de usuario") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
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
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email") },
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
                onValueChange = { password = it },
                placeholder = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
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

            Spacer(Modifier.height(16.dp))

            val gradient = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF0B2843), // 0% - Azul muy oscuro
                    Color(0xFF135B90), // 50% - Azul medio
                    Color(0xFF0B2843)  // 100% - Azul muy oscuro
                )
            )

            Button(
                onClick = { navController.navigate("login") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(gradient, RoundedCornerShape(14.dp)), // <- aquí va el degradado
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // <- para que no tape el degradado
                    contentColor = Color.White
                )
            ) {
                Text("Registrarse", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(10.dp))

            // Error
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
                    text = "¿Ya tienes cuenta?",
                    color = Color(0x55FFFFFF)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Inicia sesión",
                    color = Color(0x88FFFFFF),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
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

@Preview(showBackground = true, backgroundColor = 0xFF0B0E12)
@Composable
private fun PreviewLogin() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        RegisterScreen(rememberNavController())
    }
}
