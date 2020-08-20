package com.disoftware.repository

import NetworkBoundResource
import androidx.lifecycle.LiveData
import com.disoftware.AppExecutors
import com.disoftware.api.ApiResponse
import com.disoftware.api.GithubApi
import com.disoftware.dao.UserDao
import com.disoftware.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubApi: GithubApi
) {
    fun loadUser(login: String): LiveData<Resource<User>> {
        return object: NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) {
                userDao.insert(item)
            }

            override fun shouldFetch(data: User?): Boolean {
                return data == null // equivale a hacer el if (data == null) { return true } else { return false }
            }

            override fun loadFromDb(): LiveData<User> {
                return userDao.findByLogin(login)
            }

            override fun createCall(): LiveData<ApiResponse<User>> {
                return githubApi.getUser(login)
            }
        }.asLiveData()// trasforma el mediator liveData y lo pasa a Livedata.
    }
}