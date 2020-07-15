package dynazzviewer.services

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

open class HttpWebJsonParser(
    private val client: WebClient = HttpWebClient()
) : WebJsonParser {
    override fun <T> parseJsonRequest(uri: String, type: Class<T>): T {
        val data = client.requestData(uri)
        return parseJsonContent(data, type)
    }

    override fun <T> parseJsonRequestWithResponse(
        uri: String,
        type: Class<T>
    ): WebJsonParser.Response<T> {
        val data = client.requestData(uri)
        return WebJsonParser.Response(
            content = data,
            inner = parseJsonContent(data, type)
        )
    }

    override fun <T> parseRawString(rawContent: String, type: Class<T>): T {
        return parseJsonContent(rawContent, type)
    }

    private fun <T> parseJsonContent(data: String, type: Class<T>): T {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        return mapper.readValue(data, type)
    }
}
