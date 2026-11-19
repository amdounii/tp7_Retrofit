package com.isetr.tp5app.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EtudiantApi {

    // @GET : "api/etudiants" est l'endpoint pour récupérer la liste
    @GET("api/etudiants")
    suspend fun getEtudiants(): Response<List<Etudiant>>

    // @POST pour ajouter un étudiant : l'objet est envoyé dans le corps de la requête (@Body)
    @POST("api/addEtudiant")
    suspend fun addEtudiant(@Body etudiant: Etudiant): Response<Etudiant>

    // @PUT pour mettre à jour un étudiant (le backend renvoie un corps vide => Unit)
    @PUT("api/update")
    suspend fun updateEtudiant(@Body etudiant: Etudiant): Response<Unit>

    // @GET un étudiant par son cin
    @GET("api/etudiant/{cin}")
    suspend fun getEtudiant(@Path("cin") cin: Int): Response<Etudiant>

    // @DELETE (le backend renvoie un texte brut, ignoré => Unit)
    @DELETE("api/delete/{cin}")
    suspend fun deleteEtudiant(@Path("cin") cin: Int): Response<Unit>
}
