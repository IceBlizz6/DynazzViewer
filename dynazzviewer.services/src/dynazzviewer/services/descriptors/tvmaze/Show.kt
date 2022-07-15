package dynazzviewer.services.descriptors.tvmaze

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.services.descriptors.ResultHeader
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class Show(
    /**
     * TvMaze ID
     */
    val id: Int,
    val url: String,
    val name: String,
    val type: String,
    val language: String,
    val genres: List<String>,
    val status: String,
    val runtime: Int?,
    @Serializable(LocalDateSerializer::class)
    val premiered: LocalDate?,
    val externals: ExternalRef,
    val image: Map<ImageType, String>?
) {
    fun toResultHeader(): ResultHeader {
        return ResultHeader(
            name = name,
            imageUrl = image?.get(ImageType.MEDIUM),
            extDb = ExtDatabase.TvMaze,
            extDbCode = id.toString()
        )
    }
}
