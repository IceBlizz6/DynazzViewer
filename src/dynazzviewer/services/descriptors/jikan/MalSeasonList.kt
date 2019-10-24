package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonProperty

class MalSeasonList(
	@JsonProperty("request_hash")
	val requestHash : String,
	@JsonProperty("request_cached")
	val requestCached : Boolean,
	@JsonProperty("request_cache_expiry")
	val requestCacheExpiry : Int,
	@JsonProperty("anime")
	val anime : List<AnimeSeasonEntry>
)
