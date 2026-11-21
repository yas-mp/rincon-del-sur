package com.example.tiendaretailsur.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiendaretailsur.model.AjustesAdminUiState
import com.example.tiendaretailsur.model.ConfiguracionAcceso
import com.example.tiendaretailsur.model.SeccionAjustesAdmin
import com.example.tiendaretailsur.model.SolicitudCorreo
import com.example.tiendaretailsur.model.Usuario
import com.example.tiendaretailsur.permisos.Permisos
import com.example.tiendaretailsur.ui.theme.Exito
import com.example.tiendaretailsur.viewmodel.AjustesAdminViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesAdminScreen(
    seccionInicial: SeccionAjustesAdmin = SeccionAjustesAdmin.COLABORADORES,
    viewModel: AjustesAdminViewModel = viewModel(),
    onVolverClick: () -> Unit = {},
    onIrAMiPerfilClick: () -> Unit = {},
    onIrARegistroColaborador: () -> Unit = {},
    onCerrarSesionClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(seccionInicial) {
        viewModel.seleccionarSeccion(seccionInicial)
        viewModel.recargarDatos()
    }

    LaunchedEffect(uiState.mensajeConfirmacion) {
        uiState.mensajeConfirmacion?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.limpiarMensaje()
        }
    }

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
                        text = "Tienda Retail Sur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Subgerencia y Administración",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Gestión de usuarios") },
                    icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    colors = coloresItemDrawer,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onIrAMiPerfilClick()
                    },
                    colors = coloresItemDrawer,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onCerrarSesionClick()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = coloresItemDrawer
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Gestión de usuarios y accesos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Gestión de perfiles, usuarios y tienda",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú lateral"
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
            ) {
                TabRow(
                    selectedTabIndex = uiState.seccionActual.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    SeccionAjustesAdmin.entries.forEach { seccion ->
                        val icono = when (seccion) {
                            SeccionAjustesAdmin.COLABORADORES -> Icons.Default.People
                            SeccionAjustesAdmin.TIENDA -> Icons.Default.Store
                        }
                        Tab(
                            selected = uiState.seccionActual == seccion,
                            onClick = { viewModel.seleccionarSeccion(seccion) },
                            text = { Text(seccion.titulo) },
                            icon = { Icon(icono, contentDescription = null) }
                        )
                    }
                }
                when (uiState.seccionActual) {
                    SeccionAjustesAdmin.COLABORADORES -> {
                        SeccionColaboradoresContent(
                            uiState = uiState,
                            onBusquedaCambia = viewModel::onBusquedaCambia,
                            onFiltroPerfilCambia = viewModel::onFiltroPerfilCambia,
                            onAlternarActivo = viewModel::alternarEstadoUsuario,
                            onEditarClick = viewModel::abrirEditorUsuario,
                            onIrARegistroColaborador = onIrARegistroColaborador,
                            onResolverSolicitud = viewModel::resolverSolicitudCorreo
                        )
                    }
                    SeccionAjustesAdmin.TIENDA -> {
                        SeccionTiendaYCuentaContent(
                            metaConversionTexto = uiState.metaConversionTexto,
                            onMetaCambia = viewModel::onMetaConversionCambia,
                            onGuardarMeta = viewModel::guardarMetaConversion,
                            accesos = uiState.accesos,
                            onAlternarAcceso = viewModel::alternarEstadoAcceso,
                            onAgregarAccesoClick = viewModel::abrirNuevoAcceso,
                            onEditarAccesoClick = viewModel::abrirEditorAcceso,
                            onIrAMiPerfilClick = onIrAMiPerfilClick
                        )
                    }
                }
            }
            if (uiState.usuarioEnEdicion != null) {
                DialogoEditarUsuario(
                    uiState = uiState,
                    onDismiss = viewModel::cerrarEditorUsuario,
                    onNombreCambia = viewModel::onEditNombreCambia,
                    onRutCambia = viewModel::onEditRutCambia,
                    onCorreoCambia = viewModel::onEditCorreoCambia,
                    onTelefonoCambia = viewModel::onEditTelefonoCambia,
                    onCargoCambia = viewModel::onEditCargoCambia,
                    onPerfilCambia = viewModel::onEditPerfilCambia,
                    onActivoCambia = viewModel::onEditActivoCambia,
                    onNuevaContrasenaCambia = viewModel::onEditNuevaContrasenaCambia,
                    onGuardar = viewModel::guardarCambiosUsuario
                )
            }
            if (uiState.perfilEnEdicion != null) {
                DialogoEditarPerfil(
                    uiState = uiState,
                    onDismiss = viewModel::cerrarEditorPerfil,
                    onNombreCambia = viewModel::onEditPerfilNombreCambia,
                    onDescripcionCambia = viewModel::onEditPerfilDescripcionCambia,
                    onHabilitadoCambia = viewModel::onEditPerfilHabilitadoCambia,
                    onAplicarATodosCambia = viewModel::onAplicarATodosCambia,
                    onGuardar = viewModel::guardarCambiosPerfil
                )
            }
            if (uiState.mostrandoDialogoAcceso) {
                DialogoFormularioAcceso(
                    uiState = uiState,
                    onDismiss = viewModel::cerrarDialogoAcceso,
                    onCodigoCambia = viewModel::onEditAccesoCodigoCambia,
                    onNombreCambia = viewModel::onEditAccesoNombreCambia,
                    onDescripcionCambia = viewModel::onEditAccesoDescripcionCambia,
                    onHabilitadoCambia = viewModel::onEditAccesoHabilitadoCambia,
                    onGuardar = viewModel::guardarAcceso
                )
            }
        }
    }
}

