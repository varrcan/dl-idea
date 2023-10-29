package com.github.varrcan.dl.util

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content

object ConsoleLogs {
    const val DISPLAY_NAME = "Logs"
    const val CUSTOM_CONSOLE = "DL Tool"

    fun getConsoleViewContent(project: Project?): Content? {
        if (project == null) {
            return null
        }
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(CUSTOM_CONSOLE) ?: return null
        val consoleViewContent = getExistingConsoleViewContent(toolWindow)
        if (consoleViewContent != null) {
            return consoleViewContent
        }

        val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
        val newContent = toolWindow.contentManager.factory.createContent(consoleView.component, DISPLAY_NAME, false)
        toolWindow.contentManager.addContent(newContent)
        return newContent
    }

    fun getExistingConsoleViewContent(toolWindow: ToolWindow): Content? {
        val contentManager = toolWindow.contentManager
        return contentManager.findContent(DISPLAY_NAME)
    }

    fun activateConsoleView(project: Project?) {
        val content = getConsoleViewContent(project)
        val toolWindow = getToolWindow(project)
        if (content != null && toolWindow != null) {
            toolWindow.contentManager.removeAllContents(true)
            toolWindow.show()
            toolWindow.contentManager.setSelectedContent(content)
        }
    }

    fun getToolWindow(project: Project?): ToolWindow? {
        return if (project == null || project.isDisposed) {
            null
        } else ToolWindowManager.getInstance(project).getToolWindow(CUSTOM_CONSOLE)
    }
}
