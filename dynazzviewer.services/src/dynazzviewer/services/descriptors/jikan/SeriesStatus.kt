package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonValue

enum class SeriesStatus(
    @JsonValue
    private val value: String
) {
    NOT_YET_AIRED("Not yet aired"),
    CURRENTLY_AIRING("Currently Airing"),
    FINISHED_AIRING("Finished Airing"),
}
