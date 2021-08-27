package comp5216.sydney.edu.au.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import comp5216.sydney.edu.au.todolist.R
import android.widget.AdapterView.OnItemLongClickListener
import android.content.DialogInterface
import androidx.activity.result.ActivityResultLauncher
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.result.ActivityResultCallback
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AlertDialog
import comp5216.sydney.edu.au.todolist.EditToDoItemActivity
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    // Define variables
    var listView: ListView? = null
    var items: ArrayList<String?>? = null
    var itemsAdapter: ArrayAdapter<String?>? = null
    var addItemEditText: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main)

        // Reference the "listView" variable to the id "lstView" in the layout
        listView = findViewById<View?>(R.id.lstView) as ListView
        addItemEditText = findViewById<View?>(R.id.txtNewItem) as EditText

        // Create an ArrayList of String
        items = ArrayList()
        items!!.add("item one")
        items!!.add("item two")

        // Create an adapter for the list view using Android's built-in item layout
        itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items!!)

        // Connect the listView and the adapter
        listView!!.setAdapter(itemsAdapter)

        // Setup listView listeners
        setupListViewListener()
    }

    fun onAddItemClick(view: View?) {
        val toAddString = addItemEditText?.getText().toString()
        if (toAddString != null && toAddString.length > 0) {
            itemsAdapter?.add(toAddString) // Add text to list view adapter
            addItemEditText?.setText("")
        }
    }

    private fun setupListViewListener() {
        listView?.setOnItemLongClickListener(OnItemLongClickListener { parent, view, position, rowId ->
            Log.i("MainActivity", "Long Clicked item $position")
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_msg)
                .setPositiveButton(R.string.delete) { dialogInterface, i ->
                    items?.removeAt(position) // Remove item from the ArrayList
                    itemsAdapter?.notifyDataSetChanged() // Notify listView adapter to update the list
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    // User cancelled the dialog
                    // Nothing happens
                }
            builder.create().show()
            true
        })

        // Register a request to start an activity for result and register the result callback
        val mLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result?.getResultCode() == RESULT_OK) {
                // Extract name value from result extras
                val editedItem = result?.getData()?.getExtras()?.getString("item")
                val position = result?.getData()?.getIntExtra("position", -1)
                items?.set(position!!, editedItem)
                Log.i("Updated item in list ", "$editedItem, position: $position")

                // Make a standard toast that just contains text
                Toast.makeText(applicationContext, "Updated: $editedItem", Toast.LENGTH_SHORT)
                    .show()
                itemsAdapter?.notifyDataSetChanged()
            }
        }
        listView?.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val updateItem = itemsAdapter?.getItem(position) as String?
            Log.i("MainActivity", "Clicked item $position: $updateItem")
            val intent = Intent(this@MainActivity, EditToDoItemActivity::class.java)
            if (intent != null) {
                // put "extras" into the bundle for access in the edit activity
                intent.putExtra("item", updateItem)
                intent.putExtra("position", position)

                // bring up the second activity
                mLauncher.launch(intent)
                itemsAdapter?.notifyDataSetChanged()
            }
        })
    }
}