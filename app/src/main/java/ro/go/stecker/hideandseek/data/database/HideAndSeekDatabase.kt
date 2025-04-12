package ro.go.stecker.hideandseek.data.database

import android.content.Context
import androidx.room.*
import ro.go.stecker.hideandseek.data.*

@Database(entities = [Card::class, DrawnCard::class], version = 1, exportSchema = false)
abstract class HideAndSeekDatabase: RoomDatabase() {
    abstract fun cardDao(): CardDao
    companion object {
        @Volatile
        private var Instance: HideAndSeekDatabase? = null
        fun getDatabase(context: Context): HideAndSeekDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, HideAndSeekDatabase::class.java, "deck_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}