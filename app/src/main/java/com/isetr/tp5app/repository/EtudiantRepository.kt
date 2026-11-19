package com.isetr.tp5app.repository

import com.isetr.tp5app.data.Etudiant
import com.isetr.tp5app.data.EtudiantApi
import retrofit2.Response

/**
 * Le Repository est l'unique source de vérité : il masque le "comment" (HTTP / Retrofit)
 * et expose le "quoi" (les données) au ViewModel.
 */
class EtudiantRepository(private val api: EtudiantApi) {

    suspend fun getEtudiants(): Response<List<Etudiant>> = api.getEtudiants()

    suspend fun getEtudiant(cin: Int): Response<Etudiant> = api.getEtudiant(cin)

    suspend fun addEtudiant(etudiant: Etudiant): Response<Etudiant> = api.addEtudiant(etudiant)

    suspend fun updateEtudiant(etudiant: Etudiant): Response<Unit> = api.updateEtudiant(etudiant)

    suspend fun deleteEtudiant(cin: Int): Response<Unit> = api.deleteEtudiant(cin)
}
