package com.example.tiendaretailsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.AccesoRepository
import com.example.tiendaretailsur.data.ConteoRepository
import com.example.tiendaretailsur.data.GestorSesion
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.AccesoTienda
import com.example.tiendaretailsur.model.MenuPrincipalUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.Locale

class MenuPrincipalViewModel : AndroidViewModel {

    private val gestorSesion: GestorSesion?
    private val accesoRepository = AccesoRepository()
    private val conteoRepository = ConteoRepository()
    private val usuarioRepository = UsuarioRepository()

    // Constructor vacío útil para @Preview en Compose
    constructor() : super(Application()) {
        gestorSesion = null
    }

    // Constructor real usado por viewModel() en Android
    constructor(application: Application) : super(application) {
        gestorSesion = GestorSesion(application)
    }

    private val _uiState = MutableStateFlow(MenuPrincipalUiState())
    val uiState: StateFlow<MenuPrincipalUiState> = _uiState.asStateFlow()

    init {
        cargarDatosDesdeApi()
    }

    // Mantiene compatibilidad si tu pantalla aún llama a cargarDatosDesdeDb(isAdmin)
    fun cargarDatosDesdeDb(isAdmin: Boolean = true) {
        cargarDatosDesdeApi()
    }

    fun cargarDatosDesdeApi() {
        // Si se instanció sin Context (ej. @Preview), carga datos de muestra
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
            // 1. Obtener puertas reales desde microservicio Accesos (8083)
            val puertasApi = accesoRepository.listar()
            val listaAccesosFinal = puertasApi.ifEmpty {
                listOf(
                    AccesoTienda("A1", "Acceso Principal", "Patio de comidas"),
                    AccesoTienda("A2", "Acceso Lateral", "Pasillo norte"),
                    AccesoTienda("A3", "Acceso Estacionamiento", "Subterráneo"),
                    AccesoTienda("A4", "Acceso Conexión", "Tienda vecina")
                )
            }

            // 2. Obtener usuario logueado desde GestorSesion y descargar su avatar desde microservicio Usuarios (8082)
            val idUsuario = gestorSesion.obtenerIdUsuarioLogueado()
            val usuarioActual = if (idUsuario != -1L) {
                try {
                    usuarioRepository.obtener(idUsuario)
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }

            val avatarReal: ByteArray? = usuarioActual?.avatarUrl?.let { url ->
                if (url.isNotBlank()) {
                    val urlCompleta = if (url.startsWith("http")) {
                        url
                    } else {
                        "http://10.0.2.2:8082/${url.trimStart('/')}"
                    }
                    withContext(Dispatchers.IO) {
                        try {
                            URL(urlCompleta).readBytes()
                        } catch (_: Exception) {
                            null
                        }
                    }
                } else null
            }

            // 3. Obtener conteos reales desde microservicio Conteos (8084)
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