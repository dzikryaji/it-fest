package com.mobile.itfest.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobile.itfest.data.Repository
import com.mobile.itfest.di.Injection
import com.mobile.itfest.ui.DataVisualitation.DurationDataViewModel
import com.mobile.itfest.ui.focus.FocusViewModel
import com.mobile.itfest.ui.login.LoginViewModel
import com.mobile.itfest.ui.main.MainViewModel
import com.mobile.itfest.ui.notes.create.CreateNoteViewModel
import com.mobile.itfest.ui.notes.main.NoteViewModel
import com.mobile.itfest.ui.notes.selection.NoteTypeSelectionViewModel
import com.mobile.itfest.ui.register.RegisterViewModel
import com.mobile.itfest.ui.splashscreen.SplashScreenViewModel

class ViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) -> {
                SplashScreenViewModel(repository) as T
            }
            (modelClass.isAssignableFrom(MainViewModel::class.java)) -> {
                MainViewModel(repository) as T
            }
            (modelClass.isAssignableFrom(LoginViewModel::class.java)) -> {
                LoginViewModel(repository) as T
            }
            (modelClass.isAssignableFrom(RegisterViewModel::class.java)) -> {
                RegisterViewModel(repository) as T
            }
            (modelClass.isAssignableFrom(FocusViewModel::class.java)) -> {
                FocusViewModel(repository) as T
            }
            (modelClass.isAssignableFrom(DurationDataViewModel::class.java)) -> {
                DurationDataViewModel(repository) as T
            }
            (modelClass.isAssignableFrom(NoteViewModel::class.java)) -> {
                NoteViewModel(repository) as T
            }
            (modelClass.isAssignableFrom(CreateNoteViewModel::class.java)) -> {
                CreateNoteViewModel(repository) as T
            }
            (modelClass.isAssignableFrom(NoteTypeSelectionViewModel::class.java)) -> {
                NoteTypeSelectionViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}