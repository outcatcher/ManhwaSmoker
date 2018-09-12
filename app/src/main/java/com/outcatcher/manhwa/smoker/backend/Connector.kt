package com.outcatcher.manhwa.smoker.backend

import android.util.Log
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.outcatcher.manhwa.smoker.BuildConfig


private const val TAG = "Connector"

typealias StringMap = Map<String, String>

fun useToken(token: String) {
    val authHeaderName = "Authorization"
    var resHeaders: StringMap = FuelManager.instance.baseHeaders ?: mapOf()
    if (authHeaderName in resHeaders) {
        resHeaders = resHeaders.filterKeys { it != authHeaderName }
    }
    FuelManager.instance.baseHeaders = resHeaders + ("Authorization" to "Bearer $token")
}

fun Request.jsonBody(body: String): Request {
    this.headers["Content-Type"] = "application/json"
    return this.body(body)
}

class Connector(private val api_url: String) {

    data class TokenJson(val token: String)

    init {
        FuelManager.instance.timeoutReadInMillisecond = 20000
    }

    /**
     * Send user credentials returning received token
     */
    fun userLogin(username: String, password: String): String? {
        val credentials = Credentials(username, password).json()
        Log.d(TAG, "Credentials: $credentials")
        try {
            val (_, response, result) = "$api_url/auth".httpPost().jsonBody(credentials)
                .responseObject<TokenJson>()
            if ((result is Result.Success) && (response.statusCode == 200)) {
                val token = result.value.token
                Log.d(TAG, "Token: $token")
                useToken(token)
                return token
            }
            return null
        } catch (e: InterruptedException) {
            return null
        }
    }

    fun userLoggedIn(): Boolean {
        val (_, response, _) = "$api_url/auth".httpGet().response()
        return response.statusCode == 200
    }

    /**
     * Validated if connection to the backend is possible
     */
    tailrec fun connectionAvailable(retries: Int = 3): Boolean {
        if (retries <= 0) return false
        val result = "$api_url/".httpGet().timeout(1000).response().third
        return if (result is Result.Success) true else connectionAvailable(retries - 1)
    }

    private fun getRunList(type: String, retryCount: Int = 5): TestRunResultList {

        val (_, _, result) = "$api_url/tests/$type"
            .httpGet()
            .responseString()
        Log.d(TAG, "Raw response: ${result.get()}")  // fails here often
        return try {
            val parsedResponse = jacksonDeserializerOf<TestRunResultList>().deserialize(result.get())
            Log.d(TAG, "${type.toUpperCase()}: $parsedResponse")
            parsedResponse!!
        } catch (pe: MismatchedInputException) {
            if (retryCount == 0) throw pe
            getRunList(type, retryCount - 1)
        }
    }

    fun getCurrentRunList(): TestRunResultList = getRunList("running")

    fun getFinishedRunList(): TestRunResultList = getRunList("finished")

    /**
     * Start new run returning one of run status message
     */
    fun startSmoke(environment: String): String {
        val (_, response, result) = "$api_url/tests/new/$environment"
            .httpPost()
            .responseString()
        return when (response.statusCode) {
            200 -> "Run already running"
            201 -> "Successfully started"
            else -> {
                Log.e(TAG, result.component1())
                "Something gone wrong"
            }
        }
    }
}

val DefaultConnector = Connector(BuildConfig.API_URL)
