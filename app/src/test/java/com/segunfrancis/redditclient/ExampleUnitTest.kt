package com.segunfrancis.redditclient

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun truth_assertion_test() {
        val string = "awesome"
        Truth.assertThat(string).startsWith("awe")
        assertWithMessage("Without me, it's just aweso")
            .that(string)
            .contains("me")
    }
}
