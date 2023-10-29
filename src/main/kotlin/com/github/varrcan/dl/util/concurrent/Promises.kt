@file:Suppress("unused")

package com.github.varrcan.dl.util.concurrent

import com.github.varrcan.dl.util.DisposableRef
import com.github.varrcan.dl.util.invokeAndWait
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import org.jetbrains.concurrency.CancellablePromise
import org.jetbrains.concurrency.Promise
import java.util.function.Consumer

fun <T> Promise<T>.expireWith(parentDisposable: Disposable): Promise<T> {
    if (this !is CancellablePromise) {
        return this
    }

    @Suppress("DEPRECATION")
    if (if (parentDisposable is Project) parentDisposable.isDisposed else Disposer.isDisposed(parentDisposable)) {
        cancel()
        return this
    }

    val childDisposable = Disposable { cancel() }
    Disposer.register(parentDisposable, childDisposable)
    return onProcessed { Disposer.dispose(childDisposable) }
}

fun <T> Promise<T>.successOnUiThread(
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    uiThreadAction: (T) -> Unit
): Promise<T> = onUiThread(Promise<T>::onSuccess, modalityState, uiThreadAction)

fun <Ref, T> Promise<T>.successOnUiThread(
    disposableRef: DisposableRef<Ref>,
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    uiThreadAction: (Ref & Any, T) -> Unit
): Promise<T> = onUiThread(Promise<T>::onSuccess, disposableRef, modalityState, uiThreadAction)

fun <T> Promise<T>.finishOnUiThread(
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    uiThreadAction: (T?) -> Unit
): Promise<T> = onUiThread(Promise<T>::onProcessed, modalityState, uiThreadAction)

fun <Ref, T> Promise<T>.finishOnUiThread(
    disposableRef: DisposableRef<Ref>,
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    uiThreadAction: (Ref & Any, T?) -> Unit
): Promise<T> = onUiThread(Promise<T>::onProcessed, disposableRef, modalityState, uiThreadAction)


fun <T> Promise<T>.disposeAfterProcessing(disposableRef: DisposableRef<*>): Promise<T> {
    return onProcessed { disposableRef.disposeSelf() }
}

internal inline fun <T, V> Promise<T>.onUiThread(
    fn: Promise<T>.(Consumer<V>) -> Promise<T>,
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    crossinline uiThreadAction: (V) -> Unit
): Promise<T> {
    return fn(Consumer { result ->
        invokeAndWait(modalityState) { uiThreadAction(result) }
    })
}

internal inline fun <Ref, T, V> Promise<T>.onUiThread(
    fn: Promise<T>.(Consumer<V>) -> Promise<T>,
    disposableRef: DisposableRef<Ref>,
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    crossinline uiThreadAction: (Ref & Any, V) -> Unit
): Promise<T> {
    return expireWith(disposableRef)
        .fn(Consumer { result ->
            @Suppress("DEPRECATION")
            if (disposableRef.get() != null && !Disposer.isDisposed(disposableRef)) {
                invokeAndWait(modalityState) {
                    disposableRef.get()?.let { uiThreadAction(it, result) }
                }
            }
        })
}
