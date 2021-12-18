package dynazzviewer.services

interface WebClient {
    fun requestData(uri: String): String
}
