package com.konstantinos.myworld.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HexDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHex(hex: HexEntity)

    @Query("SELECT * FROM HexEntity WHERE hex = :hex")
    suspend fun getHex(hex: Long): HexEntity?

    @Query("SELECT * FROM HexEntity")
    fun getAll(): Flow<List<HexEntity>>

    @Query("SELECT EXISTS(SELECT * FROM HexEntity WHERE hex = :hex)")
    suspend fun isExplored(hex: Long): Boolean

    @Query("SELECT COUNT(*) FROM HexEntity")
    fun getHexCount(): Flow<Long>
}