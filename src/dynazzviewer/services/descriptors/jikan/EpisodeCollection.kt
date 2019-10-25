package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonProperty

class EpisodeCollection(
    @JsonProperty("request_hash")
       val requestHash: String,
    @JsonProperty("request_cached")
       val requestCached: Boolean,
    @JsonProperty("request_cache_expiry")
       val requestCacheExpiry: Int,
    @JsonProperty("episodes_last_page")
       val episodesLastPage: Int,
    @JsonProperty("episodes")
       val episodes: List<Episode>
)
