package dynazzviewer.services.descriptors

import dynazzviewer.services.HttpWebClient
import dynazzviewer.services.HttpWebJsonParser
import dynazzviewer.services.WebClient
import dynazzviewer.services.WebJsonParser
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class HttpThrottledWebParser(
    private val client: WebClient = HttpWebClient(),
    private val secondsInterval: Long
) : HttpWebJsonParser(client) {
    private var lastRequestTime: LocalDateTime? = null

    override fun <T> parseJsonRequest(uri: String, type: Class<T>): T {
        blockUntilReady()
        val response = super.parseJsonRequest(uri, type)
        updateRequestTimer()
        return response
    }

    override fun <T> parseJsonRequestWithResponse(
        uri: String,
        type: Class<T>
    ): WebJsonParser.Response<T> {
        blockUntilReady()
        val response = super.parseJsonRequestWithResponse(uri, type)
        updateRequestTimer()
        return response
    }

    private fun blockUntilReady() {
        val now = LocalDateTime.now()
        val requestTime = lastRequestTime
        if (requestTime != null) {
            val targetTime = requestTime.plusSeconds(secondsInterval)
            if (now.isBefore(targetTime)) {
                val milliSecondPauseTime = now.until(targetTime, ChronoUnit.MILLIS)
                sleep(milliSecondPauseTime)
            }
        }
    }

    private fun updateRequestTimer() {
        this.lastRequestTime = LocalDateTime.now()
    }

    private fun sleep(milliseconds: Long) {
        if (milliseconds > 0) {
            runBlocking {
                delay(milliseconds)
            }
        }
    }
}
