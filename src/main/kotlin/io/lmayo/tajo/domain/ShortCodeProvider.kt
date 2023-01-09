package io.lmayo.tajo.domain

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ShortCodeProvider(
    private val seed: Random
) {
    fun random(): Code {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..6)
            .map { allowedChars.random(seed) }
            .joinToString("")
            .let { Code(it) }
    }
}
