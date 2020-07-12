package dynazzviewer.storage.sqlite

import com.mysema.query.Tuple
import com.mysema.query.jpa.impl.JPAQuery
import com.mysema.query.types.Expression
import com.mysema.query.types.OrderSpecifier
import com.mysema.query.types.Path
import com.mysema.query.types.expr.BooleanExpression
import com.mysema.query.types.expr.NumberExpression
import com.mysema.query.types.path.BooleanPath
import com.mysema.query.types.path.EntityPathBase
import com.mysema.query.types.path.ListPath
import com.mysema.query.types.path.NumberPath
import javax.persistence.EntityManager

class JpaQueryStream<QTEntity : EntityPathBase<TEntity>, TEntity> : QueryStream<QTEntity, TEntity> {
    private val source: QTEntity
    private var query: JPAQuery

    constructor(source: QTEntity, entityManager: EntityManager) {
        this.source = source
        this.query = JPAQuery(entityManager)
        this.query = query.from(source)
    }

    constructor(source: QTEntity, query: JPAQuery) {
        this.source = source
        this.query = query
    }

    override fun orderBy(
        transform: (QTEntity) -> OrderSpecifier<*>
    ): QueryStream<QTEntity, TEntity> {
        query = query.orderBy(transform(source))
        return this
    }

    /**
     * Note: Adding isTrue on BooleanPath to avoid AST node path error
     *          Should investigate further and remove if possible
     */
    override fun filter(lmbd: (QTEntity) -> BooleanExpression): QueryStream<QTEntity, TEntity> {
        var expr: BooleanExpression = lmbd(source)
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

    override fun <QT : EntityPathBase<T>, T> map(transform: (QTEntity) -> QT): QueryStream<QT, T> {
        val joinedSource = transform(source)
        val joinedQuery = query.innerJoin(joinedSource)
        return JpaQueryStream(joinedSource, joinedQuery)
    }

    override fun <QT : EntityPathBase<T>, T> flatMap(
        joined: QT,
        transform: (QTEntity) -> ListPath<T, QT>
    ): QueryStream<QT, T> {
        val joinedSource: ListPath<T, QT> = transform(source)
        val joinedQuery = query.innerJoin(joinedSource, joined)
        return JpaQueryStream(joined, joinedQuery)
    }

    override fun fetchList(): List<TEntity> {
        return query.list(source)
    }

    override fun <T> fetchList(expr: (QTEntity) -> Path<T>): List<T> {
        val fetchSource = expr(source)
        return query.list(fetchSource)
    }

    override fun fetchListFields(transform: (QTEntity) -> List<Expression<*>>): List<Tuple> {
        val expressions = transform(source)
        return query.list(*expressions.toTypedArray())
    }

    override fun fetchSingle(): TEntity {
        val result: TEntity? = query.singleResult(source)
        if (result == null) {
            throw RuntimeException("No result from query: $query")
        } else {
            return result
        }
    }

    override fun <T> fetchSingleOrNull(expr: (QTEntity) -> Path<T>): T? {
        return query.singleResult(expr(source))
    }

    override fun fetchSingleOrNull(): TEntity? {
        return query.singleResult(source)
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
        return query.singleResult(sumExpr)
    }

    override fun <T> fetchSumExpression(
        expr: (QTEntity) -> NumberExpression<T>
    ): T? where T : Number, T : Comparable<*> {
        val sumExpr = expr(source).sum()
        return query.singleResult(sumExpr)
    }

    override fun fetchCount(): Long {
        return query.count()
    }

    override fun queryString(): String {
        return query.toString()
    }
}
