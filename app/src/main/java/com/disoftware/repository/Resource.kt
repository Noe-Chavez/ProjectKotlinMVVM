package com.disoftware.repository

/**
 * Clase generica que va a mantener el valor cuando estemos cargando los datos.
 * @fun loanding: Timepo que tarda en obtener el recurso, dado que es una tarea asíncrona, no sbaesmos
 * el tiempo que Tarde. Cuando terminé pasa a los otros dos estados (success o error).
 * @fun error: Cuando ocurre algún error.
 * @fun success: Cuando se ontivo el recurso.
 */
data class Resource<out T> (val status: Status, val data: T?, val message: String?) {
    companion object {
        fun<T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }
         fun<T> error(msg: String, data: T?): Resource<T> {
             return Resource(Status.ERROR, data, msg)
         }
        fun<T> loanding(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}