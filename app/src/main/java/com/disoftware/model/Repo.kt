package com.disoftware.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

/**
 * @field:Embedded(prefix = "owner_"): Para acceder a las propiedades de la clase interna Owner.
 * Index ayudan al performance en las consultas sobre la DB, sin embargo relentiza las actualizaciones
 * e inserciones.
 */
@Entity(indices = [Index("id"), Index("owner_login")], primaryKeys = ["name", "owner_login"])
data class Repo (
    val id: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("full_name")
    val fullName: String,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("owner")
    @field:Embedded(prefix = "owner_")
    val owner: Owner,
    @field:SerializedName("stargazers_count")
    val stars: Int
) {
    data class Owner (
        @field:SerializedName("login")
        val loogin: String,
        @field:SerializedName("url")
        val url: String?
    )

    companion object {
        const val UNKOWN_ID = -1 // id siempre es posotivo, si no existe entonces toma un -1.
    }
}