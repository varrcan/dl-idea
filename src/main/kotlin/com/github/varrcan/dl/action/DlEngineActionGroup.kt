package com.github.varrcan.dl.action

import com.github.varrcan.dl.DlBundle.message
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.util.NlsActions
import java.util.function.Supplier

class DlEngineActionGroup(
    name: Supplier<@NlsActions.ActionText String> = Supplier { message("dl.display.name") },
    popup: Boolean = true
) : DefaultActionGroup(name, popup), PopupAction {

    init {
        val (groupActions, unGroupActions) = DlEngineAction.actionsGroupedByAvailability()
        addAll(groupActions)

        if (unGroupActions.isNotEmpty()) {
            addSeparator()
            addAll(unGroupActions)
        }
    }

    override fun isDumbAware(): Boolean = true

    override fun actionPerformed(e: AnActionEvent) {
        showActionPopup(e.dataContext)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.setEnabled(false)
    }

    fun createActionPopup(
        context: DataContext,
        title: String? = message("dl.display.name"),
        disposeCallback: Runnable? = null
    ): ListPopup = JBPopupFactory
        .getInstance()
        .createActionGroupPopup(
            title,
            this,
            context,
            false,
            true,
            false,
            disposeCallback,
            10,
            null
        )

    fun showActionPopup(dataContext: DataContext, disposeCallback: Runnable? = null) {
        val component = PlatformCoreDataKeys.CONTEXT_COMPONENT.getData(dataContext)
        val title = if (component == null) message("dl.display.name") else null
        val popup = createActionPopup(dataContext, title, disposeCallback)
        component?.let { popup.showUnderneathOf(it) } ?: popup.showInBestPositionFor(dataContext)
    }

}
