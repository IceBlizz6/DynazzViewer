package dynazzviewer.services.descriptors.tvmaze

import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.services.descriptors.ResultHeader

class SearchShowResult(
    @JsonProperty("score", required = true)
       val score: Double,
    @JsonProperty("show", required = true)
       val show: Show
) {
    fun toResultHeader(): ResultHeader {
        return show.toResultHeader()
    }
}
