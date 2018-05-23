package ec.com.dovic.aprendiendo

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.support.multidex.MultiDexApplication
import ec.com.dovic.aprendiendo.QuestionCompletesComplete.di.QuestionCompleteModule
import ec.com.dovic.aprendiendo.QuestionnaireResumenaireResume.di.QuestionnaireResumeModule
import ec.com.dovic.aprendiendo.block.di.BlockComponent
import ec.com.dovic.aprendiendo.block.di.BlockModule
import ec.com.dovic.aprendiendo.block.di.DaggerBlockComponent
import ec.com.dovic.aprendiendo.block.ui.BlockView
import ec.com.dovic.aprendiendo.blockResume.di.BlockResumeComponent
import ec.com.dovic.aprendiendo.blockResume.di.BlockResumeModule
import ec.com.dovic.aprendiendo.blockResume.di.DaggerBlockResumeComponent
import ec.com.dovic.aprendiendo.blockResume.ui.BlockResumeView
import ec.com.dovic.aprendiendo.database.Db
import ec.com.dovic.aprendiendo.detailQuestionaire.di.DaggerQuestionnaireComponent
import ec.com.dovic.aprendiendo.detailQuestionaire.di.QuestionnaireComponent
import ec.com.dovic.aprendiendo.detailQuestionaire.di.QuestionnaireModule
import ec.com.dovic.aprendiendo.detailQuestionaire.ui.QuestionnaireView
import ec.com.dovic.aprendiendo.domain.di.DomainModule
import ec.com.dovic.aprendiendo.lib.di.LibModule
import ec.com.dovic.aprendiendo.login.di.DaggerLoginComponent
import ec.com.dovic.aprendiendo.login.di.LoginComponent
import ec.com.dovic.aprendiendo.login.di.LoginModule
import ec.com.dovic.aprendiendo.login.ui.LoginView
import ec.com.dovic.aprendiendo.menu.di.DaggerMenusComponent
import ec.com.dovic.aprendiendo.menu.di.MenusComponent
import ec.com.dovic.aprendiendo.menu.di.MenusModule
import ec.com.dovic.aprendiendo.menu.ui.MenusView
import ec.com.dovic.aprendiendo.myquestionnaires.di.DaggerMyQuestionaireComponent
import ec.com.dovic.aprendiendo.myquestionnaires.di.MyQuestionaireComponent
import ec.com.dovic.aprendiendo.myquestionnaires.di.MyQuestionaireModule
import ec.com.dovic.aprendiendo.myquestionnaires.ui.MyQuestionnariesView
import ec.com.dovic.aprendiendo.myrepository.di.DaggerMyRepositoryComponent
import ec.com.dovic.aprendiendo.myrepository.di.MyRepositoryComponent
import ec.com.dovic.aprendiendo.myrepository.di.MyRepositoryModule
import ec.com.dovic.aprendiendo.myrepository.ui.MyRepositoryView
import ec.com.dovic.aprendiendo.newQuestion.di.DaggerQuestionComponent
import ec.com.dovic.aprendiendo.newQuestion.di.QuestionComponent
import ec.com.dovic.aprendiendo.newQuestion.di.QuestionModule
import ec.com.dovic.aprendiendo.newQuestion.ui.QuestionView
import ec.com.dovic.aprendiendo.updateQuestionnaire.di.UpdateQuestionaireComponent
import ec.com.dovic.aprendiendo.updateQuestionnaire.di.UpdateQuestionaireModule
import ec.com.dovic.aprendiendo.updateQuestionnaire.ui.UpdateQuestionaireView
import ec.com.dovic.aprendiendo.profile.di.DaggerProfileComponent
import ec.com.dovic.aprendiendo.profile.di.ProfileComponent
import ec.com.dovic.aprendiendo.profile.di.ProfileModule
import ec.com.dovic.aprendiendo.profile.ui.ProfileView
import ec.com.dovic.aprendiendo.questionnaireResume.di.DaggerQuestionnaireResumeComponent
import ec.com.dovic.aprendiendo.questionnaireResume.di.QuestionnaireResumeComponent
import ec.com.dovic.aprendiendo.questionnaireResume.ui.QuestionnaireResumeView
import ec.com.dovic.aprendiendo.questionsComplete.di.DaggerQuestionCompleteComponent
import ec.com.dovic.aprendiendo.questionsComplete.di.QuestionCompleteComponent
import ec.com.dovic.aprendiendo.questionsComplete.ui.QuestionCompleteView
import ec.com.dovic.aprendiendo.repository.di.DaggerQuestionaireRepositoryComponent
import ec.com.dovic.aprendiendo.repository.di.QuestionaireRepositoryComponent
import ec.com.dovic.aprendiendo.repository.di.QuestionaireRepositoryModule
import ec.com.dovic.aprendiendo.repository.ui.QuestionnaireRepositoryView
import ec.com.dovic.aprendiendo.signup.di.DaggerSignupComponent
import ec.com.dovic.aprendiendo.signup.di.SignupComponent
import ec.com.dovic.aprendiendo.signup.di.SignupModule
import ec.com.dovic.aprendiendo.signup.ui.SignupView
import ec.com.dovic.aprendiendo.updateQuestionnaire.di.DaggerUpdateQuestionaireComponent

