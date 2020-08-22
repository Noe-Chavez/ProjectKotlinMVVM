package com.disoftware.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.disoftware.db.GithubTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(GithubTypeConverters::class)
class RepoSearchResult (
    val query: String,
    val repoIds: List<Int>, // con la clase GithubTypeConverters::class convierte a valor admitido por la DB
    val totalCount: Int?,
    val next: Int?
)