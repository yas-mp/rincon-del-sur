package com.example.tiendaretailsur.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiendaretailsur.data.GestorSesion
import com.example.tiendaretailsur.model.SeccionAjustesAdmin
import com.example.tiendaretailsur.ui.screens.AdminDashboardScreen
import com.example.tiendaretailsur.ui.screens.AjustesAdminScreen
import com.example.tiendaretailsur.ui.screens.FormularioConteoScreen
import com.example.tiendaretailsur.ui.screens.FormularioRegistroScreen
import com.example.tiendaretailsur.ui.screens.LoginScreen
import com.example.tiendaretailsur.ui.screens.MenuPrincipalScreen
import com.example.tiendaretailsur.ui.screens.PerfilScreen
import com.example.tiendaretailsur.ui.screens.PrimerIngresoScreen
import com.example.tiendaretailsur.ui.screens.RecuperarContrasenaScreen
import com.example.tiendaretailsur.ui.theme.MiRegistroTheme

@Composable
fun NavegacionApp(initialDarkTheme: Boolean = isSystemInDarkTheme()) {
    val context = LocalContext.current
    val gestorSesion = remember { GestorSesion(context) }

    var isAdmin by rememberSaveable { mutableStateOf(gestorSesion.obtenerIsAdmin()) }
    var isDarkTheme by rememberSaveable { mutableStateOf(initialDarkTheme) }
    var correoUsuarioLogueado by rememberSaveable { mutableStateOf(gestorSesion.obtenerCorreoUsuarioLogueado()) }
    var nombreUsuarioLogueado by rememberSaveable { mutableStateOf(gestorSesion.obtenerNombreUsuarioLogueado()) }
    var perfilUsuarioLogueado by rememberSaveable { mutableStateOf(gestorSesion.obtenerPerfilUsuarioLogueado()) }

    val navController = rememberNavController()

    fun destinoPorRol(perfil: String): String {
        return when (perfil.uppercase()) {
            "P-01", "JEFA DE TIENDA", "JEFA_TIENDA", "P-03", "SUPERVISOR" -> Ruta.AdminPanel.ruta
            "P-02", "SUBGERENTE" -> Ruta.AjustesAdmin.ruta
            else -> Ruta.MenuPrincipal.ruta
        }
    }

    val rutaInicial = remember {
        if (gestorSesion.haySesionActiva()) {
            destinoPorRol(gestorSesion.obtenerPerfilUsuarioLogueado())
        } else {
            Ruta.Login.ruta
        }
    }

    fun cerrarSesionYNavegar() {
        gestorSesion.cerrarSesion()
        correoUsuarioLogueado = ""
        nombreUsuarioLogueado = ""
        perfilUsuarioLogueado = ""
        isAdmin = false
        navController.navigate(Ruta.Login.ruta) {
            popUpTo(0) { inclusive = true }
        }
    }

    MiRegistroTheme(darkTheme = isDarkTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = rutaInicial,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Ruta.Login.ruta) {
                    LoginScreen(
                        onLoginSuccess = { usuarioAutenticado ->
                            correoUsuarioLogueado = usuarioAutenticado.correo
                            nombreUsuarioLogueado = usuarioAutenticado.nombreCompleto
                            perfilUsuarioLogueado = usuarioAutenticado.perfil
                            isAdmin = usuarioAutenticado.perfil.uppercase() in listOf(
                                "P-01", "JEFA_TIENDA", "JEFA DE TIENDA", "P-02", "SUBGERENTE", "P-03", "SUPERVISOR"
                            )
                            val destino = if (usuarioAutenticado.debeCambiarContrasena) {
                                Ruta.PrimerIngreso.ruta
                            } else {
                                destinoPorRol(usuarioAutenticado.perfil)
                            }
                            navController.navigate(destino) {
                                popUpTo(Ruta.Login.ruta) { inclusive = true }
                            }
                        },
                        onOlvideContrasenaClick = {
                            navController.navigate(Ruta.RecuperacionContrasena.ruta)
                        },
                        onCrearCuentaClick = {
                            navController.navigate(Ruta.FormularioRegistro.ruta)
                        }
                    )
                }

                composable(Ruta.PrimerIngreso.ruta) {
                    PrimerIngresoScreen(
                        rutOCorreoUsuario = correoUsuarioLogueado,
                        onConfiguracionCompletada = {
                            navController.navigate(destinoPorRol(perfilUsuarioLogueado)) {
                                popUpTo(Ruta.PrimerIngreso.ruta) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Ruta.RecuperacionContrasena.ruta) {
                    RecuperarContrasenaScreen(
                        onVolverALoginClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Ruta.FormularioRegistro.ruta) {
                    FormularioRegistroScreen(
                        onIrALoginClick = {
                            navController.navigate(Ruta.AjustesAdmin.ruta) {
                                popUpTo(Ruta.AjustesAdmin.ruta) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Ruta.MenuPrincipal.ruta) {
                    MenuPrincipalScreen(
                        isAdmin = isAdmin,
                        nombreUsuario = nombreUsuarioLogueado,
                        onIrAdminDashboard = {
                            navController.navigate(Ruta.AdminPanel.ruta)
                        },
                        onIrPerfil = {
                            navController.navigate(Ruta.Perfil.ruta)
                        },
                        onCerrarSesion = { cerrarSesionYNavegar() }
                    )
                }

                composable(Ruta.Perfil.ruta) {
                    PerfilScreen(
                        isAdmin = isAdmin,
                        correoLogueado = correoUsuarioLogueado,
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Ruta.FormularioConteo.ruta) {
                    FormularioConteoScreen(
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Ruta.AdminPanel.ruta) {
                    val esSupervisor = perfilUsuarioLogueado.uppercase() in listOf("P-03", "SUPERVISOR")
                    AdminDashboardScreen(
                        esSupervisor = esSupervisor,
                        mostrarAjustesAdmin = false,
                        onVolverInicio = {
                            navController.navigate(Ruta.AdminPanel.ruta) { launchSingleTop = true }
                        },
                        onAjustes = {
                            navController.navigate(Ruta.Perfil.ruta)
                        },
                        onCerrarSesionClick = { cerrarSesionYNavegar() }
                    )
                }

                composable(Ruta.AjustesAdmin.ruta) {
                    AjustesAdminScreen(
                        seccionInicial = SeccionAjustesAdmin.COLABORADORES,
                        onVolverClick = { cerrarSesionYNavegar() },
                        onIrAMiPerfilClick = { navController.navigate(Ruta.Perfil.ruta) },
                        onIrARegistroColaborador = { navController.navigate(Ruta.FormularioRegistro.ruta) },
                        onCerrarSesionClick = { cerrarSesionYNavegar() }
                    )
                }
            }
        }
    }
}