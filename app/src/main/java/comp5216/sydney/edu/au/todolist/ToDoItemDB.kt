package comp5216.sydney.edu.au.todolist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(ToDoItem::class), version = 1, exportSchema = false)
public abstract class ToDoItemDB : RoomDatabase() {

    abstract fun toDoItemDao(): ToDoItemDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ToDoItemDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ToDoItemDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoItemDB::class.java,
                    "word_database"
                )

                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}