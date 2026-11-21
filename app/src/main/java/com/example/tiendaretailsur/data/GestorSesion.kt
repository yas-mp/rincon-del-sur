package com.example.tiendaretailsur.data

import android.content.Context
import androidx.core.content.edit

class GestorSesion(context: Context) {
    private val prefs = context.getSharedPreferences("sesion_params", Context.MODE_PRIVATE)

    companion object {
        private const val CLAVE_ID_USUARIO = "id_usuario"
        private const val CLAVE_CORREO_USUARIO = "correo_usuario"
        private const val CLAVE_NOMBRE_USUARIO = "nombre_usuario"
        private const val CLAVE_PERFIL_USUARIO = "perfil_usuario"
        private const val CLAVE_IS_ADMIN = "is_admin"
        private const val SIN_SESION = -1L
    }

    fun guardarSesion(
        idUsuario: Long,
        correo: String = "",
        nombre: String = "",
        perfil: String = "",
        isAdmin: Boolean = false
    ) {
        prefs.edit {
            putLong(CLAVE_ID_USUARIO, idUsuario)
            putString(CLAVE_CORREO_USUARIO, correo)
            putString(CLAVE_NOMBRE_USUARIO, nombre)
            putString(CLAVE_PERFIL_USUARIO, perfil)
            putBoolean(CLAVE_IS_ADMIN, isAdmin)
        }
    }

    fun haySesionActiva(): Boolean =
        obtenerIdUsuarioLogueado() != SIN_SESION || obtenerCorreoUsuarioLogueado().isNotBlank()

    fun obtenerIdUsuarioLogueado(): Long = prefs.getLong(CLAVE_ID_USUARIO, SIN_SESION)

    fun obtenerCorreoUsuarioLogueado(): String = prefs.getString(CLAVE_CORREO_USUARIO, "") ?: ""

    fun obtenerNombreUsuarioLogueado(): String = prefs.getString(CLAVE_NOMBRE_USUARIO, "") ?: ""

    fun obtenerPerfilUsuarioLogueado(): String = prefs.getString(CLAVE_PERFIL_USUARIO, "") ?: ""

    fun obtenerIsAdmin(): Boolean = prefs.getBoolean(CLAVE_IS_ADMIN, false)

    fun cerrarSesion() {
        prefs.edit {
            clear()
        }
    }
}