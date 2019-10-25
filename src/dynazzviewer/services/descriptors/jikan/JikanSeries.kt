package dynazzviewer.services.descriptors.jikan

import dynazzviewer.services.descriptors.DescriptionPartCollection

class JikanSeries(
    val show: Show,
    val episodes: List<Episode>
) {
    fun toDescriptionPartCollection(): DescriptionPartCollection {
        return show.toDescriptionPartCollection(episodes)
    }
}
