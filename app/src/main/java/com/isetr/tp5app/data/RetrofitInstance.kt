package com.isetr.tp5app.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // Port de votre API locale (Spring Boot)
    private const val API_PORT = 8082

    // --- UN SEUL BASE_URL ACTIF À LA FOIS ---

    // 1. ÉMULATEUR ANDROID + API locale (Partie C : ajout, modification, suppression)
    private const val BASE_URL = "http://10.0.2.2:$API_PORT/"

    // 2. APPAREIL PHYSIQUE (mettre l'IPv4 de votre PC, voir ipconfig)
    // private const val LOCAL_IP = "192.168.1.5"
    // private const val BASE_URL = "http://$LOCAL_IP:$API_PORT/"

    // 3. API distante (Partie B : lecture seule, base partagée avec tous)
    // private const val BASE_URL = "https://gestionetudiantsnew-copy-production.up.railway.app/"

    // Délais d'attente pour la connexion, la lecture et l'écriture
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // lazy : une seule instance de Retrofit créée au premier appel
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // Fournit l'implémentation de l'interface EtudiantApi
    val api: EtudiantApi by lazy {
        retrofit.create(EtudiantApi::class.java)
    }
}