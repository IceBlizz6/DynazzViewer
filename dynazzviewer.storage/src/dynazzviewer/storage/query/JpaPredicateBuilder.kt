package dynazzviewer.storage.query

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.BooleanPath
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.ListPath
import com.querydsl.jpa.impl.JPAQuery
import kotlin.reflect.KClass

class JpaPredicateBuilder<QTEntity : EntityPathBase<TEntity>, TEntity>(
    private val source: QTEntity,
    private var query: JPAQuery<TEntity>,
    private val nameGenerator: JoinNameGenerator,
) : PredicateBuilder<QTEntity, TEntity> {
    override fun filter(transform: (QTEntity) -> BooleanExpression): PredicateBuilder<QTEntity, TEntity> {
        var expr: BooleanExpression = transform(source)
        if (expr is BooleanPath) {
            expr = expr.isTrue
        }
        query = query.where(expr)
        return this
    }

    override fun <QT : EntityPathBase<T>, T> flatMap(transform: (QTEntity) -> ListPath<T, QT>): PredicateBuilder<QT, T> {
        val joinedSource: ListPath<T, QT> = transform(source)
        val joinedType: KClass<out QT> = joinedSource.any()::class
        val joined = JpaQueryStream.generateJoinInstance(joinedType, nameGenerator)
        val joinedQuery = query.leftJoin(joinedSource, joined).select(joined)
        return JpaPredicateBuilder(joined, joinedQuery, nameGenerator)
    }

    override fun <QT : EntityPathBase<T>, T> map(transform: (QTEntity) -> QT): PredicateBuilder<QT, T> {
        val joinedSource = transform(source)
        val joinedQuery = query.select(joinedSource)
        return JpaPredicateBuilder(joinedSource, joinedQuery, nameGenerator)
    }

    override fun build(transform: (QTEntity) -> BooleanExpression): BooleanExpression {
        return transform(source)
    }

    override fun chainAny(
        transform: (PredicateBuilder<QTEntity, TEntity>) -> List<BooleanExpression>
    ): BooleanExpression {
        var combinedPredicate: BooleanExpression? = null
        for (predicate in transform(this)) {
            if (combinedPredicate == null) {
                combinedPredicate = predicate
            } else {
                combinedPredicate = combinedPredicate.or(predicate)
            }
        }
        return combinedPredicate!!
    }
}
