package dynazzviewer.storage.sqlite

import dynazzviewer.entities.QAnimeSeasonFlag
import dynazzviewer.entities.QMediaFile
import dynazzviewer.entities.QMediaUnit
import dynazzviewer.storage.JsonDumper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

internal class SqlLiteJsonDumper(
    private val context: DataContext
) : JsonDumper {
    override fun dumpTo(file: File) {
        val root = dumpRootObject()
        val json = Json.encodeToString(root)
        file.writeText(json)
    }

    private fun dumpRootObject(): JsonDumper.Root {
        return JsonDumper.Root(
            animeSeasonFlags = context.stream(QAnimeSeasonFlag.animeSeasonFlag)
                .fetchList()
                .map { JsonDumper.AnimeSeasonFlag(it.malId, it.flag) },
            mediaFiles = context.stream(QMediaFile.mediaFile)
                .fetchList()
                .map { JsonDumper.MediaFile(it.name, it.status) },
            mediaLibrary = context.stream(QMediaUnit.mediaUnit)
                .fetchList()
                .map { JsonDumper.MediaSeries.from(it) }
        )
    }
}
