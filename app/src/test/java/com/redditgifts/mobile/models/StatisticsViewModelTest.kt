package com.redditgifts.mobile.models

import com.redditgifts.mobile.any
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.StatisticsModel
import com.redditgifts.mobile.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class StatisticsViewModelTest {

    @Mock private lateinit var mockApiRepository: ApiRepository
    @Mock private lateinit var mockLocalizedErrorMessages: LocalizedErrorMessages

    private lateinit var testee: StatisticsViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = StatisticsViewModel(mockApiRepository, mockLocalizedErrorMessages)
    }

    @Test
    fun test_exchangeId_then_getStatistics(){
        //Arrange
        doReturn(Single.just(StatisticsModel(StatisticsModel.Data(0, "", 0, 0, 0, 0, 0.0, 0, 0, 0, 0.0 ,0 ,0 ,0,0, 0.0, 0.0, 0.0, 0, 0, 0))))
            .whenever(mockApiRepository).getStatistics(any())

        //Act
        testee.inputs.exchangeId("exchange")

        //Assert
        verify(mockApiRepository).getStatistics(any())
    }

    @Test
    fun test_giftId_when_didStatistics_then_emitModel(){
        //Arrange
        val test = testee.outputs.statistics().test()
        doReturn(Single.just(StatisticsModel(StatisticsModel.Data(0, "", 0, 0, 0, 0, 0.0, 0, 0, 0, 0.0 ,0 ,0 ,0,0, 0.0, 0.0, 0.0, 0, 0, 0))))
            .whenever(mockApiRepository).getStatistics(any())

        //Act
        testee.inputs.exchangeId("exchange")

        //Assert
        test.assertValueCount(1)
    }

}