package ec.edu.unl.blockstudy.block.ui

import ec.edu.unl.blockstudy.entities.Question

interface BlockView {
    fun setDataQuestion(question: Question)
    fun hideProgressDialog();
    fun showProgressDialog(message: Int)
    fun showMessagge(message: String)
}