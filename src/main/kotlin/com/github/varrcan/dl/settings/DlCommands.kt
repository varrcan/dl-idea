package com.github.varrcan.dl.settings

import com.github.varrcan.dl.DlBundle
import com.intellij.icons.AllIcons
import javax.swing.Icon

enum class DlCommands(
    val action: String,
    val text: String,
    val icon: Icon,
    val group: Boolean?,
) {
    UP(
        "up",
        DlBundle.message("dl.up.name"),
        AllIcons.Actions.Execute,
        true
    ),
    DOWN(
        "down",
        DlBundle.message("dl.down.name"),
        AllIcons.Actions.Suspend,
        true
    ),
    RESTART(
        "recreate",
        DlBundle.message("dl.recreate.name"),
        AllIcons.Actions.ForceRefresh,
        true
    ),
    STATUS(
        "status",
        DlBundle.message("dl.status.name"),
        AllIcons.Toolwindows.ToolWindowMessages,
        false
    );
}
