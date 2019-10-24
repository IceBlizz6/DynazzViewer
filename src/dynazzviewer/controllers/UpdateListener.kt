package dynazzviewer.controllers

interface UpdateListener {
	fun updateMediaUnit(id : Int, recursive : Boolean)
	
	fun updateMediaPart(id : Int)
	
	fun updateMediaFile(id : Int)
	
	fun setMediaFileId(name : String, assignedId : Int)
}
