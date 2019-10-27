package dynazzviewer.filesystem

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.stream.Collectors

class SystemFileSource(
    private val configuration: FileConfiguration
) : FileSource {
    override fun listFiles(path: String): Set<String> {
        val allowedExtensions = configuration.extensionFilter
        val files = File(path).listFiles { e -> allowedExtensions.contains(e.extension) }
        return files.map { e -> e.canonicalPath }.toSet()
    }

    override fun readCacheFile(path: String): Set<String> {
        val fileName = cacheFileName(path)
        BufferedReader(FileReader(fileName)).use { line ->
            return line.lines().collect(Collectors.toList()).toSet()
        }
    }

    override fun isCached(path: String): Boolean {
        val fileName = cacheFileName(path)
        return File(fileName).exists()
    }

    override fun saveCacheFile(path: String, content: Set<String>) {
        val fileName = cacheFileName(path)
        BufferedWriter(FileWriter(fileName)).use { writer ->
            for (line in content) {
                writer.write(line)
                writer.newLine()
            }
        }
    }

    private fun cacheFileName(path: String): String {
        val root = configuration.cacheDirectoryPath
        return root + File.separatorChar + path
            .replace(":", "__")
            .replace("/", "__")
            .replace("\\", "__")
    }
}
