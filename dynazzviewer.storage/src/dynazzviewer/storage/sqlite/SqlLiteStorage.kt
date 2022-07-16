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
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class SqlLiteStorage(
    storageMode: StorageMode
) : Storage {
    companion object {
        const val CONNECTION_PREFIX = "jdbc:sqlite:"
    }

    private val entityManagerFactory: EntityManagerFactory

    init {
        val unitInfo = HibernatePersistenceUnitInfo()
        val map = mutableMapOf<String, Any>()
        map[JPA_JDBC_DRIVER] = "org.sqlite.JDBC"
        map[JPA_JDBC_URL] = CONNECTION_PREFIX + storageMode.path
        map["hibernate.hbm2ddl.auto"] = storageMode.initOperation.opName
        map[DIALECT] = "org.sqlite.hibernate.dialect.SQLiteDialect"
        map[SHOW_SQL] = false
        map[QUERY_STARTUP_CHECKING] = false
        map[GENERATE_STATISTICS] = false
        map[USE_REFLECTION_OPTIMIZER] = false
        map[USE_SECOND_LEVEL_CACHE] = false
        map[USE_QUERY_CACHE] = false
        map[USE_STRUCTURED_CACHE] = false
        map[STATEMENT_BATCH_SIZE] = 20
        map["hibernate.connection.autocommit"] = false
        map["hibernate.connection.provider_disables_autocommit"] = true
        val persistenceProvider = HibernatePersistenceProvider()
        entityManagerFactory = persistenceProvider
            .createContainerEntityManagerFactory(unitInfo, map)
    }

    override fun readKeepAlive(): ReadOperation {
        return DataContext(this)
    }

    override fun <T> read(use: (ReadOperation) -> T): T {
        return DataContext(this).use { use(it) }
    }

    override fun <T> readWrite(use: (ReadWriteOperation) -> T): T {
        return TransactionDataContext(this).use { use(it) }
    }

    override fun dumpTo(file: File) {
        DataContext(this).use { context ->
            SqlLiteJsonDumper(context).dumpTo(file)
        }
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
