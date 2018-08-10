package com.tsystems.logiweb.manhwa.smoker.backend

import android.os.AsyncTask
import khttp.get
import org.json.JSONArray
import org.json.JSONObject

private const val SERVER_URL = "https://manhwa.caritc.com"


fun getCurrentRuns(): Array<String> {
    val runs = get("$SERVER_URL/progress/runs").jsonObject["runs"] as JSONArray
    var runDescriptions: Array<String> = emptyArray()
    for (i in 0 until runs.length()) {
        val run = runs[i] as JSONObject
        runDescriptions += run["description"] as String  // description should always be a string
    }
    return runDescriptions
}

class GetCurrentRunsAsync(): AsyncTask<Void, Void, Array<String>>() {
    override fun doInBackground(vararg params: Void?): Array<String> {
        return getCurrentRuns()
    }
}
