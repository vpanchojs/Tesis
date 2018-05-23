package ec.com.dovic.aprendiendo.block

import ec.com.dovic.aprendiendo.util.Presenter

interface BlockPresenter : Presenter {

    fun getQuestion(ids: ArrayList<Long>)
}