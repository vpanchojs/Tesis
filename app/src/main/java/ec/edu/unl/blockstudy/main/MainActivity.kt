package ec.edu.unl.blockstudy.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.blockResume.ui.BlockResumeFragment
import ec.edu.unl.blockstudy.menu.ui.MenuFragment
import ec.edu.unl.blockstudy.myquestionnaires.ui.MyQuestionnairesFragment
import ec.edu.unl.blockstudy.repository.ui.QuestionnaireRepositoryFragment
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
                supportActionBar!!.title = "Repositorio"
            }
            R.id.navigation_block -> {
                fragment = BlockResumeFragment.newInstance()
                supportActionBar!!.title = "Bloqueo"
            }
            R.id.navigation_my_profile -> {
                fragment = MenuFragment.newInstance()
                supportActionBar!!.title = "Menu"
            }
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_container, fragment).commitAllowingStateLoss()
        true
    }


}
