package com.example.mona.activities

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mona.R
import com.example.mona.data.SaveSharedPreference
import com.example.mona.task.LoginUser
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ExecutionException


class LoginActivity : AppCompatActivity() {

    private var mUsername: TextView? = null
    private var mPassword: TextView? = null
    private var mLogin: Button? = null
    private var mRegister: Button? = null
    private var mError_Message: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mUsername = findViewById(R.id.login_username)
        mPassword = findViewById(R.id.login_password)
        mLogin = findViewById(R.id.login_button)
        mRegister = findViewById(R.id.register_button)
        mError_Message = findViewById(R.id.login_error_message)

        mLogin?.setOnClickListener(View.OnClickListener {
            try {
                val loginUser = LoginUser()
                loginUser.execute(
                    mUsername?.text.toString(),
                    mPassword?.text.toString()
                )
                val response = loginUser.get()
                println(response)
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
                } else {
                    mError_Message?.text = "Veuillez verifier votre nom d'utilisateur \n ou mot de passe"
                    mError_Message?.visibility = View.VISIBLE
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
        mRegister?.setOnClickListener(View.OnClickListener {
            val myIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(myIntent)
        })
    }
}
