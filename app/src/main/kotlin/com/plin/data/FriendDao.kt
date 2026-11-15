package com.plin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.plin.domain.models.Friend
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Insert
    suspend fun insert(friend: Friend)

    @Update
    suspend fun update(friend: Friend)

    @Delete
    suspend fun delete(friend: Friend)

    @Query("SELECT * FROM friends ORDER BY name ASC")
    fun observeAll(): Flow<List<Friend>>

    @Query("SELECT * FROM friends ORDER BY name ASC")
    suspend fun getAll(): List<Friend>

    @Query("SELECT * FROM friends WHERE id = :friendId LIMIT 1")
    suspend fun getById(friendId: String): Friend?
}
