package dynazzviewer.ui.web

import graphql.schema.Coercing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateScalar : Coercing<LocalDate, String> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun serialize(value: Any): String {
        if (value is LocalDate) {
            return value.format(formatter)
        } else {
            error("Not supported for $value")
        }
    }

    override fun parseValue(value: Any): LocalDate {
        if (value is String) {
            return LocalDate.parse(value, formatter)
        } else {
            error("Not supported for $value")
        }
    }

    override fun parseLiteral(value: Any): LocalDate {
        if (value is String) {
            return LocalDate.parse(value, formatter)
        } else {
            error("Not supported for $value")
        }
    }
}
