package com.outcatcher.manhwa.smoker

import com.outcatcher.manhwa.smoker.backend.Connector
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

private var TestConnector = Connector("http://localhost:24733/api/v2")
private fun changeBaseURL(url: String) {
    TestConnector = Connector(url)
}

object FailoverListener : TestListener {

    override fun afterTest(description: Description, result: TestResult) {
        changeBaseURL("http://localhost:24733/api/v2")
    }
}

class FailoverTest : StringSpec() {
    override fun listeners(): List<TestListener> {
        return listOf(FailoverListener)
    }

    init {

        "All valid" {
            TestConnector.connectionAvailable() shouldBe true
        }

        "Unknown host" {
            changeBaseURL("http://uhgeiughshgtr.com:100/api")
            TestConnector.connectionAvailable() shouldBe false
        }

        "Unreachable server" {
            changeBaseURL("http://localhost:100/api")
            TestConnector.connectionAvailable() shouldBe false
        }

        "Invalid protocol" {
            changeBaseURL("https://localhost:5000")
            TestConnector.connectionAvailable() shouldBe false
        }

    }
}
