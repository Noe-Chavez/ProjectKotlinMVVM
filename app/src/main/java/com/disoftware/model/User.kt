package com.disoftware.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

/**
 * Clase de tipo data, implementa setters y getters, así como otros métodos por nosotros.
 * @field: Seraliza los datos del JSon a las variables establecidas (el nombre es que se describe
 * en el JSon).
 * @Entity: Establace esta clase como una entidad en la base de datos, estableciendo la propiedad
 * login como PK.
 */

@Entity(primaryKeys = ["login"])
data class User (
    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("avatar_url")
    val avatarUrk: String?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("company")
    val company: String?,
    @field:SerializedName("repos_url")
    val resposUrl: String?,
    @field:SerializedName("blog")
    val blog: String?
)