package dynazzviewer.services.descriptors.jikan

import kotlinx.serialization.Serializable

@Serializable
class AnimeRelation(
    val relation: RelatedType,
    val entry: List<AnimeShowHeader>
)
