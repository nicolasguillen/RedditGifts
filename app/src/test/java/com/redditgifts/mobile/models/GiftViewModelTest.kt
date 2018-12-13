package com.redditgifts.mobile.models

import com.redditgifts.mobile.any
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.services.HTMLParser
import com.redditgifts.mobile.services.models.DetailedGiftModel
import com.redditgifts.mobile.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class GiftViewModelTest {

    @Mock private lateinit var mockHTMLParser: HTMLParser
    @Mock private lateinit var mockLocalizedErrorMessages: LocalizedErrorMessages

    private lateinit var testee: GiftViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = GiftViewModel(mockHTMLParser, mockLocalizedErrorMessages)
    }

    @Test
    fun test_onCreate_then_loadHTML(){
        //Arrange
        val test = testee.outputs.loadHTML().test()

        //Act
        testee.inputs.giftId("1")

        //Assert
        test.assertValueCount(1)
    }

    @Test
    fun test_didLoadHtml_then_parseGift(){
        //Arrange
        doReturn(Single.just(DetailedGiftModel("", "", "", "", emptyList(), emptyList())))
            .whenever(mockHTMLParser).parseGift(any())

        //Act
        testee.inputs.giftId("1")
        testee.inputs.didLoadHtml("<html/>")

        //Assert
        verify(mockHTMLParser).parseGift(any())
    }

    @Test
    fun test_didLoadHtml_when_didParseGift_then_emitModel(){
        //Arrange
        val test = testee.outputs.detailedGift().test()
        doReturn(Single.just(DetailedGiftModel("", "", "", "", emptyList(), emptyList())))
            .whenever(mockHTMLParser).parseGift(any())

        //Act
        testee.inputs.giftId("1")
        testee.inputs.didLoadHtml("<html/>")

        //Assert
        test.assertValueCount(1)
    }
}