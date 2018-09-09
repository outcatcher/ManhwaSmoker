package com.outcatcher.manhwa.smoker.backend

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "Parsing"

enum class TestStatus { WAITING, IN_PROGRESS, PASS, FAIL }

enum class TestType {
    SMOKE, REGRESS, NAGIOS, PERFORMANCE;

    val description: String
        get() = this.name.toLowerCase().capitalize()
}

typealias TestRunResultList = List<TestRunResult>


data class Credentials(val username: String, val password: String) {
    fun json(): String{
        return mapper.writeValueAsString(this)!!
    }
}
data class TestSuiteResult(val status: TestStatus, val startTimestamp: Long)

data class TestRunResult(val startTimestamp: Long, val environment: String, val testType: TestType, val status: TestStatus, val suites: List<TestSuiteResult>) {
    var finishedPercent = 0.0
        get() {
            val finishedSuites = suites.filter { it.status !in listOf(TestStatus.WAITING, TestStatus.IN_PROGRESS) }
            return finishedSuites.size.toDouble() / suites.size
        }

    @SuppressLint("SimpleDateFormat")
    private val _startTimestamp: String = SimpleDateFormat("dd MMM HH:mm").format(Date(startTimestamp * 1000))

    val description: String = "${testType.description} on ${environment.capitalize()}, $_startTimestamp"
}
