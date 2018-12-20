package com.redditgifts.mobile.models

import com.redditgifts.mobile.any
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.ExchangeStatusModel
import com.redditgifts.mobile.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*

class ExchangeStatusViewModelTest {

    @Mock private lateinit var mockApiRepository: ApiRepository
    @Mock private lateinit var mockLocalizedErrorMessages: LocalizedErrorMessages

    private lateinit var testee: ExchangeStatusViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = ExchangeStatusViewModel(mockApiRepository, mockLocalizedErrorMessages)
    }

    @Test
    fun test_exchangeId_then_getExchangeStatus(){
        //Arrange
        doReturn(Single.just(ExchangeStatusModel(ExchangeStatusModel.Data(
            ExchangeStatusModel.Data.Info(0, false, emptyList(), emptyList()),
            ExchangeStatusModel.Data.Info(0, false, emptyList(), emptyList()),
            Date()
        )))).whenever(mockApiRepository).getExchangeStatus(any())

        //Act
        testee.inputs.exchangeId("exchange")

        //Assert
        verify(mockApiRepository).getExchangeStatus(any())
    }

    @Test
    fun test_giftId_when_didStatistics_then_emitModel(){
        //Arrange
        val test = testee.outputs.exchangeStatus().test()
        doReturn(Single.just(ExchangeStatusModel(ExchangeStatusModel.Data(
            ExchangeStatusModel.Data.Info(0, false, emptyList(), emptyList()),
            ExchangeStatusModel.Data.Info(0, false, emptyList(), emptyList()),
            Date()
        )))).whenever(mockApiRepository).getExchangeStatus(any())

        //Act
        testee.inputs.exchangeId("exchange")

        //Assert
        test.assertValueCount(1)
    }

}