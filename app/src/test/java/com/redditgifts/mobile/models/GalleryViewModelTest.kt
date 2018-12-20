package com.redditgifts.mobile.models

import com.redditgifts.mobile.any
import com.redditgifts.mobile.argumentCaptor
import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.GalleryModel
import com.redditgifts.mobile.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class GalleryViewModelTest {

    @Mock private lateinit var mockApiRepository: ApiRepository
    @Mock private lateinit var mockLocalizedErrorMessages: LocalizedErrorMessages

    private lateinit var testee: GalleryViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = GalleryViewModel(mockApiRepository, mockLocalizedErrorMessages)
    }

    @Test
    fun test_onCreate_then_loadPage1(){
        //Arrange
        doReturn(Single.just(GalleryModel(GalleryModel.Data(emptyList()))))
            .whenever(mockApiRepository).getGallery(any(), any(), any())

        //Act
        testee.inputs.exchangeId("exchange")
        testee.inputs.onCreate()

        //Assert
        argumentCaptor<Int> {
            verify(mockApiRepository).getGallery(any(), any(), capture())
            assertEquals(1, firstValue)
        }
    }

    @Test
    fun test_loadPage_when_isPage2_then_loadPage2(){
        //Arrange
        doReturn(Single.just(GalleryModel(GalleryModel.Data(emptyList()))))
            .whenever(mockApiRepository).getGallery(any(), any(), any())

        //Act
        testee.inputs.exchangeId("exchange")
        testee.inputs.onCreate()
        testee.inputs.loadPage(2)

        //Assert
        argumentCaptor<Int> {
            verify(mockApiRepository).getGallery(any(), any(), capture())
            assertEquals(1, firstValue)
            assertEquals(2, secondValue)
        }
    }

    @Test
    fun test_loadPage_when_didLoadPage2_then_emitModelForSecondTime(){
        //Arrange
        val test = testee.outputs.galleryPageData().test()
        doReturn(Single.just(GalleryModel(GalleryModel.Data(emptyList()))))
            .whenever(mockApiRepository).getGallery(any(), any(), any())

        //Act
        testee.inputs.exchangeId("exchange")
        testee.inputs.onCreate()
        testee.inputs.loadPage(2)

        //Assert
        test.assertValueCount(2)
    }

}