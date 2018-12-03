package sonnenhut.liveedit.patch;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

/**
 * Listens to Notifications and restarts the debugger when a specific one occurs
 */
public class LiveEditPatchComponent implements ProjectComponent {

    private static String LIVEEDIT_ID = "LiveEdit";
    private static String LIVEEDIT_WEB_30653_MSG = "Editing module's script is not supported.";
    private static String LIVEEDIT_WEB_30653_PATCH_MSG = "LiveEdit patch: Debugger restarted";

    private Project project;
    private boolean showInfoMessage = true;

    public LiveEditPatchComponent(Project p) {
        this.project = p;
    }

    @Override
    public void initComponent() {
        subscribeToNotifications();
    }

    private void subscribeToNotifications() {
        project.getMessageBus().connect().subscribe(Notifications.TOPIC, new BugWEB30653Listener(() -> {
            restartDebugger();
            return null;
        }));
    }

    private void restartDebugger() {
        final AnAction action = ActionManager.getInstance().getAction("Rerun");
        if(action != null) {
            final MouseEvent me = new MouseEvent(JOptionPane.getRootFrame(), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 0, false, 1);
            ActionManagerEx.getInstance().tryToExecute(action, me, null, null, false);
            notifyWorkaroundExecuted();
        }
    }

    private void notifyWorkaroundExecuted() {
        final NotificationGroup toolWindowGroup = NotificationGroup.findRegisteredGroup(LIVEEDIT_ID);
        if(toolWindowGroup != null && showInfoMessage) {
            Notification notification = toolWindowGroup.createNotification("", LIVEEDIT_WEB_30653_PATCH_MSG, NotificationType.INFORMATION, null);
            notification.notify(project);
            showInfoMessage = false; // only show it once per session
        }
    }

    /**
     * Notification listener for the bug WEB-30653
     */
    public static class BugWEB30653Listener implements Notifications {
        private final Supplier<Void> callback;

        BugWEB30653Listener(Supplier<Void> callback) {
            this.callback = callback;
        }

        @Override
        public void notify(@NotNull Notification notification) {
            if(LIVEEDIT_ID.equals(notification.getGroupId())
                    && notification.getContent().contains(LIVEEDIT_WEB_30653_MSG)) {
                notification.expire();
                callback.get();
            }
        }

        @Override
        public void register(@NotNull String s, @NotNull NotificationDisplayType notificationDisplayType) {}
        @Override
        public void register(@NotNull String s, @NotNull NotificationDisplayType notificationDisplayType, boolean b) {}
        @Override
        public void register(@NotNull String s, @NotNull NotificationDisplayType notificationDisplayType, boolean b, boolean b1) {}
    }

    /*
    // ------------------ Debugging utilities: -------------------------------------------------------------------------

    static void subscribeToActions(){
        // this subscribes to actions
        // but not to when a message is shown
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(AnActionListener.TOPIC, new MyActionListener());
    }


    static void createNotification(Project project) {
        // shows a notification just as LiveEdit shows warning messages
        NotificationGroup toolWindowGroup = NotificationGroup.findRegisteredGroup("LiveEdit");
        Notification notification = toolWindowGroup.createNotification("", "session: message", NotificationType.WARNING, null);
        notification.notify(project);
    }

    static class MyActionListener implements AnActionListener {
        @Override
        public void beforeActionPerformed(@NotNull AnAction action, @NotNull DataContext dataContext, AnActionEvent event) {
            final String text = action.toString() + " " + event.getPlace() + " " + event.getDataContext();
            System.out.println("MyActionListener: beforeActionPerformed: " + text);
        }
    }
*/
}