package com.disoftware.model

import com.google.gson.annotations.SerializedName

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