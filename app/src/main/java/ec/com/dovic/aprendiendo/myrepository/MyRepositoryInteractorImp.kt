package ec.com.dovic.aprendiendo.myrepository

import ec.com.dovic.aprendiendo.entities.Questionaire

/**
 * Created by victor on 24/2/18.
 */
class MyRepositoryInteractorImp(var repository: MyRepositoryRepository) : MyRepositoryInteractor {
    override fun onGetmyrepository() {
        repository.onGetmyrepository()
    }

    override fun onCreateQuestionaire(title: String, description: String) {
        var q = Questionaire()
        q.title = title
        q.description = description
        repository.onCreateQuestionaire(q)
    }
}