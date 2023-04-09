package com.example.famtime.ui.activity

import android.os.Looper
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest{

    private lateinit var registerActivity: RegisterActivity

    @Before
    fun setUp() {
       registerActivity = activityRule.activity
    }

   @get:Rule
   val activityRule = ActivityTestRule(RegisterActivity::class.java)

    @Test
    @UiThreadTest
    fun empty_username_return_false(){
        val result =  registerActivity.checkValid("asd@gmail.com",
            "asdasd",
            "asdasd",
            "")
        assertThat(result).isFalse()
    }
    @Test
    @UiThreadTest
    fun empty_email_return_false(){
        val result =  registerActivity.checkValid("",
            "password",
            "password",
            "hello")
        assertThat(result).isFalse()
    }
    @Test
    @UiThreadTest
    fun empty_password_return_false(){
        val result =  registerActivity.checkValid("tan@gmail.com",
            "",
            "password",
            "hello")
        assertThat(result).isFalse()
    }
    @Test
    @UiThreadTest
    fun empty_confirmpassword_return_false(){
        val result =  registerActivity.checkValid("tan@gmail.com",
            "password",
            "",
            "hello")
        assertThat(result).isFalse()
    }
    @Test
    @UiThreadTest
    fun password_diff_confirmpassword_return_false(){
        val result =  registerActivity.checkValid("tan@gmail.com",
            "password",
            "password123",
            "hello")
        assertThat(result).isFalse()
    }

    @Test
    @UiThreadTest
    fun password_length_less_than_6_return_false(){
        val result =  registerActivity.checkValid("tan@gmail.com",
            "passw",
            "password",
            "hello")
        assertThat(result).isFalse()
    }
    @After
    fun tearDown() {
        Looper.myLooper()?.quit()
    }
}