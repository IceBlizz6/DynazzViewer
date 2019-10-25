package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResult(
    @JsonProperty("request_hash")
       val requestHash: String,
    @JsonProperty("request_cached")
       val requestCached: Boolean,
    @JsonProperty("request_cache_expiry")
       val requestCacheExpiry: Int,
    @JsonProperty("last_page")
       val lastPage: Int,
    @JsonProperty("results")
       val results: List<ResultItem>
)
