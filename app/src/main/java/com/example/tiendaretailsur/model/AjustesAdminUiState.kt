package com.example.tiendaretailsur.model

enum class SeccionAjustesAdmin(val titulo: String) {
    COLABORADORES("Colaboradores"),
    TIENDA("Tienda y Cuenta")
}

data class AjustesAdminUiState(
    val seccionActual: SeccionAjustesAdmin = SeccionAjustesAdmin.COLABORADORES,
    val busqueda: String = "",
    val filtroPerfil: String = "TODOS",

    val usuarios: List<Usuario> = emptyList(),
    val perfiles: List<ConfiguracionPerfil> = emptyList(),
    val accesos: List<ConfiguracionAcceso> = emptyList(),
    val solicitudesCorreo: List<SolicitudCorreo> = emptyList(),
    val metaConversionTexto: String = "10.0",

    val usuarioEnEdicion: Usuario? = null,
    val editNombre: String = "",
    val editRut: String = "",
    val editCorreo: String = "",
    val editTelefono: String = "",
    val editCargo: String = "",
    val editPerfil: String = "P-04",
    val editActivo: Boolean = true,
    val editNuevaContrasena: String = "",
    val errorEdicionUsuario: String? = null,

    val perfilEnEdicion: ConfiguracionPerfil? = null,
    val editPerfilNombre: String = "",
    val editPerfilDescripcion: String = "",
    val editPerfilHabilitado: Boolean = true,
    val aplicarEstadoATodosLosUsuariosDelPerfil: Boolean = false,

    val mostrandoDialogoAcceso: Boolean = false,
    val accesoEnEdicion: ConfiguracionAcceso? = null,
    val editAccesoCodigo: String = "",
    val editAccesoNombre: String = "",
    val editAccesoDescripcion: String = "",
    val editAccesoHabilitado: Boolean = true,
    val errorEdicionAcceso: String? = null,

    val mensajeConfirmacion: String? = null
)