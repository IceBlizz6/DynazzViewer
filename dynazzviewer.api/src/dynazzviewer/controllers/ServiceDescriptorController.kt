package dynazzviewer.controllers

import dynazzviewer.entities.ExtDatabase
import dynazzviewer.entities.MediaUnit
import dynazzviewer.services.descriptors.DescriptionUnit
import dynazzviewer.services.descriptors.DescriptorApi
import dynazzviewer.services.descriptors.ResultHeader
import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.ReadWriteOperation
import dynazzviewer.storage.Storage

class ServiceDescriptorController(
    private val descriptorServices: List<DescriptorApi>,
    private val listener: UpdateListener,
    private val storage: Storage
) {
    fun queryDescriptors(db: ExtDatabase, name: String): List<ResultHeader> {
        return descriptorServices
            .map { e -> e.querySearch(db, name) }
            .flatMap { e -> e?.toList() ?: listOf() }
    }

    fun queryDescriptor(db: ExtDatabase, code: String): DescriptionUnit? {
        for (service in descriptorServices) {
            val response = service.queryLookup(db, code)
            if (response != null) {
                return response
            }
        }
        return null
    }

    fun add(description: DescriptionUnit): Int {
        val mediaUnitId = insertOrUpdate(description)
        listener.updateMediaUnit(mediaUnitId, recursive = true)
        return mediaUnitId
    }

    private fun insertOrUpdate(description: DescriptionUnit): Int {
        storage.readWrite().use { context ->
            val match = match(description, context)
            if (match == null) {
                val mediaUnit = insert(description, context)
                return mediaUnit.id
            } else {
                update(description, match, context)
                return match.id
            }
        }
    }

    private fun match(description: DescriptionUnit, context: ReadOperation): MediaUnit? {
        val unitUniqueKey = description.uniqueKey
        if (unitUniqueKey == null) {
            for (season in description.children) {
                val mediaPartCollection = context.mediaPartCollectionByKey(season.uniqueKey)
                if (mediaPartCollection != null) {
                    return mediaPartCollection.parent
                }
            }
            return null
        } else {
            return context.mediaUnitByKey(unitUniqueKey)
        }
    }

    private fun insert(
        description: DescriptionUnit,
        context: ReadWriteOperation
    ): MediaUnit {
        val mediaUnit = description.create(context)
        for (season in description.children) {
            val mediaPartCollection = season.create(parent = mediaUnit, context = context)
            for (episode in season.episodes) {
                val mediaPart = episode.create(parent = mediaPartCollection, context = context)
                context.save(mediaPart)
            }
        }
        return mediaUnit
    }

    private fun update(
        description: DescriptionUnit,
        mediaUnit: MediaUnit,
        context: ReadWriteOperation
    ) {
        description.update(mediaUnit, context)
        context.save(mediaUnit)
    }
}
