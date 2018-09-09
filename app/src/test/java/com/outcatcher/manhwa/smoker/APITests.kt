package com.outcatcher.manhwa.smoker

import com.outcatcher.manhwa.smoker.backend.Connector
import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.matchers.string.beEmpty
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.specs.StringSpec

private var TestConnector = Connector("http://localhost:24734/api/v2")

private fun login() = TestConnector.userLogin("admin", "12345678")

class APITest : StringSpec({

    "Received Auth Should Be Valid" {
        login()
        TestConnector.userLoggedIn() shouldBe true
    }

    "Test Runs Should Be A List" {
        login()
        val runList = TestConnector.getCurrentRunList()
        runList.size should beGreaterThan(0)
        runList[0].description shouldNot beEmpty()
    }

    "Previous Runs Should Be A List" {
        login()
        val runList = TestConnector.getFinishedRunList()
        runList.size should beGreaterThan(0)
        runList[0].description shouldNot beEmpty()
    }

})
