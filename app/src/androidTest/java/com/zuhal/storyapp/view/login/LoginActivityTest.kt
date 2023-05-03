package com.zuhal.storyapp.view.login

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.zuhal.storyapp.utils.EspressoIdlingResource
import okhttp3.mockwebserver.MockWebServer
import com.zuhal.storyapp.R
import com.zuhal.storyapp.data.remote.retrofit.RetrofitConfig
import com.zuhal.storyapp.utils.JsonConverter
import com.zuhal.storyapp.view.main.MainActivity
import okhttp3.mockwebserver.MockResponse
import org.hamcrest.Matcher

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    private val mockWebServer = MockWebServer()

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        RetrofitConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_Success() {
        onView(withId(R.id.ed_login_email)).perform(ViewActions.clearText(), ViewActions.typeText("zuhal2@gmail.com"))
        onView(withId(R.id.ed_login_password)).perform(ViewActions.clearText(), ViewActions.typeText("abcdefghi"))

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("login_success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.loginButton)).perform(click())

        onView(isRoot()).perform(waitFor(3000))

//        onView(withId(R.id.username)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}

private fun waitFor(delay: Long): ViewAction? {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> = isRoot()
        override fun getDescription(): String = "wait for $delay milliseconds"
        override fun perform(uiController: UiController, v: View?) {
            uiController.loopMainThreadForAtLeast(delay)
        }
    }
}