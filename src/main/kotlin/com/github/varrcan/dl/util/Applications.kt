@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.github.varrcan.dl.util

import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.Condition
import java.util.concurrent.Future


inline val Application: Application get() = ApplicationManager.getApplication()

inline fun invokeAndWait(
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    crossinline runnable: () -> Unit
) {
    Application.invokeAndWait({ runnable() }, modalityState)
}

inline fun invokeLater(crossinline action: () -> Unit) {
    Application.invokeLater { action() }
}

inline fun invokeLater(state: ModalityState, crossinline action: () -> Unit) {
    Application.invokeLater({ action() }, state)
}

fun Notification.show(project: Project? = null) {
    Notifications.Bus.notify(this, project)
}
