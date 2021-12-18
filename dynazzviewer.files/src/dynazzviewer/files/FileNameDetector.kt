package dynazzviewer.filesystem

class FileNameDetector : FileDetector {
    private val matchers: List<RegexMatcher> = listOf(
        RegexMatcher(
            regex = "(.*)[ .]S(\\d{1,2})E(\\d{1,2})[ .a-zA-Z]*(\\d{3,4}p)?",
            nameGroup = 1,
            seasonGroup = 2,
            episodeGroup = 3
        ),
        RegexMatcher(
            regex = "(\\[\\w+\\])\\s(\\w+\\s?\\w+?\\s?\\w+?)\\s-\\s(\\d+)\\s(\\[\\w+\\])\\.\\w+",
            nameGroup = 2,
            seasonGroup = null,
            episodeGroup = 3
        ),
        RegexMatcher(
            regex = "^\\[([A-Za-z -]+)\\][ ]([A-Za-z ()0-9-]+)[ ][-][ ][0]*([0-9]+)" +
                "([v]{0,1}[0-9]*)[ ]{0,1}(\\([A-Za-z0-9 ]+\\))*" +
                "[ ]{0,1}(\\[([A-Z0-9a-z]+)\\])*[.]([a-z]+)\$",
            nameGroup = 2,
            seasonGroup = null,
            episodeGroup = 3
        ),
        RegexMatcher(
            regex = "^([A-Za-z.]+)[Ss][0]([0-9]+)[Ee][0]*([0-9]+)",
            nameGroup = 1,
            seasonGroup = 2,
            episodeGroup = 3
        ),
        RegexMatcher(
            regex = "^\\[([A-Za-z -]+)\\][ ]([A-Za-z ()0-9-]+)[ ][-][ ][0]*([0-9]+)" +
                "([v]{0,1}[0-9]*)[ ]{0,1}(\\([A-Za-z0-9 ]+\\))*[ ]{0,1}" +
                "(\\[([A-Z0-9a-z]+)\\])*[ ]?\\[\\w+\\][.]([a-z]+)\$",
            nameGroup = 2,
            seasonGroup = null,
            episodeGroup = 3
        ),
        RegexMatcher(
            regex = "^\\[([A-Za-z -]+)\\][ ]([A-Za-z ()0-9-]+)([ ]" +
                "([0-9]*)[ ])+\\[[\\w\\ ]+\\][.]([a-z]+)\$",
            nameGroup = 2,
            seasonGroup = null,
            episodeGroup = 4
        ),
        RegexMatcher(
            regex = "^([(\\w )]+)[ ][-][ ][sS][0]?([0-9]+)[ ]" +
                "[eE][0]?([0-9]+)+[ ][-][ ][\\w -]+[(\\w -]+[)][.][\\w]+\$",
            nameGroup = 1,
            seasonGroup = 2,
            episodeGroup = 3
        )
    )

    override fun parse(fileName: String): ParserResult? {
        for (matcher in matchers) {
            val result = matcher.parse(fileName)

            if (result != null) {
                return result
            }
        }
        return null
    }
}
