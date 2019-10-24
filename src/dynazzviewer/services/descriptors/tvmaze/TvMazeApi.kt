package dynazzviewer.services.descriptors.tvmaze

import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.HttpWebJsonParser
import dynazzviewer.services.WebJsonParser
import dynazzviewer.services.descriptors.DescriptionUnit
import dynazzviewer.services.descriptors.DescriptorApi
import dynazzviewer.services.descriptors.ResultHeader

class TvMazeApi(
	private val parser : WebJsonParser
) : DescriptorApi {
	override fun querySearch(db : ExtDatabase, name : String): List<ResultHeader>? {
		return if (db == ExtDatabase.TvMaze) {
			search(name).map { e -> e.toResultHeader() }
		} else {
			null
		}
	}
	
	override fun queryLookup(db : ExtDatabase, code : String) : DescriptionUnit? {
		if (ExtDatabase.TvMaze == db) {
			val showDetails = showDetails(code)
			val episodes = episodes(code)
			return showDetails.toDescriptionUnit(episodes)
		} else {
			return null
		}
	}
	
	fun search(name: String): List<SearchShowResult> {
		val queryString = name.replace(' ', '+')
		val uri = "http://api.tvmaze.com/search/shows?q=$queryString"
		val results = parser.parseJsonRequest(uri, CollectionParser.getArrayType())
		return results.toList()
	}
	
	fun episodes(tvMazeId : String) : List<Episode> {
		val uri = "http://api.tvmaze.com/shows/$tvMazeId/episodes?specials=1"
		return parser.parseJsonRequest(uri, CollectionParser.getEpisodeArrayType()).toList()
	}
	
	fun showDetails(tvMazeId : String) : ShowDetails {
		val uri = "http://api.tvmaze.com/shows/$tvMazeId"
		return parser.parseJsonRequest(uri, ShowDetails::class.java)
	}
}
