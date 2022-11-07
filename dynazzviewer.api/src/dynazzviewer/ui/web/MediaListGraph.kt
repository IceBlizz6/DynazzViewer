package dynazzviewer.ui.web

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.ViewStatus
import dynazzviewer.storage.MediaUnitSort
import dynazzviewer.storage.SortOrder
import dynazzviewer.storage.Storage
import dynazzviewer.ui.web.WebApplication.Companion.read

class MediaListGraph(
    builder: SchemaBuilder,
    private val storage: Storage
) {
    init {
        builder.apply {
            query("listMediaUnits") {
                resolver {
                    context: Context,
                    skip: Int,
                    take: Int,
                    sort: MediaUnitSort,
                    order: SortOrder ->
                    context.read().mediaUnits(skip, take, sort, order)
                }
            }
            query("internalMediaSearch") {
                resolver { context: Context, name: String ->
                    context.read().mediaPartCollectionsLike(name)
                }
            }
            query("internalMediaLookup") {
                resolver { context: Context, db: ExtDatabase, code: String ->
                    context.read().mediaPartCollectionByCode(db, code)
                }
            }
            mutation("setEpisodeWatchState") {
                resolver { mediaPartId: Int, status: ViewStatus ->
                    storage.readWrite { context ->
                        val mediaPart = context.mediaPartById(mediaPartId)
                        mediaPart.status = status
                        mediaPart.mediaFile?.let {
                            it.status = status
                        }
                    }
                    true
                }
            }
        }
    }
}
