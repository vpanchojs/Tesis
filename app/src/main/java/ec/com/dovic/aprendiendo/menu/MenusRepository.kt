package ec.com.dovic.aprendiendo.menu

/**
 * Created by victor on 27/1/18.
 */
interface MenusRepository {
    fun onSingOut()
    fun onUpdatePassword(password: String, passwordOld: String)
    fun getMyProfile()
}