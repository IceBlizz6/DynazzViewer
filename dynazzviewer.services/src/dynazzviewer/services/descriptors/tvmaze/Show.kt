package dynazzviewer.services.descriptors.tvmaze

import com.fasterxml.jackson.annotation.JsonProperty
import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.descriptors.ResultHeader
import java.time.LocalDate

class Show(
    /**
     * TvMaze ID
     */
    @JsonProperty("id", required = true)
    val id: Int,
    @JsonProperty("url", required = true)
    val url: String,
    @JsonProperty("name", required = true)
    val name: String,
    @JsonProperty("type", required = true)
    val type: String,
    @JsonProperty("language", required = true)
    val language: String,
    @JsonProperty("genres", required = true)
    val genres: List<String>,
    @JsonProperty("status", required = true)
    val status: String,
    @JsonProperty("runtime", required = true)
    val runtime: Int,
    @JsonProperty("premiered", required = true)
    val premiered: LocalDate?,
    @JsonProperty("externals", required = true)
    val externals: Map<String, String>,
    @JsonProperty("image", required = true)
    val image: Map<ImageType, String>
) {
    fun toResultHeader(): ResultHeader {
        return ResultHeader(
            name = name,
            imageUrl = image[ImageType.MEDIUM]!!,
            extDb = ExtDatabase.TvMaze,
            extDbCode = id.toString()
        )
    }
}
