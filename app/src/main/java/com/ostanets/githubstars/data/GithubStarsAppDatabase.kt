package com.ostanets.githubstars.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GithubUser::class, GithubRepository::class, GithubStargazer::class],
    version = 1, exportSchema = false)
abstract class GithubStarsAppDatabase : RoomDatabase() {

    abstract fun getGithubStarsDao(): GithubStarsDao

    companion object {

        private const val DATABASE_NAME = "github_stars"

        @Volatile
        private var INSTANCE: GithubStarsAppDatabase? = null

        fun getDatabase(context: Context): GithubStarsAppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GithubStarsAppDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}