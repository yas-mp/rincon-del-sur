package com.example.tiendaretailsur.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiendaretailsur.R
import com.example.tiendaretailsur.model.PasoRecuperacion
import com.example.tiendaretailsur.ui.components.CampoContrasena
import com.example.tiendaretailsur.ui.components.CampoTexto
import com.example.tiendaretailsur.ui.theme.Dimens
import com.example.tiendaretailsur.viewmodel.RecuperarContrasenaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarContrasenaScreen(
    viewModel: RecuperarContrasenaViewModel = viewModel(),
    onVolverALoginClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val colores = MaterialTheme.colorScheme
    val tipografia = MaterialTheme.typography
    val formas = MaterialTheme.shapes
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.pasoActual) {
        if (uiState.pasoActual == PasoRecuperacion.COMPLETADO) {
            snackbarHostState.showSnackbar(
                message = "¡Contraseña actualizada con éxito! Ya puedes iniciar sesión.",
                duration = SnackbarDuration.Short
            )
            onVolverALoginClick()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = colores.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioGrande)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(color = colores.surface, shape = formas.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_candado),
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.tamanoIcono),
                        tint = colores.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.espacioMedio))
                Text(
                    text = "Recuperar acceso",
                    style = tipografia.titleLarge,
                    color = colores.onBackground
                )
            }

            Text(
                text = "Valida tu identidad con tu pregunta de seguridad o solicita una clave temporal a tu correo registrado.",
                style = tipografia.bodyMedium,
                color = colores.onBackground.copy(alpha = 0.85f)
            )

            if (uiState.pasoActual == PasoRecuperacion.BUSCAR_USUARIO) {
                Card(
                    shape = formas.large,
                    colors = CardDefaults.cardColors(containerColor = colores.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.espacioMedio),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
                    ) {
                        Text(
                            text = "Paso 1: Identifica tu cuenta",
                            style = tipografia.titleMedium,
                            color = colores.onSurfaceVariant
                        )

                        CampoTexto(
                            valor = uiState.identificadorUsuario,
                            onValorCambia = viewModel::onIdentificadorCambia,
                            etiqueta = "Correo electrónico o RUT",
                            icono = R.drawable.ic_persona,
                            error = uiState.errorUsuario
                        )

                        Button(
                            onClick = { viewModel.buscarUsuario() },
                            enabled = !uiState.cargandoCorreo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.alturaBoton),
                            shape = RoundedCornerShape(Dimens.radioBoton),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colores.primary,
                                contentColor = colores.onPrimary
                            )
                        ) {
                            Text(
                                text = "Continuar con pregunta de seguridad",
                                style = tipografia.labelLarge
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f))
                            Text(
                                text = "  o  ",
                                style = tipografia.bodySmall,
                                color = colores.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f))
                        }

                        OutlinedButton(
                            onClick = { viewModel.enviarClaveAlCorreo() },
                            enabled = !uiState.cargandoCorreo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.alturaBoton),
                            shape = RoundedCornerShape(Dimens.radioBoton),
                            border = BorderStroke(1.dp, colores.primary)
                        ) {
                            if (uiState.cargandoCorreo) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = colores.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enviando clave...",
                                    style = tipografia.labelLarge,
                                    color = colores.primary
                                )
                            } else {
                                Text(
                                    text = "Enviar clave temporal a mi correo",
                                    style = tipografia.labelLarge,
                                    color = colores.primary
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.pasoActual == PasoRecuperacion.PREGUNTAS_SEGURIDAD) {
                var expandido by remember { mutableStateOf(false) }

                Card(
                    shape = formas.large,
                    colors = CardDefaults.cardColors(containerColor = colores.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.espacioMedio),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
                    ) {
                        Text(
                            text = "Paso 2: Pregunta de seguridad",
                            style = tipografia.titleMedium,
                            color = colores.onSurfaceVariant
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            ExposedDropdownMenuBox(
                                expanded = expandido,
                                onExpandedChange = { expandido = !expandido },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = uiState.pregunta1,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Selecciona tu pregunta de seguridad") },
                                    isError = uiState.errorPregunta1 != null,
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_candado),
                                            contentDescription = null,
                                            modifier = Modifier.size(Dimens.tamanoIcono)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                                    },
                                    shape = formas.medium,
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expandido,
                                    onDismissRequest = { expandido = false }
                                ) {
                                    uiState.listaPreguntas.forEach { pregunta ->
                                        DropdownMenuItem(
                                            text = { Text(text = pregunta) },
                                            onClick = {
                                                viewModel.onPregunta1Cambia(pregunta)
                                                expandido = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (uiState.errorPregunta1 != null) {
                                Text(
                                    text = uiState.errorPregunta1!!,
                                    color = colores.error,
                                    style = tipografia.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                        CampoTexto(
                            valor = uiState.respuesta1,
                            onValorCambia = viewModel::onRespuesta1Cambia,
                            etiqueta = "Tu respuesta de seguridad",
                            error = uiState.errorRespuesta1
                        )

                        Button(
                            onClick = { viewModel.verificarRespuestas() },
                            enabled = !uiState.cargandoCorreo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.alturaBoton),
                            shape = RoundedCornerShape(Dimens.radioBoton),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colores.primary,
                                contentColor = colores.onPrimary
                            )
                        ) {
                            Text(
                                text = "Verificar respuesta",
                                style = tipografia.labelLarge
                            )
                        }

                        TextButton(
                            onClick = { viewModel.enviarClaveAlCorreo() },
                            enabled = !uiState.cargandoCorreo,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = if (uiState.cargandoCorreo) "Enviando correo..."
                                else "¿Olvidaste tu respuesta? Enviar clave temporal al correo",
                                style = tipografia.bodySmall,
                                color = colores.primary
                            )
                        }
                    }
                }
            }

            if (uiState.pasoActual == PasoRecuperacion.CAMBIAR_CONTRASENA) {
                Card(
                    shape = formas.large,
                    colors = CardDefaults.cardColors(containerColor = colores.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.espacioMedio),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
                    ) {
                        Text(
                            text = "Paso 3: Crea tu nueva contraseña",
                            style = tipografia.titleMedium,
                            color = colores.onSurfaceVariant
                        )

                        CampoContrasena(
                            valor = uiState.nuevaContrasena,
                            onValorCambia = viewModel::onNuevaContrasenaCambia,
                            etiqueta = "Nueva contraseña",
                            visible = uiState.nuevaContrasenaVisible,
                            onAlternarContrasenaVisible = viewModel::onAlternarNuevaVisible,
                            error = uiState.errorNuevaContrasena
                        )

                        CampoContrasena(
                            valor = uiState.confirmarContrasena,
                            onValorCambia = viewModel::onConfirmarContrasenaCambia,
                            etiqueta = "Confirmar nueva contraseña",
                            visible = uiState.confirmarContrasenaVisible,
                            onAlternarContrasenaVisible = viewModel::onAlternarConfirmarVisible,
                            error = uiState.errorConfirmarContrasena
                        )

                        Button(
                            onClick = { viewModel.guardarNuevaContrasena() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.alturaBoton),
                            shape = RoundedCornerShape(Dimens.radioBoton),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colores.primary,
                                contentColor = colores.onPrimary
                            )
                        ) {
                            Text(
                                text = "Actualizar contraseña",
                                style = tipografia.labelLarge
                            )
                        }
                    }
                }
            }
            if (uiState.pasoActual == PasoRecuperacion.CORREO_ENVIADO) {
                Card(
                    shape = formas.large,
                    colors = CardDefaults.cardColors(containerColor = colores.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.espacioMedio),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
                    ) {
                        Text(
                            text = "¡Clave temporal enviada!",
                            style = tipografia.titleMedium,
                            color = colores.primary
                        )
                        Text(
                            text = "Hemos enviado una clave temporal al correo registrado de la cuenta (${uiState.correoUsuarioEncontrado}).",
                            style = tipografia.bodyMedium,
                            color = colores.onSurfaceVariant
                        )
                        Text(
                            text = "Revisa tu bandeja de entrada (o carpeta de spam), inicia sesión con esa clave y el sistema te pedirá crear una contraseña nueva.",
                            style = tipografia.bodySmall,
                            color = colores.onSurfaceVariant.copy(alpha = 0.85f)
                        )
                        Button(
                            onClick = onVolverALoginClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.alturaBoton),
                            shape = RoundedCornerShape(Dimens.radioBoton),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colores.primary,
                                contentColor = colores.onPrimary
                            )
                        ) {
                            Text(
                                text = "Ir a iniciar sesión",
                                style = tipografia.labelLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onVolverALoginClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Volver al inicio de sesión",
                    style = tipografia.labelLarge,
                    color = colores.onBackground
                )
            }
        }
    }
}