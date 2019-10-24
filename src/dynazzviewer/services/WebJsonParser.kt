package dynazzviewer.services

interface WebJsonParser {
	fun <T> parseJsonRequest(uri : String, type : Class<T>) : T
}
