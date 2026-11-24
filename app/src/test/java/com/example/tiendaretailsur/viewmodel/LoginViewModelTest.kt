package com.example.tiendaretailsur.viewmodel

import android.app.Application
import com.example.tiendaretailsur.data.FakeRolApi
import com.example.tiendaretailsur.data.FakeUsuariosApi
import com.example.tiendaretailsur.data.UsuarioRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel

    @Before
    fun preparar() {
        val repoFalso = UsuarioRepository(FakeUsuariosApi(), FakeRolApi())
        viewModel = LoginViewModel(
            application = Application(),
            usuarioRepository = repoFalso,
            gestorSesion = null
        )
    }

    @Test
    fun validarCorreoVacioAlIniciarSesion() {
        viewModel.login()

        assertEquals("Ingresa tu correo o RUT", viewModel.uiState.value.errorCorreo)
    }

    @Test
    fun validarContrasenaVaciaAlIniciarSesion() {
        viewModel.onCorreoChanged("yasna@gmail.com")
        viewModel.login()

        assertEquals("La contraseña es requerida", viewModel.uiState.value.errorContrasena)
    }

}