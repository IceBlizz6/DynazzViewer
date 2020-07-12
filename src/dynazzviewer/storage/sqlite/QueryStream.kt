package dynazzviewer.storage.sqlite

import com.mysema.query.Tuple
import com.mysema.query.types.Expression
import com.mysema.query.types.OrderSpecifier
import com.mysema.query.types.Path
import com.mysema.query.types.expr.BooleanExpression
import com.mysema.query.types.expr.NumberExpression
import com.mysema.query.types.path.EntityPathBase
import com.mysema.query.types.path.ListPath
import com.mysema.query.types.path.NumberPath

interface QueryStream<QTEntity, TEntity> {
    /**
     * Filter result set on predicate
     */
    fun filter(transform: (QTEntity) -> BooleanExpression): QueryStream<QTEntity, TEntity>

    /**
     * Maps result rows one to many
     */
    fun <QT : EntityPathBase<T>, T> flatMap(
        joined: QT,
        transform: (QTEntity) -> ListPath<T, QT>
    ): QueryStream<QT, T>

    /**
     * Maps result rows one to one
     */
    fun <QT : EntityPathBase<T>, T> map(transform: (QTEntity) -> QT): QueryStream<QT, T>

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

    fun fetchListFields(transform: (QTEntity) -> List<Expression<*>>): List<Tuple>

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
    fun <T> fetchSumExpression(
        expr: (QTEntity) -> NumberExpression<T>
    ): T? where T : Number, T : Comparable<*>

    /**
     * Runs query to retrieve number of rows
     */
    fun fetchCount(): Long

    /**
     * Returns current built query from methods calls
     */
    fun queryString(): String
}
