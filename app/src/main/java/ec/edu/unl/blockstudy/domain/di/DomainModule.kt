package ec.edu.unl.blockstudy.domain.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.ObjectBoxApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.VolleyApi
import io.objectbox.BoxStore
import javax.inject.Singleton

@Module
class DomainModule(var app: MyApplication, var boxStore: BoxStore) {

    @Provides
    @Singleton
    fun providesVolleyApi(): VolleyApi {
        return VolleyApi(app)
    }

    @Provides
    @Singleton
    fun providesObjectBoxApi(): ObjectBoxApi {
        return ObjectBoxApi(boxStore)
    }

    @Provides
    @Singleton
    fun providesSharePreferences(): SharePreferencesApi {
        return SharePreferencesApi(app.getSharePreferences())
    }

    @Provides
    @Singleton
    fun providesFirebaseApi(): FirebaseApi {
        return FirebaseApi(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance(), FirebaseStorage.getInstance().reference)
    }


}