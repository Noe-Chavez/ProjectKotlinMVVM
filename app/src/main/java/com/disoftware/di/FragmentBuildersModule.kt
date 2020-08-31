package com.disoftware.di

import com.disoftware.ui.repo.RepoFragment
import com.disoftware.ui.search.SearchFragment
import com.disoftware.ui.user.UserFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoFragment(): RepoFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}