package dynazzviewer.services.descriptors.jikan

import dynazzviewer.services.descriptors.DescriptionPartCollection

class JikanSeries(
    val show: AnimeShow,
    val episodes: List<AnimeEpisode>
) {
    fun toDescriptionPartCollection(): DescriptionPartCollection {
        return show.toDescriptionPartCollection(episodes)
    }
}
