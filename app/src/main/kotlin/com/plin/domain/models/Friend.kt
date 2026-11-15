package com.plin.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A friend in the Friend of the Week pool.
 *
 * [id] is a stable UUID used to track friend data across weekly assignments.
 * [frequency] is collected by the Friends UI (1–5); selection does not use it yet.
 */
@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey val id: String,
    val name: String,
    val frequency: Int,
) {
    companion object {
        fun create(name: String, frequency: Int): Friend {
            val trimmed = name.trim()
            return Friend(
                id = UUID.randomUUID().toString(),
                name = trimmed,
                frequency = frequency,
            )
        }
    }
}
