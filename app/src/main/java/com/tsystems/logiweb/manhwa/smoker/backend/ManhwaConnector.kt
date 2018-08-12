package com.tsystems.logiweb.manhwa.smoker.backend

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import khttp.get
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

private const val SERVER_URL = "https://manhwa.caritc.com"
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
    val env = groups[2]!!.value
    val time = SimpleDateFormat("HH:mm").format(Date(timestamp.roundToLong()))
    return "$type on $env ($time)"
}

fun getCurrentRuns(): List<RunResult> {
    val runs = getWithRetry("$SERVER_URL/progress/runs")["runs"] as JSONArray
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

fun getPreviousRuns(): List<RunResult> {
    return listOf(RunResult("Not Implemented Yet", 0.0))
}

fun startSmoke(env: String): String {
    val requestParams = mapOf(
        "secret" to LEGAL_LOLY
    )
    val response = get("$SERVER_URL/remote_run/$env", params = requestParams)
    return when (response.statusCode) {
        200 -> "Run already running"
        201 -> "Successfully started"
        else -> {
            Log.e(TAG, response.text)
            "Something gone wrong"
        }
    }
}

abstract class ResultListAsyncTask : AsyncTask<Unit, Unit, List<RunResult>>()

class GetCurrentRunsAsync : ResultListAsyncTask() {
    override fun doInBackground(vararg params: Unit?): List<RunResult> {
        return getCurrentRuns()
    }
}

class GetPreviousRunsAsync : ResultListAsyncTask() {
    override fun doInBackground(vararg params: Unit?): List<RunResult> {
        return getPreviousRuns()
    }
}

class StartRunAsync(private val env: String) : AsyncTask<String, Unit, String>() {
    override fun doInBackground(vararg params: String): String {
        return startSmoke(env)
    }

}
