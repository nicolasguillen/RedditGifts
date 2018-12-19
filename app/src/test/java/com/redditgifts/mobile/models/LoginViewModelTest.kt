package com.redditgifts.mobile.models

import com.redditgifts.mobile.any
import com.redditgifts.mobile.argumentCaptor
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.storage.CookieRepository
import com.redditgifts.mobile.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class LoginViewModelTest {

    @Mock private lateinit var mockApiRepository: ApiRepository
    @Mock private lateinit var mockCookieRepository: CookieRepository
    @Mock private lateinit var mockLocalizedErrorMessages: LocalizedErrorMessages

    private lateinit var testee: LoginViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = LoginViewModel(mockApiRepository, mockCookieRepository, mockLocalizedErrorMessages)
    }

    @Test
    fun test_didPressLogin_then_login(){
        //Arrange
        doReturn(Single.just(mapOf("sessionid" to "b1nc2re2b7t2epczv145vn0c0kj97p2d", "csrftoken" to "qDeOAO3JavGiqbZzfs3oGACmna0xukHg")))
            .whenever(mockApiRepository).login(any(), any(), any())

        //Act
        testee.inputs.email("email")
        testee.inputs.password("password")
        testee.inputs.cookie("csrftoken=ZiVSLTKmxsKMqrpDqXd9AiOvXFhRgzGQ; ause=30076f7cc275ae786c47d700fbb5b8ad; sessionid=w03n69wkd2ksiieu7p37z4237431hbnx; __utma=81911445.1054149289.1545208111.1545208111.1545208111.1; __utmc=81911445; __utmz=81911445.1545208111.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __adroll_fpc=ec400324776059b6f41d35fe99fca9f4; __ar_v4=%7CXNYNP5KHKJCBXOHQ4TW3VD%3A20190018%3A1%7CFFSJP3C7YNCPNDYECLA5HK%3A20190018%3A1%7CDHPD7LCWMVGA7O4O6FDS3V%3A20190018%3A1; _fbp=fb.1.1545208114539.1624750328")
        testee.inputs.didPressLogin()

        //Assert
        verify(mockApiRepository).login(any(), any(), any())
    }

    @Test
    fun test_didPressLogin_when_didGetSessionId_then_storeCookieWithSessionId(){
        //Arrange
        val cookie = "csrftoken=ZiVSLTKmxsKMqrpDqXd9AiOvXFhRgzGQ; ause=30076f7cc275ae786c47d700fbb5b8ad; sessionid=w03n69wkd2ksiieu7p37z4237431hbnx; __utma=81911445.1054149289.1545208111.1545208111.1545208111.1; __utmc=81911445; __utmz=81911445.1545208111.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __adroll_fpc=ec400324776059b6f41d35fe99fca9f4; __ar_v4=%7CXNYNP5KHKJCBXOHQ4TW3VD%3A20190018%3A1%7CFFSJP3C7YNCPNDYECLA5HK%3A20190018%3A1%7CDHPD7LCWMVGA7O4O6FDS3V%3A20190018%3A1; _fbp=fb.1.1545208114539.1624750328"
        val expectedCookie = "sessionid=b1nc2re2b7t2epczv145vn0c0kj97p2d; csrftoken=qDeOAO3JavGiqbZzfs3oGACmna0xukHg"
        doReturn(Single.just(mapOf("sessionid" to "b1nc2re2b7t2epczv145vn0c0kj97p2d", "csrftoken" to "qDeOAO3JavGiqbZzfs3oGACmna0xukHg")))
            .whenever(mockApiRepository).login(any(), any(), any())
        doReturn(Single.just(Unit))
            .whenever(mockCookieRepository).storeCookie(any())

        //Act
        testee.inputs.email("email")
        testee.inputs.password("password")
        testee.inputs.cookie(cookie)
        testee.inputs.didPressLogin()

        //Assert
        argumentCaptor<String>().apply {
            verify(mockCookieRepository).storeCookie(capture())
            assertEquals(firstValue, expectedCookie)
        }
    }

    @Test
    fun test_didPressLogin_when_didStoreCookie_then_emitFinish(){
        //Arrange
        val test = testee.outputs.finish().test()
        doReturn(Single.just(mapOf("sessionid" to "b1nc2re2b7t2epczv145vn0c0kj97p2d", "csrftoken" to "qDeOAO3JavGiqbZzfs3oGACmna0xukHg")))
            .whenever(mockApiRepository).login(any(), any(), any())
        doReturn(Single.just(Unit))
            .whenever(mockCookieRepository).storeCookie(any())

        //Act
        testee.inputs.email("email")
        testee.inputs.password("password")
        testee.inputs.cookie("csrftoken=ZiVSLTKmxsKMqrpDqXd9AiOvXFhRgzGQ; ause=30076f7cc275ae786c47d700fbb5b8ad; sessionid=w03n69wkd2ksiieu7p37z4237431hbnx; __utma=81911445.1054149289.1545208111.1545208111.1545208111.1; __utmc=81911445; __utmz=81911445.1545208111.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __adroll_fpc=ec400324776059b6f41d35fe99fca9f4; __ar_v4=%7CXNYNP5KHKJCBXOHQ4TW3VD%3A20190018%3A1%7CFFSJP3C7YNCPNDYECLA5HK%3A20190018%3A1%7CDHPD7LCWMVGA7O4O6FDS3V%3A20190018%3A1; _fbp=fb.1.1545208114539.1624750328")
        testee.inputs.didPressLogin()

        //Assert
        test.assertValueCount(1)
    }

}