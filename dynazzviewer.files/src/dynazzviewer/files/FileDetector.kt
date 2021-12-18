package dynazzviewer.files

interface FileDetector {
    fun parse(fileName: String): ParserResult?
}
