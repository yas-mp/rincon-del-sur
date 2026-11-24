package com.example.tiendaretailsur.viewmodel

import com.example.tiendaretailsur.data.FakeUsuariosApi
import com.example.tiendaretailsur.data.UsuarioRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RecuperarContrasenaViewModelTest {
    private lateinit var viewModel : RecuperarContrasenaViewModel

    @Before
    fun preparar(){
        viewModel = RecuperarContrasenaViewModel()
        val repo = UsuarioRepository(FakeUsuariosApi())
    }

    @Test
    fun buscarUsuarioParaRecuperarContrasena_UsuarioVacio_muestraError() {
        viewModel.buscarUsuario()
        assertEquals("Ingresa tu correo electrónico o RUT", viewModel.uiState.value.errorUsuario)
    }
    @Test
    fun buscarUsuarioParaRecuperarContrasena_Usuario(){
        viewModel.onIdentificadorCambia("yasna@gmail.com")
        assertEquals("yasna@gmail.com", viewModel.uiState.value.identificadorUsuario)
    }
    @Test
    fun recuperarContrasenaCambiaPorUnaNueva_SinError(){
        viewModel.onNuevaContrasenaCambia("yasnita")
        viewModel.onConfirmarContrasenaCambia("yasnita")
        assertEquals("yasnita", viewModel.uiState.value.nuevaContrasena)
    }
    @Test
    fun recuperarContrasenaNoCambiaContrasena_ClavesDiferenetes(){
        viewModel.onNuevaContrasenaCambia("yasna1")
        viewModel.onConfirmarContrasenaCambia("yasna12")
        viewModel.guardarNuevaContrasena()
        assertEquals("Las contraseñas no coinciden",viewModel.uiState.value.errorConfirmarContrasena)

    }



}