package com.tsystems.logiweb.manhwa.smoker.backend

import android.os.AsyncTask
import android.util.Log
import khttp.get
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val SERVER_URL = "https://manhwa.caritc.com"
private const val TAG = "DataConnector"

fun getCurrentRuns(): Array<String> {
    val response = get("$SERVER_URL/progress/runs")
    val runs: JSONArray
    try {
        runs = response.jsonObject["runs"] as JSONArray
    } catch (e: JSONException) {
        throw Exception("Invalid response: ${response.content}")
    }

    var runDescriptions: Array<String> = emptyArray()
    for (i in 0 until runs.length()) {
        val run = runs[i] as JSONObject

        runDescriptions += run["description"] as String  // description should always be a string
    }
    Log.d(TAG, "Current Runs list updated")
    return runDescriptions
}

fun getPreviousRuns(): Array<String> {
    return emptyArray()
}

abstract class StringArrayAsyncTask : AsyncTask<Unit, Unit, Array<String>>()

object GetCurrentRunsAsync : StringArrayAsyncTask() {
    override fun doInBackground(vararg params: Unit?): Array<String> {
        return getCurrentRuns()
    }
}

object GetPreviousRunsAsync : StringArrayAsyncTask() {
    override fun doInBackground(vararg params: Unit?): Array<String> {
        return getPreviousRuns()
    }
}
