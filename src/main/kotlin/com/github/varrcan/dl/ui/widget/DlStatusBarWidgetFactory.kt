package com.github.varrcan.dl.ui.widget

import com.github.varrcan.dl.DlBundle.message
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.WindowManager

class DlStatusBarWidgetFactory : StatusBarWidgetFactory {
    init {
        for (project in ProjectManager.getInstance().openProjects) {
            val statusBar = WindowManager.getInstance().getStatusBar(project)
            statusBar?.updateWidget(message("dl.widget.id"))
        }
    }

    override fun getId(): String {
        return message("dl.widget.factory.id")
    }

    override fun getDisplayName(): String {
        return message("dl.display.name")
    }

    override fun isAvailable(project: Project): Boolean {
        return true
    }

    override fun createWidget(project: Project): StatusBarWidget {
        return DlStatusBarWidget(project)
    }

    override fun disposeWidget(statusBarWidget: StatusBarWidget) {
        Disposer.dispose(statusBarWidget)
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        return true
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }
}
