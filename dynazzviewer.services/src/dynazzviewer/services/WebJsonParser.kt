package dynazzviewer.services

interface WebJsonParser {
    fun <T> parseJsonRequest(uri: String, type: Class<T>): T

    fun <T> parseJsonRequestWithResponse(uri: String, type: Class<T>): Response<T>

    fun <T> parseRawString(rawContent: String, type: Class<T>): T

    class Response<T>(
        val inner: T,
        val content: String
    )
}
