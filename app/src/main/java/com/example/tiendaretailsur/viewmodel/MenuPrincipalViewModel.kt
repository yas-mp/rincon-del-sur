package com.example.tiendaretailsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.AccesoRepository
import com.example.tiendaretailsur.data.AvatarRepository
import com.example.tiendaretailsur.data.ConteoRepository
import com.example.tiendaretailsur.data.GestorSesion
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.AccesoTienda
import com.example.tiendaretailsur.model.MenuPrincipalUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class MenuPrincipalViewModel : AndroidViewModel {

    private val gestorSesion: GestorSesion?
    private val accesoRepository = AccesoRepository()
    private val conteoRepository = ConteoRepository()
    private val usuarioRepository = UsuarioRepository()
    private val avatarRepository = AvatarRepository()

    constructor() : super(Application()) {
        gestorSesion = null
    }

    constructor(application: Application) : super(application) {
        gestorSesion = GestorSesion(application)
    }

    private val _uiState = MutableStateFlow(MenuPrincipalUiState())
    val uiState: StateFlow<MenuPrincipalUiState> = _uiState.asStateFlow()

    init {
        cargarDatosDesdeApi()
    }

    fun cargarDatosDesdeDb(isAdmin: Boolean = true) {
        cargarDatosDesdeApi()
    }

    fun cargarDatosDesdeApi() {
        if (gestorSesion == null) {
            _uiState.update {
                it.copy(
                    ultimoResumen = null,
                    listaAccesos = listOf(
                        AccesoTienda("A1", "Acceso Principal", "Patio de comidas"),
                        AccesoTienda("A2", "Acceso Lateral", "Pasillo norte"),
                        AccesoTienda("A3", "Acceso Estacionamiento", "Subterráneo"),
                        AccesoTienda("A4", "Acceso Conexión", "Tienda vecina")
                    ),
                    totalIngresosCuatroPuertas = 470,
                    porcentajeTransito = 60.0f,
                    porcentajeClientes = 40.0f,
                    conteosPorPuerta = mapOf("A1" to 205, "A2" to 115, "A3" to 68, "A4" to 82),
                    avatarBytes = null
                )
            }
            return
        }

        viewModelScope.launch {
            val puertasApi = accesoRepository.listar()
            val listaAccesosFinal = puertasApi.ifEmpty {
                listOf(
                    AccesoTienda("A1", "Acceso Principal", "Patio de comidas"),
                    AccesoTienda("A2", "Acceso Lateral", "Pasillo norte"),
                    AccesoTienda("A3", "Acceso Estacionamiento", "Subterráneo"),
                    AccesoTienda("A4", "Acceso Conexión", "Tienda vecina")
                )
            }

            val idUsuario = gestorSesion.obtenerIdUsuarioLogueado()
            val correoSesion = gestorSesion.obtenerCorreoUsuarioLogueado()

            val usuarioActual = if (idUsuario != -1L) {
                try {
                    usuarioRepository.obtener(idUsuario)
                } catch (_: Exception) {
                    null
                }
            } else if (correoSesion.isNotBlank()) {
                try {
                    usuarioRepository.listar(correoSesion).firstOrNull()
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }

            val idNumerico = usuarioActual?.codigo?.toLongOrNull() ?: idUsuario
            val avatarReal: ByteArray? = if (idNumerico != -1L) {
                avatarRepository.obtenerBytes(idNumerico)
            } else {
                null
            }

            val conteos = conteoRepository.listarConteos()

            val totalTransito = conteos.sumOf { it.cantidadTransito }
            val totalClientes = conteos.sumOf { it.cantidadClientes }
            val totalIngresos = totalTransito + totalClientes

            val porcentajeT = if (totalIngresos > 0) (totalTransito * 100f) / totalIngresos else 0f
            val porcentajeC = if (totalIngresos > 0) (totalClientes * 100f) / totalIngresos else 0f

            val conteosPorPuerta = conteos.groupBy { it.puertaAcceso }
                .mapValues { (_, lista) -> lista.sumOf { it.cantidadTransito + it.cantidadClientes } }

            _uiState.update {
                it.copy(
                    ultimoResumen = null,
                    listaAccesos = listaAccesosFinal,
                    totalIngresosCuatroPuertas = totalIngresos,
                    porcentajeTransito = String.format(Locale.getDefault(), "%.1f", porcentajeT).toFloat(),
                    porcentajeClientes = String.format(Locale.getDefault(), "%.1f", porcentajeC).toFloat(),
                    conteosPorPuerta = conteosPorPuerta,
                    avatarBytes = avatarReal
                )
            }
        }
    }
}