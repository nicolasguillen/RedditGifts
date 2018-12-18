package com.redditgifts.mobile.models

import com.redditgifts.mobile.any
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.DetailedGiftModel
import com.redditgifts.mobile.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*

class GiftViewModelTest {

    @Mock private lateinit var mockApiRepository: ApiRepository
    @Mock private lateinit var mockLocalizedErrorMessages: LocalizedErrorMessages

    private lateinit var testee: GiftViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = GiftViewModel(mockApiRepository, mockLocalizedErrorMessages)
    }

    @Test
    fun test_giftId_then_getDetailedGift(){
        //Act
        testee.inputs.exchangeId("exchange")
        testee.inputs.giftId("1")

        //Assert
        verify(mockApiRepository).getDetailedGift(any(), any())
    }

    @Test
    fun test_giftId_when_didGetDetailedGift_then_emitModel(){
        //Arrange
        val test = testee.outputs.detailedGift().test()
        doReturn(Single.just(DetailedGiftModel(DetailedGiftModel.Data("", Date(), "", 0, "", emptyList(), 0))))
            .whenever(mockApiRepository).getDetailedGift(any(), any())

        //Act
        testee.inputs.exchangeId("exchange")
        testee.inputs.giftId("1")

        //Assert
        test.assertValueCount(1)
    }

}