package com.tsystems.logiweb.manhwa.smoker

import com.tsystems.logiweb.manhwa.smoker.backend.*
import org.junit.Assert
import org.junit.Test


class APITest {


    private val defaultConfig = defaultConfiguration
    private val testConfiguration = ConfigurationContainer("http://localhost:5000", "api")

    @Test
    fun authWorks() {
        SharedConfiguration.configuration = testConfiguration
        val token = UserLoginAsync.userLogin("akachuri", "akachuri")!!
        Assert.assertTrue(VerifyTokenAsync.verifyToken(token))
    }
}
