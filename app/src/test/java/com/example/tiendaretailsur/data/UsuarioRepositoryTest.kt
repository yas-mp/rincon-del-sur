package com.example.tiendaretailsur.data

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue

class UsuarioRepositoryTest {
    private lateinit var apiFalsa: FakeUsuariosApi
    private lateinit var repositorio: UsuarioRepository

    @Before
    fun preparar(){
        apiFalsa = FakeUsuariosApi()
        repositorio = UsuarioRepository(usuariosApi = apiFalsa, rolApi = FakeRolApi())
    }

    @Test
    fun iniciarSesion_ConvierteRespuestDeUsuario() = runBlocking {
        val usuario = repositorio.iniciarSesion("yasna@gmail.com", "1234")

        assertEquals("Yasna Mora", usuario.nombre)
        assertEquals("Admnistrador", usuario.cargo)
    }

    @Test
    fun noIniciarSesion_ConCredencialesIncorrectas() {
        val error = assertThrows(Exception::class.java){
            runBlocking {
                repositorio.iniciarSesion("yasnita@gmail.com", "12344")
            }
        }
        assertTrue(error.message!!.isNotEmpty())
    }
    @Test
    fun listarSinConexionDeRed_LazarErrorDeRed(){
        apiFalsa.sinConexion = true
        val error = assertThrows(Exception::class.java){
            runBlocking { repositorio.listar("") }
        }
        assertTrue(error.message!!.isNotEmpty())
    }
    @Test
    fun listar_conTextoVacio_enviaNullALaApi() = runBlocking {
        val lista = repositorio.listar("   ")

        assertEquals(2, lista.size)
        assertNull(apiFalsa.ultimoBuscar)
    }
    @Test
    fun listarRoles_devuelveRolesConvertidos() = runBlocking {
        val roles = repositorio.listarRoles()

        assertEquals(2, roles.size)
        assertEquals("Supervisor", roles[1].nombre)
    }


}