package ec.com.dovic.aprendiendo.main

/**
 * Created by victor on 27/1/18.
 */
class MainInteractorImp(var repository: MainRepository) : MainInteractor {

    override fun inSession() {
        repository.inSession()
    }

    override fun onInSessionRemove() {
        repository.onInSessionRemove()
    }
}