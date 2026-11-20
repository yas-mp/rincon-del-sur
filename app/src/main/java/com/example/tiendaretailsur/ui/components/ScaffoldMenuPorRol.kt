package com.example.tiendaretailsur.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tiendaretailsur.navigation.Ruta
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldMenuPorRol(
    tituloPantalla: String,
    subtituloPantalla: String = "",
    perfilUsuario: String,
    rutaActual: String,
    onNavegar: (String) -> Unit,
    onCerrarSesion: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    contenido: @Composable (Modifier) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val rol = perfilUsuario.uppercase()

    val opcionesMenu: List<Triple<String, String, androidx.compose.ui.graphics.vector.ImageVector>> = when (rol) {
        "P-01", "JEFA_TIENDA" -> listOf(
            Triple(Ruta.AdminPanel.ruta, "Dashboard Tienda", Icons.Default.Dashboard),
            Triple(Ruta.Perfil.ruta, "Mi Perfil", Icons.Default.Person)
        )
        "P-02", "SUBGERENTE" -> listOf(
            Triple(Ruta.AjustesAdmin.ruta, "Gestión y Cuentas", Icons.Default.Settings),
            Triple(Ruta.Perfil.ruta, "Mi Perfil", Icons.Default.Person)
        )
        "P-03", "SUPERVISOR" -> listOf(
            Triple(Ruta.AdminPanel.ruta, "Supervisión en Terreno", Icons.Default.Dashboard),
            Triple(Ruta.Perfil.ruta, "Mi Perfil", Icons.Default.Person)
        )
        else -> listOf(
            Triple(Ruta.MenuPrincipal.ruta, "Accesos y Conteo", Icons.Default.DoorFront),
            Triple(Ruta.Perfil.ruta, "Mi Perfil", Icons.Default.Person)
        )
    }

    val etiquetaRol = when (rol) {
        "P-01", "JEFA_TIENDA" -> "Jefa de Tienda (P-01)"
        "P-02", "SUBGERENTE" -> "Subgerente (P-02)"
        "P-03", "SUPERVISOR" -> "Supervisora (P-03)"
        else -> "Observador (P-04)"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                val coloresItem = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Rincón del Sur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = etiquetaRol,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                opcionesMenu.forEach { (ruta, etiqueta, icono) ->
                    NavigationDrawerItem(
                        label = { Text(etiqueta) },
                        icon = { Icon(icono, contentDescription = null) },
                        selected = rutaActual == ruta,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavegar(ruta)
                        },
                        colors = coloresItem,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    },
                    colors = coloresItem,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = tituloPantalla,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (subtituloPantalla.isNotBlank()) {
                                Text(
                                    text = subtituloPantalla,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Desplegar menú lateral"
                            )
                        }
                    }
                )
            },
            snackbarHost = snackbarHost
        ) { innerPadding ->
            contenido(Modifier.padding(innerPadding))
        }
    }
}