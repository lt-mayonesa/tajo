package io.lmayo.tajo.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.random.Random

@Configuration
class RandomConfiguration {

    @Bean
    fun random(): Random = Random

}