package com.example.budgetcontrol_jetpack

import android.app.Application
import com.example.clean.containers.RepositoryContainer

class MyApp : Application() {
    lateinit var repositoryContainer: RepositoryContainer
        private set

    override fun onCreate() {
        super.onCreate()
        repositoryContainer = RepositoryContainer(this)
    }
}