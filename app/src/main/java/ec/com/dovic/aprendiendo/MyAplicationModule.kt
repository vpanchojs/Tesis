package ec.com.dovic.aprendiendo

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MyAplicationModule(var app: MyApplication) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return app.applicationContext;
    }

    @Provides
    @Singleton
    fun provideAppliclation(): Application {
        return app;
    }


}