package com.isetr.tp5app.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Data class correspondant exactement au JSON renvoyé par l'API :
 * {"cin":123,"nom":"Ahmed","prenom":"Ben Ahmed","mail":"Ahmed@yahoo.com","password":"AAAA","classe":"DSI"}
 * Les champs String sont nullables : l'API peut renvoyer null.
 */
data class Etudiant(
    @SerializedName("cin") val cin: Int = 0,
    @SerializedName("nom") val nom: String? = null,
    @SerializedName("prenom") val prenom: String? = null,
    @SerializedName("mail") val mail: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("classe") val classe: String? = null
) : Serializable
