package com.example.tiendaretailsur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tiendaretailsur.R
import com.example.tiendaretailsur.data.UsuarioDbHelper
import com.example.tiendaretailsur.model.PrimerIngresoUiState
import com.example.tiendaretailsur.ui.components.BotonPrincipal
import com.example.tiendaretailsur.ui.components.CampoContrasena
import com.example.tiendaretailsur.ui.components.CampoTexto
import com.example.tiendaretailsur.ui.components.TituloScreen
import com.example.tiendaretailsur.ui.theme.Dimens
import com.example.tiendaretailsur.ui.theme.MiRegistroTheme
import com.example.tiendaretailsur.viewmodel.PrimerIngresoViewModel

@Composable
fun PrimerIngresoScreen(
    rutOCorreoUsuario: String,
    dbHelper: UsuarioDbHelper = UsuarioDbHelper(LocalContext.current),
    viewModel: PrimerIngresoViewModel = viewModel(
        factory = viewModelFactory {
            initializer { PrimerIngresoViewModel(dbHelper) }
        }
    ),
    onConfiguracionCompletada: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PrimerIngresoContent(
        uiState = uiState,
        listaPreguntas = viewModel.listaPreguntasSeg,
        onNuevaContrasenaCambia = viewModel::onNuevaContrasenaCambia,
        onConfirmarContrasenaCambia = viewModel::onConfirmarContrasenaCambia,
        onAlternarNuevaContrasenaVisible = viewModel::onAlternarNuevaContrasenaVisible,
        onAlternarConfirmarContrasenaVisible = viewModel::onAlternarConfirmarContrasenaVisible,
        onPreguntaSeleccionada = viewModel::onPreguntaSeleccionada,
        onRespuestaCambia = viewModel::onRespuestaCambia,
        onGuardarClick = { viewModel.onGuardarConfiguracion(rutOCorreoUsuario) },
        onConfiguracionCompletada = onConfiguracionCompletada
    )
}

@Composable
fun PrimerIngresoContent(
    uiState: PrimerIngresoUiState,
    listaPreguntas: List<String> = emptyList(),
    onNuevaContrasenaCambia: (String) -> Unit,
    onConfirmarContrasenaCambia: (String) -> Unit,
    onAlternarNuevaContrasenaVisible: () -> Unit,
    onAlternarConfirmarContrasenaVisible: () -> Unit,
    onPreguntaSeleccionada: (String) -> Unit,
    onRespuestaCambia: (String) -> Unit,
    onGuardarClick: () -> Unit,
    onConfiguracionCompletada: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = Dimens.espacioPantalla,
                    end = Dimens.espacioPantalla,
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TituloScreen(
                titulo = "Activa tu cuenta",
                subtitulo = "Por seguridad, define tu contraseña personal y una pregunta de recuperación antes de comenzar."
            )
            Spacer(modifier = Modifier.height(28.dp))

            // Nueva contraseña
            Text(
                text = "Nueva contraseña",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            CampoContrasena(
                valor = uiState.nuevaContrasena,
                onValorCambia = onNuevaContrasenaCambia,
                etiqueta = "Crea tu contraseña",
                visible = uiState.nuevaContrasenaVisible,
                onAlternarContrasenaVisible = onAlternarNuevaContrasenaVisible,
                error = uiState.errorNuevaContrasena
            )
            Spacer(modifier = Modifier.height(Dimens.espacioInterno))

            // Confirmar contraseña
            Text(
                text = "Confirmar contraseña",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            CampoContrasena(
                valor = uiState.confirmarContrasena,
                onValorCambia = onConfirmarContrasenaCambia,
                etiqueta = "Repite tu contraseña",
                visible = uiState.confirmarContrasenaVisible,
                onAlternarContrasenaVisible = onAlternarConfirmarContrasenaVisible,
                error = uiState.errorConfirmarContrasena
            )
            Spacer(modifier = Modifier.height(Dimens.espacioInterno))

            // Pregunta de seguridad
            var preguntaAbierta by remember { mutableStateOf(false) }
            Text(
                text = "Pregunta de seguridad",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                CampoTexto(
                    valor = uiState.preguntaSeleccionada,
                    onValorCambia = {},
                    etiqueta = "Selecciona una pregunta",
                    icono = R.drawable.ic_persona,
                    error = uiState.errorPregunta
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { preguntaAbierta = true }
                )
                DropdownMenu(
                    expanded = preguntaAbierta,
                    onDismissRequest = { preguntaAbierta = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    listaPreguntas.forEach { pregunta ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = pregunta,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onPreguntaSeleccionada(pregunta)
                                preguntaAbierta = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimens.espacioInterno))

            // Respuesta de seguridad
            Text(
                text = "Respuesta de seguridad",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            CampoTexto(
                valor = uiState.respuestaSeguridad,
                onValorCambia = onRespuestaCambia,
                etiqueta = "Tu respuesta",
                icono = R.drawable.ic_persona,
                error = uiState.errorRespuesta
            )
            Spacer(modifier = Modifier.height(Dimens.espacioInterno))

            uiState.errorGeneral?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            BotonPrincipal(
                texto = "Guardar y continuar",
                onClick = onGuardarClick,
                icono = R.drawable.ic_check,
                modifier = Modifier.fillMaxWidth(),
            )

            if (uiState.configuracionExitosa) {
                AlertDialog(
                    containerColor = MaterialTheme.colorScheme.surface,
                    onDismissRequest = onConfiguracionCompletada,
                    title = {
                        Text(
                            text = "¡Cuenta activada!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Text(
                            text = "Tu contraseña y pregunta de seguridad han sido guardadas correctamente.",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    confirmButton = {
                        Button(onClick = onConfiguracionCompletada) {
                            Text("Continuar")
                        }
                    }
                )
            }

            Spacer(Modifier.height(Dimens.espacioPantalla))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimerIngresoPreview() {
    MiRegistroTheme {
        PrimerIngresoContent(
            uiState = PrimerIngresoUiState(),
            onNuevaContrasenaCambia = {},
            onConfirmarContrasenaCambia = {},
            onAlternarNuevaContrasenaVisible = {},
            onAlternarConfirmarContrasenaVisible = {},
            onPreguntaSeleccionada = {},
            onRespuestaCambia = {},
            onGuardarClick = {}
        )
    }
}