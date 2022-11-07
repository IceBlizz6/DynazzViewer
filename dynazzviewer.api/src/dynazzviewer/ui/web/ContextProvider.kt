package dynazzviewer.ui.web

import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.Storage

class ContextProvider(
    private val connection: Storage
) {
    private var cachedContext: ReadOperation? = null

    val context: ReadOperation
        get() {
            cachedContext.let { cached ->
                if (cached == null) {
                    val createdContext = connection.readKeepAlive()
                    cachedContext = createdContext
                    return createdContext
                } else {
                    return cached
                }
            }
        }

    fun close() {
        cachedContext?.close()
    }
}
