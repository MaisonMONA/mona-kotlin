package com.example.mona.activities


import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mona.R
import com.example.mona.task.RegisterUser
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ExecutionException
import com.example.mona.data.SaveSharedPreference

class RegisterActivity : AppCompatActivity() {

    private var mUsername: TextView? = null
    private var mPassword: TextView? = null
    private var mPassword_confirmation: TextView? = null
    private var mCreate: Button? = null
    private var mError_message: TextView? = null
    private val registerUser: RegisterUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mUsername = findViewById(R.id.register_username)
        mPassword = findViewById(R.id.register_password)
        mPassword_confirmation = findViewById(R.id.register_password_confirmation)
        mCreate = findViewById(R.id.create_button)
        mError_message = findViewById(R.id.register_error_message)

        mCreate?.setOnClickListener(View.OnClickListener {
            try {
                val registerUser = RegisterUser()
                registerUser.execute(
                    mUsername?.text.toString(),
                    mPassword?.text.toString(),
                    mPassword_confirmation?.text.toString()
                )
                val response = registerUser.get()
                val reader = JSONObject(response)
                if (reader.has("token")) {

                    //Save the token and the username in the SharedPrefference
                    val token = reader.getString("token")
                    val username = mUsername?.text.toString()
                    SaveSharedPreference.setToken(this, token)
                    SaveSharedPreference.setUsername(this, username)

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Yess!")
                    startActivity(intent)
                }
                if (reader.has("errors")) {
                    val errors = reader.getJSONObject("errors")
                    if (errors.has("username")) {
                        mError_message?.text="Le nom d'usager n'est pas disponible"
                        mError_message?.visibility = View.VISIBLE
                    } else if (errors.has("password")) {
                        val password = errors.getJSONArray("password")
                        var words = ""
                        for (i in 0 until password.length()) {
                            words += password[i].toString() + "\n"
                        }
                        mError_message?.text = words
                        mError_message?.visibility = View.VISIBLE
                    }
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
    }
}