package dynazzviewer.storage.sqlite

import jakarta.persistence.SharedCacheMode
import jakarta.persistence.ValidationMode
import jakarta.persistence.spi.ClassTransformer
import jakarta.persistence.spi.PersistenceUnitInfo
import jakarta.persistence.spi.PersistenceUnitTransactionType
import java.net.URL
import java.util.Collections
import java.util.Properties
import javax.sql.DataSource

internal class HibernatePersistenceUnitInfo : PersistenceUnitInfo {
    override fun getPersistenceUnitRootUrl(): URL? {
        return null
    }

    override fun getJtaDataSource(): DataSource? {
        return null
    }

    override fun getMappingFileNames(): MutableList<String> {
        return Collections.emptyList()
    }

    override fun getNewTempClassLoader(): ClassLoader? {
        return null
    }

    override fun getPersistenceUnitName(): String {
        return "ApplicationPersistenceUnit"
    }

    override fun getSharedCacheMode(): SharedCacheMode? {
        return null
    }

    override fun getClassLoader(): ClassLoader? {
        return null
    }

    override fun getTransactionType(): PersistenceUnitTransactionType {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL
    }

    override fun getProperties(): Properties {
        return Properties()
    }

    override fun getPersistenceXMLSchemaVersion(): String? {
        return null
    }

    override fun addTransformer(p0: ClassTransformer?) {
    }

    override fun getManagedClassNames(): MutableList<String> {
        return Collections.emptyList()
    }

    override fun getJarFileUrls(): MutableList<URL> {
        return Collections.list(
            this.javaClass
                .classLoader
                .getResources("")
        )
    }

    override fun getPersistenceProviderClassName(): String {
        return "org.hibernate.jpa.HibernatePersistenceProvider"
    }

    override fun getNonJtaDataSource(): DataSource? {
        return null
    }

    override fun excludeUnlistedClasses(): Boolean {
        return false
    }

    override fun getValidationMode(): ValidationMode? {
        return null
    }
}
