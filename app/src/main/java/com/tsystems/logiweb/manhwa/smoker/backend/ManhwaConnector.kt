package com.tsystems.logiweb.manhwa.smoker.backend

import android.os.AsyncTask
import android.util.Log
import khttp.get
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

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

fun getCurrentRuns(): List<String> {
    val runs = getWithRetry("$SERVER_URL/progress/runs")["runs"] as JSONArray
    var runDescriptions: List<String> = emptyList()
    for (i in 0 until runs.length()) {
        val run = runs[i] as JSONObject
        runDescriptions += run["description"] as String  // description should always be a string
    }
    Log.d(TAG, "Current Runs list updated")
    return runDescriptions
}

fun getPreviousRuns(): List<String> {
    return listOf("Not Implemented Yet")
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

abstract class StringArrayAsyncTask : AsyncTask<Unit, Unit, List<String>>()

class GetCurrentRunsAsync : StringArrayAsyncTask() {
    override fun doInBackground(vararg params: Unit?): List<String> {
        return getCurrentRuns()
    }
}

class GetPreviousRunsAsync : StringArrayAsyncTask() {
    override fun doInBackground(vararg params: Unit?): List<String> {
        return getPreviousRuns()
    }
}

class StartRunAsync(private val env: String) : AsyncTask<String, Unit, String>() {
    override fun doInBackground(vararg params: String): String {
        return startSmoke(env)
    }

}
