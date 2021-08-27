package comp5216.sydney.edu.au.todolist

import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.os.Bundle
import comp5216.sydney.edu.au.todolist.R
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.AdapterView
import android.content.DialogInterface
import androidx.activity.result.ActivityResultLauncher
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.result.ActivityResultCallback
import android.app.Activity
import android.view.View
import android.widget.Toast
import android.widget.AdapterView.OnItemClickListener
import comp5216.sydney.edu.au.todolist.EditToDoItemActivity

class EditToDoItemActivity : Activity() {
    var position = 0
    var etItem: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Populate the screen using the layout
        setContentView(R.layout.activity_edit_item)

        // Get the data from the main activity screen
        val editItem = intent.getStringExtra("item")
        position = intent.getIntExtra("position", -1)

        // Show original content in the text field
        etItem = findViewById<View?>(R.id.etEditItem) as EditText
        etItem!!.setText(editItem)
    }

    fun onSubmit(v: View?) {
        etItem = findViewById<View?>(R.id.etEditItem) as EditText

        // Prepare data intent for sending it back
        val data = Intent()

        // Pass relevant data back as a result
        data.putExtra("item", etItem!!.getText().toString())
        data.putExtra("position", position)

        // Activity finishes OK, return the data
        setResult(RESULT_OK, data) // Set result code and bundle data for response
        finish() // Close the activity, pass data to parent
    }
}