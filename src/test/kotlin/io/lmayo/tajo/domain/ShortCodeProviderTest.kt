package io.lmayo.tajo.domain

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.text.MatchesPattern.matchesPattern
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.random.Random


class ShortCodeProviderTest {

    @TestFactory
    fun `generate random token based on seed`() = listOf(
        1 to "HQMuRz",
        15 to "nS1sUw",
        345 to "80Pf4e",
        234 to "9P71df",
    ).map { (seed, expected) ->
        dynamicTest("random string with seed $seed should be $expected") {
            val target = ShortCodeProvider(Random(seed))
            assertEquals(Code(expected), target.random())
        }
    }

    @Test
    fun `generate random token containing only letters and numbers`() {
        val target = ShortCodeProvider(Random(1))

        assertThat(target.random().token, matchesPattern("^\\w+\$"))
        assertThat(target.random().token, matchesPattern("^\\w+\$"))
        assertThat(target.random().token, matchesPattern("^\\w+\$"))
        assertThat(target.random().token, matchesPattern("^\\w+\$"))
        assertThat(target.random().token, matchesPattern("^\\w+\$"))
    }


}