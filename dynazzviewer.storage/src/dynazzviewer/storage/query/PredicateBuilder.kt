package dynazzviewer.storage.query

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.ListPath

interface PredicateBuilder<QTEntity : EntityPathBase<TEntity>, TEntity> {
    fun filter(transform: (QTEntity) -> BooleanExpression): PredicateBuilder<QTEntity, TEntity>

    fun <QT : EntityPathBase<T>, T> flatMap(transform: (QTEntity) -> ListPath<T, QT>): PredicateBuilder<QT, T>

    fun <QT : EntityPathBase<T>, T> map(transform: (QTEntity) -> QT): PredicateBuilder<QT, T>

    fun chainAny(transform: (PredicateBuilder<QTEntity, TEntity>) -> List<BooleanExpression>): BooleanExpression

    fun build(transform: (QTEntity) -> BooleanExpression): BooleanExpression
}
