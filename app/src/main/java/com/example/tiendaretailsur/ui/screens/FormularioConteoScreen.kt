package com.example.tiendaretailsur.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiendaretailsur.model.FormularioConteoUiState
import com.example.tiendaretailsur.viewmodel.FormularioConteoViewModel

@Composable
fun FormularioConteoScreen(
    puertaInicial: String = "A1",
    nombreObservador: String = "",
    modifier: Modifier = Modifier,
    viewModel: FormularioConteoViewModel = viewModel(),
    onVolver: () -> Unit = {}
) {
    LaunchedEffect(puertaInicial) {
        viewModel.setPuerta(puertaInicial)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val estaPuertaBloqueada = viewModel.estaPuertaActualBloqueada()

    FormularioConteoContent(
        uiState = uiState,
        estaBloqueada = estaPuertaBloqueada,
        modifier = modifier,
        onIncrementarTransito = { viewModel.incrementarTransito(1) },
        onDecrementarTransito = viewModel::decrementarTransito,
        onIncrementarClientes = { cantidad -> viewModel.incrementarClientes(cantidad) },
        onDecrementarClientes = viewModel::decrementarClientes,
        onSeleccionarEdad = viewModel::seleccionarRangoEtario,
        onSeleccionarCompania = viewModel::seleccionarCompania,
        onEnviar = { viewModel.enviarFormulario(nombreObservador) },
        onLimpiarFeedback = viewModel::limpiarMensajeFeedback,
        onVolver = onVolver
    )
}

@Composable
fun FormularioConteoContent(
    uiState: FormularioConteoUiState,
    estaBloqueada: Boolean = false,
    modifier: Modifier = Modifier,
    onIncrementarTransito: () -> Unit,
    onDecrementarTransito: () -> Unit,
    onIncrementarClientes: (Int) -> Unit,
    onDecrementarClientes: () -> Unit,
    onSeleccionarEdad: (String) -> Unit,
    onSeleccionarCompania: (String) -> Unit,
    onEnviar: () -> Unit,
    onLimpiarFeedback: () -> Unit = {},
    onVolver: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var cantidadGrupo by remember { mutableIntStateOf(1) }

    val coloresChip = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
        selectedLabelColor = MaterialTheme.colorScheme.primary
    )

    LaunchedEffect(uiState.compania) {
        cantidadGrupo = when (uiState.compania) {
            "Pareja" -> 2
            "Con niños", "Grupo" -> 3
            else -> 1
        }
    }
    LaunchedEffect(uiState.mensajeFeedback) {
        val mensaje = uiState.mensajeFeedback
        if (!mensaje.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = mensaje,
                duration = SnackbarDuration.Short
            )
            onLimpiarFeedback()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Registro de Aforo en Vivo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = if (estaBloqueada) {
                            MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Puerta Activa: ${uiState.puertaAcceso}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (estaBloqueada) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                OutlinedButton(
                    onClick = onVolver,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Volver al Menú",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            if (estaBloqueada) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Este acceso se encuentra CERRADO por administración (mantención/evento). No se pueden enviar conteos.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tránsito (Solo Atajo)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Personas que cruzaron por la tienda sin comprar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = "${uiState.cantidadTransito}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDecrementarTransito,
                            enabled = !estaBloqueada && uiState.puedeRestarTransito,
                            border = BorderStroke(
                                1.dp,
                                if (!estaBloqueada && uiState.puedeRestarTransito) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Restar (-1)")
                        }
                        Button(
                            onClick = onIncrementarTransito,
                            enabled = !estaBloqueada,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sumar (+1)")
                        }
                    }
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cliente (Compra)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Selecciona rango etario y compañía antes de sumar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = "${uiState.cantidadClientes}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (uiState.compania in listOf("Pareja", "Con niños", "Grupo")) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Personas:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            listOf(1, 2, 3, 4).forEach { cant ->
                                FilterChip(
                                    selected = cantidadGrupo == cant,
                                    onClick = { cantidadGrupo = cant },
                                    colors = coloresChip,
                                    label = { Text(if (cant == 4) "+4" else "+$cant") }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDecrementarClientes,
                            enabled = !estaBloqueada && uiState.puedeRestarCliente,
                            border = BorderStroke(
                                1.dp,
                                if (!estaBloqueada && uiState.puedeRestarCliente) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Restar (-1)")
                        }
                        Button(
                            onClick = {
                                onIncrementarClientes(cantidadGrupo)
                                cantidadGrupo = 1
                            },
                            enabled = !estaBloqueada,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sumar (+$cantidadGrupo)")
                        }
                    }
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Rango Etario Estimado *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("18–25", "26–40", "41–60", "61+").forEach { rango ->
                            FilterChip(
                                enabled = !estaBloqueada,
                                modifier = Modifier.weight(1f),
                                selected = uiState.rangoEtario == rango,
                                onClick = { onSeleccionarEdad(rango) },
                                colors = coloresChip,
                                label = {
                                    Text(
                                        text = rango,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            )
                        }
                    }
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tipo de Compañía *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Solo", "Pareja").forEach { comp ->
                                FilterChip(
                                    enabled = !estaBloqueada,
                                    modifier = Modifier.weight(1f),
                                    selected = uiState.compania == comp,
                                    onClick = { onSeleccionarCompania(comp) },
                                    colors = coloresChip,
                                    label = {
                                        Text(
                                            text = comp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Con niños", "Grupo").forEach { comp ->
                                FilterChip(
                                    enabled = !estaBloqueada,
                                    modifier = Modifier.weight(1f),
                                    selected = uiState.compania == comp,
                                    onClick = { onSeleccionarCompania(comp) },
                                    colors = coloresChip,
                                    label = {
                                        Text(
                                            text = comp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onEnviar,
                enabled = !estaBloqueada,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Enviar Registro de Aforo", style = MaterialTheme.typography.titleMedium)
            }

            if (estaBloqueada) {
                AlertDialog(
                    onDismissRequest = onVolver,
                    title = { Text("Acceso Inhabilitado") },
                    text = { Text("La puerta ${uiState.puertaAcceso} ha sido cerrada temporalmente por administración. Por favor selecciona otra puerta.") },
                    confirmButton = {
                        Button(onClick = onVolver) {
                            Text("Volver a la Lista")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}