package ec.com.dovic.aprendiendo.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.blockResume.ui.BlockResumeFragment
import ec.com.dovic.aprendiendo.menu.ui.MenuFragment
import ec.com.dovic.aprendiendo.myquestionnaires.ui.MyQuestionnairesFragment
import ec.com.dovic.aprendiendo.repository.ui.QuestionnaireRepositoryFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_my_questionary

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_my_questionary -> {
                fragment = MyQuestionnairesFragment.newInstance()
                supportActionBar!!.title = "Mis Cuestionarios"
            }
            R.id.navigation_repository -> {
                fragment = QuestionnaireRepositoryFragment.newInstance()
                supportActionBar!!.title = "Repositorio Público"
            }
            R.id.navigation_block -> {
                fragment = BlockResumeFragment.newInstance()
                supportActionBar!!.title = "Configuración del Bloqueo"
            }
            R.id.navigation_my_profile -> {
                fragment = MenuFragment.newInstance()
                supportActionBar!!.title = "Menú"
            }
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, fragment).commitAllowingStateLoss()
        true
    }


}
