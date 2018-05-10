package ec.edu.unl.blockstudy.menu

/**
 * Created by victor on 27/1/18.
 */
class MenusInteractorImp(var repository: MenusRepository) : MenusInteractor {
    override fun onSingOut() {
        repository.onSingOut()
    }

    override fun onUpdatePassword(password: String, passwordOld: String) {
        repository.onUpdatePassword(password, passwordOld)
    }

    override fun getMyProfile() {
        repository.getMyProfile()
    }
}