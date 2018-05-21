package ec.edu.unl.blockstudy.block

import ec.edu.unl.blockstudy.util.Presenter

interface BlockPresenter : Presenter {

    fun getQuestion(ids: ArrayList<Long>)
}