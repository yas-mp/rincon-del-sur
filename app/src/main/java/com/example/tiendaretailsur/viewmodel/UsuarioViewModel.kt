package com.example.tiendaretailsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaretailsur.data.GestorSesion
import com.example.tiendaretailsur.data.UsuarioRepository
import com.example.tiendaretailsur.model.RolUsuario
import com.example.tiendaretailsur.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val gestorSesion = GestorSesion(application)
    private val usuarioRepository = UsuarioRepository()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarUsuarios()
        cargarUsuarioActual()
    }

    fun cargarUsuarios(buscar: String = "") {
        viewModelScope.launch {
            try {
                _usuarios.value = usuarioRepository.listar(buscar)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun cargarUsuarioActual() {
        val idUsuario = gestorSesion.obtenerIdUsuarioLogueado()
        if (idUsuario == -1L) {
            _usuarioActual.value = null
            return
        }

        viewModelScope.launch {
            try {
                _usuarioActual.value = usuarioRepository.obtener(idUsuario)
            } catch (_: Exception) {
                _usuarioActual.value = null
            }
        }
    }

    fun registrarUsuario(
        usuario: Usuario,
        contrasenaPlana: String = "Rincon2026",
        turno: String = "Diurno",
        pregunta: String = "¿Cuál es el nombre de tu primera mascota?",
        respuesta: String = "Rincon"
    ) {
        viewModelScope.launch {
            try {
                val rolId = resolverRolId(usuario.perfil)
                val creado = usuarioRepository.registrar(
                    rut = usuario.rut,
                    nombre = usuario.nombreCompleto,
                    correo = usuario.correo,
                    telefono = usuario.telefono,
                    contrasena = contrasenaPlana,
                    cargo = usuario.cargo,
                    turno = turno,
                    rolId = rolId,
                    pregunta = pregunta,
                    respuesta = respuesta
                )
                cargarUsuarios()
                _usuarioActual.value = creado
                creado.codigo.toLongOrNull()?.let { id ->
                    gestorSesion.guardarSesion(id, creado.correo)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun actualizarUsuario(
        indice: Int,
        editado: Usuario,
        turno: String = "Diurno",
        pregunta: String = "",
        respuesta: String = ""
    ) {
        val anterior = _usuarios.value.getOrNull(indice) ?: return
        val idUsuario = anterior.codigo.toLongOrNull() ?: return

        viewModelScope.launch {
            try {
                val rolId = resolverRolId(editado.perfil)
                val actualizado = usuarioRepository.actualizar(
                    id = idUsuario,
                    nombre = editado.nombreCompleto,
                    correo = editado.correo,
                    telefono = editado.telefono,
                    cargo = editado.cargo,
                    turno = turno,
                    rolId = rolId,
                    activo = editado.activo,
                    pregunta = pregunta,
                    respuesta = respuesta
                )
                cargarUsuarios()
                if (_usuarioActual.value?.codigo == anterior.codigo) {
                    _usuarioActual.value = actualizado
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun eliminarUsuario(idUsuario: Long) {
        viewModelScope.launch {
            try {
                usuarioRepository.eliminar(idUsuario)
                cargarUsuarios()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun cerrarSesion() {
        gestorSesion.cerrarSesion()
        _usuarioActual.value = null
    }

    private fun resolverRolId(perfil: String): Long {
        return RolUsuario.entries.find {
            it.name.equals(perfil, ignoreCase = true) ||
                    it.toString().equals(perfil, ignoreCase = true)
        }?.id ?: RolUsuario.OBSERVADOR.id
    }
}