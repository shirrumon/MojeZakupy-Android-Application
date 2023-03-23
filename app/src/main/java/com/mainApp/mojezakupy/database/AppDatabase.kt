package com.mainApp.mojezakupy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mainApp.mojezakupy.database.dataAccessObject.TaskDAO
import com.mainApp.mojezakupy.database.dataAccessObject.TaskListDAO
import com.mainApp.mojezakupy.database.entity.TaskEntity
import com.mainApp.mojezakupy.database.entity.TaskListEntity

@Database(entities = [
    TaskListEntity::class,
    TaskEntity::class],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskListDAO(): TaskListDAO
    abstract fun tasksDAO(): TaskDAO

    companion object {
        @Volatile
        private var DB_INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = DB_INSTANCE

            if(tempInstance !== null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database",
                ).fallbackToDestructiveMigration().build()
                DB_INSTANCE = instance

                return instance
            }
        }
    }
}