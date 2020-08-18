package com.disoftware

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppExecutors (
    val diskIo: Executor, // Disco, báse de datos local (ROOM).
    val networkIo: Executor, // La que utiliza el Webservice (Retrofit).
    val mainThread: Executor // en el hilo principal.
) {
    /**
     * @newSingleThreadExecutor: Poder crear multiples tareas, si es necesario crear varias peticiones
     * a la base de datos local, poder hacer muchas consultas (se acumulan en una cola).
     * @newFixedThreadPool: Crea un grupo de hilos con n llamadas al servidor activas al mismo tiempo.
     * Si se realiza una petición y aún no han terminado de librear los n hilos de proceso, entonces
     * se encolan.
     * @MainThreadExecutor(): Se utiliza para procesar tareas en el hilo principal.
     */
    @Inject
    constructor(): this(
        Executors.newSingleThreadExecutor(),
        Executors.newFixedThreadPool(3),
        MainThreadExecutor()
    )

    fun diskIo(): Executor {
        return diskIo
    }

    fun networkIo(): Executor {
        return networkIo
    }

    fun mainThread(): Executor {
        return mainThread
    }

    private class MainThreadExecutor: Executor {
        val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

}