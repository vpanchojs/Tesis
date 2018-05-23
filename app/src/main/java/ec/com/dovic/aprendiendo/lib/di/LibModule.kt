package ec.com.dovic.aprendiendo.lib.di

import ec.com.dovic.aprendiendo.lib.EventBusImp
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Module
class LibModule {

    @Provides
    @Singleton
    fun providesEventBus(eventBus: EventBus): EventBusInterface {
        return EventBusImp(eventBus);
    }

    @Provides
    @Singleton
    fun providesLibraryEventBus(): EventBus {
        return EventBus.getDefault()
    }
}