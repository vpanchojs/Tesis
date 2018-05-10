package ec.edu.unl.blockstudy.lib.di

import ec.edu.unl.blockstudy.lib.EventBusImp
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
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