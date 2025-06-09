package com.konstantinos.myworld.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [HexEntity::class],
    version = 1,
    exportSchema = true
)
abstract class HexDatabase : RoomDatabase() {
    abstract fun hexDao(): HexDAO

    companion object {
        @Volatile
        private var INSTANCE: HexDatabase? = null

        fun getDatabase(context: Context): HexDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    HexDatabase::class.java,
                    "hex_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}