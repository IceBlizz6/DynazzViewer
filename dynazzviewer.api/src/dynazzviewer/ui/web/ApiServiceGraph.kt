package dynazzviewer.ui.web

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dynazzviewer.controllers.AnimeSeasonController
import dynazzviewer.controllers.ServiceDescriptorController
import dynazzviewer.entities.AnimeSeasonFlagState
import dynazzviewer.entities.ExtDatabase
import dynazzviewer.services.descriptors.ResultHeader
import dynazzviewer.services.descriptors.jikan.MalYearSeason
import dynazzviewer.storage.Storage

class ApiServiceGraph(
    builder: SchemaBuilder,
    private val service: ServiceDescriptorController,
    private val storage: Storage,
    private val controller: AnimeSeasonController
) {
    init {
        builder.apply {
            query("externalMediaSearch") {
                resolver { db: ExtDatabase, name: String ->
                    val result: List<ResultHeader> = service.queryDescriptors(db, name)
                    storage.read { context ->
                        val storedState = context.mediaUnitExist(result)
                        result.map {
                            MediaSearchResultItem(
                                name = it.name,
                                saved = storedState[it]!!,
                                extDb = it.extDb,
                                extDbCode = it.extDbCode,
                                imageUrl = it.imageUrl
                            )
                        }
                    }
                }
            }
            query("externalMediaLookup") {
                resolver { db: ExtDatabase, code: String ->
                    service.queryDescriptor(db, code)
                }
            }
            mutation("externalMediaAdd") {
                resolver { db: ExtDatabase, code: String ->
                    val description = service.queryDescriptor(db, code)
                    if (description == null) {
                        false
                    } else {
                        service.add(description)
                        true
                    }
                }
            }
            query("animeSeasonList") {
                resolver<List<AnimeSeasonController.MalSeasonIdentifier>> {
                    controller.list()
                }
            }
            query("animeSeason") {
                resolver { year: Int, season: MalYearSeason ->
                    controller.load(year, season)
                }
            }
            mutation("animeSeasonAdd") {
                resolver { year: Int, season: MalYearSeason ->
                    controller.addAnimeSeason(year, season)
                    true
                }
            }
            mutation("animeSeasonMark") {
                resolver { malId: Int, flag: AnimeSeasonFlagState ->
                    controller.markSeries(malId, flag)
                    true
                }
            }
        }
    }

    class MediaSearchResultItem(
        val name: String,
        val imageUrl: String?,
        val extDb: ExtDatabase,
        val extDbCode: String,
        val saved: Boolean
    )
}
