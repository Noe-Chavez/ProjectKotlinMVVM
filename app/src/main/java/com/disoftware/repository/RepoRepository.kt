package com.disoftware.repository

import NetworkBoundResource
import androidx.lifecycle.LiveData
import com.disoftware.AppExecutors
import com.disoftware.api.ApiResponse
import com.disoftware.api.GithubApi
import com.disoftware.dao.RepoDao
import com.disoftware.db.GitHubDb
import com.disoftware.model.Contributor
import com.disoftware.model.Repo
import com.disoftware.utils.RateLimiter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: GitHubDb,
    private val repoDao: RepoDao,
    private val githubApi: GithubApi
) {
    // Para gestionar peticiones despues de cierto tiempo, cada 10 minutos lo leemos desde la db local.
    private val repoListRateLimiter = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
        return object: NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            // Salvar en la base de datos ese método.
            override fun saveCallResult(item: List<Repo>) = repoDao.insertRepos(item)

            override fun shouldFetch(data: List<Repo>?): Boolean {
                 //Petición al servidor, si es que es nula o vacía o si ya han pasado 10 minutos..
                return data == null || data.isEmpty() || repoListRateLimiter.shouldFetch(owner)
            }

            // Cargar de la base de datos.
            override fun loadFromDb(): LiveData<List<Repo>> = repoDao.loadRepositories(owner)

            // Si es necesario lanzar una petición a nuestro servidor.
            override fun createCall(): LiveData<ApiResponse<List<Repo>>> = githubApi.getRepos(owner)

            override fun onFetchFailed() {
                repoListRateLimiter.reset(owner)
            }

        }.asLiveData()
    }
    fun loadRepo(owner: String, name: String): LiveData<Resource<Repo>> {
        return object: NetworkBoundResource<Repo, Repo>(appExecutors) {

            override fun saveCallResult(item: Repo) = repoDao.insert(item)

            override fun shouldFetch(data: Repo?): Boolean = data == null

            override fun loadFromDb(): LiveData<Repo> = repoDao.load(
                ownerLogin = owner,
                name = name
            )

            override fun createCall(): LiveData<ApiResponse<Repo>> = githubApi.getRepo(
                owner = owner,
                name = name
            )

        }.asLiveData()
    }

    fun loadContributors(owner: String, name: String): LiveData<Resource<List<Contributor>>> {
        return object: NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {

            override fun saveCallResult(item: List<Contributor>) {
                item.forEach {
                    it.repoName = name
                    it.repoOwner = owner
                }
                db.runInTransaction {
                    repoDao.createRepoIfNotExists(
                        Repo(
                            id = Repo.UNKOWN_ID,// si el repo no existe.
                            name = name,
                            fullName = "$owner/$name",
                            description = "",
                            owner = Repo.Owner(owner, null),
                            stars = 0
                        )
                    )
                    // Insertar toda la lista de contribuidores.
                    repoDao.insertContributors(item)
                }
            }

            override fun shouldFetch(data: List<Contributor>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Contributor>> = repoDao.loadContributors(owner, name)

            override fun createCall(): LiveData<ApiResponse<List<Contributor>>> = githubApi.getContributors(owner, name)

        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            query = query,
            githubApi = githubApi,
            db = db
        )
        appExecutors.networkIo.execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }
}