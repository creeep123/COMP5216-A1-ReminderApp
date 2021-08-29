package comp5216.sydney.edu.au.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    // Define variables
    private var listView: ListView? = null
    private var displayItems: ArrayList<String?>? = null
    private var databaseItems: ArrayList<String?>? = null

    //[["task1","2021-9-29 19:45"],[],[]]
    private var itemsAdapter: ArrayAdapter<String?>? = null
    private val EDIT_ITEM_REQUEST_CODE = 647
    private var addItemEditText: EditText? = null
    private var toDoItemDao: ToDoItemDao? = null

    private fun calculateDueTime(dueTimeString: String?): String {
        if(dueTimeString==""){
            return "LONG-TERM TODO"
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val timeRemain: Long = sdf.parse(dueTimeString).time
        val curTime: Long? = Date().time
        val between: Long = (timeRemain - curTime!!) / 1000 //除以1000是为了转换成秒
        val day1 = between / (24 * 3600)
        val hour1 = between % (24 * 3600) / 3600
        val minute1 = between % 3600 / 60
        val second1 = between % 60 / 60

        return "Time Remain: $day1 days $hour1 hours $minute1 minutes "
    }

    private fun updateDisplayItems(){
        if(databaseItems!!.isEmpty()){
            return
        }
        for (item in databaseItems!!){

        }
    }

    // Register a request to start an activity for result and register the result callback
    private val mLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult? ->
        if (result?.resultCode == RESULT_OK) {
            // Extract name value from result extras
            val editedItem = result?.data?.extras?.getString("item")
            val dueTimeString = result?.data?.extras?.getString("dueTimeString","")
            val position = result?.data?.getIntExtra("position", -1)
            val remainTime = calculateDueTime(dueTimeString)
            if(position==-1){
                //创建新任务
                displayItems?.add("$editedItem\n$remainTime")
            }else{
                //编辑已有任务
                displayItems?.set(position!!, "$editedItem\n$remainTime")
                Log.i("Updated item in list ", "$editedItem, position: $position")
                // Make a standard toast that just contains text
                Toast.makeText(applicationContext, "Updated: $editedItem", Toast.LENGTH_SHORT)
                    .show()
            }
            itemsAdapter?.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main)

        // Reference the "listView" variable to the id "lstView" in the layout
        listView = findViewById<View?>(R.id.lstView) as ListView
        addItemEditText = findViewById<View?>(R.id.txtNewItem) as EditText

        // No need to cancel this scope as it'll be torn down with the process
        val applicationScope = CoroutineScope(SupervisorJob())
        // Using by lazy so the database and the repository are only created when they're needed
        // rather than when the application starts
        val database by lazy { ToDoItemDB.getDatabase(this, applicationScope) }
        toDoItemDao = database.toDoItemDao()

        // Must call it before creating the adapter, because it references the right item list
        readItemsFromDatabase(toDoItemDao!!)

        // Create an adapter for the list view using Android's built-in item layout
        itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayItems!!)

        // Connect the listView and the adapter
        listView!!.adapter = itemsAdapter

        // Setup listView listeners
        setupListViewListener()
    }

    fun onAddItemClick(view: View?) {
        val toAddString = addItemEditText?.text.toString()
        if (toAddString != null && toAddString.length > 0) {
//            itemsAdapter?.add(toAddString)
            addItemEditText?.setText("")
//            saveItemsToDatabase()
            val intent = Intent(this@MainActivity, EditToDoItemActivity::class.java)
            if (intent != null) {
                intent.putExtra("item", toAddString)
                // bring up the second activity
                mLauncher.launch(intent)
                itemsAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_ITEM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Extract name value from result extras
                val editedItem = data!!.extras!!.getString("item")
                val position = data.getIntExtra("position", -1)
                displayItems!![position] = editedItem
                Log.i(
                    "Updated Item in list:", editedItem + ",position:"
                            + position
                )
                Toast.makeText(this, "updated:$editedItem", Toast.LENGTH_SHORT).show()
                itemsAdapter!!.notifyDataSetChanged()
//                saveItemsToFile()
                saveItemsToDatabase()
            }
        }
    }

    private fun setupListViewListener() {
        listView?.onItemLongClickListener = OnItemLongClickListener { parent, view, position, rowId ->
            Log.i("MainActivity", "Long Clicked item $position")
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_msg)
                .setPositiveButton(R.string.delete) { dialogInterface, i ->
                    displayItems?.removeAt(position) // Remove item from the ArrayList
                    itemsAdapter?.notifyDataSetChanged() // Notify listView adapter to update the list
                    saveItemsToDatabase()
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    // User cancelled the dialog
                    // Nothing happens
                }
            builder.create().show()
            true
        }

        listView?.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val updateItem = itemsAdapter?.getItem(position) as String?
            Log.i("MainActivity", "Clicked item $position: $updateItem")
            val intent = Intent(this@MainActivity, EditToDoItemActivity::class.java)
            if (intent != null) {
                // put "extras" into the bundle for access in the edit activity
                intent.putExtra("item", updateItem)
//                intent.putExtra("due_time",)
                intent.putExtra("position", position)

                // bring up the second activity
                mLauncher.launch(intent)
                itemsAdapter?.notifyDataSetChanged()
            }
        }
    }

    // read displayItems from the database
    private fun readItemsFromDatabase(toDoItemDao: ToDoItemDao) {
        try {
            displayItems = ArrayList()
            GlobalScope.launch {
                getItems()
            }
        }catch (e: Exception){
            Log.i("Exception thrown", e.message.toString())
        }
    }
    private suspend fun getItems() {
        withContext(Dispatchers.IO) {
            // Get entities from database on IO thread.
            val ids = toDoItemDao?.getAllIds()
            ids?.forEach { id ->
                val item = toDoItemDao?.getById(id)
                displayItems!!.add(item?.toDoItemName + "" + item?.toDoItemDueTime)
            }
        }
    }

    // write displayItems to the database
    private fun saveItemsToDatabase() {
        try {
            GlobalScope.launch {
                deleteAll()
                for (todo in displayItems!!) {
                    Log.i("SQLite saved item", todo!!)
                    val item = ToDoItem(todo,"2021/06/11 10:00")
                    Log.i("item--->", item.toString())
                    insert(item)
                }
            }
        }catch (e:Exception){
            Log.i("Exception thrown", e.message.toString())
        }
    }
    suspend fun insert(toDoItem: ToDoItem) {
        toDoItemDao?.insert(toDoItem)
    }
    private suspend fun deleteAll() {
        toDoItemDao?.deleteAll()
    }
}