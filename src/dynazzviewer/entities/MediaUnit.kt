package dynazzviewer.entities

import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.OneToMany

@Entity
class MediaUnit(
    uniqueExtKey: String?,
    override var name: String
) : EntityModel, ExtReference(uniqueExtKey), NameContainer, IdContainer {
    @ManyToMany
    var tags: MutableList<MediaUnitTag> = mutableListOf()
        private set

    @OneToMany(mappedBy = "parent")
    lateinit var children: List<MediaPartCollection>
        private set

    @OneToMany(mappedBy = "mediaUnit")
    lateinit var images: List<MediaImage>
        private set

    override val root: MediaUnit
        get() = this
}
