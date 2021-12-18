package dynazzviewer.entities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class AnimeSeasonFlag(
    @Id
    val malId: Int,
    var flag: AnimeSeasonFlagState
) : EntityModel
