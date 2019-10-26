package dynazzviewer.filesystem

import dynazzviewer.entities.MediaPart
import dynazzviewer.entities.MediaPartCollection
import dynazzviewer.storage.ReadOperation
import kotlin.math.abs

class SeriesNameMatcher(
    private val context: ReadOperation
) {
    fun matchClosest(seriesName: String, season: Int?, episode: Int): MediaPart? {
        val trimmedSeriesName = trimParanthesis(seriesName).toLowerCase()
        val sqlLikeString = createLikeString(trimmedSeriesName)
        val matchingSeasons = matchSeasonTitles(sqlLikeString, season)
        if (matchingSeasons.isEmpty()) {
            return null
        } else {
            val seasonMatch = matchNear(seriesName, matchingSeasons)
            return seasonMatch.children.singleOrNull { e -> e.episodeNumber == episode }
        }
    }

    private fun matchSeasonTitles(sqlLike: String, season: Int?): Map<MediaPartCollection, String> {
        var alternativeTitles = context.alternativeTitleLike(sqlLike)
        if (season != null) {
            alternativeTitles = alternativeTitles.filter { e -> e.parent.seasonNumber == season }
        }
        val map = alternativeTitles.map { it.parent to it.name }.toMap()
        if (season == null) {
            return map
        } else {
            val mediaUnits = context.mediaUnitsLike(sqlLike)
            val mediaPartCollections = mediaUnits
                .flatMap { e -> e.children }
                .filter { e -> e.seasonNumber == season }
                .filter { e -> !map.containsKey(e) }
            val mutableMap = mutableMapOf<MediaPartCollection, String>()
            mutableMap.putAll(map)
            mutableMap.putAll(mediaPartCollections.map { e -> e to e.name })
            return mutableMap
        }
    }

    private fun matchNear(
        seriesName: String,
        seasonWithMatchName: Map<MediaPartCollection, String>
    ): MediaPartCollection {
        return seasonWithMatchName
            .minBy { e -> abs(e.value.length - seriesName.length) }!!
            .key
    }

    private fun createLikeString(input: String): String {
        var sqlLikeString = "%"
        for (ch in input) {
            if (ch.isLetter()) {
                sqlLikeString += "$ch%"
            }
        }
        return sqlLikeString
    }

    private fun trimParanthesis(input: String): String {
        val regex = "^([A-Za-z -]+)(\\([A-Za-z ]+\\))?\$"
        return regex.toRegex().findAll(input).single().groupValues[1].trim()
    }
}
