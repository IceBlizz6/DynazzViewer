package dynazzviewer.services

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

open class HttpWebClient : WebClient {
    override fun requestData(uri: String): String {
        val httpClient = HttpClients.createDefault()
        val request = HttpGet(uri)
        httpClient.execute(request).use { response ->
            return EntityUtils.toString(response.entity)
        }
    }
}
