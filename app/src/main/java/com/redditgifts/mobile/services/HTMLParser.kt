package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.AccountModel
import io.reactivex.Single
import org.jsoup.Jsoup

interface HTMLParser {
    fun parseAccount(html: String): Single<AccountModel>
}

class JsoupHTMLParser: HTMLParser {

    override fun parseAccount(html: String): Single<AccountModel> {
        val document = Jsoup.parse(html)
        val welcomeMessage = document.select("span[class=welcome-msg]").first()
            ?: return Single.error(HTMLError.LoadHTMLError)
        val needsLogin = welcomeMessage.text() == "Welcome to redditgifts!"
        if(needsLogin){
            return Single.error(HTMLError.NeedsLogin)
        }

        val title = welcomeMessage.text().replace("Welcome ", "").replace("!", "")
        val imageURL = document.select("img[class=profile-form__pic]").attr("src")
        val description = document.select("textarea[class=profile-form__text-area]").text()
        if(title.isEmpty() || imageURL.isEmpty() || description.isEmpty()) {
            return Single.error(HTMLError.LoadHTMLError)
        }
        return Single.just(AccountModel(title, imageURL, description))
    }

}

