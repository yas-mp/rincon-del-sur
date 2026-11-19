package com.example.tiendaretailsur.data.remote
//roles
data class RolRequestDTO(
    val nombre: String,
    val descripcion: String,
    val habilitado: Boolean
)
data class RolResponseDTO(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val habilitado: Boolean
)
//Accesos
data class AccesosRequestDTO(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val estaHabilitado: Boolean
)

data class AccesosResponseDTO(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val estaHabilitado: Boolean
)
//conteos
data class ConteoRequest(
    val puertaAcceso: String,
    val cantidadTransito: Int,
    val cantidadClientes: Int,
    val rangoEtario: String,
    val compania: String,
    val observador: String,
    val hora: String,
    val usuarioId: Long? = null
)
data class ConteoResponse(
    val id: Long,
    val puertaAcceso: String,
    val cantidadTransito: Int,
    val cantidadClientes: Int,
    val rangoEtario: String,
    val compania: String,
    val observador: String,
    val hora: String,
    val usuarioId: Long?
)
data class DatoGraficoResponse(
    val etiqueta: String,
    val valor: Int
)
//usuarios
data class ActualizarUsuarioRequest(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val cargo: String,
    val turno: String,
    val rolId: Long,
    val activo: Boolean,
    val preguntaSeguridad: String,
    val respuestaSeguridad: String
)
data class CambiarContrasenaRequest(
    val actual: String,
    val nueva: String
)
data class LoginRequest(
    val correo: String,
    val contrasena: String
)
data class PrimerIngresoRequest(
    val nuevaContrasena: String,
    val preguntaSeguridad: String,
    val respuestaSeguridad: String
)
data class RecuperarPreguntaRequest(
    val rutOCorreo: String
)
data class RegistroRequest(
    val rut: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val contrasena: String,
    val cargo: String,
    val turno: String,
    val rolId: Long,
    val activo: Boolean,
    val preguntaSeguridad: String,
    val respuestaSeguridad: String
)
data class RestablecerContrasenaRequest(
    val rutOCorreo: String,
    val nuevaContrasena: String
)
data class UsuarioResponse(
    val id: Long,
    val rut: String,
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val cargo: String,
    val turno: String?,
    val rolId: Long,
    val rolNombre: String,
    val activo: Boolean,
    val primerIngreso: Boolean,
    val avatarUrl: String?
)
data class VerificarRespuestaRequest(
    val rutOCorreo: String,
    val respuesta: String
)
data class PreguntaResponse(
    val pregunta: String
)

data class VerificacionResponse(
    val valida: Boolean
)
data class SolicitudCorreoRequest(
    val rutUsuario: String,
    val nombreUsuario: String,
    val correoActual: String,

)
