package dynazzviewer.services.descriptors

import dynazzviewer.base.ExtDatabase

interface DescriptorApi {
    fun querySearch(db: ExtDatabase, name: String): List<ResultHeader>?

    fun queryLookup(db: ExtDatabase, code: String): DescriptionUnit?
}