private fun formatearNombrePerfil(codigo: String): String = when (codigo.trim().uppercase()) {
    "TODOS" -> "Todos los perfiles"
    "P-01", "JEFA_TIENDA", "JEFA DE TIENDA" -> "Jefa de Tienda"
    "P-02", "SUBGERENTE", "ADMINISTRADOR" -> "Subgerente"
    "P-03", "SUPERVISOR", "SUPERVISORA" -> "Supervisor"
    "P-04", "OBSERVADOR" -> "Observador"
    else -> codigo
}

@Composable
private fun SeccionColaboradoresContent(
    uiState: AjustesAdminUiState,
    onBusquedaCambia: (String) -> Unit,
    onFiltroPerfilCambia: (String) -> Unit,
    onAlternarActivo: (Usuario, Boolean) -> Unit,
    onEditarClick: (Usuario) -> Unit,
    onIrARegistroColaborador: () -> Unit,
    onResolverSolicitud: (SolicitudCorreo, Boolean) -> Unit
) {
    val usuariosFiltrados = uiState.usuarios.filter { u ->
        val coincideTexto = uiState.busqueda.isBlank() ||
                u.nombreCompleto.contains(uiState.busqueda, ignoreCase = true) ||
                u.correo.contains(uiState.busqueda, ignoreCase = true) ||
                u.rut.contains(uiState.busqueda, ignoreCase = true)
        val coincidePerfil = uiState.filtroPerfil == "TODOS" ||
                formatearNombrePerfil(u.perfil).equals(formatearNombrePerfil(uiState.filtroPerfil), ignoreCase = true)
        coincideTexto && coincidePerfil
    }

    val totalActivos = uiState.usuarios.count { it.activo }
    val totalInactivos = uiState.usuarios.size - totalActivos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaContadorAdmin(
                titulo = "Total Cuentas",
                valor = "${uiState.usuarios.size}",
                modifier = Modifier.weight(1f)
            )
            TarjetaContadorAdmin(
                titulo = "Habilitados",
                valor = "$totalActivos",
                modifier = Modifier.weight(1f)
            )
            TarjetaContadorAdmin(
                titulo = "Inactivos",
                valor = "$totalInactivos",
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = uiState.busqueda,
            onValueChange = onBusquedaCambia,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por nombre, correo o RUT...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val opciones = listOf("TODOS", "P-01", "P-02", "P-03", "P-04")
            opciones.forEach { codigo ->
                FilterChip(
                    selected = uiState.filtroPerfil == codigo,
                    onClick = { onFiltroPerfilCambia(codigo) },
                    label = { Text(formatearNombrePerfil(codigo)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
        Button(
            onClick = onIrARegistroColaborador,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrar Nuevo Colaborador", fontWeight = FontWeight.Bold)
        }

        if (uiState.solicitudesCorreo.isNotEmpty()) {
            Text(
                text = "Solicitudes de Cambio de Correo (${uiState.solicitudesCorreo.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            uiState.solicitudesCorreo.forEach { solicitud ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${solicitud.nombreUsuario} (${solicitud.rutUsuario})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Actual: ${solicitud.correoActual}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Nuevo solicitado: ${solicitud.correoNuevo}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (solicitud.motivo.isNotBlank()) {
                            Text(
                                text = "Motivo: ${solicitud.motivo}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onResolverSolicitud(solicitud, false) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Rechazar")
                            }
                            Button(
                                onClick = { onResolverSolicitud(solicitud, true) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Aprobar")
                            }
                        }
                    }
                }
            }
        }

        usuariosFiltrados.forEach { usuario ->
            val perfilHabilitado = uiState.perfiles.find { it.nombre == usuario.perfil || it.codigo == usuario.perfil }?.habilitado ?: true
            TarjetaColaboradorItem(
                usuario = usuario,
                perfilGlobalHabilitado = perfilHabilitado,
                onAlternarActivo = { nuevoEstado -> onAlternarActivo(usuario, nuevoEstado) },
                onEditarClick = { onEditarClick(usuario) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TarjetaContadorAdmin(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    valorColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valorColor
            )
        }
    }
}

@Composable
private fun TarjetaColaboradorItem(
    usuario: Usuario,
    perfilGlobalHabilitado: Boolean,
    onAlternarActivo: (Boolean) -> Unit,
    onEditarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    color = if (usuario.activo && perfilGlobalHabilitado) Exito else MaterialTheme.colorScheme.error,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = usuario.nombreCompleto,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "RUT: ${usuario.rut}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = formatearNombrePerfil(usuario.perfil),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            Text(
                text = "Correo: ${usuario.correo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Teléfono: +56 ${usuario.telefono}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!perfilGlobalHabilitado) {
                Text(
                    text = "Aviso: El perfil ${formatearNombrePerfil(usuario.perfil)} está deshabilitado globalmente",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = usuario.activo,
                        onCheckedChange = onAlternarActivo
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (usuario.activo) "Habilitado" else "Deshabilitado",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (usuario.activo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                    )
                }

                OutlinedButton(onClick = onEditarClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Modificar datos")
                }
            }
        }
    }
}

