package com.tobey.catapp

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.tobey.catapp.models.Ticket
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.tobey.catapp.ui.tickets.tickets
import org.json.JSONArray
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class TicketActivity : AppCompatActivity() {
    private lateinit var ticket: Ticket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
        setSupportActionBar(findViewById(R.id.toolbar))
        ticket = Ticket()

        ticket.record_id = getIntent().getStringExtra("record_id") ?: "Nil"
        ticket.name = getIntent().getStringExtra("name") ?: "Nil"
        ticket.source = getIntent().getStringExtra("source") ?: "Nil"
        ticket.destination = getIntent().getStringExtra("destination") ?: "Nil"
        ticket.ticket_no = getIntent().getStringExtra("ticket_no") ?: "Nil"
        ticket.date = getIntent().getStringExtra("date") ?: "Nil"
        ticket.image = getIntent().getStringExtra("image") ?: "Nil"

        val ownerName: TextView = findViewById(R.id.owner_name)
        val startingFrom: TextView = findViewById(R.id.starting_from)
        val destination: TextView = findViewById(R.id.destination)
        val ticketNumber: TextView = findViewById(R.id.ticket_number)
        val date: TextView = findViewById(R.id.ticket_date)
        val deletion: TextView = findViewById(R.id.deletion_button)

        val image: ImageView = findViewById(R.id.ticket_image)

        ownerName.text = ticket.name
        startingFrom.text = ticket.source
        destination.text = ticket.destination
        ticketNumber.text = ticket.ticket_no
        date.text = ticket.date


        deletion.setOnClickListener {
            basicAlert(ticket.ticket_no).run {
                Toast.makeText(applicationContext,
                        android.R.string.yes, Toast.LENGTH_SHORT).show()
            }

//            val task = AsyncTicketAction(this)
//            task.id = ticket.ticket_no
//
//            val result = task.execute().get()
//
//            Log.d("RESULT", result)
        }

    }

    private fun basicAlert(id: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning")
        builder.setMessage("Are you sure you want to delete this ticket? $id")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(applicationContext,
                    android.R.string.yes, Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            deleteFun(id)
        }

//        builder.setNeutralButton("Maybe") { dialog, which ->
//            Toast.makeText(applicationContext,
//                    "Maybe", Toast.LENGTH_SHORT).show()
//        }
        builder.show()
    }

    private fun deleteFun(id: String) {
        val task = AsyncTicketAction(this)
        task.id = id
        try {
            val result = task.execute().get()

            Log.d("RESULT", result)
            Toast.makeText(applicationContext,
                    android.R.string.no, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.d("RESULT", e.toString())
        }

    }

    companion object {
        class AsyncTicketAction internal constructor(context: Context) : AsyncTask<String, String, String>() {
            lateinit var con: HttpURLConnection
            lateinit var resulta:String
            val builder = Uri.Builder()
            private val cont: Context = context
            var id = ""


            override fun onPreExecute() {
                super.onPreExecute()
                Log.d("RESULT", "result")

                val progressBar = ProgressBar(cont)
                progressBar.isIndeterminate = true
                progressBar.visibility = View.VISIBLE
                builder .appendQueryParameter("key", "oooo")
                builder .appendQueryParameter("ticket_id", id)
            }

            override fun doInBackground(vararg params: String?):  String? {
                try {

                    val query = builder.build().encodedQuery
                    val url = "http://165.22.218.191/delete_ticket.php"
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

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                try {
                    try {

                    } catch (e: Exception) {
                        Log.v("error", e.toString() )
                    }
                } catch (e: Exception) {
                    Log.d("Exception", e.toString());
                }
            }
        }

    }

}