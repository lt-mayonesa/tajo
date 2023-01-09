package io.lmayo.tajo.support

import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class DateTimeProvider {

    fun now(): OffsetDateTime = OffsetDateTime.now()

}