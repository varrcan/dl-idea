package com.github.varrcan.dl

import com.intellij.util.messages.Topic
import com.github.varrcan.dl.settings.DlCommands
import com.intellij.codeInsight.codeVision.settings.CodeVisionSettings
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import java.util.*
import kotlin.properties.Delegates

class DlState {

    @Volatile
    private var state = State()

    companion object {
        @JvmStatic
        fun getInstance(): DlState = ApplicationManager.getApplication().getService(DlState::class.java)
    }

    class State {
        var command: DlCommands
                by Delegates.observable(DlCommands.UP) { _, oldValue: DlCommands?, newValue: DlCommands ->
                    if (oldValue != newValue) {
                        STATUS_CHANGE_PUBLISHER.onStatusChanged(newValue)
                    }
                }
    }
}

val STATUS_CHANGE_PUBLISHER: StatusChangeListener =
    ApplicationManager.getApplication().messageBus.syncPublisher(StatusChangeListener.TOPIC)

interface StatusChangeListener {

    fun onStatusChanged(commands: DlCommands) {}

    companion object {
        val TOPIC: Topic<StatusChangeListener> =
            Topic.create("DlStatusChanged", StatusChangeListener::class.java)
    }
}
