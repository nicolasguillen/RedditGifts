package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.AccountModel
import com.redditgifts.mobile.services.models.DetailedGiftModel
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class JsoupHTMLParserTest {

    private lateinit var testee: HTMLParser

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        testee = JsoupHTMLParser()
    }

    @Test
    fun test_parseAccount_when_wrongHtml_then_throwLoadError(){
        //Arrange
        @Language("HTML")
        val html = """
            <body>
            </body>
        """

        //Act
        val test = testee.parseAccount(html).test()

        //Assert
        test.assertError(HTMLError.LoadHTMLError)
    }

    @Test
    fun test_parseAccount_when_noUserName_then_throwNeedsLogin(){
        //Arrange
        @Language("HTML")
        val html = """
            <body>
                <span class='welcome-msg'>Welcome to redditgifts!</span>
            </body>
        """

        //Act
        val test = testee.parseAccount(html).test()

        //Assert
        test.assertError(HTMLError.NeedsLogin)
    }

    @Test
    fun test_parseAccount_when_hasUserName_then_emitAccountData(){
        //Arrange
        val name = "Dude"
        @Language("HTML")
        val html = """
            <body>
                <span class='welcome-msg'>Welcome $name!</span>
                <img class='profile-form__pic' src='URL' />
                <textarea class='profile-form__text-area'>Description</textarea>
            </body>
        """

        //Act
        val test = testee.parseAccount(html).test()

        //Assert
        val account = test.events[0].first() as AccountModel
        assert(account.name == name)
        assert(account.imageURL == "URL")
        assert(account.description == "Description")
    }

    @Test
    fun test_parseGift_when_wrongHtml_then_throwLoadError(){
        //Arrange
        @Language("HTML")
        val html = """
            <body>
            </body>
        """

        //Act
        val test = testee.parseGift(html).test()

        //Assert
        test.assertError(HTMLError.LoadHTMLError)
    }

    @Test
    fun test_parseGift_when_oneImage_then_emitDataWithOneImage(){
        //Arrange
        @Language("HTML")
        val html = """
            <body>
                <h1 class='product-title__name'>Gift!</h1>
                <div class='product-title__sold-by'>Time</div>
                <div class='product__info__item descr-container'>Description</div>
                <span class='product-votes__number js-upvote-number'>2</span>
                <img class='product__image js-product-image' src='URL' />
                <div class='comment-body' >
                    <h1>Comment 1</h1>
                </div>
            </body>
        """

        //Act
        val test = testee.parseGift(html).test()

        //Assert
        val gift = test.events[0].first() as DetailedGiftModel
        assert(gift.images.size == 1)
    }

    @Test
    fun test_parseGift_when_hasMultipleImages_then_emitDataWithMultipleImage(){
        //Arrange
        @Language("HTML")
        val html = """
            <body>
                <h1 class='product-title__name'>Gift!</h1>
                <div class='product-title__sold-by'>Time</div>
                <div class='product__info__item descr-container'>Description</div>
                <span class='product-votes__number js-upvote-number'>2</span>
                <li class='images-list__image js-images-list-item 1' data-src='URL'/>
                <li class='images-list__image js-images-list-item 2' data-src='URL'/>
                <li class='images-list__image js-images-list-item 3' data-src='URL'/>
                <div class='comment-body' >
                    <h1>Comment 1</h1>
                </div>
            </body>
        """

        //Act
        val test = testee.parseGift(html).test()

        //Assert
        val gift = test.events[0].first() as DetailedGiftModel
        assert(gift.images.size == 3)
    }

}