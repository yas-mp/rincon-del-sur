package com.example.tiendaretailsur.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminDashboardUiStateTest {

    @Test
    fun estadoPorDefecto_tieneTodosLosValoresEnCeroOVacios() {
        val state = AdminDashboardUiState()

        assertEquals(0, state.totalVisitas)
        assertEquals(0, state.observadoresActivos)
        assertTrue(state.flujoPorHora.isEmpty())
        assertTrue(state.segmentacion.isEmpty())
        assertTrue(state.listaSesiones.isEmpty())
        assertFalse(state.estaCargando)
    }

    @Test
    fun copy_actualizaCamposCorrectamenteSinAlterarLosDemas() {
        val estadoInicial = AdminDashboardUiState()
        val grafico = listOf(DatoGrafico("10:00", 25))
        val sesion = SesionResumen(
            id = 1L,
            observador = "Carlos",
            hora = "10:00",
            conteo = 25,
            estado = 1
        )

        val estadoActualizado = estadoInicial.copy(
            totalVisitas = 25,
            observadoresActivos = 1,
            flujoPorHora = grafico,
            listaSesiones = listOf(sesion),
            estaCargando = true
        )

        assertEquals(25, estadoActualizado.totalVisitas)
        assertEquals(1, estadoActualizado.observadoresActivos)
        assertEquals(1, estadoActualizado.flujoPorHora.size)
        assertEquals("10:00", estadoActualizado.flujoPorHora[0].etiqueta)
        assertEquals(1, estadoActualizado.listaSesiones.size)
        assertEquals("Carlos", estadoActualizado.listaSesiones[0].observador)
        assertTrue(estadoActualizado.estaCargando)
        assertTrue(estadoActualizado.segmentacion.isEmpty())
    }
}