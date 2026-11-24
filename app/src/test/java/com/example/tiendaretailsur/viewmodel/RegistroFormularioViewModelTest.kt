package com.example.tiendaretailsur.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegistroFormularioViewModelTest {
    private lateinit var viewModel : FormularioRegistroViewModel

    @Before
    fun preparar(){
        viewModel = FormularioRegistroViewModel()
    }

    @Test
    fun validarFormatoRut(){
        val resultadoRut = viewModel.validarFormatoRut("188663338")
        assertTrue(resultadoRut)
    }
    @Test
    fun ValidarFormatoRut_QueLanceInvalidoFormatoFalse(){
        val resultadoRut = viewModel.validarFormatoRut("1886633399")
        assertFalse(resultadoRut)
    }
    @Test
    fun validarFormatoDeCorreo(){
        viewModel.onCorreoCambia("yasna@gmail.com")
        viewModel.onRegistrar()
        assertEquals("yasna@gmail.com", viewModel.uiState.value.correo)

    }
    @Test
    fun validarFormatoDeCorreoInvalido(){
        viewModel.onCorreoCambia("yasnagmail.com")
        viewModel.onRegistrar()
        assertEquals( "Ingrese un correo válido", viewModel.uiState.value.errorFormatoCorreo)
    }
    @Test
    fun seLlenaYLimpiaFormularioRegistro_funcionCorrectamente(){
        viewModel.onRutCambia("188663338")
        viewModel.onNombreCambia("Yasna Mora")
        viewModel.onCorreoCambia("yasna@gmail.com")
        viewModel.onTelefonoCambia("978512596")
        viewModel.onTerminosCambia(true)

        assertEquals("188663338", viewModel.uiState.value.rut)
        assertEquals("Yasna Mora", viewModel.uiState.value.nombre)
        assertEquals("yasna@gmail.com", viewModel.uiState.value.correo)
        assertEquals("978512596", viewModel.uiState.value.telefono)
        assertTrue(viewModel.uiState.value.aceptarTerminos)

        viewModel.onLimpiarFormulario()
        assertEquals("", viewModel.uiState.value.rut)
        assertEquals("", viewModel.uiState.value.nombre)
        assertFalse(viewModel.uiState.value.aceptarTerminos)
    }


}