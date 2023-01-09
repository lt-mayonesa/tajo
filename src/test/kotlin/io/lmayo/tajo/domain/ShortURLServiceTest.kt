package io.lmayo.tajo.domain

import io.lmayo.tajo.support.DateTimeProvider
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.setAndAwait
import org.springframework.data.redis.core.setIfAbsentAndAwait
import java.net.URI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Stack

class ShortURLServiceTest {

    private val shortCodeProvider = mockk<ShortCodeProvider>()
    private val dateTimeProvider = mockk<DateTimeProvider>()
    private val opsForValue = mockk<ReactiveValueOperations<String, String>>()
    private val target = ShortURLService(shortCodeProvider, dateTimeProvider, opsForValue)

    private val dateTimeNow = OffsetDateTime.now()

    @BeforeEach
    fun setUp() {
        // suspending extension functions need to be mocked static
        mockkStatic("org.springframework.data.redis.core.ReactiveValueOperationsExtensionsKt")
        every { dateTimeProvider.now() } returns dateTimeNow
    }

    @Test
    fun `given a valid URI generate a new code`() {
        val url = URI.create("https://google.com")
        val code = Code("aSd123")
        val generatedUrl = URI.create("http://localhost:8080/$code")
        val createdAt = dateTimeNow.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        every { shortCodeProvider.random() } returns code
        coEvery { opsForValue.setIfAbsentAndAwait(code.token, url.toString()) } returns true
        coEvery { opsForValue.setAndAwait("${code.token}:created_at", createdAt) } returns true

        val result = runBlocking { target.generate(url) }

        assertEquals(generatedUrl, result)
        verify(exactly = 1) { shortCodeProvider.random() }
        coVerify(exactly = 1) { opsForValue.setAndAwait("${code.token}:created_at", createdAt) }
    }

    @Test
    fun `given a valid URI and a code store the provided one`() {
        val url = URI.create("https://google.com")
        val code = Code("aSd123")
        val generatedUrl = URI.create("http://localhost:8080/$code")
        val createdAt = dateTimeNow.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        coEvery { opsForValue.setIfAbsentAndAwait(code.token, url.toString()) } returns true
        coEvery { opsForValue.setAndAwait("${code.token}:created_at", createdAt) } returns true


        val result = runBlocking { target.generate(url, shortCode = code) }

        assertEquals(generatedUrl, result)
        verify(exactly = 0) { shortCodeProvider.random() }
        coVerify(exactly = 1) { opsForValue.setAndAwait("${code.token}:created_at", createdAt) }
    }

    @Test
    fun `given a valid URI and a code that already exists dont store it`() {
        val url = URI.create("https://google.com")
        val code = Code("aSd123")

        coEvery { opsForValue.setIfAbsentAndAwait(code.token, url.toString()) } returns false

        val result = runBlocking { target.generate(url, shortCode = code) }

        assertEquals(null, result)
        verify(exactly = 0) { shortCodeProvider.random() }
    }

    @Test
    fun `given the generated code exists then regenerate it`() {
        val createdAt = dateTimeNow.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val randoms = Stack<Code>().apply {
            addAll(listOf(Code("aSd125"), Code("aSd124"), Code("aSd123")))
        }
        val url = URI.create("https://google.com")

        every { shortCodeProvider.random() } answers {
            randoms.pop()
        }
        coEvery { opsForValue.setIfAbsentAndAwait("asd123", url.toString()) } returns false
        coEvery { opsForValue.setIfAbsentAndAwait("asd124", url.toString()) } returns false
        coEvery { opsForValue.setIfAbsentAndAwait("asd125", url.toString()) } returns true
        coEvery { opsForValue.setAndAwait("asd125:created_at", createdAt) } returns true

        val result = runBlocking { target.generate(url) }

        assertEquals(URI.create("http://localhost:8080/aSd125"), result)
        verify(exactly = 3) { shortCodeProvider.random() }
        coVerify(exactly = 1) { opsForValue.setAndAwait("asd125:created_at", createdAt) }
    }

}