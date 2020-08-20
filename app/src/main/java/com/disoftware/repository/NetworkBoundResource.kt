import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.disoftware.AppExecutors
import com.disoftware.api.ApiEnptyResponse
import com.disoftware.api.ApiErrorResponse
import com.disoftware.api.ApiResponse
import com.disoftware.api.ApiSucessResponse
import com.disoftware.repository.Resource

/**
 * Clase genereica que traera el recurso ya sea de la DB local o del webservice, segun se requiera.
 * @ResultType: El tipo que va a devolver.
 * @RequestType: Tipo de dato de la solucitud.
 */
abstract class NetworkBoundResource<ResultType, RequestType>
// @MainThread: Se ejecuta desde el hilo principal.
@MainThread constructor(private val appExecutors: AppExecutors) {
    // MediatorLiveData: ayuda a adminsitrar diversos livedata, sin necesidad de estar suscribiendo cada uno,
    // Cada que hay un cambio el MediatorLiveData, comunica a dicho componente.
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        // Nota: recordar que value se ejecuta en el hilo principal.
        // Esta en modo loanding hasta que el web service nos halla regresado el dato correctamente.
        result.value = Resource.loanding(null)
        val dbSource = loadFromDb()
        // Añadir la fuente de datos LiveData, al MediatorLiveData.
        // Cada vez que hay un cambio en la base de datos se ejecuta el siguiente código.
        result.addSource(dbSource){
                data-> result.removeSource(dbSource) // Pare esa fuente de datos, ya que se terminó de revisar.
            if(shouldFetch(data)) {
                // Dado que el recurso caduque por alguna razon (exeso de tiempo, por ejemplo)
                // se ejecute el código.
                fetchFromNetwork(dbSource)
            } else { // Para leer desde la base de datos local.
                result.addSource(dbSource) {
                        newData-> setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>){
        if(result.value != newValue){
            result.value = newValue
        }
    }

    /**
     * En caso de que ta peticion al web servece falle por alguna razon, o tarde demaciado,
     * se muestren al usuario los últimos datos guardados en la base local.
     */
    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        result.addSource(dbSource){
            newData -> setValue(Resource.loanding(newData))
        }
        result.addSource(apiResponse) {
            response ->
                result.removeSource(apiResponse)
                result.removeSource(dbSource)
            when (response) {
                is ApiSucessResponse -> {
                    appExecutors.diskIo().execute {
                        saveCallResult(processResponse(response))
                        appExecutors.mainThread().execute {
                            result.addSource(loadFromDb()) {
                                newData -> setValue(Resource.success(newData))
                                appExecutors.mainThread().execute {
                                    // Nueva fuente de la base de datos local.
                                    result.addSource(loadFromDb()) {
                                        newData -> setValue(Resource.success(newData))
                                    }
                                }
                            }
                        }
                    }
                }
                is ApiEnptyResponse -> {
                    result.addSource(loadFromDb()) {
                        newData -> setValue(Resource.success(newData))
                    }
                }
                is ApiEnptyResponse -> {
                    onFetchFailed()
                    result.addSource(dbSource) {
                        newData -> setValue(Resource.error((response as ApiErrorResponse).errorMessage, newData))
                    }
                }
            }
        }
    }

    /**
     * En caso de que falle la petición.
     */
    protected open fun onFetchFailed(){}

    /**
     * Pasar de MediatorLiveData a LiveData.
     * @as: hace el cast a ReultType.
     */
    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSucessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    /**
     * Verificamos si el recurso se encuentra dentro de la DB local, si no se encuentra, es necesario
     * traerlo desde el Web Service.
     */
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}