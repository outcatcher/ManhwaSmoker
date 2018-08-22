package com.tsystems.logiweb.manhwa.smoker.backend

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.FileReader
import java.net.URL

data class ConfigurationContainer(val serverUrl: String, val apiUrl: String)

fun loadConfig(configName: String): ConfigurationContainer {
    val resource = ::ConfigurationContainer.javaClass.getResource("/api_configuration/$configName") as URL
    val resourceFileReader = FileReader(resource.file)
    val root = JSONParser().parse(resourceFileReader) as JSONObject
    val serverUrl = root["serverUrl"] as String
    val apiPath = root["apiPath"] as String
    return ConfigurationContainer(serverUrl, apiPath)
}

val defaultConfiguration = loadConfig("default.json")

object SharedConfiguration {
    var configuration = defaultConfiguration
}
