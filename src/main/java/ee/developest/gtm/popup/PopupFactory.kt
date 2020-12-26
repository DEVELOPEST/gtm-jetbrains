package ee.developest.gtm.popup

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class PopupFactory {

    companion object {
        private const val GROUP_ID = "gtm"

        fun showInitConfirmation(project: Project, onYes: () -> Unit, onNo: () -> Unit?) {
            val res = Messages.showDialog(project, "Do you want to initialize gtm time tracking for current repository?", "Gtm Enhanced", arrayOf("Yes", "No"), 0, Messages.getQuestionIcon())
            if (res == 0) {
                onYes()
            } else {
                onNo()
            }
        }

        fun showInfoMessage(title: String, text: String) {
            Messages.showInfoMessage(text, title)
        }

        fun showInfoNotification(title: String, text: String) {
            Notifications.Bus.notify(Notification(GROUP_ID, title, text, NotificationType.INFORMATION))
        }

        fun showErrorNotification(title: String, text: String) {
            Notifications.Bus.notify(Notification(GROUP_ID, title, text, NotificationType.ERROR))
        }
    }
}