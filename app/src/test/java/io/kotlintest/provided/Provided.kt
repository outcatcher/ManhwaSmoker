package io.kotlintest.provided

import com.tsystems.logiweb.manhwa.smoker.backend.ConfigurationContainer
import com.tsystems.logiweb.manhwa.smoker.backend.SharedConfiguration
import com.tsystems.logiweb.manhwa.smoker.backend.defaultConfiguration
import io.kotlintest.AbstractProjectConfig


object ProjectConfig : AbstractProjectConfig() {
    private val defaultConfig = defaultConfiguration
    private val testConfiguration = ConfigurationContainer("http://localhost:5000", "api")

    override fun afterAll() {
        SharedConfiguration.configuration = defaultConfig
    }

    override fun beforeAll() {
        SharedConfiguration.configuration = testConfiguration
    }
}


