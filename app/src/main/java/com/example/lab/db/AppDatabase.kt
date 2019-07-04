package com.example.lab.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import com.example.lab.db.dao.*
import com.example.lab.db.models.*


@Database(entities = [Complaint::class, User::class, Commentary::class, ComplaintVote::class, Product::class,
    Cart::class,ProductCart::class], version = 5, exportSchema = false)
public abstract class AppDatabase : RoomDatabase() {

    abstract fun complaintDao(): ComplaintDao
    abstract fun userDao(): UserDao
    abstract fun commentaryDao(): CommentaryDao
    abstract fun productDao(): ProductDao
    abstract fun complaintVoteDao(): ComplaintVoteDao
    abstract fun productCartDao(): ProductCartDao
    abstract fun cartDao(): CartDao


    // Add Singleton to prevent having multiple instances
    // of the database opened at the same time.
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }
        }
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }
        }
    }



}