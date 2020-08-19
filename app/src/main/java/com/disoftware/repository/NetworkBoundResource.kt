package com.disoftware.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.disoftware.AppExecutors

/**
 * Clase genereica que traera el recurso ya sea de la DB local o del webservice, segun se requiera.
 * @ResultType: El tipo que va a devolver.
 * @RequestType: Tipo de dato de la solucitud.
 */
abstract class NetworkBoundResource<ResultType, RequestType>
// @MainThread: Se ejecuta desde el hilo principal.
@MainThread constructor(private val appExecutors: AppExecutors ) {
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
        result.addSource(dbSource) {
            data -> result.removeSource(dbSource) // Pare esa fuente de datos, ya que se terminó de revisar.
            if (shouldFetch(data)) {
                // Dado que el recurso caduque por alguna razon (exeso de tiempo, por ejemplo)
                // se ejecute el código.
                fetchFromNetwork(dbSource)
            }
        }
    }

    private  fun fetchFromNetwork(dbSource: LiveData<ResultType>) {

    }

    /**
     * Verificamos si el recurso se encuentra dentro de la DB local, si no se encuentra, es necesario
     * traerlo desde el Web Service.
     */
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>
}