class MyApplication : MultiDexApplication() {
    val SHARED_PREFERENCES_NAME = "dsafio_preferences"
    var domainModule: DomainModule? = null
    var appModule: MyAplicationModule? = null
    lateinit var db: Db

    override fun onCreate() {
        super.onCreate()
        initModules();
    }

    fun getSharePreferences(): SharedPreferences {
        return getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun initModules() {
        db = Room.databaseBuilder(this, Db::class.java, "block-db").build()
        appModule = MyAplicationModule(this)
        domainModule = DomainModule(this, db)
    }


    fun getLoginComponent(view: LoginView): LoginComponent {
        return DaggerLoginComponent.builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .loginModule(LoginModule(view))
                .build();
    }


    fun getSignupComponent(signupView: SignupView): SignupComponent {
        return DaggerSignupComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .signupModule(SignupModule(signupView))
                .build()
    }

    fun getMenusComponent(menusView: MenusView): MenusComponent {
        return DaggerMenusComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .menusModule(MenusModule(menusView))
                .build()
    }

    fun getProfileComponent(profileView: ProfileView): ProfileComponent {
        return DaggerProfileComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .profileModule(ProfileModule(profileView))
                .build()
    }

    fun getNewQuestionaireComponent(view: UpdateQuestionaireView): UpdateQuestionaireComponent {
        return DaggerUpdateQuestionaireComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .updateQuestionaireModule(UpdateQuestionaireModule(view))
                .build()
    }

    fun getMyQuestionnarieComponent(view: MyQuestionnariesView): MyQuestionaireComponent {
        return DaggerMyQuestionaireComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .myQuestionaireModule(MyQuestionaireModule(view))
                .build()
    }

    fun getMyRepositoryComponent(view: MyRepositoryView): MyRepositoryComponent {
        return DaggerMyRepositoryComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .myRepositoryModule(MyRepositoryModule(view))
                .build()

    }


    fun getQuestionnarieComponent(view: QuestionnaireView): QuestionnaireComponent {
        return DaggerQuestionnaireComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .questionnaireModule(QuestionnaireModule(view))
                .build()
    }

    fun getQuestionComponent(view: QuestionView): QuestionComponent {
        return DaggerQuestionComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .questionModule(QuestionModule(view))
                .build()
    }

    fun getQuestionCompleteComponent(view: QuestionCompleteView): QuestionCompleteComponent {
        return DaggerQuestionCompleteComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .questionCompleteModule(QuestionCompleteModule(view))
                .build()
    }

    fun getQuestionnaireRepoComponent(view: QuestionnaireRepositoryView): QuestionaireRepositoryComponent {
        return DaggerQuestionaireRepositoryComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .questionaireRepositoryModule(QuestionaireRepositoryModule(view))
                .build()
    }

    fun getQuestionnaireResumeComponent(view: QuestionnaireResumeView): QuestionnaireResumeComponent {
        return DaggerQuestionnaireResumeComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .questionnaireResumeModule(QuestionnaireResumeModule(view))
                .build()
    }

    fun getBlockResumeComponent(view: BlockResumeView): BlockResumeComponent {
        return DaggerBlockResumeComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .blockResumeModule(BlockResumeModule(view))
                .build()
    }

    fun getBlockComponent(view: BlockView): BlockComponent {
        return DaggerBlockComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .blockModule(BlockModule(view))
                .build()
    }


}