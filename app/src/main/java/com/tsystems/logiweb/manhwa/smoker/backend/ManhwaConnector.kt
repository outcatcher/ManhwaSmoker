package com.tsystems.logiweb.manhwa.smoker.backend

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import khttp.get
import khttp.post
import khttp.structures.authorization.Authorization
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

val configuration = SharedConfiguration.configuration
private var serverUrl = configuration.serverUrl
private var apiUrl = "$serverUrl/${configuration.apiUrl}"
private const val TAG = "ManhwaConnector"
private const val LEGAL_LOLY = "WHbbVIbAjEpXzlwgvvgv"

/**
 * Runs GET on given URL, retrying `retryCount` times, returning root JSON object of response as result
 */
private fun getWithRetry(url: String, retryCount: Int = 5): JSONObject {
    val response = get(url)
    return try {
        response.jsonObject
    } catch (e2: JSONException) {
        if (retryCount > 0) getWithRetry(url, retryCount - 1) else JSONObject()
    }
}

data class RunResult(val description: String, val percent: Double)

val descriptionRegex = ".+`(\\w+?)`.+on (\\w+).+".toRegex()

@SuppressLint("SimpleDateFormat")
private fun convertDescription(src: String, timestamp: Float): String {
    val groups = descriptionRegex.matchEntire(src)!!.groups
    val type = groups[1]!!.value.capitalize()
    val env = groups[2]!!.value.removePrefix("ELD").toLowerCase().capitalize()
    val time = SimpleDateFormat("HH:mm").format(Date(timestamp.roundToLong()))
    return "$type on $env ($time)"
}

abstract class ResultListAsyncTask : AsyncTask<Unit, Unit, List<RunResult>>()

class GetCurrentRunsAsync : ResultListAsyncTask() {
    override fun doInBackground(vararg params: Unit?): List<RunResult> {
        val runs = getWithRetry("$serverUrl/progress/runs")["runs"] as JSONArray
        var runResults: List<RunResult> = emptyList()
        for (i in 0 until runs.length()) {
            val run = runs[i] as JSONObject
            val timestamp = (run["start_timestamp"] as String).toFloat() * 1000
            val description = convertDescription(run["description"] as String, timestamp)
            val suites = run["test_suites"] as JSONArray
            var finishedSuites = 0.0
            val allSuites = suites.length()
            for (j in 0 until allSuites) {
                val suite = suites[j] as JSONObject
                if (suite["status"] != "waiting") finishedSuites += 1
            }
            val percent = finishedSuites / allSuites
            runResults += RunResult(description, percent)
        }
        Log.d(TAG, "Current Runs list updated")
        return runResults
    }
}

class GetPreviousRunsAsync : ResultListAsyncTask() {
    override fun doInBackground(vararg params: Unit?): List<RunResult> {
        return listOf(RunResult("Not Implemented Yet", 0.0))
    }
}

class StartSmokeAsync(private val env: String) : AsyncTask<String, Unit, String>() {
    override fun doInBackground(vararg params: String): String {
        val requestParams = mapOf(
            "secret" to LEGAL_LOLY
        )
        val response = get("$serverUrl/remote_run/$env", params = requestParams)
        return when (response.statusCode) {
            200 -> "Run already running"
            201 -> "Successfully started"
            else -> {
                Log.e(TAG, response.text)
                "Something gone wrong"
            }
        }
    }

}


class UserLoginAsync(private val username: String, private val password: String, private val tokenCallback: (String) -> Unit) : AsyncTask<Unit, Unit, Boolean>() {

    companion object {
        /**
         * Send user credentials returning JWT
         */
        fun userLogin(username: String, password: String): String? {
            try {
                val response = post("$apiUrl/auth", json = mapOf("username" to username, "password" to password))
                if (response.statusCode == 200) {
                    return response.jsonObject["token"] as String
                }
                return null
            } catch (e: InterruptedException) {
                return null
            }
        }
    }

    override fun doInBackground(vararg params: Unit): Boolean {
        val token = userLogin(username, password) ?: return false
        tokenCallback(token)
        return true
    }
}

fun jwtAuth(token: String) : Authorization {
    return object: Authorization {
        override val header = "Authorization" to "Bearer $token"
    }
}


class VerifyTokenAsync(private val token: String): AsyncTask<Unit, Unit, Boolean>(){

    companion object {

        fun verifyToken(token: String): Boolean {
            val response = get("$apiUrl/auth/validate", auth = jwtAuth(token))
            return response.statusCode == 200
        }

    }

    override fun doInBackground(vararg params: Unit?): Boolean {
        return verifyToken(token)
    }

}
