package com.redditgifts.mobile.models

import com.redditgifts.mobile.libs.LocalizedErrorMessages
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.models.ProfileModel
import com.redditgifts.mobile.storage.CookieRepository
import com.redditgifts.mobile.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class AccountViewModelTest {

    @Mock private lateinit var mockApiRepository: ApiRepository
    @Mock private lateinit var mockCookieRepository: CookieRepository
    @Mock private lateinit var mockLocalizedErrorMessages: LocalizedErrorMessages

    private lateinit var testee: AccountViewModel

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = AccountViewModel(mockApiRepository, mockCookieRepository, mockLocalizedErrorMessages)
    }

    @Test
    fun test_onCreate_then_parseAccount(){
        //Arrange
        doReturn(Single.just(ProfileModel(ProfileModel.Data("", "", "", "", ""))))
            .whenever(mockApiRepository).getProfile()

        //Act
        testee.inputs.onCreate()

        //Assert
        verify(mockApiRepository).getProfile()
    }

    @Test
    fun test_onCreate_when_didGetProfile_then_emitModel(){
        //Arrange
        val test = testee.outputs.profileModel().test()
        doReturn(Single.just(ProfileModel(ProfileModel.Data("", "", "", "", ""))))
            .whenever(mockApiRepository).getProfile()

        //Act
        testee.inputs.onCreate()

        //Assert
        test.assertValueCount(1)
    }
}