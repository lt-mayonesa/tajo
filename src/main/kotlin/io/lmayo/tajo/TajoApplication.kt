package io.lmayo.tajo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TajoApplication

fun main(args: Array<String>) {
    runApplication<TajoApplication>(*args)
}
