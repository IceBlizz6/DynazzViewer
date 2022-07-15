package dynazzviewer.services.descriptors.tvmaze

import dynazzviewer.services.descriptors.ResultHeader
import kotlinx.serialization.Serializable

@Serializable
class SearchShowResult(
    val score: Double,
    val show: Show
) {
    fun toResultHeader(): ResultHeader {
        return show.toResultHeader()
    }
}
