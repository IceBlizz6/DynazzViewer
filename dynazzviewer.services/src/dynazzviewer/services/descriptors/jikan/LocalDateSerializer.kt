package dynazzviewer.services.descriptors.jikan

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00+00:00'")
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        val output = formatter.format(value)
        encoder.encodeString(output)
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val string = decoder.decodeString()
        return ZonedDateTime.parse(string).toLocalDate()
    }
}
