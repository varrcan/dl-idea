package com.github.varrcan.dl.ui.window

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import javax.swing.Icon

class ConsolePanelToolBarFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {}

    override fun getIcon(): Icon {
        return AllIcons.Actions.ProjectWideAnalysisOff
    }
}
