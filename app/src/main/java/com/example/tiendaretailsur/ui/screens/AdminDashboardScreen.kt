package com.example.tiendaretailsur.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiendaretailsur.R
import com.example.tiendaretailsur.model.AdminDashboardUiState
import com.example.tiendaretailsur.model.DatoGrafico
import com.example.tiendaretailsur.model.SesionResumen
import com.example.tiendaretailsur.ui.theme.MiRegistroTheme
import com.example.tiendaretailsur.viewmodel.AdminDashboardViewModel
import com.example.tiendaretailsur.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
    esSupervisor: Boolean = false,
    mostrarAjustesAdmin: Boolean = false,
    adminViewModel: AdminDashboardViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel(),
    onVolverInicio: () -> Unit = {},
    onAjustes: () -> Unit = {},
    onCerrarSesionClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        adminViewModel.cargarDatosDashboard()
    }
    val uiState by adminViewModel.uiState.collectAsStateWithLifecycle()
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsStateWithLifecycle()

    AdminDashboardContent(
        uiState = uiState,
        esSupervisor = esSupervisor,
        mostrarAjustesAdmin = mostrarAjustesAdmin,
        nombreUsuario = usuarioActual?.nombreCompleto ?: if (esSupervisor) "Supervisor" else "Jefa de tienda",
        onVolverInicio = onVolverInicio,
        onAjustes = onAjustes,
        onCerrarSesion = {
            usuarioViewModel.cerrarSesion()
            onCerrarSesionClick()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    uiState: AdminDashboardUiState,
    esSupervisor: Boolean = false,
    mostrarAjustesAdmin: Boolean = false,
    nombreUsuario: String = "Jefa de tienda",
    onVolverInicio: () -> Unit = {},
    onAjustes: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                val coloresItemDrawer = NavigationDrawerItemDefaults.colors(
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
                        text = if (esSupervisor) "Supervisión en Terreno" else "Panel Administrativo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                NavigationDrawerItem(
                    label = { Text(if (esSupervisor) "Supervisión" else "Reporte de flujo") },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVolverInicio()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = coloresItemDrawer
                )
                NavigationDrawerItem(
                    label = { Text(if (mostrarAjustesAdmin) "Ajustes Administrador" else "Mi Perfil") },
                    icon = {
                        Icon(
                            imageVector = if (mostrarAjustesAdmin) Icons.Default.Settings else Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onAjustes()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = coloresItemDrawer
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = coloresItemDrawer
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
                                text = if (esSupervisor) "Supervisión en Terreno" else "Rincón del Sur",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bienvenido, $nombreUsuario",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
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
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(15.dp))

                if (esSupervisor) {
                    val totalPersonasHistorial = uiState.listaSesiones.sumOf { it.conteo }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TarjetaResumen(
                            titulo = "Sesiones Hoy",
                            valor = "${uiState.listaSesiones.size}",
                            icono = Icons.Default.Groups,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaResumen(
                            titulo = "Aforo Contado",
                            valor = "$totalPersonasHistorial pers.",
                            icono = Icons.Default.People,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TarjetaResumen(
                            titulo = "Total Visitas",
                            valor = "${uiState.totalVisitas}",
                            icono = Icons.Default.People,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaResumen(
                            titulo = "En Terreno",
                            valor = "${uiState.observadoresActivos} Activos",
                            icono = Icons.Default.Store,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    val totalSegmentos = uiState.segmentacion.sumOf { it.valor }.coerceAtLeast(1)
                    val transitoVal = uiState.segmentacion.find {
                        it.etiqueta.contains("Tránsito", ignoreCase = true) ||
                                it.etiqueta.contains("Transito", ignoreCase = true)
                    }?.valor ?: 0
                    val clientesVal = uiState.segmentacion.find {
                        it.etiqueta.contains("Clientes", ignoreCase = true)
                    }?.valor ?: 0

                    val pctTransito = transitoVal.toFloat() / totalSegmentos
                    val pctClientes = clientesVal.toFloat() / totalSegmentos

                    MetricasDeFlujo(
                        clientes = if (totalSegmentos > 1) pctTransito else 0.5f,
                        visita = if (totalSegmentos > 1) pctClientes else 0.5f
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    GraficoFlujo(datos = uiState.flujoPorHora)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.historial),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Historial Reciente de Conteos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                uiState.listaSesiones.forEach { sesion ->
                    ItemSesionResumen(sesion = sesion)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TarjetaResumen(
    titulo: String,
    valor: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun GraficoFlujo(datos: List<DatoGrafico> = emptyList()) {
    val colorBarra1 = MaterialTheme.colorScheme.primary
    val colorBarra2 = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val coloresBarras = listOf(colorBarra1, colorBarra2)

    val total = datos.sumOf { it.valor }
    val valorMaximo = datos.maxOfOrNull { it.valor }?.toFloat() ?: 100f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Flujo por Turno",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        datos.forEachIndexed { index, dato ->
                            val colorBarra = coloresBarras[index % coloresBarras.size]

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = "${dato.valor}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .fillMaxHeight((dato.valor / valorMaximo).coerceIn(0.15f, 1f))
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colorBarra)
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        datos.forEach { dato ->
                            Text(
                                text = dato.etiqueta,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Total: $total",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun ItemSesionResumen(sesion: SesionResumen) {
    val estadoTexto = if (sesion.estado == 1) "Finalizada" else "En Curso"
    val estadoColor = if (sesion.estado == 1) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sesion.observador,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${sesion.conteo} personas · ${sesion.hora}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
            Surface(
                color = estadoColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = estadoTexto,
                    style = MaterialTheme.typography.labelMedium,
                    color = estadoColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MetricasDeFlujo(
    clientes: Float = 0.54f,
    visita: Float = 0.46f
) {
    val pesoClientes = clientes.coerceAtLeast(0.01f)
    val pesoVisita = visita.coerceAtLeast(0.01f)
    val colorClientes = MaterialTheme.colorScheme.primary
    val colorVisita = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Segmentación de flujo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "General",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Tránsito (Pasillo)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(clientes * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Clientes / Visita",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(visita * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier
                        .weight(pesoClientes)
                        .fillMaxHeight()
                        .background(colorClientes)
                )
                Box(
                    modifier = Modifier
                        .weight(pesoVisita)
                        .fillMaxHeight()
                        .background(colorVisita)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    MiRegistroTheme(darkTheme = false) {
        AdminDashboardContent(
            uiState = AdminDashboardUiState(
                totalVisitas = 125,
                observadoresActivos = 3,
                flujoPorHora = listOf(
                    DatoGrafico("Turno Mañana", 70),
                    DatoGrafico("Turno Tarde", 55)
                ),
                listaSesiones = listOf(
                    SesionResumen(1, "Juan Pérez", "13:30 hrs", 45, 1),
                    SesionResumen(2, "Carlos Rodríguez", "17:30 hrs", 20, 0)
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SupervisorDashboardPreview() {
    MiRegistroTheme(darkTheme = false) {
        AdminDashboardContent(
            uiState = AdminDashboardUiState(
                listaSesiones = listOf(
                    SesionResumen(1, "Juan Pérez", "13:30 hrs", 45, 1),
                    SesionResumen(2, "Carlos Rodríguez", "17:30 hrs", 20, 0)
                )
            ),
            esSupervisor = true,
            nombreUsuario = "Supervisora"
        )
    }
}