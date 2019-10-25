package dynazzviewer.filesystem

interface FileDetector {
    fun parse(fileName: String): ParserResult?
}
