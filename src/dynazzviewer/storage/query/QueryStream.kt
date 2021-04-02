package dynazzviewer.storage.query

import com.querydsl.core.ResultTransformer
import com.querydsl.core.Tuple
import com.querydsl.core.types.Expression
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.*
import kotlin.reflect.KClass

interface QueryStream<QTEntity : EntityPathBase<TEntity>, TEntity> {
    /**
     * Filter result set on predicate
     */
    fun filter(transform: (QTEntity) -> BooleanExpression): QueryStream<QTEntity, TEntity>

    /**
     * Filter result set on predicate that can map to other entities
     */
    fun filterBuild(transform: (PredicateBuilder<QTEntity, TEntity>) -> BooleanExpression): QueryStream<QTEntity, TEntity>

    /**
     * Filter result set on predicate that can map to other entities
     * Returns result that matches any of the predicates in list
     */
    fun filterBuildAny(transform: (PredicateBuilder<QTEntity, TEntity>) -> List<BooleanExpression>): QueryStream<QTEntity, TEntity>

    /**
     * Maps result rows one to many
     */
    fun <QT : EntityPathBase<T>, T> flatMap(transform: (QTEntity) -> ListPath<T, QT>): QueryStream<QT, T>

    /**
     * Maps to unrelated entity
     */
    fun <QT : EntityPathBase<T>, T> flatMapOn(joinedType: KClass<QT>, condition: (QTEntity, QT) -> Predicate): QueryStream<QT, T>

    /**
     * Stores source as reference for later query
     */
    fun <V> withSource(defaultValue: V, transform: (source: QTEntity, stream: QueryStream<QTEntity, TEntity>) -> V): V

    /**
     * Maps result rows one to one
     */
    fun <QT : EntityPathBase<T>, T> map(transform: (QTEntity) -> QT): QueryStream<QT, T>

    fun <T : Comparable<Nothing>?> fetchDateSingle(path: ((QTEntity) -> DatePath<T>), expr: ((DatePath<T>) -> DateExpression<T>)): T?

    /**
     * Returns results with given transformation
     */
    fun <V> fetchWithTransform(defaultValue: V, transform: (QTEntity) -> ResultTransformer<V>): V

    fun <K, V> fetchMap(key: (QTEntity) -> Expression<K>, value: (QTEntity) -> Expression<V>): Map<K, V>

    /**
     * Skips n rows in result
     */
    fun skip(count: Long): QueryStream<QTEntity, TEntity>

    /**
     * Filter number of rows in result
     */
    fun limit(count: Long): QueryStream<QTEntity, TEntity>

    /**
     * Sorts result by expression
     */
    fun orderBy(transform: (QTEntity) -> OrderSpecifier<*>): QueryStream<QTEntity, TEntity>

    /**
     * Returns all rows
     */
    fun fetchList(): List<TEntity>

    /**
     * Returns value from all rows
     */
    fun <T> fetchList(expr: (QTEntity) -> Path<T>): List<T>

    /**
     * Returns value from single row or throws if not exists
     */
    fun <T> fetchSingle(expr: (QTEntity) -> Path<T>): T

    /**
     * Returns single row or throws if not exists
     */
    fun fetchSingle(): TEntity

    /**
     * Returns value from single row or null if not exists
     */
    fun <T> fetchSingleOrNull(expr: (QTEntity) -> Path<T>): T?

    /**
     * Returns single row or null if not exists
     */
    fun fetchSingleOrNull(): TEntity?

    /**
     * Returns sum of values from all rows
     */
    fun <T> fetchSum(expr: (QTEntity) -> NumberPath<T>): T? where T : Number, T : Comparable<*>

    /**
     * Calculates sum from all rows using given expression
     */
    fun <T> fetchSumExpression(expr: (QTEntity) -> NumberExpression<T>): T? where T : Number, T : Comparable<*>

    /**
     * Runs query to retrieve number of rows
     */
    fun fetchCount(): Long

    /**
     * Returns true of entity exists in query result
     */
    fun fetchContains(item: TEntity): Boolean

    /**
     * Returns current built query from methods calls
     */
    fun queryString(): String?

    /**
     * Filter entities on ID (does not check if ID field exists)
     */
    fun unsafeFilterId(id: Long): QueryStream<QTEntity, TEntity>

    fun fetchListFields(transform: (QTEntity) -> List<Expression<*>>): List<Tuple>
}
