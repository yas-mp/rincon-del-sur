package com.example.tiendaretailsur.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java

object RetrofitClient {

    private const val URL_ROLES = "http://10.0.2.2:8081/"
    private const val URL_USUARIOS = "http://10.0.2.2:8082/"
    private const val URL_ACCESOS = "http://10.0.2.2:8083/"
    private const val URL_CONTEOS = "http://10.0.2.2:8084/"

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