package dynazzviewer.base

class Matcher {
    fun <TOriginal : UniqueKey, TUpdate : UniqueKey> match(
        original: List<TOriginal>,
        updated: List<TUpdate>
    ): MatchResult<TOriginal, TUpdate> {
        return MatchResult<TOriginal, TUpdate>(
            added = updated
                .filterNot { e -> original.any { f -> f.uniqueKey == e.uniqueKey } },
            removed = original
                .filterNot { e -> updated.any { f -> f.uniqueKey == e.uniqueKey } },
            matched = original
                .map { e -> e to updated.firstOrNull { f -> f.uniqueKey == e.uniqueKey } }
                .filter { e -> e.second != null }
                .toMap() as Map<TOriginal, TUpdate>
        )
    }

    fun <TOriginal : UniqueKey> matchWithString(
        original: List<TOriginal>,
        updated: List<String>
    ): MatchResult<TOriginal, String> {
        return MatchResult<TOriginal, String>(
            added = updated
                .filterNot { e -> original.any { f -> f.uniqueKey == e } },
            removed = original
                .filterNot { e -> updated.any { f -> f == e.uniqueKey } },
            matched = original
                .map { e -> e to updated.firstOrNull { f -> f == e.uniqueKey } }
                .filter { e -> e.second != null }
                .toMap() as Map<TOriginal, String>
        )
    }

    class MatchResult<TOriginal, TUpdate>(
        val added: List<TUpdate>,
        val removed: List<TOriginal>,
        val matched: Map<TOriginal, TUpdate>
    )
}
