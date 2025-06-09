package com.konstantinos.myworld.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HexEntity(
    @PrimaryKey val hex: Long
)
