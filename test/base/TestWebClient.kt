package base

import dynazzviewer.services.WebClient
import java.lang.RuntimeException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

class TestWebClient(
	private val fileLookup : Map<String, String>
) : WebClient {
	var callCount : Int = 0
		private set
	
	override fun requestData(uri : String) : String {
		callCount++
		val resourceName : String? = fileLookup[uri]
		
		if (resourceName == null) {
			throw KotlinNullPointerException("No file lookup found for " + uri)
		} else {
			val nullableResource : URL? = javaClass.classLoader.getResource(resourceName)
			
			if (nullableResource == null) {
				throw KotlinNullPointerException("Resource " + resourceName + " not found")
			} else {
				val resource = nullableResource
				val path = Paths.get(resource.toURI())
				val content = Files.readAllBytes(path)
				return String(content)
			}
		}
	}
}
