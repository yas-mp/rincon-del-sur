package com.example.tiendaretailsur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.AccesoRepository
import com.example.tiendaretailsur.data.RolRepository
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.AjustesAdminUiState
import com.example.tiendaretailsur.model.ConfiguracionAcceso
import com.example.tiendaretailsur.model.ConfiguracionPerfil
import com.example.tiendaretailsur.model.SeccionAjustesAdmin
import com.example.tiendaretailsur.model.SolicitudCorreo
import com.example.tiendaretailsur.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AjustesAdminViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(),
    private val accesoRepository: AccesoRepository = AccesoRepository(),
    private val rolRepository: RolRepository = RolRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AjustesAdminUiState())
    val uiState: StateFlow<AjustesAdminUiState> = _uiState.asStateFlow()

    init {
        recargarDatos()
    }

    private fun obtenerRolIdDesdePerfil(perfil: String): Long {
        return when (perfil.trim().replace("_", " ").lowercase()) {
            "p-01", "jefa de tienda", "jefe de tienda", "1" -> 1L
            "p-02", "subgerente", "admin", "administrador", "2" -> 2L
            "p-03", "supervisor", "supervisora", "3" -> 3L
            "p-04", "observador", "4" -> 4L
            else -> 4L
        }
    }

    fun recargarDatos() {
        viewModelScope.launch {
            val usuariosList = try {
                usuarioRepository.listar("")
            } catch (e: Exception) {
                emptyList()
            }

            val accesosList = try {
                accesoRepository.listar()
            } catch (e: Exception) {
                emptyList()
            }

            val solicitudesList = try {
                usuarioRepository.listarSolicitudesPendientes()
            } catch (e: Exception) {
                emptyList()
            }

            val accesosConfig = accesosList.map { puerta ->
                ConfiguracionAcceso(
                    codigo = puerta.id,
                    nombre = puerta.nombre,
                    ubicacion = puerta.descripcion,
                    descripcion = puerta.descripcion,
                    habilitado = puerta.estaHabilitado,
                    modoOfflinePrioritario = false
                )
            }

            _uiState.update {
                it.copy(
                    usuarios = usuariosList,
                    accesos = accesosConfig,
                    solicitudesCorreo = solicitudesList,
                    metaConversionTexto = "15.0"
                )
            }
        }
    }

    fun resolverSolicitudCorreo(solicitud: SolicitudCorreo, aprobar: Boolean) {
        viewModelScope.launch {
            val exito = try {
                usuarioRepository.resolverSolicitudCorreo(solicitud.id.toLong(), aprobar)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            recargarDatos()
            val accion = if (aprobar) "aprobada y correo actualizado" else "rechazada"
            _uiState.update {
                it.copy(
                    mensajeConfirmacion = if (exito) {
                        "Solicitud de ${solicitud.nombreUsuario} $accion"
                    } else {
                        "Error al procesar la solicitud de ${solicitud.nombreUsuario}"
                    }
                )
            }
        }
    }

    fun seleccionarSeccion(seccion: SeccionAjustesAdmin) {
        _uiState.update { it.copy(seccionActual = seccion, mensajeConfirmacion = null) }
    }

    fun onBusquedaCambia(texto: String) {
        _uiState.update { it.copy(busqueda = texto) }
    }

    fun onFiltroPerfilCambia(codigoPerfil: String) {
        _uiState.update { it.copy(filtroPerfil = codigoPerfil) }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeConfirmacion = null) }
    }

    fun alternarEstadoUsuario(usuario: Usuario, nuevoEstado: Boolean) {
        viewModelScope.launch {
            val estadoTexto = if (nuevoEstado) "habilitado" else "deshabilitado"
            try {
                usuarioRepository.actualizar(
                    id = usuario.codigo.toLongOrNull() ?: 1L,
                    nombre = usuario.nombreCompleto,
                    correo = usuario.correo,
                    telefono = usuario.telefono,
                    cargo = usuario.cargo,
                    turno = "",
                    rolId = obtenerRolIdDesdePerfil(usuario.perfil),
                    activo = nuevoEstado,
                    pregunta = usuario.preguntaSeguridad1,
                    respuesta = usuario.respuestaHash1
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            recargarDatos()
            _uiState.update {
                it.copy(mensajeConfirmacion = "Colaborador ${usuario.nombreCompleto} $estadoTexto")
            }
        }
    }

    fun abrirEditorUsuario(usuario: Usuario) {
        _uiState.update {
            it.copy(
                usuarioEnEdicion = usuario,
                editNombre = usuario.nombreCompleto,
                editRut = usuario.rut,
                editCorreo = usuario.correo,
                editTelefono = usuario.telefono,
                editCargo = usuario.cargo,
                editPerfil = usuario.perfil,
                editActivo = usuario.activo,
                editNuevaContrasena = "",
                errorEdicionUsuario = null
            )
        }
    }

    fun cerrarEditorUsuario() {
        _uiState.update { it.copy(usuarioEnEdicion = null, errorEdicionUsuario = null) }
    }

    fun onEditNombreCambia(valor: String) {
        _uiState.update { it.copy(editNombre = valor, errorEdicionUsuario = null) }
    }

    fun onEditRutCambia(valor: String) {
        _uiState.update { it.copy(editRut = valor, errorEdicionUsuario = null) }
    }

    fun onEditCorreoCambia(valor: String) {
        _uiState.update { it.copy(editCorreo = valor, errorEdicionUsuario = null) }
    }

    fun onEditTelefonoCambia(valor: String) {
        val limpio = valor.filter { it.isDigit() }.take(9)
        _uiState.update { it.copy(editTelefono = limpio, errorEdicionUsuario = null) }
    }

    fun onEditCargoCambia(valor: String) {
        _uiState.update { it.copy(editCargo = valor, errorEdicionUsuario = null) }
    }

    fun onEditPerfilCambia(codigoPerfil: String) {
        _uiState.update { it.copy(editPerfil = codigoPerfil, errorEdicionUsuario = null) }
    }

    fun onEditActivoCambia(activo: Boolean) {
        _uiState.update { it.copy(editActivo = activo) }
    }

    fun onEditNuevaContrasenaCambia(valor: String) {
        _uiState.update { it.copy(editNuevaContrasena = valor, errorEdicionUsuario = null) }
    }

    fun guardarCambiosUsuario() {
        val estado = _uiState.value
        val usuarioOriginal = estado.usuarioEnEdicion ?: return

        if (estado.editNombre.isBlank()) {
            _uiState.update { it.copy(errorEdicionUsuario = "El nombre completo no puede estar vacío") }
            return
        }
        if (!estado.editCorreo.contains("@") || !estado.editCorreo.contains(".")) {
            _uiState.update { it.copy(errorEdicionUsuario = "Ingresa un correo electrónico válido") }
            return
        }
        if (estado.editTelefono.length < 8) {
            _uiState.update { it.copy(errorEdicionUsuario = "El teléfono debe tener al menos 8 dígitos") }
            return
        }
        if (estado.editNuevaContrasena.isNotBlank() && estado.editNuevaContrasena.length < 4) {
            _uiState.update { it.copy(errorEdicionUsuario = "La nueva contraseña debe tener mínimo 4 caracteres") }
            return
        }

        viewModelScope.launch {
            try {
                usuarioRepository.actualizar(
                    id = usuarioOriginal.codigo.toLongOrNull() ?: 1L,
                    nombre = estado.editNombre,
                    correo = estado.editCorreo,
                    telefono = estado.editTelefono,
                    cargo = estado.editCargo,
                    turno = "",
                    rolId = obtenerRolIdDesdePerfil(estado.editPerfil),
                    activo = estado.editActivo,
                    pregunta = usuarioOriginal.preguntaSeguridad1,
                    respuesta = usuarioOriginal.respuestaHash1
                )
                if (estado.editNuevaContrasena.isNotBlank()) {
                    usuarioRepository.cambiarContrasena(
                        id = usuarioOriginal.codigo.toLongOrNull() ?: 1L,
                        actual = "",
                        nueva = estado.editNuevaContrasena
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            recargarDatos()

            _uiState.update {
                it.copy(
                    usuarioEnEdicion = null,
                    errorEdicionUsuario = null,
                    mensajeConfirmacion = "Datos de ${estado.editNombre} actualizados correctamente"
                )
            }
        }
    }

    fun alternarEstadoPerfil(perfil: ConfiguracionPerfil, habilitado: Boolean) {
        val textoEstado = if (habilitado) "habilitado" else "deshabilitado"
        _uiState.update { estadoActual ->
            val listaPerfilesActualizada = estadoActual.perfiles.map { p ->
                if (p.codigo == perfil.codigo) {
                    p.copy(habilitado = habilitado)
                } else {
                    p
                }
            }
            estadoActual.copy(
                perfiles = listaPerfilesActualizada,
                mensajeConfirmacion = "Perfil ${perfil.codigo} (${perfil.nombre}) $textoEstado"
            )
        }
    }

    fun abrirEditorPerfil(perfil: ConfiguracionPerfil) {
        _uiState.update {
            it.copy(
                perfilEnEdicion = perfil,
                editPerfilNombre = perfil.nombre,
                editPerfilDescripcion = perfil.descripcion,
                editPerfilHabilitado = perfil.habilitado,
                aplicarEstadoATodosLosUsuariosDelPerfil = false
            )
        }
    }

    fun cerrarEditorPerfil() {
        _uiState.update { it.copy(perfilEnEdicion = null) }
    }

    fun onEditPerfilNombreCambia(valor: String) {
        _uiState.update { it.copy(editPerfilNombre = valor) }
    }

    fun onEditPerfilDescripcionCambia(valor: String) {
        _uiState.update { it.copy(editPerfilDescripcion = valor) }
    }

    fun onEditPerfilHabilitadoCambia(valor: Boolean) {
        _uiState.update { it.copy(editPerfilHabilitado = valor) }
    }

    fun onAplicarATodosCambia(valor: Boolean) {
        _uiState.update { it.copy(aplicarEstadoATodosLosUsuariosDelPerfil = valor) }
    }

    fun guardarCambiosPerfil() {
        val estado = _uiState.value
        val perfil = estado.perfilEnEdicion ?: return
        if (estado.editPerfilNombre.isBlank()) return

        _uiState.update {
            it.copy(
                perfilEnEdicion = null,
                mensajeConfirmacion = "Configuración del perfil ${perfil.codigo} guardada"
            )
        }
    }

    fun onMetaConversionCambia(valor: String) {
        _uiState.update { it.copy(metaConversionTexto = valor) }
    }

    fun guardarMetaConversion() {
        val valorNum = _uiState.value.metaConversionTexto.replace(",", ".").toDoubleOrNull()
        if (valorNum != null && valorNum in 0.1..100.0) {
            _uiState.update {
                it.copy(
                    mensajeConfirmacion = "Meta mensual actualizada a $valorNum%"
                )
            }
        } else {
            _uiState.update {
                it.copy(mensajeConfirmacion = "Ingresa un porcentaje válido entre 0.1 y 100")
            }
        }
    }

    fun alternarEstadoAcceso(codigoAcceso: String, habilitado: Boolean) {
        viewModelScope.launch {
            val estadoTexto = if (habilitado) "operativo" else "cerrado por evento/mantención"
            try {
                accesoRepository.cambiarEstado(codigoAcceso.toLongOrNull() ?: 1L, habilitado)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            recargarDatos()
            _uiState.update {
                it.copy(
                    mensajeConfirmacion = "Acceso $codigoAcceso marcado como $estadoTexto"
                )
            }
        }
    }
    fun abrirNuevoAcceso() {
        val siguienteNumero = (_uiState.value.accesos.size + 1)
        _uiState.update {
            it.copy(
                mostrandoDialogoAcceso = true,
                accesoEnEdicion = null,
                editAccesoCodigo = "A$siguienteNumero",
                editAccesoNombre = "",
                editAccesoDescripcion = "",
                editAccesoHabilitado = true,
                errorEdicionAcceso = null
            )
        }
    }

    fun abrirEditorAcceso(acceso: ConfiguracionAcceso) {
        _uiState.update {
            it.copy(
                mostrandoDialogoAcceso = true,
                accesoEnEdicion = acceso,
                editAccesoCodigo = "A${acceso.codigo}",
                editAccesoNombre = acceso.nombre,
                editAccesoDescripcion = acceso.descripcion,
                editAccesoHabilitado = acceso.habilitado,
                errorEdicionAcceso = null
            )
        }
    }

    fun cerrarDialogoAcceso() {
        _uiState.update {
            it.copy(
                mostrandoDialogoAcceso = false,
                accesoEnEdicion = null,
                errorEdicionAcceso = null
            )
        }
    }

    fun onEditAccesoCodigoCambia(valor: String) {
        _uiState.update { it.copy(editAccesoCodigo = valor.uppercase(), errorEdicionAcceso = null) }
    }

    fun onEditAccesoNombreCambia(valor: String) {
        _uiState.update { it.copy(editAccesoNombre = valor, errorEdicionAcceso = null) }
    }

    fun onEditAccesoDescripcionCambia(valor: String) {
        _uiState.update { it.copy(editAccesoDescripcion = valor, errorEdicionAcceso = null) }
    }

    fun onEditAccesoHabilitadoCambia(valor: Boolean) {
        _uiState.update { it.copy(editAccesoHabilitado = valor) }
    }

    fun guardarAcceso() {
        val estado = _uiState.value
        if (estado.editAccesoCodigo.isBlank()) {
            _uiState.update { it.copy(errorEdicionAcceso = "El código es obligatorio (ej: A5)") }
            return
        }
        if (estado.editAccesoNombre.isBlank()) {
            _uiState.update { it.copy(errorEdicionAcceso = "El nombre del acceso es obligatorio") }
            return
        }

        viewModelScope.launch {
            val esEdicion = estado.accesoEnEdicion != null
            val exito = if (esEdicion) {
                val idNumerico = estado.accesoEnEdicion!!.codigo.toLongOrNull() ?: 1L
                accesoRepository.actualizar(
                    idAcceso = idNumerico,
                    codigo = estado.editAccesoCodigo.trim(),
                    nombre = estado.editAccesoNombre.trim(),
                    descripcion = estado.editAccesoDescripcion.trim().ifBlank { "Acceso general" },
                    habilitado = estado.editAccesoHabilitado
                )
            } else {
                accesoRepository.crear(
                    codigo = estado.editAccesoCodigo.trim(),
                    nombre = estado.editAccesoNombre.trim(),
                    descripcion = estado.editAccesoDescripcion.trim().ifBlank { "Acceso general" },
                    habilitado = estado.editAccesoHabilitado
                )
            }

            if (exito) {
                recargarDatos()
                _uiState.update {
                    it.copy(
                        mostrandoDialogoAcceso = false,
                        accesoEnEdicion = null,
                        errorEdicionAcceso = null,
                        mensajeConfirmacion = if (esEdicion) {
                            "Acceso '${estado.editAccesoNombre}' actualizado correctamente"
                        } else {
                            "Nuevo acceso '${estado.editAccesoNombre}' creado correctamente"
                        }
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorEdicionAcceso = "No se pudo guardar. Verifica que el código no esté repetido.")
                }
            }
        }
    }
}