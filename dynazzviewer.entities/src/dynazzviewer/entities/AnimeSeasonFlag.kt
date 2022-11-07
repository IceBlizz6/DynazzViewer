package dynazzviewer.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class AnimeSeasonFlag(
    @Id
    val malId: Int,
    var flag: AnimeSeasonFlagState
) : EntityModel
