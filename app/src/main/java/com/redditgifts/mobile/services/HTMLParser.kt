package com.redditgifts.mobile.services

import com.redditgifts.mobile.services.models.*
import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.regex.Pattern

interface HTMLParser {
    fun parseExchanges(html: String): Single<ExchangeOverviewModel>
    fun parseStatuses(html: String): Single<ExchangeStatusModel>
    fun parseGallery(html: String): Single<List<GiftModel>>
    fun parseGift(html: String): Single<DetailedGiftModel>
    fun parseAccount(html: String): Single<AccountModel>
    fun parseStatistics(html: String): Single<StatisticsModel>
}

class JsoupHTMLParser: HTMLParser {

    override fun parseExchanges(html: String): Single<ExchangeOverviewModel> {
        val document = Jsoup.parse(html)
        val welcomeMessage = document.select("span[class=welcome-msg]").first()
            ?: return Single.error(HTMLError.LoadHTMLError)
        val needsLogin = welcomeMessage.text() == "Welcome to redditgifts!"
        if(needsLogin){
            return Single.error(HTMLError.NeedsLogin)
        }

        val matcher = Pattern.compile("[0-9]+").matcher(document.select("h2[class=ema-select-title ng-binding]").first().text())
        val credits = if(matcher.find()){
            matcher.group().toInt()
        } else { 0 }
        val listOfExchanges = mutableListOf<ExchangeOverviewModel.CurrentExchange>()
        val exchanges = document.select("div[class=exchange-group__exchanges]")
        for (i in exchanges.indices) {
            try {
                val referenceId = exchanges[i].select("a[class=exchange-group__link]").attr("href").replace("#/status/", "")
                val imageURL = exchanges[i].select("div[class=exchange-group__image]").attr("background-img")
                val title = exchanges[i].select("h3[class=exchange-group__title ng-binding]").first().text()
                if(referenceId.isEmpty() || imageURL.isEmpty() || title.isEmpty()){
                    continue
                }

                listOfExchanges.add(ExchangeOverviewModel.CurrentExchange(referenceId, title, imageURL))
            } catch (e: Exception) {  }
        }
        return Single.just(ExchangeOverviewModel(credits, listOfExchanges))
    }

    override fun parseStatuses(html: String): Single<ExchangeStatusModel> {
        val document = Jsoup.parse(html)
        val santaData = document.select("ol[class=participating-list]")[0]
        val santaStatusData = this.parseStatus(santaData)
        val gifteeData = document.select("ol[class=participating-list]")[1]
        val gifteeStatusData = this.parseStatus(gifteeData)
        if(santaStatusData.isEmpty() || gifteeStatusData.isEmpty()) {
            return Single.error(HTMLError.LoadHTMLError)
        }
        return Single.just(ExchangeStatusModel(santaStatusData, gifteeStatusData))
    }

    override fun parseGallery(html: String): Single<List<GiftModel>> {
        val document = Jsoup.parse(html)
        val gifts = mutableListOf<GiftModel>()
        val links = document.select("a[href]")
        for (i in links.indices) {
            try {
                val referenceId = links[i].attr("href")
                val imageURL = links[i].attr("style").replace("background-image:url(\"", "").replace(");", "").replace("\"", "")
                val title = links[i+1].attr("title")
                if(referenceId.isEmpty() || imageURL.isEmpty() || title.isEmpty()){
                    continue
                }

                gifts.add(GiftModel(referenceId, title, imageURL))
            } catch (e: Exception) {  }
        }
        return Single.just(gifts)
    }

    override fun parseGift(html: String): Single<DetailedGiftModel> {
        val document = Jsoup.parse(html)
        val titleElement: Element = document.select("h1[class=product-title__name]").first()
            ?: return Single.error(HTMLError.LoadHTMLError)

        val title = titleElement.text()
        val timeAndSource = document.select("div[class=product-title__sold-by]").first().text()
        val description = document.select("div[class=product__info__item descr-container]").first().text()
        val upvotes = document.select("span[class=product-votes__number js-upvote-number]").first().text()
        val images = document.select("li[class^=images-list__image js-images-list-item]")
        val imageURLs = mutableListOf<String>()
        if(images.isEmpty()){
            val image = document.select("img[class=product__image js-product-image]").first().attr("src")
            imageURLs.add(image)
        } else {
            for (image in images) {
                try {
                    val imageURL = image.attr("data-src")
                    if (imageURL.isEmpty()) {
                        continue
                    }
                    imageURLs.add(imageURL)
                } catch (e: Exception) {
                }
            }
        }
        val comments = document.select("div[class=comment-body]")
        val listOfComments = mutableListOf<String>()
        for (comment in comments) {
            try {
                val commentText = comment.text()
                if(commentText.isEmpty()){
                    continue
                }

                listOfComments.add(commentText)
            } catch (e: Exception) {  }
        }
        return Single.just(DetailedGiftModel(title, timeAndSource, description, upvotes, imageURLs, listOfComments))
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

    override fun parseStatistics(html: String): Single<StatisticsModel> {
        val document = Jsoup.parse(html)
        document.select("span[class=welcome-msg]").first()
            ?: return Single.error(HTMLError.LoadHTMLError)
        val title = document.select("p[class=ema-stats-flavor ng-binding ng-scope]").first().select("strong[class=ema-strong ng-binding]")
        val participantsValue = title[0].text().replace(",", "")
        val countriesValue = title[1].text()
        val littleViews = document.select("div[class=ema-stats-minor-inner]")
        val matches = littleViews[0].selectFirst("div[class=ema-stats-minor-number ng-binding]").text().replace(",", "")
        val retrieved = littleViews[1].selectFirst("div[class=ema-stats-minor-number ng-binding]").text().replace(",", "")
        val shipped = littleViews[2].selectFirst("div[class=ema-stats-minor-number ng-binding]").text().replace(",", "")
        val gifted = littleViews[3].selectFirst("div[class=ema-stats-minor-number ng-binding]").text().replace(",", "")
        val rematchSign = littleViews[4].selectFirst("div[class=ema-stats-minor-number ng-binding]").text().replace(",", "")
        val rematch = littleViews[5].selectFirst("div[class=ema-stats-minor-number ng-binding]").text().replace(",", "")
        return Single.just(StatisticsModel(
            participantsValue.toInt(), countriesValue.toInt(), matches.toInt(), retrieved.toInt(), shipped.toInt(), gifted.toInt(), rematchSign.toInt(), rematch.toInt(), 0.0,0.0,0.0,0.0,0.0
        ))
    }

}

