package dynazzviewer.storage.query

import com.querydsl.core.ResultTransformer
import com.querydsl.core.Tuple
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.*
import com.querydsl.jpa.impl.JPAQuery
import javax.persistence.EntityManager
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaType

class JpaQueryStream<QTEntity : EntityPathBase<TEntity>, TEntity> : QueryStream<QTEntity, TEntity> {
    private val source: QTEntity
    private var query: JPAQuery<TEntity>
    private val joinNameGenerator: JoinNameGenerator

    constructor(source: QTEntity, entityManager: EntityManager) {
        this.source = source
        this.query = JPAQuery(entityManager)
        this.query = query.from(source)
        this.joinNameGenerator = JoinNameGenerator()
    }

    private constructor(
        source: QTEntity,
        query: JPAQuery<TEntity>,
        joinNameGenerator: JoinNameGenerator
    ) {
        this.source = source
        this.query = query
        this.joinNameGenerator = joinNameGenerator
    }

    override fun orderBy(
        transform: (QTEntity) -> OrderSpecifier<*>
    ): QueryStream<QTEntity, TEntity> {
        query = query.orderBy(transform(source))
        return this
    }

    override fun fetchListFields(transform: (QTEntity) -> List<Expression<*>>): List<Tuple> {
        val expressions = transform(source)
        return query.select(*expressions.toTypedArray()).fetch()
    }

    /**
     * Note: Adding isTrue on BooleanPath to avoid AST node path error
     *          Should investigate further and remove if possible
     */
    override fun filter(
        transform: (QTEntity) -> BooleanExpression
    ): QueryStream<QTEntity, TEntity> {
        var expr: BooleanExpression = transform(source)
        if (expr is BooleanPath) {
            expr = expr.isTrue
        }
        query = query.where(expr)
        return this
    }

    override fun skip(count: Long): QueryStream<QTEntity, TEntity> {
        query = query.offset(count)
        return this
    }

    override fun limit(count: Long): QueryStream<QTEntity, TEntity> {
        query = query.limit(count)
        return this
    }

    override fun orderByWithBuilder(
        build: (QueryBuilder<QTEntity, TEntity, OrderSpecifier<*>>) -> OrderSpecifier<*>
    ): QueryStream<QTEntity, TEntity> {
        val builder = JpaQueryBuilder<QTEntity, TEntity, OrderSpecifier<*>>(
            source,
            query,
            joinNameGenerator
        )
        query = query.orderBy(build(builder))
        return this
    }

    override fun <QT : EntityPathBase<T>, T> map(
        transform: (QTEntity) -> QT
    ): QueryStream<QT, T> {
        val joinedSource: QT = transform(source)
        val joinedQuery: JPAQuery<T> = query.select(joinedSource)
        return JpaQueryStream(joinedSource, joinedQuery, joinNameGenerator)
    }

    override fun <QT : EntityPathBase<T>, T> flatMap(
        transform: (QTEntity) -> ListPath<T, QT>
    ): QueryStream<QT, T> {
        val joinedSource: ListPath<T, QT> = transform(source)
        val joinedType: KClass<out QT> = joinedSource.any()::class
        val joined = generateJoinInstance(joinedType, joinNameGenerator)
        val joinedQuery = query.innerJoin(joinedSource, joined).select(joined)
        return JpaQueryStream(joined, joinedQuery, joinNameGenerator)
    }

    override fun <QT : EntityPathBase<T>, T> flatMapOn(
        joinedType: KClass<QT>,
        condition: (QTEntity, QT) -> Predicate
    ): QueryStream<QT, T> {
        val joined = generateJoinInstance(joinedType, joinNameGenerator)
        val predicate = condition(source, joined)
        val joinedQuery: JPAQuery<T> = query.innerJoin(joined).on(predicate).select(joined)
        return JpaQueryStream(joined, joinedQuery, joinNameGenerator)
    }

    override fun fetchList(): List<TEntity> {
        return query.distinct().select(source).fetch()
    }