@Composable
private fun SeccionTiendaYCuentaContent(
    metaConversionTexto: String,
    onMetaCambia: (String) -> Unit,
    onGuardarMeta: () -> Unit,
    accesos: List<ConfiguracionAcceso>,
    onAlternarAcceso: (String, Boolean) -> Unit,
    onAgregarAccesoClick: () -> Unit,
    onEditarAccesoClick: (ConfiguracionAcceso) -> Unit,
    onIrAMiPerfilClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Cuenta de Administrador y Dispositivo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Actualiza tu fotografía de perfil o gestiona los permisos de cámara y almacenamiento del teléfono.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onIrAMiPerfilClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mi Perfil")
                    }
                    OutlinedButton(
                        onClick = { Permisos.abrirAjustes(context) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Permisos App")
                    }
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Meta Mensual de Conversión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Define el porcentaje objetivo de boletas sobre ingresos totales para comparar los reportes semanales.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = metaConversionTexto,
                        onValueChange = onMetaCambia,
                        label = { Text("Meta (%)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = onGuardarMeta) {
                        Text("Guardar Meta")
                    }
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Estado de Accesos de la Tienda",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Agrega, modifica o marca como cerrado un acceso por mantención o evento.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    Button(
                        onClick = onAgregarAccesoClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                accesos.forEach { acceso ->
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${acceso.codigo} - ${acceso.nombre}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = acceso.ubicacion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onEditarAccesoClick(acceso) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Modificar acceso",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Switch(
                                checked = acceso.habilitado,
                                onCheckedChange = { onAlternarAcceso(acceso.codigo, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun DialogoEditarUsuario(
    uiState: AjustesAdminUiState,
    onDismiss: () -> Unit,
    onNombreCambia: (String) -> Unit,
    onRutCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onTelefonoCambia: (String) -> Unit,
    onCargoCambia: (String) -> Unit,
    onPerfilCambia: (String) -> Unit,
    onActivoCambia: (Boolean) -> Unit,
    onNuevaContrasenaCambia: (String) -> Unit,
    onGuardar: () -> Unit
) {
    val rolesDisponibles = listOf(
        "P-01" to "Jefa de Tienda",
        "P-02" to "Subgerente",
        "P-03" to "Supervisor",
        "P-04" to "Observador"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar Colaborador", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.errorEdicionUsuario?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = uiState.editNombre,
                    onValueChange = onNombreCambia,
                    label = { Text("Nombre Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.editRut,
                    onValueChange = onRutCambia,
                    label = { Text("RUT") },
                    singleLine = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.editCorreo,
                    onValueChange = onCorreoCambia,
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.editTelefono,
                    onValueChange = onTelefonoCambia,
                    label = { Text("Teléfono") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Rol / Perfil de Acceso",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    rolesDisponibles.chunked(2).forEach { filaRoles ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filaRoles.forEach { (codigo, nombreRol) ->
                                val seleccionado = formatearNombrePerfil(uiState.editPerfil)
                                    .equals(nombreRol, ignoreCase = true)

                                FilterChip(
                                    selected = seleccionado,
                                    onClick = {
                                        onPerfilCambia(codigo)
                                        onCargoCambia(nombreRol)
                                    },
                                    label = { Text(nombreRol) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Switch(checked = uiState.editActivo, onCheckedChange = onActivoCambia)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uiState.editActivo) "Usuario Activo" else "Usuario Inactivo")
                }
            }
        },
        confirmButton = {
            Button(onClick = onGuardar) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DialogoEditarPerfil(
    uiState: AjustesAdminUiState,
    onDismiss: () -> Unit,
    onNombreCambia: (String) -> Unit,
    onDescripcionCambia: (String) -> Unit,
    onHabilitadoCambia: (Boolean) -> Unit,
    onAplicarATodosCambia: (Boolean) -> Unit,
    onGuardar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Perfil ${uiState.perfilEnEdicion?.codigo}") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = uiState.editPerfilNombre,
                    onValueChange = onNombreCambia,
                    label = { Text("Nombre del Perfil") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.editPerfilDescripcion,
                    onValueChange = onDescripcionCambia,
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Switch(checked = uiState.editPerfilHabilitado, onCheckedChange = onHabilitadoCambia)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uiState.editPerfilHabilitado) "Perfil Habilitado" else "Perfil Deshabilitado")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = uiState.aplicarEstadoATodosLosUsuariosDelPerfil, onCheckedChange = onAplicarATodosCambia)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aplicar estado a todos los usuarios de este perfil", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

}
@Composable
private fun DialogoFormularioAcceso(
    uiState: AjustesAdminUiState,
    onDismiss: () -> Unit,
    onCodigoCambia: (String) -> Unit,
    onNombreCambia: (String) -> Unit,
    onDescripcionCambia: (String) -> Unit,
    onHabilitadoCambia: (Boolean) -> Unit,
    onGuardar: () -> Unit
) {
    val esEdicion = uiState.accesoEnEdicion != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (esEdicion) "Modificar Punto de Acceso" else "Agregar Punto de Acceso",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = uiState.editAccesoCodigo,
                    onValueChange = onCodigoCambia,
                    label = { Text("Código (ej: A5)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.editAccesoNombre,
                    onValueChange = onNombreCambia,
                    label = { Text("Nombre de la puerta") },
                    placeholder = { Text("Ej: Acceso Norte / Estacionamiento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.editAccesoDescripcion,
                    onValueChange = onDescripcionCambia,
                    label = { Text("Descripción / Ubicación") },
                    placeholder = { Text("Ej: Entrada sector bodega") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.editAccesoHabilitado) "Acceso operativo" else "Acceso bloqueado",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = uiState.editAccesoHabilitado,
                        onCheckedChange = onHabilitadoCambia
                    )
                }
                if (uiState.errorEdicionAcceso != null) {
                    Text(
                        text = uiState.errorEdicionAcceso,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onGuardar) {
                Text(if (esEdicion) "Guardar cambios" else "Crear acceso")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
