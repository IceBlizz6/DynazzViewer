package dynazzviewer.services.descriptors.jikan

import com.fasterxml.jackson.annotation.JsonValue

enum class MalType(
	@JsonValue
	private val value : String
) {
	TV("TV"),
	ANIME("anime"),
	MANGA("manga"),
	ONA("ONA"),
	OVA("OVA"),
	MOVIE("Movie"),
	SPECIAL("Special"),
	UNKNOWN("Unknown"),
	MUSIC("Music"),
}
