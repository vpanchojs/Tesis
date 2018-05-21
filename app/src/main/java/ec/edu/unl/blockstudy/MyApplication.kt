package ec.edu.unl.blockstudy

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.support.multidex.MultiDexApplication
import ec.edu.unl.blockstudy.QuestionCompletesComplete.di.QuestionCompleteModule
import ec.edu.unl.blockstudy.QuestionnaireResumenaireResume.di.QuestionnaireResumeModule
import ec.edu.unl.blockstudy.block.di.BlockComponent
import ec.edu.unl.blockstudy.block.di.BlockModule
import ec.edu.unl.blockstudy.block.di.DaggerBlockComponent
import ec.edu.unl.blockstudy.block.ui.BlockView
import ec.edu.unl.blockstudy.blockResume.di.BlockResumeComponent
import ec.edu.unl.blockstudy.blockResume.di.BlockResumeModule
import ec.edu.unl.blockstudy.blockResume.di.DaggerBlockResumeComponent
import ec.edu.unl.blockstudy.blockResume.ui.BlockResumeView
import ec.edu.unl.blockstudy.database.Db
import ec.edu.unl.blockstudy.detailQuestionaire.di.DaggerQuestionnaireComponent
import ec.edu.unl.blockstudy.detailQuestionaire.di.QuestionnaireComponent
import ec.edu.unl.blockstudy.detailQuestionaire.di.QuestionnaireModule
import ec.edu.unl.blockstudy.detailQuestionaire.ui.QuestionnaireView
import ec.edu.unl.blockstudy.domain.di.DomainModule
import ec.edu.unl.blockstudy.lib.di.LibModule
import ec.edu.unl.blockstudy.login.di.DaggerLoginComponent
import ec.edu.unl.blockstudy.login.di.LoginComponent
import ec.edu.unl.blockstudy.login.di.LoginModule
import ec.edu.unl.blockstudy.login.ui.LoginView
import ec.edu.unl.blockstudy.menu.di.DaggerMenusComponent
import ec.edu.unl.blockstudy.menu.di.MenusComponent
import ec.edu.unl.blockstudy.menu.di.MenusModule
import ec.edu.unl.blockstudy.menu.ui.MenusView
import ec.edu.unl.blockstudy.myquestionnaires.di.DaggerMyQuestionaireComponent
import ec.edu.unl.blockstudy.myquestionnaires.di.MyQuestionaireComponent
import ec.edu.unl.blockstudy.myquestionnaires.di.MyQuestionaireModule
import ec.edu.unl.blockstudy.myquestionnaires.ui.MyQuestionnariesView
import ec.edu.unl.blockstudy.myrepository.di.DaggerMyRepositoryComponent
import ec.edu.unl.blockstudy.myrepository.di.MyRepositoryComponent
import ec.edu.unl.blockstudy.myrepository.di.MyRepositoryModule
import ec.edu.unl.blockstudy.myrepository.ui.MyRepositoryView
import ec.edu.unl.blockstudy.newQuestion.di.DaggerQuestionComponent
import ec.edu.unl.blockstudy.newQuestion.di.QuestionComponent
import ec.edu.unl.blockstudy.newQuestion.di.QuestionModule
import ec.edu.unl.blockstudy.newQuestion.ui.QuestionView
import ec.edu.unl.blockstudy.newQuestionnaire.di.DaggerNewQuestionaireComponent
import ec.edu.unl.blockstudy.newQuestionnaire.di.NewQuestionaireComponent
import ec.edu.unl.blockstudy.newQuestionnaire.di.NewQuestionaireModule
import ec.edu.unl.blockstudy.newQuestionnaire.ui.NewQuestionaireView
import ec.edu.unl.blockstudy.profile.di.DaggerProfileComponent
import ec.edu.unl.blockstudy.profile.di.ProfileComponent
import ec.edu.unl.blockstudy.profile.di.ProfileModule
import ec.edu.unl.blockstudy.profile.ui.ProfileView
import ec.edu.unl.blockstudy.questionnaireResume.di.DaggerQuestionnaireResumeComponent
import ec.edu.unl.blockstudy.questionnaireResume.di.QuestionnaireResumeComponent
import ec.edu.unl.blockstudy.questionnaireResume.servicie.di.DaggerDownComponent
import ec.edu.unl.blockstudy.questionnaireResume.servicie.di.DownComponent
import ec.edu.unl.blockstudy.questionnaireResume.servicie.di.DownModule
import ec.edu.unl.blockstudy.questionnaireResume.ui.QuestionnaireResumeView
import ec.edu.unl.blockstudy.questionsComplete.di.DaggerQuestionCompleteComponent
import ec.edu.unl.blockstudy.questionsComplete.di.QuestionCompleteComponent
import ec.edu.unl.blockstudy.questionsComplete.ui.QuestionCompleteView
import ec.edu.unl.blockstudy.repository.di.DaggerQuestionaireRepositoryComponent
import ec.edu.unl.blockstudy.repository.di.QuestionaireRepositoryComponent
import ec.edu.unl.blockstudy.repository.di.QuestionaireRepositoryModule
import ec.edu.unl.blockstudy.repository.ui.QuestionnaireRepositoryView
import ec.edu.unl.blockstudy.signup.di.DaggerSignupComponent
import ec.edu.unl.blockstudy.signup.di.SignupComponent
import ec.edu.unl.blockstudy.signup.di.SignupModule
import ec.edu.unl.blockstudy.signup.ui.SignupView

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

    fun getNewQuestionaireComponent(view: NewQuestionaireView): NewQuestionaireComponent {
        return DaggerNewQuestionaireComponent
                .builder()
                .domainModule(domainModule)
                .libModule(LibModule())
                .newQuestionaireModule(NewQuestionaireModule(view))
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

    fun getDownComponent(): DownComponent {
        return DaggerDownComponent
                .builder()
                .domainModule(domainModule)
                .downModule(DownModule())
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