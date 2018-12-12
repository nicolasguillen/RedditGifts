package com.redditgifts.mobile.models

import com.redditgifts.mobile.any
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.AccountModel
import com.redditgifts.mobile.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class AccountViewModelTest {

    @Mock private lateinit var mockHTMLParser: HTMLParser
    @Mock private lateinit var mockLocalizedErrorMessages: LocalizedErrorMessages

    private lateinit var testee: AccountViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = AccountViewModel(mockHTMLParser, mockLocalizedErrorMessages)
    }

    @Test
    fun test_onCreate_then_loadHTML(){
        //Arrange
        val test = testee.outputs.loadHTML().test()

        //Act
        testee.inputs.onCreate()

        //Assert
        test.assertValueCount(1)
    }

    @Test
    fun test_didLoadHtml_then_parseAccount(){
        //Arrange
        doReturn(Single.just(AccountModel("", "", "")))
            .whenever(mockHTMLParser).parseAccount(any())

        //Act
        testee.inputs.onCreate()
        testee.inputs.didLoadHtml("<html/>")

        //Assert
        verify(mockHTMLParser).parseAccount(any())
    }

    @Test
    fun test_didLoadHtml_when_didParseAccount_then_emitModel(){
        //Arrange
        val test = testee.outputs.accountModel().test()
        doReturn(Single.just(AccountModel("", "", "")))
            .whenever(mockHTMLParser).parseAccount(any())

        //Act
        testee.inputs.onCreate()
        testee.inputs.didLoadHtml("<html/>")

        //Assert
        test.assertValueCount(1)
    }
}