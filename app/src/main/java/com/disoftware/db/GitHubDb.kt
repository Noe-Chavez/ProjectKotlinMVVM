package com.disoftware.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.disoftware.dao.RepoDao
import com.disoftware.dao.UserDao
import com.disoftware.model.Contributor
import com.disoftware.model.Repo
import com.disoftware.model.RepoSearchResult
import com.disoftware.model.User

/**
 * configuración DB.
 */

@Database(
    entities = [
        User::class,
        Repo::class,
        Contributor::class,
        RepoSearchResult::class
    ],
    version = 1
)
abstract class GitHubDb : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun repoDao(): RepoDao
}