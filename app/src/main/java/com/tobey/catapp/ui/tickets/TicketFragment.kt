package com.tobey.catapp.ui.tickets

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobey.catapp.R
import com.tobey.catapp.adapters.AllTicketAdapters
import com.tobey.catapp.models.Ticket
import org.json.JSONArray
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

val tickets: ArrayList<Ticket> = ArrayList()

class TicketFragment : Fragment() {
    private lateinit var homeViewModel: TicketViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var ticketS: ArrayList<Ticket> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(TicketViewModel::class.java)
        val root = inflater.inflate(R.layout.activity_all_tickets, container, false)

        recyclerView = root.findViewById(R.id.all_tickets_recycler)

        val result = AllTicketsAsync(root.context).execute().get()
        val parsedResult = JSONArray(result)

        for (data in 0 until parsedResult.length()) {
            val ticket = Ticket()
            ticket.name = parsedResult.getJSONObject(data).optString("name")
            ticket.source = parsedResult.getJSONObject(data).optString("source")
            ticket.destination = parsedResult.getJSONObject(data).optString("destination")
            ticket.ticket_no = parsedResult.getJSONObject(data).optString("ticket_num")
            ticket.ticket_no = parsedResult.getJSONObject(data).optString("id")
            ticket.date = parsedResult.getJSONObject(data).optString("ticket_date")
            ticket.time = parsedResult.getJSONObject(data).optString("travel_datetime")

            ticketS.add(ticket)
        }

        recyclerView.adapter = AllTicketAdapters(tickets = ticketS)

        linearLayoutManager = LinearLayoutManager(root.context)
        recyclerView.layoutManager = linearLayoutManager


        Log.d("RESULT", result)

        return root
    }


    companion object {
        class AllTicketsAsync internal constructor(context: Context) : AsyncTask<String, String, String>() {
            lateinit var con: HttpURLConnection
            lateinit var resulta:String
            val builder = Uri.Builder()
            private val cont: Context = context
            var phone = ""
            var password = ""
            fun Context.toast(message: CharSequence) =
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            override fun onPreExecute() {
                super.onPreExecute()

                val progressBar = ProgressBar(cont)
                progressBar.isIndeterminate=true
                progressBar.visibility = View.VISIBLE
                builder .appendQueryParameter("key", "oooo")
            }

            override fun doInBackground(vararg params: String?):  String? {
                try {

                    val query = builder.build().encodedQuery
                    val url: String = "http://165.22.218.191/fetch_tickets.php"
                    val obj = URL(url)
                    con = obj.openConnection() as HttpURLConnection
                    con.setRequestMethod("POST")
                    con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)")
                    con.setRequestProperty("Accept-Language", "UTF-8")
                    con.setDoOutput(true)
                    val outputStreamWriter = OutputStreamWriter(con.getOutputStream())
                    outputStreamWriter.write(query)
                    outputStreamWriter.flush()
                    Log.e("pass 1", "connection success ")
                } catch (e: Exception) {
                    Log.e("Fail 1", e.toString())
                }
                try {
                    resulta = con.inputStream.bufferedReader().readText()
                    Log.e("data", resulta)
                } catch (e: java.lang.Exception) {
                    Log.e("Fail 2", e.toString())
                }

                return resulta
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                try {
                    try {
                        val json_data = JSONArray(resulta)

                        for (data in 0 until json_data.length()) {
                            val ticket = Ticket()
                            ticket.name = json_data.getJSONObject(data).optString("name")
                            ticket.source = json_data.getJSONObject(data).optString("source")
                            ticket.destination = json_data.getJSONObject(data).optString("destination")
//                            ticket.ticket_no = json_data.getJSONObject(data).optString("ticket_num")
                            ticket.ticket_no = json_data.getJSONObject(data).optString("id")
                            ticket.record_id = json_data.getJSONObject(data).optString("id")

                            tickets.add(ticket)
                        }

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