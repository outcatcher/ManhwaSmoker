package com.tsystems.logiweb.manhwa.smoker.backend

data class ConfigurationContainer(val serverUrl: String, val apiUrl: String)

//fun loadConfig(configName: String): ConfigurationContainer {
//    val resource = ::ConfigurationContainer.javaClass.getResource(configName) as URL
//    val resourceFileReader = FileReader(resource.file)
//    val root = JSONParser().parse(resourceFileReader) as JSONObject
//    val serverUrl = root["serverURL"] as String
//    val apiPath = root["apiPath"] as String
//    return ConfigurationContainer(serverUrl, "$serverUrl/$apiPath")
//}

val defaultConfiguration = ConfigurationContainer("https://manhwa.caritc.com", "api")

object SharedConfiguration {
    var configuration = defaultConfiguration
}
