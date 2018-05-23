package ec.com.dovic.aprendiendo.block

interface BlockRepository {

    fun getQuestion(questionPath: ArrayList<Long>)
}