package io.kotlintest.provided

import com.tsystems.logiweb.manhwa.smoker.backend.SharedConfiguration
import com.tsystems.logiweb.manhwa.smoker.backend.defaultConfiguration
import com.tsystems.logiweb.manhwa.smoker.backend.loadConfig
import io.kotlintest.AbstractProjectConfig


object ProjectConfig : AbstractProjectConfig() {
    private val defaultConfig = defaultConfiguration
    private val testConfig = loadConfig("test.json")

    override fun afterAll() {
        SharedConfiguration.configuration = defaultConfig
    }

    override fun beforeAll() {
        SharedConfiguration.configuration = testConfig
    }
}


