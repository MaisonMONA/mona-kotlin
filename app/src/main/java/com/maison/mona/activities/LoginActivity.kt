package com.maison.mona.activities

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.maison.mona.R
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.task.LoginUser
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ExecutionException

class LoginActivity : AppCompatActivity() {

    private var mUsername: TextView? = null
    private var mPassword: TextView? = null
    private var mLogin: Button? = null
    private var mRegister: Button? = null
    private var mErrorMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mUsername = findViewById(R.id.login_username)
        mPassword = findViewById(R.id.login_password)
        mLogin = findViewById(R.id.login_button)
        mRegister = findViewById(R.id.register_button)
        mErrorMessage = findViewById(R.id.login_error_message)

        mLogin?.setOnClickListener {
            try {
                val loginUser = LoginUser()
                loginUser.execute(
                    mUsername?.text.toString(),
                    mPassword?.text.toString()
                )

                @Suppress("DEPRECATION") val response = loginUser.get()
                println(response)
                if (response == null){
                    mErrorMessage?.setText(R.string.login_connexion_error_message)
                    mErrorMessage?.visibility = View.VISIBLE
                } else {
                    val reader = JSONObject(response)

                    if (reader.has("token")) {
                        saveSharedPreferences(mUsername, reader)
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    } else {
                        mErrorMessage?.setText(R.string.login_error_message)
                        mErrorMessage?.visibility = View.VISIBLE
                    }
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        mRegister?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }
    private fun saveSharedPreferences(mUsername: TextView?, reader: JSONObject){
        val token = reader.getString("token")
        val username = mUsername?.text.toString()
        SaveSharedPreference.setToken(this, token)
        SaveSharedPreference.setUsername(this, username)
    }
}