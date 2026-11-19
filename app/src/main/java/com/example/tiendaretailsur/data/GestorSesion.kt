package com.example.tiendaretailsur.data

import android.content.Context
import androidx.core.content.edit

class GestorSesion (context: Context) {
    private val prefs = context.getSharedPreferences("sesion_params", Context.MODE_PRIVATE)

    companion object {
        private const val CLAVE_ID_USUARIO = "id_usuario"
        private const val CLAVE_CORREO_USUARIO = "correo_usuario"
        private const val SIN_SESION = -1L
    }

    fun guardarSesion(idUsuario: Long, correo: String = "") {
        prefs.edit {
            putLong(CLAVE_ID_USUARIO, idUsuario)
            putString(CLAVE_CORREO_USUARIO, correo)
        }
    }

    fun obtenerIdUsuarioLogueado(): Long = prefs.getLong(CLAVE_ID_USUARIO, SIN_SESION)

    fun obtenerCorreoUsuarioLogueado(): String = prefs.getString(CLAVE_CORREO_USUARIO, "") ?: ""

    fun cerrarSesion() {
        prefs.edit {
            remove(CLAVE_ID_USUARIO)
            remove(CLAVE_CORREO_USUARIO)
        }
    }
}