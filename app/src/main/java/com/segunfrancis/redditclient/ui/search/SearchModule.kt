package com.segunfrancis.redditclient.ui.search

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {

    viewModelOf(::SearchViewModel)
}
