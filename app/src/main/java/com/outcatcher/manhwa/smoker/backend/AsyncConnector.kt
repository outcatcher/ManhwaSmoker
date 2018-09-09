package com.outcatcher.manhwa.smoker.backend

import android.os.AsyncTask

abstract class TestResultListAsyncTask : AsyncTask<Unit, Unit, List<TestRunResult>>()


class StartSmokeAsync(private val env: String) : AsyncTask<String, Unit, String>() {
    override fun doInBackground(vararg params: String): String {
        return DefaultConnector.startSmoke(env)
    }

}


class UserLoginAsync(private val username: String, private val password: String, private val tokenCallback: (String) -> Unit) : AsyncTask<Unit, Unit, Boolean>() {
    override fun doInBackground(vararg params: Unit?): Boolean {
        val token = DefaultConnector.userLogin(username, password) ?: return false
        tokenCallback(token)
        return true
    }
}

abstract class AsyncUnitCheck : AsyncTask<Unit, Unit, Boolean>()

class VerifyTokenAsync(private val token: String) : AsyncUnitCheck() {

    override fun doInBackground(vararg params: Unit?): Boolean {
        useToken(token)
        return DefaultConnector.userLoggedIn()
    }

}

class VerifyConnection : AsyncUnitCheck() {
    override fun doInBackground(vararg params: Unit?): Boolean {
        return DefaultConnector.connectionAvailable()
    }
}

fun serverAvailable(): Boolean {
    return VerifyConnection().execute().get()
}

class GetCurrentRunsAsync : TestResultListAsyncTask() {
    override fun doInBackground(vararg params: Unit?): List<TestRunResult> {
        return DefaultConnector.getCurrentRunList()
    }
}

class GetPreviousRunsAsync : TestResultListAsyncTask() {
    override fun doInBackground(vararg params: Unit?): List<TestRunResult> {
        return DefaultConnector.getFinishedRunList()
    }
}