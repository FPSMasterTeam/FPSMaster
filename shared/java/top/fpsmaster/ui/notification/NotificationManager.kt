package top.fpsmaster.ui.notification

import java.util.concurrent.CopyOnWriteArrayList

val notifications: CopyOnWriteArrayList<Notification> = CopyOnWriteArrayList()

fun addNotification(notification: Notification) {
    notifications.add(notification)
}

fun addNotification(title: String, desc: String, duration: Float) {
    notifications.add(Notification(title, desc, Type.INFO, duration))
}

fun drawNotifications() {
    var y = 20f

    for (notification in notifications) {
        notification.draw(0f, y)
        if (notification.anim.end == 100.0 && notification.anim.value == 100.0){
            notifications.remove(notification)
        }
        y += 40f
    }
}
