package com.tobey.catapp.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.tobey.catapp.R
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class HomeFragment : Fragment(), DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var sourceDate: EditText
  private lateinit var destinationDate: EditText

  var day = 0
  var month: Int = 0
  var year: Int = 0
  var hour: Int = 0
  var minute: Int = 0
  var myDay = 0
  var myMonth: Int = 0
  var myYear: Int = 0
  var myHour: Int = 0
  var myMinute: Int = 0
  var dateTime: String = ""
  var sourceSet: Boolean = false

  private lateinit var homeViewModel: HomeViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_home, container, false)

    val ticketNum = java.util.UUID.randomUUID().toString()
    val customerId: EditText = root.findViewById(R.id.customer_id)
    val createButton: Button = root.findViewById(R.id.create_ticket_button)
    val ticketNumber: TextView = root.findViewById(R.id.ticket_number)
    ticketNumber.text = ticketNum

    sourceDate = root.findViewById(R.id.start_time)

    val textView = root.findViewById(R.id.autocomplete_country) as AutoCompleteTextView
    val startDatePicker: Button = root.findViewById(R.id.start_date_picker)

    val countries: Array<out String> = resources.getStringArray(R.array.countries_array)
    ArrayAdapter<String>(root.context, android.R.layout.simple_list_item_1, countries).also { adapter ->
      textView.setAdapter(adapter)
    }

    val destinationView = root.findViewById(R.id.destination) as AutoCompleteTextView
    ArrayAdapter<String>(root.context, android.R.layout.simple_list_item_1, countries).also { adapter ->
      destinationView.setAdapter(adapter)
    }


    startDatePicker.setOnClickListener {
      sourceSet = true
      val calendar: Calendar = Calendar.getInstance()
      day = calendar.get(Calendar.DAY_OF_MONTH)
      month = calendar.get(Calendar.MONTH)
      year = calendar.get(Calendar.YEAR)

      val datePickerDialog =
              DatePickerDialog(root.context, this, year, month,day)
      datePickerDialog.show()
    }

    fun doClean() {
      textView.setText("", TextView.BufferType.EDITABLE)
      destinationView.setText("", TextView.BufferType.EDITABLE)
      customerId.setText("", TextView.BufferType.EDITABLE)
      sourceDate.setText("", TextView.BufferType.EDITABLE)
    }

    createButton.setOnClickListener {
      val task = AsyncTicketAction(root.context)
      task.ticketID = ticketNum
      task.source = textView.text.toString()
      task.destination = destinationView.text.toString()
      task.customerId = customerId.text.toString()
      task.startTime = dateTime

      if (dateTime.isNotEmpty() && customerId.text.toString().isNotEmpty()
              && destinationView.text.toString().isNotEmpty() && textView.text.toString().isNotEmpty()) {
        if (destinationView.text.toString() == textView.text.toString()) {
          Toast.makeText(root.context, "Error Source and destination must be different", Toast.LENGTH_SHORT).show()
          doClean()
        } else {
          val result = task.execute().get()
          val parsedResult = JSONObject(result)

          when {
              parsedResult.optString("code") == "1" -> {
                Toast.makeText(root.context, "Success", Toast.LENGTH_SHORT).show()
                doClean()
              }
              parsedResult.optString("code") == "2" -> {
                Toast.makeText(root.context, "Error: User with customer id ${customerId.text} does not exit", Toast.LENGTH_SHORT).show()
                doClean()
              }
              else -> {
                Toast.makeText(root.context, "Error", Toast.LENGTH_SHORT).show()
              }
          }
        }
      } else {
        Toast.makeText(root.context, "Error all fields must be filled", Toast.LENGTH_SHORT).show()
      }
    }


    return root
  }

  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    myDay = dayOfMonth
    myYear = year
    myMonth = month

    val calendar: Calendar = Calendar.getInstance()
    hour = calendar.get(Calendar.HOUR)
    minute = calendar.get(Calendar.MINUTE)
    val timePickerDialog = TimePickerDialog(context, this, hour, minute,
            DateFormat.is24HourFormat(context))
    timePickerDialog.show()
  }

  override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
    myHour = hourOfDay
    myMinute = minute

    var _month = myMonth.toString()
    if (myMonth < 10) _month = "0$myMonth"

    dateTime = "$myYear-$_month-$myDay $myHour:$myMinute"
    sourceDate.setText(dateTime, TextView.BufferType.EDITABLE);
  }

  companion object {
    class AsyncTicketAction internal constructor(context: Context) : AsyncTask<String, String, String>() {
      lateinit var con: HttpURLConnection
      lateinit var resulta:String
      val builder = Uri.Builder()
      private val cont: Context = context
      var source = ""
      var destination = ""
      var ticketID = ""
      var startTime = ""
      var customerId = ""


      override fun onPreExecute() {
        super.onPreExecute()

        val progressBar = ProgressBar(cont)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        builder .appendQueryParameter("key", "oooo")
        builder .appendQueryParameter("ticket_number", ticketID)
        builder .appendQueryParameter("source", source)
        builder .appendQueryParameter("destination", destination)
        builder .appendQueryParameter("travel_datetime", startTime)
        builder .appendQueryParameter("customer_id", customerId)
      }

      override fun doInBackground(vararg params: String?):  String? {
        try {

          val query = builder.build().encodedQuery
          val url = "http://165.22.218.191/new_ticket.php"
          val obj = URL(url)

          con = obj.openConnection() as HttpURLConnection
          con.setRequestMethod("POST")
          con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)")
          con.setRequestProperty("Accept-Language", "UTF-8")
          con.setDoOutput(true)
          val outputStreamWriter = OutputStreamWriter(con.getOutputStream())
          outputStreamWriter.write(query)
          outputStreamWriter.flush()

          Log.e("pass 2", "connection success ")
        } catch (e: Exception) {
          Log.e("Fail 2", e.toString())
        }

        try {
          resulta = con.inputStream.bufferedReader().readText()
          Log.e("data", resulta)
        } catch (e: java.lang.Exception) {
          Log.e("Fail 4", e.toString())
        }

        return resulta
      }
    }

  }
}