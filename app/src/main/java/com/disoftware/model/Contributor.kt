package com.disoftware.model

import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.gson.annotations.SerializedName

@Entity(
    primaryKeys = ["repoName", "repoOwner", "login"],
    foreignKeys = [ForeignKey(
        entity = Repo::class,
        parentColumns = ["name", "owner_login"], // propiedades de la clase Repo (PK)
        childColumns = ["repoName", "repoOwner"],
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Contributor (
    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("contributions")
    val contributions: Int,
    @field:SerializedName("avatar_url")
    val avatarUrl: String
) {
    // Variables asociadas a repo.kt con relación a primaryKeys = ["name", "owner_login"].
    lateinit var repoName: String // guarda el nombre del repo al cual pertenece esta contribucion.
    lateinit var repoOwner: String // prpietario del repo al que se esta contribuyendo.
}