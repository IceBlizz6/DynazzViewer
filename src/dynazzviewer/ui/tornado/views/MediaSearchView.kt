package dynazzviewer.ui.tornado.views

import dynazzviewer.base.ExtDatabase
import dynazzviewer.services.descriptors.ResultHeader
import dynazzviewer.ui.tornado.MainController
import dynazzviewer.ui.tornado.inputmodels.MediaSearchModel
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.image.Image
import tornadofx.*

class MediaSearchView() : View() {
    val mainController: MainController by inject()
    val service = mainController.serviceController
    val searchModel = MediaSearchModel()
    val searchResults: ObservableList<ResultHeader> = observableListOf()

    override val root: Parent = borderpane {
        left {
            form {
                fieldset {
                    field("Name") {
                        textfield(searchModel.name)
                    }
                    togglegroup {
                        bind(searchModel.selectedExtDatabase)
                        radiobutton("MyAnimeList", value = ExtDatabase.MyAnimeList) {
                            isSelected = true
                        }
                        radiobutton("IMDB", value = ExtDatabase.TvMaze)
                    }
                }
                button("Search").action {
                    searchResults.clear()
                    val searchQueryText = searchModel.name.value
                    val extDatabase = searchModel.selectedExtDatabase.value
                    val results = service.queryDescriptors(extDatabase, searchQueryText)
                    searchResults.addAll(results)
                }
            }
        }
        center {
            tableview(searchResults) {
                readonlyColumn("Image", ResultHeader::imageUrl).cellFormat {
                    graphic = hbox {
                        minWidth = 100.0
                        alignment = Pos.CENTER
                        imageview(
                                Image(
                                        it,
                                        100.0,
                                        100.0,
                                        true,
                                        false,
                                        true)
                        )
                    }
                }
                readonlyColumn("Title", ResultHeader::name)
                readonlyColumn("Reference", ResultHeader::extDbCode)
                readonlyColumn("Action", ResultHeader::extReference).cellFormat {
                    graphic = hbox {
                        button("Add/Update").action {
                            val description = service.queryDescriptor(it.first, it.second)
                            if (description == null) {
                                TODO("Error dialogue (Unable to lookup external media)")
                            } else {
                                service.add(description)
                            }
                        }
                    }
                }
            }
        }
    }
}
