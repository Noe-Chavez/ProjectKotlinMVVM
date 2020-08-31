package com.disoftware

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun supportFragmentInjector() = dispatchingAndroidInjector

}