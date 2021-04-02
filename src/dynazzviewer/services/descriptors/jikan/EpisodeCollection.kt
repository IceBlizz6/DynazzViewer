package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonProperty

class EpisodeCollection(
    @JsonProperty("request_hash", required = true)
    val requestHash: String,
    @JsonProperty("request_cached", required = true)
    val requestCached: Boolean,
    @JsonProperty("request_cache_expiry", required = true)
    val requestCacheExpiry: Int,
    @JsonProperty("episodes_last_page", required = true)
    val episodesLastPage: Int,
    @JsonProperty("episodes", required = true)
    val episodes: List<Episode>
)
