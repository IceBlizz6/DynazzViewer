package dynazzviewer.storage

import dynazzviewer.entities.*
import java.io.Closeable
import javax.swing.text.html.HTML

interface ReadOperation : Closeable {
	fun mediaUnitById(id : Int) : MediaUnit
	
	fun mediaPartById(id : Int) : MediaPart
	
	fun mediaUnits() : List<MediaUnit>
	
	fun mediaPartCollections() : List<MediaPartCollection>
	
	fun mediaParts() : List<MediaPart>
	
	fun matchExtKey(keys : List<String>) : MediaUnit?
}
