package dynazzviewer.storage.query

import com.querydsl.core.ResultTransformer
import com.querydsl.core.Tuple
import com.querydsl.core.types.Expression
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.*
import kotlin.reflect.KClass

class EmptyQueryStream<QTEntity : EntityPathBase<TEntity>, TEntity> :
    QueryStream<QTEntity, TEntity> {
    override fun filter(lmbd: (QTEntity) -> BooleanExpression): QueryStream<QTEntity, TEntity> {
        return this
    }

    override fun fetchList(): List<TEntity> {
        return emptyList()
    }

    override fun <T> fetchSingle(expr: (QTEntity) -> Path<T>): T {
        throw RuntimeException("Missing result")
    }

    override fun <T> fetchSum(
        expr: (QTEntity) -> NumberPath<T>
    ): T? where T : Number, T : Comparable<*> {
        return null
    }

    override fun <T> fetchSumExpression(
        expr: (QTEntity) -> NumberExpression<T>
    ): T? where T : Number, T : Comparable<*> {
        return null
    }

    companion object {
        fun <QT : EntityPathBase<T>, T> empty(): EmptyQueryStream<QT, T> {
            return EmptyQueryStream()
        }
    }

    override fun filterBuild(
        transform: (QueryBuilder<QTEntity, TEntity, BooleanExpression>) -> BooleanExpression
    ): QueryStream<QTEntity, TEntity> {
        return this
    }

    override fun <QT : EntityPathBase<T>, T> map(
        transform: (QTEntity) -> QT
    ): QueryStream<QT, T> {
        return EmptyQueryStream()
    }

    override fun fetchSingle(): TEntity {
        throw RuntimeException("Missing result")
    }

    override fun <T> fetchSingleOrNull(expr: (QTEntity) -> Path<T>): T? {
        return null
    }

    override fun fetchSingleOrNull(): TEntity? {
        return null
    }

    override fun fetchCount(): Long {
        return 0
    }

    override fun skip(count: Long): QueryStream<QTEntity, TEntity> {
        return this
    }

    override fun limit(count: Long): QueryStream<QTEntity, TEntity> {
        return this
    }

    override fun orderByWithBuilder(
        builder: (QueryBuilder<QTEntity, TEntity, OrderSpecifier<*>>) -> OrderSpecifier<*>
    ): QueryStream<QTEntity, TEntity> {
        return this
    }

    override fun orderBy(
        transform: (QTEntity) -> OrderSpecifier<*>
    ): QueryStream<QTEntity, TEntity> {
        return this
    }

    override fun <T> fetchList(expr: (QTEntity) -> Path<T>): List<T> {
        return emptyList()
    }

    override fun queryString(): String? {
        return null
    }

    override fun fetchContains(item: TEntity): Boolean {
        return false
    }

    override fun <T : Comparable<Nothing>?> fetchDateSingle(
        path: (QTEntity) -> DatePath<T>,
        expr: (DatePath<T>) -> DateExpression<T>
    ): T? {
        return null
    }

    override fun <V> withSource(
        defaultValue: V,
        transform: (source: QTEntity, stream: QueryStream<QTEntity, TEntity>) -> V
    ): V {
        return defaultValue
    }

    override fun <V> fetchWithTransform(
        defaultValue: V,
        transform: (QTEntity) -> ResultTransformer<V>
    ): V {
        return defaultValue
    }

    override fun <K, V> fetchMap(
        key: (QTEntity) -> Expression<K>,
        value: (QTEntity) -> Expression<V>
    ): Map<K, V> {
        return emptyMap()
    }

    override fun unsafeFilterId(id: Long): QueryStream<QTEntity, TEntity> {
        return this
    }

    override fun <QT : EntityPathBase<T>, T> flatMapOn(
        joinedType: KClass<QT>,
        condition: (QTEntity, QT) -> Predicate
    ): QueryStream<QT, T> {
        return EmptyQueryStream()
    }

    override fun <QT : EntityPathBase<T>, T> flatMap(
        transform: (QTEntity) -> ListPath<T, QT>
    ): QueryStream<QT, T> {
        return EmptyQueryStream()
    }

    override fun fetchListFields(transform: (QTEntity) -> List<Expression<*>>): List<Tuple> {
        return emptyList()
    }
}
