package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.AccountModel
import com.redditgifts.mobile.services.models.ExchangeStatusModel
import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

interface HTMLParser {
    fun parseStatuses(html: String): Single<ExchangeStatusModel>
    fun parseAccount(html: String): Single<AccountModel>
}

class JsoupHTMLParser: HTMLParser {

    override fun parseStatuses(html: String): Single<ExchangeStatusModel> {
        val document = Jsoup.parse(html)
        val list = document.select("ol[class=participating-list]")
        if(list.isEmpty()){
            return Single.error(HTMLError.LoadHTMLError)
        }
        val santaData = list[0]
        val santaStatusData = this.parseStatus(santaData)
        val gifteeData = list[1]
        val gifteeStatusData = this.parseStatus(gifteeData)
        if(santaStatusData.isEmpty() || gifteeStatusData.isEmpty()) {
            return Single.error(HTMLError.LoadHTMLError)
        }
        return Single.just(ExchangeStatusModel(santaStatusData, gifteeStatusData))
    }

    private fun parseStatus(document: Element): List<ExchangeStatusModel.StatusData> {
        val elements = document.select("li[class^=ng-binding ng-scope]")
        val data = mutableListOf<ExchangeStatusModel.StatusData>()
        for (i in elements.indices) {
            try {
                val title = "%d. %s".format(i+1, elements[i].text())
                val clazz = elements[i].attr("class")
                val state = when(clazz) {
                    "ng-binding ng-scope completed" -> ExchangeStatusModel.Status.COMPLETED
                    "ng-binding ng-scope active" -> ExchangeStatusModel.Status.ACTIVE
                    "ng-binding ng-scope" -> ExchangeStatusModel.Status.ACTIVE
                    "ng-binding ng-scope incomplete" -> ExchangeStatusModel.Status.INCOMPLETE
                    else -> ExchangeStatusModel.Status.INCOMPLETE
                }
                if(title.isEmpty()){
                    continue
                }

                data.add(ExchangeStatusModel.StatusData(title, state))
            } catch (e: Exception) {  }
        }
        return data
    }

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

