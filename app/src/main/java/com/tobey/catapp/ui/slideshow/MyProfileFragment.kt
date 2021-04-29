package com.tobey.catapp.ui.slideshow

import android.content.Context
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.tobey.catapp.MainActivity
import com.tobey.catapp.R
import com.tobey.catapp.Signup
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MyProfileFragment : Fragment() {

  private lateinit var myProfileViewModel: MyProfileViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    myProfileViewModel =
            ViewModelProvider(this).get(MyProfileViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

    val name: TextView = root.findViewById(R.id.name)
    val cPassword: TextView = root.findViewById(R.id.c_password)
    val password: TextView = root.findViewById(R.id.password)
    val rPassword: TextView = root.findViewById(R.id.r_password)
    val signBtn: TextView = root.findViewById(R.id.sign_btn)
    val sharedPreferences: SharedPreferences = root.context.getSharedPreferences("myData", AppCompatActivity.MODE_PRIVATE)
    val user = sharedPreferences.getString("user", "") ?: "{}"
    val parsedUser = JSONObject(user)
    val phoneNumber = parsedUser.optString("phone_number")

    signBtn.setOnClickListener {
      if (cPassword.text.isNotEmpty() && password.text.isNotEmpty() && rPassword.text.isNotEmpty()) {
        if (password.text.toString() == rPassword.text.toString()) {
          val signup = MyAsyncTask(root.context)
          signup.name = name.text.toString()
          signup.newPassword = password.text.toString()
          signup.cPassword = cPassword.text.toString()
          signup.phoneN = phoneNumber
          Log.d("phoneNumber", phoneNumber)

          val result = signup.execute().get()
          val parsedResult = JSONObject(result)

          when {
              parsedResult.optString("code") == "1" -> {
                Toast.makeText(root.context, "Success", Toast.LENGTH_SHORT).show()
              }
              parsedResult.optString("code") == "1" -> {
                Toast.makeText(root.context, "Error: Current password is wrong", Toast.LENGTH_SHORT).show()
              }
              else -> {
                Toast.makeText(root.context, "Error", Toast.LENGTH_SHORT).show()
              }
          }
        } else {
          Toast.makeText(root.context, "Error: passwords don't match", Toast.LENGTH_SHORT).show()
        }
      } else {
        Toast.makeText(root.context, "Error: please fill all fields", Toast.LENGTH_SHORT).show()
      }
//            startActivity(Intent(this, MainActivity::class.java))
    }
    return root
  }

  companion object {
    class MyAsyncTask internal constructor(context: Context) : AsyncTask<String, String, String>() {
      lateinit var con: HttpURLConnection
      lateinit var resulta:String
      val builder = Uri.Builder()
      private val cont: Context = context
      var name = ""
      var phoneN = ""
      var newPassword = ""
      var cPassword = ""

      override fun onPreExecute() {
        super.onPreExecute()

        val progressBar = ProgressBar(cont)
        progressBar.isIndeterminate=true
        progressBar.visibility = View.VISIBLE
        builder .appendQueryParameter("name", name)
        builder .appendQueryParameter("phone_number", phoneN)
        builder .appendQueryParameter("password", cPassword)
        builder .appendQueryParameter("new_password", newPassword)
        builder .appendQueryParameter("key", "oooo")
      }

      override fun doInBackground(vararg params: String?):  String? {
        try {

          val query = builder.build().encodedQuery
          val url: String = "http://165.22.218.191/update.php"
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
          Log.e("data",code.toString())
          if (code == 1) {
//                        val com: JSONArray = json_data.getJSONArray("userdetails")
//                        val comObject = com[0] as JSONObject
//                        Log.e("data",""+comObject.optString("fname"))
            val toMain = Intent(cont, MainActivity::class.java)
            cont.startActivity(toMain)
          }
        }

        return null;
      }
    }

  }
}