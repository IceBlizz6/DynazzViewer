package dynazzviewer.storage.sqlite

import dynazzviewer.base.Configuration
import dynazzviewer.entities.IdContainer
import dynazzviewer.entities.NameContainer
import dynazzviewer.storage.StorageMode
import dynazzviewer.storage.ReadOperation
import dynazzviewer.storage.ReadWriteOperation
import dynazzviewer.storage.Storage
import org.hibernate.cfg.AvailableSettings.*
import org.jinq.orm.stream.JinqStream
import java.io.File
import javax.persistence.EntityManagerFactory
import org.jinq.jpa.*;
import java.util.HashMap
import javax.persistence.EntityManager
import org.hibernate.jpa.HibernatePersistenceProvider


class SqlLiteStorage : Storage {
	companion object {
		const val CONNECTION_PREFIX = "jdbc:sqlite:";
		const val DB_FILENAME = "media.db";
	}
	
	private val entityManagerFactory : EntityManagerFactory
	private val streamProvider : JinqJPAStreamProvider
	
	constructor(configuration : Configuration) {
		val unitInfo = HibernatePersistenceUnitInfo()
		val map = HashMap<String, Any>()
		map.put(JPA_JDBC_DRIVER, "org.sqlite.JDBC")
		
		if (configuration.storageMode == StorageMode.FILE) {
			val filename = configuration.rootStorageDirectory + File.separatorChar + DB_FILENAME;
			map.put(JPA_JDBC_URL, CONNECTION_PREFIX + filename)
			map.put("hibernate.hbm2ddl.auto", "update")
		} else if(configuration.storageMode == StorageMode.MEMORY) {
			map.put(JPA_JDBC_URL, CONNECTION_PREFIX + ":memory:")
			map.put("hibernate.hbm2ddl.auto", "create-drop")
		} else {
			throw RuntimeException("Unknown storage mode: " + configuration.storageMode)
		}
		map.put(DIALECT, "org.hibernate.dialect.SQLiteDialect")
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
		entityManagerFactory = persistenceProvider.createContainerEntityManagerFactory(unitInfo, map)
		streamProvider = JinqJPAStreamProvider(entityManagerFactory)
		streamProvider.registerAssociationAttribute(IdContainer::class.java.getMethod("getId"), "id", false)
		streamProvider.registerAssociationAttribute(NameContainer::class.java.getMethod("getName"), "name", false)
	}
	
	override fun read(): ReadOperation {
		return DataContext(this)
	}
	
	override fun readWrite(): ReadWriteOperation {
		return TransactionDataContext(this)
	}

	internal fun createEntityManager() : EntityManager {
		return entityManagerFactory.createEntityManager();
	}
	
	internal fun <T> stream(entityManager : EntityManager, entityType : Class<T>) : JinqStream<T> {
		return streamProvider.streamAll(entityManager, entityType);
	}
}