    override fun <T> fetchList(expr: (QTEntity) -> Path<T>): List<T> {
        val fetchSource = expr(source)
        return query.distinct().select(fetchSource).fetch()
    }

    override fun fetchSingle(): TEntity {
        val result: TEntity? = query.distinct().select(source).fetchOne()
        if (result == null) {
            throw RuntimeException("No result from query: $query")
        } else {
            return result
        }
    }

    override fun <T> fetchSingleOrNull(expr: (QTEntity) -> Path<T>): T? {
        return query.distinct().select(expr(source)).fetchOne()
    }

    override fun fetchSingleOrNull(): TEntity? {
        return query.distinct()
            .select(source)
            .fetchOne()
    }

    override fun <T> fetchSingle(expr: (QTEntity) -> Path<T>): T {
        val result: T? = fetchSingleOrNull(expr)
        if (result == null) {
            throw RuntimeException("Missing result")
        } else {
            return result
        }
    }

    override fun <T> fetchSum(
        expr: (QTEntity) -> NumberPath<T>
    ): T? where T : Number, T : Comparable<*> {
        val sumExpr = expr(source).sum()
        return query.distinct().select(sumExpr).fetchOne()
    }

    override fun <T> fetchSumExpression(
        expr: (QTEntity) -> NumberExpression<T>
    ): T? where T : Number, T : Comparable<*> {
        val sumExpr = expr(source).sum()
        return query.distinct().select(sumExpr).fetchOne()
    }

    override fun <T : Comparable<Nothing>?> fetchDateSingle(
        path: ((QTEntity) -> DatePath<T>),
        expr: ((DatePath<T>) -> DateExpression<T>)
    ): T? {
        val datePath: DatePath<T> = path(source)
        val dateExpression: DateExpression<T> = expr(datePath)
        return query.distinct().select(dateExpression).fetchOne()
    }

    override fun fetchCount(): Long {
        return query.fetchCount()
    }

    override fun fetchContains(item: TEntity): Boolean {
        return query
            .distinct()
            .where(source.eq(item))
            .fetchCount() > 0
    }

    override fun queryString(): String {
        return query.distinct().toString()
    }

    override fun filterBuild(
        transform: (QueryBuilder<QTEntity, TEntity, BooleanExpression>) -> BooleanExpression
    ): QueryStream<QTEntity, TEntity> {
        val builder = JpaQueryBuilder<QTEntity, TEntity, BooleanExpression>(
            source,
            query,
            joinNameGenerator
        )
        query = query.where(transform(builder)).select(source)
        return this
    }

    override fun <V> withSource(
        defaultValue: V,
        transform: (source: QTEntity, stream: QueryStream<QTEntity, TEntity>) -> V
    ): V {
        return transform(source, this)
    }

    override fun <V> fetchWithTransform(
        defaultValue: V,
        transform: (QTEntity) -> ResultTransformer<V>
    ): V {
        val resultTransformer: ResultTransformer<V> = transform(source)
        return query.distinct().transform(resultTransformer)
    }

    override fun <K, V> fetchMap(
        key: (QTEntity) -> Expression<K>,
        value: (QTEntity) -> Expression<V>
    ): Map<K, V> {
        return query.distinct().transform(groupBy(key(source)).`as`(value(source)))
    }

    override fun unsafeFilterId(id: Long): QueryStream<QTEntity, TEntity> {
        query = query.where(Expressions.booleanTemplate("${source.metadata.element}.id = $id"))
        return this
    }

    companion object {
        fun <T : Any> generateJoinInstance(
            type: KClass<out T>,
            nameGenerator: JoinNameGenerator
        ): T {
            val generatedName = nameGenerator.generate()
            val constructors = type.constructors
            val constructorMatch = constructors
                .filter { it.parameters.size == 1 }
                .first { it.parameters.single().type.javaType == java.lang.String::class.java }
            return constructorMatch.call(generatedName)
        }
    }
}
