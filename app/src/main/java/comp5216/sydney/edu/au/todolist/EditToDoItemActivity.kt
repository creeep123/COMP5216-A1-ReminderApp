package comp5216.sydney.edu.au.todolist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.DatePicker.OnDateChangedListener
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class EditToDoItemActivity : Activity() {
    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    var position = 0
//    var dueTimeString = ""
    var etItem: EditText? = null
    var etDueTime: TimePicker? = null
    var etDueDate: DatePicker? = null
    var txViewDeadline: TextView? = null
    var deadLineString: String? = null

//    var formatter: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_item)
        etItem = findViewById<View?>(R.id.etEditItem) as EditText
        etDueTime = findViewById<View?>(R.id.timepicker) as TimePicker
        etDueDate = findViewById<View?>(R.id.datepicker) as DatePicker
        txViewDeadline = findViewById<View?>(R.id.textViewDeadline) as TextView

        // Get the default data from the main activity screen
        val itemTitle = intent.getStringExtra("item")
        etItem!!.setText(itemTitle)
        position = intent.getIntExtra("position", -1)

        //获取当前日期
        val calendar: Calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        //监听datepicker选择结果
        etDueDate!!.init(year, month, day,
            OnDateChangedListener { view, year, monthOfYear, dayOfMonth -> //获取选中的年月日
                this@EditToDoItemActivity.year = year
                //月份是从0开始的
                this@EditToDoItemActivity.month = monthOfYear + 1
                this@EditToDoItemActivity.day = dayOfMonth
                //弹窗显示
//                Toast.makeText(
//                    this@EditToDoItemActivity,
//                    this@EditToDoItemActivity.year.toString() + "年" + this@EditToDoItemActivity.month + "月" + this@EditToDoItemActivity.day + "日",
//                    Toast.LENGTH_SHORT
//                ).show()
            })

        if(position!==-1){//编辑任务
//            etDueTime
        }
    }

    fun onSubmit(v: View?) {
        etItem = findViewById<View?>(R.id.etEditItem) as EditText
        etDueTime = findViewById<View?>(R.id.timepicker) as TimePicker
        etDueDate = findViewById<View?>(R.id.datepicker) as DatePicker

        // Prepare data intent for sending it back
        val data = Intent()
        val decimalFormat = DecimalFormat("00")
        val hour = decimalFormat.format(etDueTime!!.hour)
        val minute = decimalFormat.format(etDueTime!!.minute)
        deadLineString = "$year-$month-$day $hour:$minute"
        Log.i("deadLineString", deadLineString!!)

        // Pass relevant data back as a result
        data.putExtra("item", etItem!!.getText().toString())
        data.putExtra("dueTimeString", deadLineString)
        data.putExtra("position", position)

        Toast.makeText(
            this@EditToDoItemActivity,
            deadLineString,
            Toast.LENGTH_SHORT
        ).show()


        // Activity finishes OK, return the data
        setResult(RESULT_OK, data) // Set result code and bundle data for response
        finish() // Close the activity, pass data to parent
    }
}