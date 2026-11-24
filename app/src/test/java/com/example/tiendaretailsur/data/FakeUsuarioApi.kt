package com.example.tiendaretailsur.data

import com.example.tiendaretailsur.data.remote.ActualizarUsuarioRequest
import com.example.tiendaretailsur.data.remote.CambiarContrasenaRequest
import com.example.tiendaretailsur.data.remote.LoginRequest
import com.example.tiendaretailsur.data.remote.PreguntaResponse
import com.example.tiendaretailsur.data.remote.PrimerIngresoRequest
import com.example.tiendaretailsur.data.remote.RecuperarPreguntaRequest
import com.example.tiendaretailsur.data.remote.RegistroRequest
import com.example.tiendaretailsur.data.remote.RestablecerContrasenaRequest
import com.example.tiendaretailsur.data.remote.RolApi
import com.example.tiendaretailsur.data.remote.RolRequestDTO
import com.example.tiendaretailsur.data.remote.RolResponseDTO
import com.example.tiendaretailsur.data.remote.SolicitudCorreoRequest
import com.example.tiendaretailsur.data.remote.SolicitudCorreoResponse
import com.example.tiendaretailsur.data.remote.UsuarioResponse
import com.example.tiendaretailsur.data.remote.UsuariosApi
import com.example.tiendaretailsur.data.remote.VerificacionResponse
import com.example.tiendaretailsur.data.remote.VerificarRespuestaRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import retrofit2.Response

class FakeUsuariosApi : UsuariosApi {

    private val usuarios = listOf(
        UsuarioResponse(1L, "188663338",
            "Yasna Mora", "yasna@gmail.com","078512596","Admnistrador",
            "Turno tarde", 1L, "Administrador", true, true, null),
        UsuarioResponse(2L, "128794697", "Jasmin Valencia", "jasmin@gmail.com","978512596",
            "Supervisor", "Turno Tarde", 3L, "Supervisor", true, true, null)
    )

    var sinConexion = false

    var ultimoBuscar: String? = "sin llamar"

    override suspend fun listar(buscar: String?): Response<List<UsuarioResponse>>{
        if(sinConexion) throw IOException("Servidor Apagado")
        ultimoBuscar = buscar
        return Response.success(usuarios)
    }

    override suspend fun iniciarSesion(datos: LoginRequest) : Response<UsuarioResponse> {
        if (sinConexion) throw IOException("Servidor apagado")
        val usuarioEncontrado = usuarios.find {
            it.correo == datos.correo && datos.contrasena == "1234"
        }
        return if (usuarioEncontrado != null) {
            Response.success(usuarioEncontrado)
        } else {
            Response.error(401,"Credenciales incorrectas".toResponseBody("text/plain".toMediaType()))
        }
    }

    override suspend fun obtener(id: Long): Response<UsuarioResponse> = TODO()
    override suspend fun registrar(datos: RegistroRequest): Response<UsuarioResponse> = TODO()
    override suspend fun actualizar(id: Long, datos: ActualizarUsuarioRequest): Response<UsuarioResponse> = TODO()
    override suspend fun cambiarContrasena(id: Long, datos: CambiarContrasenaRequest): Response<Unit> = TODO()
    override suspend fun completarPrimerIngreso(id: Long, datos: PrimerIngresoRequest): Response<UsuarioResponse> = TODO("")
    override suspend fun listarPreguntasSeguridad(): Response<List<String>> = TODO("")
    override suspend fun obtenerPreguntaSeguridad(datos: RecuperarPreguntaRequest): Response<PreguntaResponse> = TODO()
    override suspend fun verificarRespuesta(datos: VerificarRespuestaRequest): Response<VerificacionResponse> = TODO()
    override suspend fun restablecerContrasena(datos: RestablecerContrasenaRequest): Response<Unit> = TODO()
    override suspend fun eliminar(id: Long): Response<Unit> = TODO()
    override suspend fun crearSolicitudCorreo(request: SolicitudCorreoRequest): Response<SolicitudCorreoResponse> = TODO()
    override suspend fun listarSolicitudesPendientes(): Response<List<SolicitudCorreoResponse>> = TODO()
    override suspend fun obtenerPendientePorRut(rut: String): Response<SolicitudCorreoResponse> = TODO()
    override suspend fun resolverSolicitudCorreo(id: Long, aprobar: Boolean): Response<Unit> = TODO()
    override suspend fun recuperarPorCorreo(datos: RecuperarPreguntaRequest): Response<Unit> = TODO()
}

class FakeRolApi : RolApi {

    private val roles = listOf(
        RolResponseDTO(1L, "Administrador", "Encargado de administracion", true),
        RolResponseDTO(3L, "Supervisor", "Encargado de la Supervision", true)
    )

    override suspend fun listar(): Response<List<RolResponseDTO>> =
        Response.success(roles)

    override suspend fun obtenerRol(id: Long): Response<RolResponseDTO> {
        val rol = roles.find { it.id == id }
        return if (rol != null) {
            Response.success(rol)
        } else {
            Response.error(404, "Rol no encontrado".toResponseBody("text/plain".toMediaType()))
        }
    }

    override suspend fun actualizarOl(id: Long, requestDTO: RolRequestDTO): Response<RolResponseDTO> {
        val actualizado = RolResponseDTO(
            id = id,
            nombre = requestDTO.nombre,
            descripcion = requestDTO.descripcion,
            habilitado = true
        )
        return Response.success(actualizado)
    }
}














