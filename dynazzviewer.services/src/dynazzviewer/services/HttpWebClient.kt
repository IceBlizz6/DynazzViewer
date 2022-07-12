package dynazzviewer.services

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

open class HttpWebClient(
    private val secondsThrottleDelay: Long
) : WebClient {
    private var lastRequestTime: LocalDateTime? = null

    override fun requestData(uri: String): String {
        blockUntilReady()
        val httpClient = HttpClients.createDefault()
        val request = HttpGet(uri)
        httpClient.execute(request).use { response ->
            return EntityUtils.toString(response.entity)
        }
    }

    private fun blockUntilReady() {
        val now = LocalDateTime.now()
        val requestTime = lastRequestTime
        if (requestTime == null) {
            this.lastRequestTime = now
        } else {
            val targetTime = requestTime.plusSeconds(secondsThrottleDelay)
            if (now.isBefore(targetTime)) {
                val milliSecondPauseTime = now.until(targetTime, ChronoUnit.MILLIS)
                sleep(milliSecondPauseTime)
            }
        }
    }

    private fun sleep(milliseconds: Long) {
        if (milliseconds > 0) {
            runBlocking {
                delay(milliseconds)
            }
        }
    }
}
