package com.universidad.taskmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TaskEntity::class, PostEntity::class],  // ← agregar PostEntity
    version = 3,                                         // ← incrementar a 3
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun postDao(): PostDao                      // ← agregar

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "taskmanager.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)  // ← agregar MIGRATION_2_3
                    .build()
                    .also { INSTANCE = it }
            }
    }
}