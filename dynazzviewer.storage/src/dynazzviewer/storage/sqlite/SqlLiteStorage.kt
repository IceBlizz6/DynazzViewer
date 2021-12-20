package dynazzviewer.storage.sqlite

import com.querydsl.core.types.dsl.EntityPathBase
import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.ReadWriteOperation
import dynazzviewer.storage.Storage
import dynazzviewer.storage.StorageMode
import dynazzviewer.storage.query.JpaQueryStream
import dynazzviewer.storage.query.QueryStream
import org.hibernate.cfg.AvailableSettings.DIALECT
import org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS
import org.hibernate.cfg.AvailableSettings.JPA_JDBC_DRIVER
import org.hibernate.cfg.AvailableSettings.JPA_JDBC_URL
import org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING
import org.hibernate.cfg.AvailableSettings.SHOW_SQL
import org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE
import org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE
import org.hibernate.cfg.AvailableSettings.USE_REFLECTION_OPTIMIZER
import org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE
import org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE
import org.hibernate.jpa.HibernatePersistenceProvider
import java.io.File
import java.util.HashMap
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class SqlLiteStorage(
    storageMode: StorageMode,
    rootStorageDirectory: String
) : Storage {
    companion object {
        const val CONNECTION_PREFIX = "jdbc:sqlite:"
        const val DB_FILENAME = "media.db"
    }

    private val entityManagerFactory: EntityManagerFactory

    init {
        val unitInfo = HibernatePersistenceUnitInfo()
        val map = HashMap<String, Any>()
        map.put(JPA_JDBC_DRIVER, "org.sqlite.JDBC")
        when (storageMode) {
            StorageMode.FILE -> {
                val filename = rootStorageDirectory + File.separatorChar + DB_FILENAME
                map.put(JPA_JDBC_URL, CONNECTION_PREFIX + filename)
                map.put("hibernate.hbm2ddl.auto", "update")
            }
            StorageMode.MEMORY -> {
                map.put(JPA_JDBC_URL, "$CONNECTION_PREFIX:memory:")
                map.put("hibernate.hbm2ddl.auto", "create-drop")
            }
        }.let { }
        map.put(DIALECT, "org.sqlite.hibernate.dialect.SQLiteDialect")
        map.put(SHOW_SQL, false)
        map.put(QUERY_STARTUP_CHECKING, false)
        map.put(GENERATE_STATISTICS, false)
        map.put(USE_REFLECTION_OPTIMIZER, false)
        map.put(USE_SECOND_LEVEL_CACHE, false)
        map.put(USE_QUERY_CACHE, false)
        map.put(USE_STRUCTURED_CACHE, false)
        map.put(STATEMENT_BATCH_SIZE, 20)
        map.put("hibernate.connection.autocommit", false)
        map.put("hibernate.connection.provider_disables_autocommit", true)
        val persistenceProvider = HibernatePersistenceProvider()
        entityManagerFactory = persistenceProvider
            .createContainerEntityManagerFactory(unitInfo, map)
    }

    override fun read(): ReadOperation {
        return DataContext(this)
    }

    override fun readWrite(): ReadWriteOperation {
        return TransactionDataContext(this)
    }

    internal fun createEntityManager(): EntityManager {
        return entityManagerFactory.createEntityManager()
    }

    internal fun <QTEntity : EntityPathBase<TEntity>, TEntity> stream(
        entityManager: EntityManager,
        source: QTEntity
    ): QueryStream<QTEntity, TEntity> {
        return JpaQueryStream(source, entityManager)
    }
}