package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonValue

enum class SeriesStatus(
    @JsonValue
       private val value: String
) {
    FINISHED_AIRING("Finished Airing"),
}
