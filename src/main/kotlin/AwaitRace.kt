import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
public suspend fun <T> awaitRace(vararg deferreds: Deferred<T>, callback: (result: T) -> Unit): T {
    var isFirst = true
    val completableDeferred = CompletableDeferred<T>()
    deferreds.forEach { deferred ->
        deferred.invokeOnCompletion {
            if (deferred.isCompleted && isFirst) {
                isFirst = false
                completableDeferred.complete(deferred.getCompleted())
            } else {
                callback(deferred.getCompleted())
            }
        }
    }
    return completableDeferred.await()
}

public suspend fun <T> awaitRace(vararg deferreds: Deferred<T>): T = awaitRace(*deferreds) {}

public suspend fun <T> Collection<Deferred<T>>.awaitRace(): T = awaitRace(*this.toTypedArray()) {}
public suspend fun <T : Any> Collection<Deferred<T>>.awaitRace(callback: (result: T) -> Unit): T = awaitRace(*this.toTypedArray()) { callback.invoke(it) }
