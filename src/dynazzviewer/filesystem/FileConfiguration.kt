package dynazzviewer.filesystem

interface FileConfiguration {
    val videoExtensions: Set<String>

    val extensionFilter: Set<String>
}
