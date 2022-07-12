package dynazzviewer.services.descriptors.jikan

import kotlinx.serialization.Serializable

@Serializable
class JikanResponse<T>(
    val pagination: Pagination?,
    val data: T
)
