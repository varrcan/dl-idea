package com.github.varrcan.dl.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.github.varrcan.dl.DlBundle.message

object NotificationHelper {
    @JvmStatic
    fun showNotification(project: Project?, content: String?, type: NotificationType?) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(message("dl.notification.group"))
            .createNotification(content!!, type ?: NotificationType.INFORMATION)
            .setTitle("Deploy Local")
            .setSuggestionType(true)
            .notify(project)
    }
}
