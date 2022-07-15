package dynazzviewer.services.descriptors.jikan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Pagination(
    @SerialName("last_visible_page")
    val lastVisiblePage: Int,
    @SerialName("has_next_page")
    val hasNextPage: Boolean
)
