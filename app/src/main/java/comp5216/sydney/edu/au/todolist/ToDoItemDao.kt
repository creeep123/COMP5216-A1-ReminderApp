package comp5216.sydney.edu.au.todolist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoItemDao {

    @Query("SELECT * FROM todolist")
    fun getAlphabetizedWords(): Flow<List<ToDoItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(toDoItem: ToDoItem)

    @Query("DELETE FROM todolist")
    suspend fun deleteAll()

    @Query("SELECT toDoItemID FROM todolist")
    fun getAllIds(): List<Int> // If your primary key is Integer.

    @Query("SELECT * FROM todolist WHERE toDoItemID = :id")
    fun getById(id: Int): ToDoItem?

}