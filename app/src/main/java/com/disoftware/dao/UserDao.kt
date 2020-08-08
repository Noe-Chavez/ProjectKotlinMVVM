package com.disoftware.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.disoftware.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Cuando encuentra un id ya existente lo actualiza.
    fun insert(user: User)

    @Query("SELECT * FROM user WHERE login = :login")
    fun findByLogin(login: String): LiveData<User>
}