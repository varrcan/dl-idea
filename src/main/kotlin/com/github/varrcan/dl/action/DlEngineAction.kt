package com.github.varrcan.dl.action

import com.github.varrcan.dl.settings.DlCommands
import com.github.varrcan.dl.util.ConsoleLogs.activateConsoleView
import com.github.varrcan.dl.util.ConsoleLogs.getConsoleViewContent
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.content.Content
import java.nio.charset.StandardCharsets
import kotlin.properties.Delegates

class DlEngineAction(private val commands: DlCommands) : AnAction(commands.text, null, commands.icon) {

    fun isGroup(): Boolean = commands.group == true

    override fun actionPerformed(e: AnActionEvent) {
//        DlState.getInstance().state.command = commands
        val project = e.project ?: return

        activateConsoleView(project);
        val content: Content = getConsoleViewContent(project)!!
        val console = content.component as ConsoleView


        val commandLine: GeneralCommandLine = GeneralCommandLine()
            .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.SYSTEM)
            .withWorkDirectory(project.basePath)
            .withExePath("dl")
            .withParameters(commands.action)

        val handler = ColoredProcessHandler(commandLine.withCharset(StandardCharsets.UTF_8))

        console.attachToProcess(handler);
        handler.startNotify()
    }

    companion object {
        private val ACTIONS: List<DlEngineAction> =
            DlCommands.entries.map { DlEngineAction(it) }

        fun actionsGroupedByAvailability(): Pair<List<DlEngineAction>, List<DlEngineAction>> {
            return ACTIONS.groupBy { it.isGroup() }.let {
                it.getOrDefault(true, emptyList()) to it.getOrDefault(false, emptyList())
            }
        }
    }

    override fun isDumbAware(): Boolean {
        return true
    }
}
