package com.segunfrancis.redditclient.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SubRedditEntity::class], version = 1, exportSchema = true)
abstract class RedditClientDatabase : RoomDatabase() {
    abstract fun getDao(): RedditClientDao

    companion object {
        @Volatile
        var INSTANCE: RedditClientDatabase? = null
        fun getDatabase(context: Context): RedditClientDatabase? {
            return try {
                INSTANCE ?: synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        RedditClientDatabase::class.java,
                        "country_holiday_database"
                    )
                        .build()
                    INSTANCE
                }
            } catch (e: Throwable) {
                null
            }
        }
    }
}
