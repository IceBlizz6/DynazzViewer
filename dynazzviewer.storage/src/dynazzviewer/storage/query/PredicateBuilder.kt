package dynazzviewer.storage.query

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.ListPath

interface QueryBuilder<QTEntity : EntityPathBase<TEntity>, TEntity, TResult : Any> {
    fun filter(transform: (QTEntity) -> BooleanExpression): QueryBuilder<QTEntity, TEntity, TResult>

    fun <QT : EntityPathBase<T>, T> flatMap(
        transform: (QTEntity) -> ListPath<T, QT>
    ): QueryBuilder<QT, T, TResult>

    fun <QT : EntityPathBase<T>, T> map(
        transform: (QTEntity) -> QT
    ): QueryBuilder<QT, T, TResult>

    fun build(transform: (QTEntity) -> TResult): TResult
}
