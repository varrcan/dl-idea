package com.github.varrcan.dl.ui.widget

import com.github.varrcan.dl.DlBundle.message
import com.github.varrcan.dl.DlState
import com.github.varrcan.dl.StatusChangeListener
import com.github.varrcan.dl.action.DlEngineActionGroup
import com.github.varrcan.dl.settings.DlCommands
import com.github.varrcan.dl.util.DisposableRef
import com.github.varrcan.dl.util.concurrent.disposeAfterProcessing
import com.github.varrcan.dl.util.concurrent.expireWith
import com.github.varrcan.dl.util.concurrent.finishOnUiThread
import com.github.varrcan.dl.util.concurrent.successOnUiThread
import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IconLikeCustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.impl.status.TextPanel.WithIconAndArrows
import com.intellij.ui.ClickListener
import com.intellij.ui.awt.RelativePoint
import org.jetbrains.concurrency.runAsync
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.JComponent

class DlStatusBarWidget(private val project: Project) : WithIconAndArrows(), IconLikeCustomStatusBarWidget {
    companion object {
        val ID: String = message("dl.widget.id")
    }

    private var isLoadingDlEngines = false

    init {
        setTextAlignment(CENTER_ALIGNMENT)
    }

    override fun ID(): String {
        return ID
    }

    override fun getComponent(): JComponent = this

    override fun install(statusBar: StatusBar) {
        if (project.isDisposed) {
            return
        }

        setupClickListener()
//        subscribeToStatusChangeEvents(statusBar)
        update { statusBar.updateWidget(ID) }
    }

    private fun setupClickListener() {
        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                if (!project.isDisposed) {
                    showPopup()
                }
                return true
            }
        }.installOn(this, true)
    }

    private fun showPopup() {
        if (isLoadingDlEngines) {
            return
        }

        isLoadingDlEngines = true
        val widgetRef = DisposableRef.create(this, this)
        runAsync { DlEngineActionGroup() }
            .expireWith(this)
            .successOnUiThread(widgetRef) { widget, group ->
                val context = DataManager.getInstance().getDataContext(widget)
                val popup = group.createActionPopup(context)
                val at = Point(0, -popup.content.preferredSize.height)
                popup.show(RelativePoint(widget, at))
            }
            .finishOnUiThread(widgetRef, ModalityState.any()) { widget, _ ->
                widget.isLoadingDlEngines = false
            }
            .disposeAfterProcessing(widgetRef)
    }

//    private fun subscribeToStatusChangeEvents(statusBar: StatusBar) {
//        project.messageBus
//            .connect(this)
//            .subscribe(StatusChangeListener.TOPIC, object : StatusChangeListener {
//                override fun onStatusChanged(commands: DlCommands) {
//                    println(commands.action)
//                    update { statusBar.updateWidget(ID) }
//                }
//            })
//    }

    private fun update(onUpdated: (() -> Unit)? = null) {
        text = "DL Tool"
//        icon = AllIcons.Debugger.ThreadFrozen
        onUpdated?.invoke()
        repaint()
    }

    override fun dispose() {}
}
