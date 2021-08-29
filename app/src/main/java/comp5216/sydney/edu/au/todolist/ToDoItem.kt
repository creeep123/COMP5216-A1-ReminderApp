package comp5216.sydney.edu.au.todolist

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "todolist")
data class ToDoItem(
    @field:ColumnInfo(name = "toDoItemName") var toDoItemName: String,
    @ColumnInfo(name = "toDoItemDueTime") var toDoItemDueTime: String,
) {
    @PrimaryKey(autoGenerate = true)
    var toDoItemID = 0
}