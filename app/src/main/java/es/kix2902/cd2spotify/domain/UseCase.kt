package es.kix2902.cd2spotify.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class UseCase<out Type, in Params>(private val scope: CoroutineScope) where Type : Any {

    abstract suspend fun run(params: Params): Result

    operator fun invoke(params: Params, onResult: (Result) -> Unit = {}) {
        val job = scope.async(Dispatchers.IO) { run(params) }
        scope.launch(Dispatchers.Main) { onResult(job.await()) }
    }

    class None

    sealed class Result {
        data class Success<Type>(val data: Type) : Result()
        object Error : Result()
    }
}