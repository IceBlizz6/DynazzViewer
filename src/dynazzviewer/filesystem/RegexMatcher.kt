package dynazzviewer.filesystem

class RegexMatcher(
    val regex: String,
    val nameGroup: Int,
    val seasonGroup: Int?,
    val episodeGroup: Int?
) {
    fun parse(input: String): ParserResult? {
        val result = regex
            .toRegex()
            .findAll(input)
            .singleOrNull()
        if (result == null) {
            return null
        } else {
            val name = result.groupValues[nameGroup].replace('.', ' ')

            val season: Int?
            if (seasonGroup == null) {
                season = null
            } else {
                val parseSeason = result.groupValues[seasonGroup].toIntOrNull()
                if (parseSeason == null) {
                    return null
                } else {
                    season = parseSeason
                }
            }

            val episode: Int?
            if (episodeGroup == null) {
                episode = null
            } else {
                val parseEpisode = result.groupValues[episodeGroup].toIntOrNull()
                if (parseEpisode == null) {
                    return null
                } else {
                    episode = parseEpisode
                }
            }

            return ParserResult(
                name = name,
                season = season,
                episode = episode
            )
        }
    }
}
