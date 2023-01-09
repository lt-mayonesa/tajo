package io.lmayo.tajo.api

import com.ninjasquad.springmockk.MockkBean
import io.lmayo.tajo.domain.Code
import io.lmayo.tajo.domain.ShortURLService
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.JsonPathAssertions
import org.springframework.test.web.reactive.server.StatusAssertions
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.body
import java.net.URI

@WebFluxTest(controllers = [URLSubmitController::class])
class URLSubmitControllerTest(
    @Autowired private val webClient: WebTestClient
) {

    @MockkBean
    private lateinit var shortURLService: ShortURLService

    @Test
    fun `submit payload without shortcode`() {
        val targetUrl = "https://google.com"
        coEvery { shortURLService.generate(URI.create(targetUrl)) } returns
                URI.create("https://locahost:8080/short")


        """
        {
            "url": "$targetUrl"
        }
        """ postAtSubmitAndExpect {
            jsonPath("$.url") equals targetUrl
            jsonPath("$.shortUrl") equals "https://locahost:8080/short"
        }
    }

    @Test
    fun `submit payload with shortcode`() {
        val targetUrl = "https://google.com"
        val shortCode = "shortcode"
        coEvery { shortURLService.generate(URI.create(targetUrl), Code(shortCode)) } returns
                URI.create("https://locahost:8080/$shortCode")


        """
            {
                "url": "$targetUrl",
                "shortCode": "$shortCode"
            }
        """ postAtSubmitAndExpect {
            jsonPath("$.url") equals targetUrl
            jsonPath("$.shortUrl") equals "https://locahost:8080/$shortCode"
        }
    }

    @TestFactory
    fun `submit payload with shortcode must have at least 4 characters`() = listOf(
        "",
        "1",
        "14",
        "1as",
    ).map { shortCode ->
        dynamicTest("shortcode is $shortCode") {
            """
            {
                "url": "https://google.com",
                "shortCode": "$shortCode"
            }
            """ postAtSubmit {
                expectStatus { isBadRequest }
                expectBody {
                    jsonPath("$.errors[0].message") equals "property shortCode must be at least 4 characters long"
                }
            }
        }
    }

    @TestFactory
    fun `submit payload with shortcode must only contain numbers and letters`() = listOf(
        "sh?rtcode",
        "#098234!",
        "sh?  rtcode",
        "sh?__-rtcode",
    ).map { shortCode ->
        dynamicTest("shortcode is $shortCode") {
            """
            {
                "url": "https://google.com",
                "shortCode": "$shortCode"
            }
            """ postAtSubmit {
                expectStatus { isBadRequest }
                expectBody {
                    jsonPath("$.errors[0].message") equals "property shortCode can only contain numbers and letters"
                }
            }
        }
    }

    private infix fun String.postAtSubmitAndExpect(
        jsonExpect: WebTestClient.BodyContentSpec.() -> WebTestClient.BodyContentSpec
    ): WebTestClient.BodyContentSpec = postAtSubmit {
        expectStatus().isOk
            .expectBody()
            .apply {
                jsonExpect()
            }
    }

    private infix fun WebTestClient.ResponseSpec.expectStatus(block: StatusAssertions.() -> WebTestClient.ResponseSpec) =
        expectStatus().apply { block() }

    private infix fun WebTestClient.ResponseSpec.expectBody(jsonExpect: WebTestClient.BodyContentSpec.() -> WebTestClient.BodyContentSpec) =
        expectBody().apply { jsonExpect() }

    private infix fun <T> String.postAtSubmit(block: WebTestClient.ResponseSpec.() -> T) =
        webClient.post()
            .uri("/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .body(flowOf(this.trimIndent()))
            .exchange()
            .run { block() }

    private infix fun JsonPathAssertions.equals(expectedValue: Any) =
        this.isEqualTo(expectedValue)
}