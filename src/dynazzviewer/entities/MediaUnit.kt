package dynazzviewer.entities

import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.OneToMany

@Entity
class MediaUnit(
    uniqueExtKey: String?,
    var name: String
) : EntityModel, ExtReference(uniqueExtKey), IdContainer {
    @ManyToMany
    var tags: MutableList<MediaUnitTag> = mutableListOf()
        private set

    @OneToMany(mappedBy = "parent")
    lateinit var children: List<MediaPartCollection>
        private set

    @OneToMany
    var images: MutableList<MediaImage> = mutableListOf()
        private set

    override val root: MediaUnit
        get() = this
}
