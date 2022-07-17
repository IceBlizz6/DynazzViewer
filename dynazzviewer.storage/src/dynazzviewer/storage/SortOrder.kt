package dynazzviewer.storage

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.ComparableExpressionBase

enum class SortOrder(
    val func: (ComparableExpressionBase<*>) -> OrderSpecifier<*>
) {
    ASCENDING({ it.asc() }),
    DESCENDING({ it.desc() })
}
