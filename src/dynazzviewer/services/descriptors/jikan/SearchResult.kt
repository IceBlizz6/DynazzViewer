package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResult(
    @JsonProperty("request_hash", required = true)
       val requestHash: String,
    @JsonProperty("request_cached", required = true)
       val requestCached: Boolean,
    @JsonProperty("request_cache_expiry", required = true)
       val requestCacheExpiry: Int,
    @JsonProperty("last_page", required = true)
       val lastPage: Int,
    @JsonProperty("results", required = true)
       val results: List<ResultItem>
)
