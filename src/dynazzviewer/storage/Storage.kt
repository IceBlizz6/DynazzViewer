package dynazzviewer.storage

interface Storage {
	fun read() : ReadOperation
	
	fun readWrite() : ReadWriteOperation
}
