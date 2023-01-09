package io.lmayo.tajo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.lmayo.tajo.api", "io.lmayo.tajo.domain"])
class TajoApplication

fun main(args: Array<String>) {
    runApplication<TajoApplication>(*args)
}
