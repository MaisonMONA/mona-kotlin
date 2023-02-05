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
import com.maison.mona.task.RegisterUser
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ExecutionException

class RegisterActivity : AppCompatActivity() {
    private val ERRORS = "errors"
    private val TOKEN = "token"
    private val PASSWORD = "password"

    var mUsername: TextView? = null
    var mPassword: TextView? = null
    var mPasswordConfirmation: TextView? = null
    var mCreate: Button? = null
    var mErrorMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mUsername = findViewById(R.id.register_username)
        mPassword = findViewById(R.id.register_password)
        mPasswordConfirmation = findViewById(R.id.register_password_confirmation)
        mCreate = findViewById(R.id.create_button)
        mErrorMessage = findViewById(R.id.register_error_message)

        mCreate?.setOnClickListener {
            try {
                val registerUser = RegisterUser()
                @Suppress("DEPRECATION") registerUser.execute(
                    mUsername?.text.toString(),
                    mPassword?.text.toString(),
                    mPasswordConfirmation?.text.toString()
                )
                @Suppress("DEPRECATION") val response = registerUser.get()
                val reader = JSONObject(response)

                if (reader.has(TOKEN)) {
                    saveSharedPreferences(mUsername, reader)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Yess!")  // TODO: delete this line maybe?
                    startActivity(intent)
                }
                if (reader.has(ERRORS)) {
                    errorMessageHandling(mErrorMessage, reader)
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveSharedPreferences(mUsername: TextView?, reader: JSONObject) {
        val token = reader.getString(TOKEN)
        val username = mUsername?.text.toString()
        SaveSharedPreference.setToken(this, token)
        SaveSharedPreference.setUsername(this, username)
    }

    private fun errorMessageHandling(mErrorMessage: TextView?, reader: JSONObject){
        val errors = reader.getJSONObject(ERRORS)
        if (errors.has("username")) {
            mErrorMessage?.setText(R.string.register_name_not_available)
            mErrorMessage?.visibility = View.VISIBLE
        } else if (errors.has(PASSWORD)) {
            val password = errors.getJSONArray(PASSWORD)
            var words = ""
            for (i in 0 until password.length()) {
                words += password[i].toString() + "\n"
            }
            mErrorMessage?.text = words
            mErrorMessage?.visibility = View.VISIBLE
        }
    }
}