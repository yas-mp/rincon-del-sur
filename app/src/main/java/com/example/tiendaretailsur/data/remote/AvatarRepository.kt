package com.example.tiendaretailsur.data.remote

import com.example.tiendaretailsur.model.Usuario
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class AvatarRepository (
    private val avatarApi: AvatarApi = RetrofitClient.avatarApi
) {
    suspend fun subir(idUsuario: Long, bytes: ByteArray): Usuario {
        val cuerpo = bytes.toRequestBody("image/jpeg".toMediaType())
        val parte = MultipartBody.Part.createFormData("archivo", "avatar.jpg", cuerpo)
        return try {
            val response = avatarApi.subir(idUsuario, parte)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.aUsuario()
            } else {
                throw Exception("Error al subir foto (${response.code()})")
            }
        } catch (e: HttpException) {
            throw Exception("No se pudo subir la foto (error ${e.code()})")
        } catch (e: IOException) {
            throw Exception("No se pudo conectar con el servidor")
        }
    }
}

private fun UsuarioResponse.aUsuario(): Usuario = Usuario(
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
