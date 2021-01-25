package com.grinvald.grinvaldmadventure

import android.content.Context
import junit.framework.Assert.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ValidationSignInTest {

    @Mock
    lateinit var mockContext : Context

    @Test
    fun validateFieldsTest() {

        val signInActivity = SignIn(mockContext)
        val test1 = signInActivity.validate("", "")
        val test2 = signInActivity.validate("sadfasdf", "")
        val test3 = signInActivity.validate("email@mail.com", "")
        val test4 = signInActivity.validate(null, null)
        val test5 = signInActivity.validate("email@mail.com", "123")

        assertEquals(test1, false)
        assertEquals(test2, false)
        assertEquals(test3, false)
        assertEquals(test4, false)
        assertEquals(test5, true)

    }

}