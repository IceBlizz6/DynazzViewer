package dynazzviewer.files

interface FileConfiguration {
    val videoExtensions: Set<String>

    val extensionFilter: Set<String>

    var rootDirectoryPaths: Set<String>

    val cacheDirectoryPath: String
}
