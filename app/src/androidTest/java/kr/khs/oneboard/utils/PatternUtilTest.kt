package kr.khs.oneboard.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class PatternUtilTest {
    @Test
    fun checkEmailPatternTest_Correct() {
        val result = PatternUtil.checkEmailPattern("ks96ks@naver.com")

        assertEquals(true, result)
    }

    @Test
    fun checkEmailPatternTest_Incorrect_no_at_sign() {
        val result = PatternUtil.checkEmailPattern("ks96ksnaver.com")

        assertEquals(false, result)
    }

    @Test
    fun checkEmailPatternTest_Incorrect_no_dot() {
        val result = PatternUtil.checkEmailPattern("ks96ks@navercom")

        assertEquals(false, result)
    }
}