package com.example.gesport.ui.dashboard.GesUserScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gesport.data.DataUserRepository
import com.example.gesport.models.User
import com.example.gesport.models.UserRoles

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GesUserScreen(
    navController: NavHostController
) {
    val viewModel: GesUserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = DataUserRepository
                return GesUserViewModel(repo) as T
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    val users = viewModel.users
    val selectedRole = viewModel.selectedRole
    val searchQuery = viewModel.searchQuery

    var userToDelete by remember { mutableStateOf<User?>(null) }

    val bg = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B0E12), Color(0xFF12171E))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Usuarios",
                            color = Color(0xFFE7F1FF),
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("formuser") },
                    containerColor = Color(0xFF135B90),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir usuario"
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 🔍 Buscador por nombre / email
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    placeholder = { Text("Buscar por nombre o email") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF135B90),
                        unfocusedIndicatorColor = Color(0x334B5563),
                        focusedPlaceholderColor = Color(0x55FFFFFF),
                        unfocusedPlaceholderColor = Color(0x55FFFFFF),
                        focusedLeadingIconColor = Color(0xFF135B90),
                        unfocusedLeadingIconColor = Color(0x99FFFFFF),
                        cursorColor = Color(0xFF135B90),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Filtros por rol
                Text(
                    text = "Filtrar por rol",
                    color = Color(0xCCFFFFFF),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    FilterChip(
                        selected = selectedRole == null,
                        onClick = { viewModel.onRoleSelected(null) },
                        label = { Text("TODOS") },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF0B0E12),
                            labelColor = Color.White,
                            selectedContainerColor = Color(0xFF135B90),
                            selectedLabelColor = Color.White
                        ),
                        border = null
                    )

                    UserRoles.allRoles.forEach { (roleKey, roleLabel) ->
                        FilterChip(
                            selected = selectedRole == roleKey,
                            onClick = {
                                val newRole = if (selectedRole == roleKey) null else roleKey
                                viewModel.onRoleSelected(newRole)
                            },
                            label = { Text(roleLabel) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFF0B0E12),
                                labelColor = Color.White,
                                selectedContainerColor = Color(0xFF135B90),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Listado de usuarios
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(users) { user ->
                        UserListItem(
                            user = user,
                            onEdit = {
                                navController.navigate("formuser/${user.id}")
                            },
                            onDelete = {
                                userToDelete = user
                            }
                        )
                    }
                }
            }
        }

        if (userToDelete != null) {
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                title = {
                    Text(
                        text = "Eliminar usuario",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar a ${userToDelete!!.nombre}?",
                        color = Color.White
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteUser(userToDelete!!.id)
                            userToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = Color(0xFFFF5555))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) {
                        Text("Cancelar", color = Color.White)
                    }
                },
                containerColor = Color(0xFF0B0E12)
            )
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cardGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0B2843),
            Color(0xFF135B90),
            Color(0xFF0B2843)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = user.nombre,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = user.rol,
                        color = Color(0xCCFFFFFF),
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar usuario",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar usuario",
                        tint = Color(0xFFFF5555)
                    )
                }
            }
        }
    }
}
