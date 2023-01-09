package io.lmayo.tajo.api

import com.ninjasquad.springmockk.MockkBean
import io.lmayo.tajo.domain.ShortURLGeneratorService
import io.mockk.every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.net.URI

@WebFluxTest(controllers = [URLAccessController::class])
class URLAccessControllerTest(
    @Autowired private val webTestClient: WebTestClient
) {

    @MockkBean
    private lateinit var shortURLGeneratorService: ShortURLGeneratorService

    @Test
    fun `short url for code exists`() {
        every { shortURLGeneratorService.find("sarasa") } returns
                URI.create("https://google.com")

        webTestClient.get()
            .uri("/sarasa")
            .exchange()
            .expectStatus().isFound
            .expectHeader().location("https://google.com")
    }

    @Test
    fun `short url for code does not exist`() {
        every { shortURLGeneratorService.find("sarasa") } returns null

        webTestClient.get()
            .uri("/sarasa")
            .exchange()
            .expectStatus().isNotFound
    }
}