package com.example.tiendaretailsur.data

import com.example.tiendaretailsur.data.remote.ActualizarUsuarioRequest
import com.example.tiendaretailsur.data.remote.CambiarContrasenaRequest
import com.example.tiendaretailsur.data.remote.LoginRequest
import com.example.tiendaretailsur.data.remote.PrimerIngresoRequest
import com.example.tiendaretailsur.data.remote.RecuperarPreguntaRequest
import com.example.tiendaretailsur.data.remote.RegistroRequest
import com.example.tiendaretailsur.data.remote.RestablecerContrasenaRequest
import com.example.tiendaretailsur.data.remote.RetrofitClient
import com.example.tiendaretailsur.data.remote.RolApi
import com.example.tiendaretailsur.data.remote.UsuariosApi
import com.example.tiendaretailsur.data.remote.VerificarRespuestaRequest
import com.example.tiendaretailsur.model.RolUsuario
import com.example.tiendaretailsur.model.Usuario
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UsuarioRepository (
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi,
    private val rolApi: RolApi = RetrofitClient.rolApi
) {
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario =
        llamar { usuariosApi.iniciarSesion(LoginRequest(correo, contrasena)) }.aUsuario()

    suspend fun registrar(
        rut: String,
        nombre: String,
        correo: String,
        telefono: String,
        contrasena: String,
        cargo: String,
        turno: String,
        rolId: Long,
        pregunta: String,
        respuesta: String
    ): Usuario {
        val request = RegistroRequest(
            rut = rut,
            nombre = nombre,
            correo = correo,
            telefono = telefono,
            contrasena = contrasena,
            cargo = cargo,
            turno = turno,
            rolId = rolId,
            activo = true,
            preguntaSeguridad = pregunta,
            respuestaSeguridad = respuesta
        )
        return llamar { usuariosApi.registrar(request) }.aUsuario()
    }

    suspend fun obtener(id: Long): Usuario =
        llamar { usuariosApi.obtener(id) }.aUsuario()

    suspend fun listar(buscar: String): List<Usuario> =
        llamar { usuariosApi.listar(buscar.ifBlank { null }) }.map { it.aUsuario() }

    suspend fun actualizar(
        id: Long,
        nombre: String,
        correo: String,
        telefono: String,
        cargo: String,
        turno: String,
        rolId: Long,
        activo: Boolean,
        pregunta: String,
        respuesta: String
    ): Usuario {
        val request = ActualizarUsuarioRequest(
            nombre = nombre,
            correo = correo,
            telefono = telefono,
            cargo = cargo,
            turno = turno,
            rolId = rolId,
            activo = activo,
            preguntaSeguridad = pregunta,
            respuestaSeguridad = respuesta
        )
        return llamar { usuariosApi.actualizar(id, request) }.aUsuario()
    }

    suspend fun completarPrimerIngreso(
        id: Long,
        nuevaContrasena: String,
        pregunta: String,
        respuesta: String
    ): Usuario {
        val request = PrimerIngresoRequest(
            nuevaContrasena = nuevaContrasena,
            preguntaSeguridad = pregunta,
            respuestaSeguridad = respuesta
        )
        return llamar { usuariosApi.completarPrimerIngreso(id, request) }.aUsuario()
    }

    suspend fun obtenerPreguntaSeguridad(rutOCorreo: String): String {
        val response = llamar { usuariosApi.obtenerPreguntaSeguridad(
            RecuperarPreguntaRequest(
                rutOCorreo
            )
        ) }
        return response.pregunta
    }

    suspend fun verificarRespuesta(rutOCorreo: String, respuesta: String): Boolean {
        val response = llamar { usuariosApi.verificarRespuesta(
            VerificarRespuestaRequest(
                rutOCorreo,
                respuesta
            )
        ) }
        return response.valida
    }

    suspend fun restablecerContrasena(rutOCorreo: String, nuevaContrasena: String) {
        llamar { usuariosApi.restablecerContrasena(
            RestablecerContrasenaRequest(
                rutOCorreo,
                nuevaContrasena
            )
        ) }
    }

    suspend fun cambiarContrasena(id: Long, actual: String, nueva: String) {
        llamar { usuariosApi.cambiarContrasena(id, CambiarContrasenaRequest(actual, nueva)) }
    }

    suspend fun eliminar(id: Long) {
        llamar { usuariosApi.eliminar(id) }
    }

    suspend fun listarRoles(): List<RolUsuario> {
        val rolesDto = llamar { rolApi.listar() }
        return rolesDto.map {
            RolUsuario.entries.find { r -> r.id == it.id } ?: RolUsuario.OBSERVADOR
        }
    }

    private suspend fun <T> llamar(peticion: suspend () -> Response<T>): T {
        try {
            val response = peticion()
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!
            } else {
                val errorBody = response.errorBody()?.string()
                val mensaje = try {
                    JSONObject(errorBody ?: "").optString("mensaje")
                } catch (ex: Exception) {
                    ""
                }
                throw Exception(mensaje.ifBlank { "Error del servidor (${response.code()})" })
            }
        } catch (e: HttpException) {
            throw Exception("Error de servidor (${e.code()})")
        } catch (e: IOException) {
            throw Exception("No se pudo conectar con el servidor. Verifica tu conexión.")
        }
    }
}


private fun com.example.tiendaretailsur.data.remote.UsuarioResponse.aUsuario(): Usuario = Usuario(
    codigo = id.toString(),
    nombreCompleto = nombre,
    rut = rut,
    cargo = cargo,
    perfil = rolNombre,
    correo = correo,
    telefono = telefono ?: "",
    activo = activo,
    debeCambiarContrasena = primerIngreso
)