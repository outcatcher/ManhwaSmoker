package com.tsystems.logiweb.manhwa.smoker.backend

import android.os.AsyncTask
import android.util.Log
import khttp.get
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val SERVER_URL = "https://manhwa.caritc.com"
private const val TAG = "DataConnector"

private fun getRuns(retry: Int = 5): JSONArray {
    val response = get("$SERVER_URL/progress/runs")
    val emptyJSON = JSONArray()
    return try {
        response.jsonObject["runs"] as JSONArray
    } catch (e: JSONException) {
        if (retry > 0) getRuns(retry - 1) else emptyJSON
    } catch (all: Exception) {
        emptyJSON
    }
}

fun getCurrentRuns(): Array<String> {
    val runs = getRuns()
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

class GetCurrentRunsAsync : StringArrayAsyncTask() {
    override fun doInBackground(vararg params: Unit?): Array<String> {
        return getCurrentRuns()
    }
}

class GetPreviousRunsAsync : StringArrayAsyncTask() {
    override fun doInBackground(vararg params: Unit?): Array<String> {
        return getPreviousRuns()
    }
}
