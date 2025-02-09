package com.segunfrancis.redditclient

import android.app.Application
import com.segunfrancis.redditclient.data.dataModule
import com.segunfrancis.redditclient.ui.details.detailsModule
import com.segunfrancis.redditclient.ui.home.homeModule
import com.segunfrancis.redditclient.ui.search.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class RedditClientApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@RedditClientApp)
            androidLogger(level = Level.DEBUG)
            modules(dataModule, homeModule, searchModule, detailsModule)
        }
    }
}
