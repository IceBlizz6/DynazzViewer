package dynazzviewer.services

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

class HttpWebJsonParser(
	private val client : WebClient = HttpWebClient()
) : WebJsonParser {
	override fun <T> parseJsonRequest(uri : String, type : Class<T>) : T {
		val data = client.requestData(uri)
		return parseJsonContent(data, type)
	}
	
	private fun <T> parseJsonContent(data : String, type : Class<T>) : T {
		val mapper = ObjectMapper()
		mapper.registerModule(JavaTimeModule())
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper.readValue(data, type)
	}
}
