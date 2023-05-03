package com.zuhal.storyapp.view.login

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.zuhal.storyapp.R
import com.zuhal.storyapp.data.remote.retrofit.RetrofitConfig
import com.zuhal.storyapp.utils.EspressoIdlingResource
import com.zuhal.storyapp.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    private val mockWebServer = MockWebServer()
    private val targetContext = ApplicationProvider.getApplicationContext<Context>()
    private val resources: Resources = targetContext.resources

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

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

        onView(withText(resources.getString(R.string.next))).perform(click())

        onView(withId(R.id.username)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_logout)).perform(click())

        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}