package com.disoftware.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.disoftware.model.Repo
import com.disoftware.model.User
import com.disoftware.repository.RepoRepository
import com.disoftware.repository.Resource
import com.disoftware.repository.UserRepository
import com.disoftware.utils.AbsentLiveData
import javax.inject.Inject

class UserViewModel @Inject constructor(
    userRepository: UserRepository,
    repoRepository: RepoRepository): ViewModel() {
    private val _login = MutableLiveData<String>()

    val login: LiveData<String>
        get() = _login
    //  Transformations.switchMap(_login): transforma para cada logín a LiveData.
    val repositories: LiveData<Resource<List<Repo>>> = Transformations
        .switchMap(_login) {
            login ->
            if (login == null)
                AbsentLiveData.create()
            else
                repoRepository.loadRepos(login)
        }

    val user: LiveData<Resource<User>> = Transformations
        .switchMap(_login) {
            login ->
            if (login == null)
                AbsentLiveData.create()
            else
                userRepository.loadUser(login)
        }

    fun setLogin(login: String?) {
        if (_login.value != login)
            _login.value = login
    }

    fun retry() {
        _login.value?.let {
            _login.value = it
        }
    }

}