package com.outcatcher.manhwa.smoker.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.outcatcher.manhwa.smoker.R
import com.outcatcher.manhwa.smoker.backend.UserLoginAsync
import com.outcatcher.manhwa.smoker.backend.VerifyTokenAsync
import com.outcatcher.manhwa.smoker.backend.serverAvailable
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

/**
 * A login screen that offers login via username/password.
 */
class LoginActivity : Activity() {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var authTask: UserLoginAsync? = null
    private val preferences
        get() = getSharedPreferences(
            getString(R.string.pref_file_key),
            Context.MODE_PRIVATE
        )

    private fun getToken(): String? {
        return preferences.getString("jwt", null)
    }

    private fun setToken(token: String) {
        with(preferences.edit()) {
            putString(TOKEN_KEY, token)
            apply()
        }
        Log.d(TAG, "Token is set")
    }

    private fun clearToken() {
        Log.d(TAG, "Removing token...")
        with(preferences.edit()) {
            remove(TOKEN_KEY)
            apply()
        }
    }

    private fun checkToken(token: String): Boolean {
        return VerifyTokenAsync(token).execute().get(5, TimeUnit.SECONDS)
    }

    private fun noConnection() {
        val noConnection = AlertDialog.Builder(this)
        noConnection.setMessage("No connection to the backend")
        noConnection.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        sign_in_button.setOnClickListener { attemptLogin() }

        // first of all — check if connection to BE is available
        if (!serverAvailable()) {
            noConnection()
        } else {
            val token = getToken()
            if (token != null) {
                if (checkToken(token)) {
                    moveToMainActivity()
                } else {
                    clearToken()
                }
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

        if (!serverAvailable()){
            noConnection()
            return
        }

        if (authTask != null) {
            return
        }

        // Reset errors.
        username.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (passwordStr == "") {
            password.error = getString(R.string.error_field_required)
            focusView = password
            cancel = true
        }

        if (usernameStr == "") {
            username.error = getString(R.string.error_field_required)
            focusView = username
            cancel = true
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            authTask = UserLoginAsync(usernameStr, passwordStr) { setToken(it) }
            val loginSucceed = authTask!!.execute().get()
            showProgress(false)
            if (loginSucceed) {
                moveToMainActivity()
            } else {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            }
        }
        authTask = null
    }

    private fun moveToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }

    companion object {
        private const val TOKEN_KEY = "jwt"
        private const val TAG = "LoginActivity"
    }

}

