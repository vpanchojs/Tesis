package ec.edu.unl.blockstudy.block

interface BlockRepository {

    fun getQuestion(questionPath: ArrayList<Long>)
}