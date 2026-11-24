package com.example.tiendaretailsur.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java

object RetrofitClient {

    private const val URL_GATEWAY = "https://htf54zfs-8080.brs.devtunnels.ms/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_GATEWAY)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val rolApi: RolApi by lazy {
        retrofit.create(RolApi::class.java)
    }

    val usuariosApi: UsuariosApi by lazy {
        retrofit.create(UsuariosApi::class.java)
    }

    val avatarApi: AvatarApi by lazy {
        retrofit.create(AvatarApi::class.java)
    }

    val accesosApi: AccesosApi by lazy {
        retrofit.create(AccesosApi::class.java)
    }

    val conteosApi: ConteosApi by lazy {
        retrofit.create(ConteosApi::class.java)
    }
}