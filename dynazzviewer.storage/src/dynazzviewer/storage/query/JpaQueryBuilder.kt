package dynazzviewer.storage.query

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.BooleanPath
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.ListPath
import com.querydsl.jpa.impl.JPAQuery
import kotlin.reflect.KClass

class JpaQueryBuilder<QTEntity : EntityPathBase<TEntity>, TEntity, TResult : Any>(
    private val source: QTEntity,
    private var query: JPAQuery<TEntity>,
    private val nameGenerator: JoinNameGenerator
) : QueryBuilder<QTEntity, TEntity, TResult> {
    override fun filter(
        transform: (QTEntity) -> BooleanExpression
    ): QueryBuilder<QTEntity, TEntity, TResult> {
        var expr: BooleanExpression = transform(source)
        if (expr is BooleanPath) {
            expr = expr.isTrue
        }
        query = query.where(expr)
        return this
    }

    override fun <QT : EntityPathBase<T>, T> flatMap(
        transform: (QTEntity) -> ListPath<T, QT>
    ): QueryBuilder<QT, T, TResult> {
        val joinedSource: ListPath<T, QT> = transform(source)
        val joinedType: KClass<out QT> = joinedSource.any()::class
        val joined = JpaQueryStream.generateJoinInstance(joinedType, nameGenerator)
        val joinedQuery = query.leftJoin(joinedSource, joined).select(joined)
        return JpaQueryBuilder(joined, joinedQuery, nameGenerator)
    }

    override fun <QT : EntityPathBase<T>, T> map(
        transform: (QTEntity) -> QT
    ): QueryBuilder<QT, T, TResult> {
        val joinedSource = transform(source)
        val joinedQuery = query.select(joinedSource)
        return JpaQueryBuilder(joinedSource, joinedQuery, nameGenerator)
    }

    override fun build(transform: (QTEntity) -> TResult): TResult {
        return transform(source)
    }
}
