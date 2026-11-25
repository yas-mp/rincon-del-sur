package com.example.tiendaretailsur.viewmodel

import com.example.tiendaretailsur.model.Perfil
import com.example.tiendaretailsur.model.PerfilUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PerfilViewModelTest {

    @Test
    fun estadoInicial_tieneValoresPorDefecto() {
        val state = PerfilUiState()

        assertFalse(state.dialogoCorreoAbierto)
        assertEquals("", state.nuevoCorreoSolicitado)
        assertNull(state.errorNuevoCorreo)
        assertFalse(state.contrasenaActualVisible)
        assertFalse(state.nuevaContrasenaVisible)
        assertFalse(state.confirmarContrasenaVisible)
        assertNull(state.mensajeSnackbar)
    }

    @Test
    fun dialogoCorreo_abreYGuardaNuevoCorreoYError() {
        val state = PerfilUiState().copy(
            dialogoCorreoAbierto = true,
            nuevoCorreoSolicitado = "correo_malo",
            errorNuevoCorreo = "Ingresa un correo electrónico válido"
        )

        assertTrue(state.dialogoCorreoAbierto)
        assertEquals("correo_malo", state.nuevoCorreoSolicitado)
        assertEquals("Ingresa un correo electrónico válido", state.errorNuevoCorreo)
    }

    @Test
    fun cambiarContrasena_guardaErroresYVisibilidad() {
        val state = PerfilUiState().copy(
            contrasenaActual = "1234",
            nuevaContrasena = "abc",
            confirmarContrasena = "xyz",
            errorNuevaContrasena = "Mínimo 8 caracteres",
            errorConfirmarContrasena = "Las contraseñas no coinciden",
            contrasenaActualVisible = true
        )

        assertEquals("Mínimo 8 caracteres", state.errorNuevaContrasena)
        assertEquals("Las contraseñas no coinciden", state.errorConfirmarContrasena)
        assertTrue(state.contrasenaActualVisible)
    }

    @Test
    fun perfil_actualizaDatosDelUsuario() {
        val perfil = Perfil(
            nombre = "Yasna Mora",
            correo = "yasna@gmail.com",
            turno = "Turno Diurno",
            cargo = "OBSERVADOR"
        )
        val state = PerfilUiState(perfil = perfil)

        assertEquals("Yasna Mora", state.perfil.nombre)
        assertEquals("yasna@gmail.com", state.perfil.correo)
        assertEquals("OBSERVADOR", state.perfil.cargo)
    }
}