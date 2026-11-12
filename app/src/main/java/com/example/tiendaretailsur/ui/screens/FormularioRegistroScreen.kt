package com.example.tiendaretailsur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tiendaretailsur.R
import com.example.tiendaretailsur.data.UsuarioDbHelper
import com.example.tiendaretailsur.model.FormularioRegistroUiState
import com.example.tiendaretailsur.model.RolUsuario
import com.example.tiendaretailsur.ui.components.BotonPrincipal
import com.example.tiendaretailsur.ui.components.CampoTexto
import com.example.tiendaretailsur.ui.components.CampoTextoRut
import com.example.tiendaretailsur.ui.components.TituloScreen
import com.example.tiendaretailsur.ui.theme.Dimens
import com.example.tiendaretailsur.ui.theme.MiRegistroTheme
import com.example.tiendaretailsur.viewmodel.FormularioRegistroViewModel
import android.widget.Toast
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

@Composable
fun FormularioRegistroScreen(
    dbHelper: UsuarioDbHelper = UsuarioDbHelper(LocalContext.current),
    viewModel: FormularioRegistroViewModel = viewModel(
        factory = viewModelFactory {
            initializer { FormularioRegistroViewModel(dbHelper) }
        }
    ),
    onIrALoginClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FormularioRegistroContent(
        uiState = uiState,
        onRutCambia = viewModel::onRutCambia,
        onNombreCambia = viewModel::onNombreCambia,
        onCorreoCambia = viewModel::onCorreoCambia,
        onTelefonoCambia = viewModel::onTelefonoCambia,
        onSeleccionRol = viewModel::onSeleccionRol,
        onLimpiarFormulario = viewModel::onLimpiarFormulario,
        onRegistrar = viewModel::onRegistrar,
        onIrALoginClick = onIrALoginClick
    )
}

@Composable
fun FormularioRegistroContent(
    uiState: FormularioRegistroUiState,
    onRutCambia: (String) -> Unit,
    onNombreCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onTelefonoCambia: (String) -> Unit,
    onSeleccionRol: (RolUsuario) -> Unit,
    onLimpiarFormulario: () -> Unit,
    onRegistrar: () -> Unit,
    onIrALoginClick: () -> Unit = {}
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
                    top = innerPadding.calculateTopPadding() / 2,
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            IconButton(
                onClick = onIrALoginClick,
                modifier = Modifier
                    .padding(bottom = Dimens.espacioPequeno)
                    .size(44.dp)
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                Icon(
                    painter = painterResource(R.drawable.atras),
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            TituloScreen(
                titulo = stringResource(R.string.titulo_registro),
                subtitulo = stringResource(R.string.subtitulo_registro),
            )
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "RUT",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            CampoTextoRut(
                valor = uiState.rut,
                onValorCambia = onRutCambia,
                etiqueta = stringResource(R.string.campo_rut),
                icono = R.drawable.rut,
                error = uiState.errorFormatoRut
            )
            Spacer(modifier = Modifier.height(Dimens.espacioInterno))

            Text(
                text = "Nombre completo",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            CampoTexto(
                valor = uiState.nombre,
                onValorCambia = onNombreCambia,
                etiqueta = stringResource(R.string.campo_nombre),
                icono = R.drawable.ic_persona,
                error = uiState.errorNombre
            )
            Spacer(modifier = Modifier.height(Dimens.espacioInterno))

            Text(
                text = "Teléfono",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            CampoTexto(
                valor = uiState.telefono,
                onValorCambia = onTelefonoCambia,
                etiqueta = stringResource(R.string.campo_telefono),
                icono = R.drawable.telefono,
                error = uiState.errorTelefono,
                tipoTeclado = KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(Dimens.espacioInterno))

            Text(
                text = "Correo electrónico",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            CampoTexto(
                valor = uiState.correo,
                onValorCambia = onCorreoCambia,
                etiqueta = stringResource(R.string.campo_correo),
                icono = R.drawable.correo,
                error = uiState.errorFormatoCorreo,
                tipoTeclado = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(Dimens.espacioInterno))

            var menuRolAbierto by remember { mutableStateOf(false) }
            Text(
                text = "Selecciona el rol",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                CampoTexto(
                    valor = uiState.rolSeleccionado.name,
                    onValorCambia = {},
                    etiqueta = "Seleccionar Rol",
                    icono = R.drawable.ic_persona
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { menuRolAbierto = true }
                )
                DropdownMenu(
                    expanded = menuRolAbierto,
                    onDismissRequest = { menuRolAbierto = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    RolUsuario.entries.forEach { rol ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = rol.name,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onSeleccionRol(rol)
                                menuRolAbierto = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioInterno)) {
                BotonPrincipal(
                    texto = stringResource(R.string.btn_registro),
                    onClick = onRegistrar,
                    icono = R.drawable.ic_check,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current

            if (uiState.registroExitoso) {
                AlertDialog(
                    containerColor = MaterialTheme.colorScheme.surface,
                    onDismissRequest = {
                        onLimpiarFormulario()
                        onIrALoginClick()
                    },
                    title = {
                        Text(
                            text = "¡Registro Exitoso!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "El colaborador ${uiState.nombre} ha sido registrado correctamente.",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            SelectionContainer {
                                Text(
                                    text = "Contraseña temporal: ${uiState.contrasenaTemporalGenerada}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "Compártela con el colaborador para que active su cuenta en su primer inicio de sesión.",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(uiState.contrasenaTemporalGenerada))
                                Toast.makeText(context, "Contraseña temporal copiada", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Copiar clave")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onLimpiarFormulario()
                                onIrALoginClick()
                            }
                        ) {
                            Text("Aceptar")
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
fun FormularioRegistroContentPreview() {
    MiRegistroTheme {
        FormularioRegistroContent(
            uiState = FormularioRegistroUiState(
                rolSeleccionado = RolUsuario.OBSERVADOR
            ),
            onRutCambia = {},
            onNombreCambia = {},
            onCorreoCambia = {},
            onTelefonoCambia = {},
            onSeleccionRol = {},
            onRegistrar = {},
            onLimpiarFormulario = {},
            onIrALoginClick = {}
        )
    }
}