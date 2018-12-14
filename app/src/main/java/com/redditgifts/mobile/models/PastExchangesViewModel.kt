package com.redditgifts.mobile.models

import com.redditgifts.mobile.R
import com.redditgifts.mobile.services.models.PastExchangeModel
import com.redditgifts.mobile.ui.viewholders.PastExchangeViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface PastExchangesViewModelInputs : PastExchangeViewHolder.Delegate {
    fun onCreate()
}

interface PastExchangesViewModelOutputs {
    fun pastExchanges(): Observable<List<PastExchangeModel>>
    fun startExchangeGallery(): Observable<PastExchangeModel>
}

class PastExchangesViewModel: DisposableViewModel(), PastExchangesViewModelInputs, PastExchangesViewModelOutputs {

    //INPUTS
    private val onCreate = PublishSubject.create<Unit>()

    //OUTPUTS
    private val pastExchanges = PublishSubject.create<List<PastExchangeModel>>()
    private val startExchangeGallery = PublishSubject.create<PastExchangeModel>()

    val inputs: PastExchangesViewModelInputs = this
    val outputs: PastExchangesViewModelOutputs = this

    init {
        this.onCreate
            .map { listOf(
                PastExchangeModel("holiday-cards-2018-zero-credit", R.string.exchange_holiday_cards_2018, R.drawable.logo_holiday_cards_2018_zero_credit),
                PastExchangeModel("secret-santa-2018", R.string.exchange_secret_santa_2018, R.drawable.logo_secret_santa_2018),
                PastExchangeModel("postcards-zero-credit-2018", R.string.exchange_postcards_2018, R.drawable.logo_postcards_zero_credit_2018),
                PastExchangeModel("funko-pop-exchange-2018", R.string.exchange_funko_pop_2018, R.drawable.logo_funko_pop_exchange_2018),
                PastExchangeModel("coffee-and-tea-2018", R.string.exchange_coffee_and_tea_2018, R.drawable.logo_coffee_and_tea_2018),

                PastExchangeModel("star-wars-2018", R.string.exchange_star_wars_2018, R.drawable.logo_star_wars_2018),



                PastExchangeModel("ugly-mugs-2018", R.string.exchange_ugly_mugs_2018, R.drawable.logo_ugly_mugs_2018),
                PastExchangeModel("harry-potter-2018", R.string.exchange_harry_potter_2018, R.drawable.logo_harry_potter_2018),
                PastExchangeModel("art-supplies-2018", R.string.exchange_art_supplies_2018, R.drawable.logo_art_supplies_2018),
                PastExchangeModel("diwali-2018", R.string.exchange_diwali_2018, R.drawable.logo_diwali_2018),
                PastExchangeModel("trick-or-treat-2018", R.string.exchange_trick_or_treat_2018, R.drawable.logo_trick_or_treat_2018),
                PastExchangeModel("kitchen-goods-exchange-2018", R.string.exchange_kitchen_goods_2018, R.drawable.logo_kitchen_goods_exchange_2018)
            ) }
            .crashingSubscribe { this.pastExchanges.onNext(it) }
    }

    override fun onCreate() = this.onCreate.onNext(Unit)
    override fun didSelectExchange(exchange: PastExchangeModel) = this.startExchangeGallery.onNext(exchange)
    override fun pastExchanges(): Observable<List<PastExchangeModel>> = this.pastExchanges
    override fun startExchangeGallery(): Observable<PastExchangeModel> = this.startExchangeGallery
}

