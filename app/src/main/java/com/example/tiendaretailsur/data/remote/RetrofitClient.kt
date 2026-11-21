package com.example.tiendaretailsur.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java

object RetrofitClient {

    private const val URL_ROLES = "https://4n1b70n0-8083.brs.devtunnels.ms/"
    private const val URL_USUARIOS = "https://4n1b70n0-8084.brs.devtunnels.ms/"
    private const val URL_ACCESOS = "https://4n1b70n0-8081.brs.devtunnels.ms/"
    private const val URL_CONTEOS = "https://4n1b70n0-8082.brs.devtunnels.ms/"
    private fun crear(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val rolApi: RolApi by lazy {
        crear(URL_ROLES).create(RolApi::class.java)
    }

    val usuariosApi: UsuariosApi by lazy {
        crear(URL_USUARIOS).create(UsuariosApi::class.java)
    }

    val avatarApi: AvatarApi by lazy {
        crear(URL_USUARIOS).create(AvatarApi::class.java)
    }

    val accesosApi: AccesosApi by lazy {
        crear(URL_ACCESOS).create(AccesosApi::class.java)
    }

    val conteosApi: ConteosApi by lazy {
        crear(URL_CONTEOS).create(ConteosApi::class.java)
    }
}