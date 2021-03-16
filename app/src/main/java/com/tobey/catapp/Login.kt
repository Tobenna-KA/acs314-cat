package com.tobey.catapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val signUpBtn: TextView = findViewById(R.id.sign_btn)
        signUpBtn.setOnClickListener { startActivity(Intent(this, Signup::class.java)) }

        val phone: EditText = findViewById(R.id.phoneNumber)
        val password: EditText = findViewById(R.id.password)

        val loginBtn: Button = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            val login = MyAsyncTask(this)
            login.phone = phone.text.toString()
            login.password = password.text.toString()

            login.execute()
//            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    companion object {
        class MyAsyncTask internal constructor(context: Context) : AsyncTask<String, String, String>() {
            lateinit var con: HttpURLConnection
            lateinit var resulta:String
            val builder = Uri.Builder()
            private val cont: Context = context
            var phone = ""
            var password = ""

            override fun onPreExecute() {
                super.onPreExecute()

                val progressBar = ProgressBar(cont)
                progressBar.isIndeterminate=true
                progressBar.visibility = View.VISIBLE
                builder .appendQueryParameter("phone_number", phone)
                builder .appendQueryParameter("password", "0000")
                builder .appendQueryParameter("key", "oooo")
            }

            override fun doInBackground(vararg params: String?):  String? {
                try {

                    val query = builder.build().encodedQuery
                    val url: String = "http://165.22.218.191/read.php"
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
                fun onPostExecute(result: String?) {
                    super.onPostExecute(result)

                    var json_data = JSONObject(resulta)
                    val code: Int = json_data.getInt("code")

                    if (code == 1) {
//                        val com: JSONArray = json_data.getJSONArray("userdetails")
//                        val comObject = com[0] as JSONObject
//                        Log.e("data",""+comObject.optString("fname"))
                        val toMain = Intent(cont, MainActivity::class.java)
                        Log.e("data",code.toString())
                        cont.startActivity(toMain)
                    }
                }

                return null;
            }
        }

    }
}