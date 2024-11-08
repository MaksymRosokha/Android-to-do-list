package com.rosokha.todolist.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.rosokha.todolist.R;
import com.rosokha.todolist.authorization.MainActivity;
import com.rosokha.todolist.dao.LoaderOfTasksDao;
import com.rosokha.todolist.dao.UserDao;
import com.rosokha.todolist.task.Task;
import com.rosokha.todolist.users.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Являє собою сервіс перевірки повідомлень для користувача
 *
 * @see Thread
 */
public class NotificationService extends Thread {

    // Ідентифікатор повідомлення
    private static int NOTIFY_ID = 1;

    // Ідентифікатор канала
    private static String CHANNEL_ID = "To Do List channel";

    /**
     * Користувач, для якого перевіряються повідомлення
     */
    User user;

    /**
     * Вікно, з якого викликаний клас
     */
    Context context;

    /**
     * Конструктор
     *
     * @param user користувач
     * @param context вікно, з якого викликаний клас
     */
    public NotificationService(User user, Context context){
        this.context = context;
        this.user = user;
    }

    /**
     * Викликається при початку роботи потоку
     */
    @Override
    public void run() {
        checkingNotifications();
        if(Thread.currentThread().isInterrupted()){
            return;
        }
        super.run();
    }

    /**
     * Перевіряє на наявність повідомлень кожні 5 секунд
     */
    private void checkingNotifications() {
        List<Task> tasks;
        while (true) {

            if(Thread.currentThread().isInterrupted()){
                return;
            }

            try {
                tasks = LoaderOfTasksDao.getListOfActiveTasks(user.getId());
            } catch (SQLException ex) {
                return;
            }

            for (Task task : tasks) {
                if (!task.isNotificationAboutCreatingTask()) {
                    sendNotification("Нове завдання", "У вас нове завдання:\n" + task.getName());
                    UserDao.markNotificationAboutCreatingTask(user, task, true);
                    task.setNotificationAboutCreatingTask(true);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!task.isNotificationTaskCompletionWarning() && task.getEndDateTime().minusDays(1).isBefore(LocalDateTime.now())) {
                        sendNotification("Попередження", "Залишилося менше 1 дня до закінчення завдання:\n" + task.getName());
                        UserDao.markNotificationTaskCompletionWarning(user, task, true);
                        task.setNotificationTaskCompletionWarning(true);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!task.isNotificationLateCompletionOfTask() && LocalDateTime.now().isAfter(task.getEndDateTime())) {
                        sendNotification("Завдання не виконано", "Завдання:\n" + task.getName() + "\nне виконане вчасно");
                        UserDao.markNotificationLateCompletionOfTask(user, task, true);
                        task.setNotificationLateCompletionOfTask(true);
                    }
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); //відновлення статусу перерваний*
                e.printStackTrace();
            }
        }
    }

    /**
     * Відправляє повідомлення
     * 
     * @param title заголовок повідомлення
     * @param content зміст повідомлення
     * @see NotificationCompat
     * @see NotificationManagerCompat
     * @see NotificationManagerCompat#notify(int, Notification)
     */
    private void sendNotification(String title, String content) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(content));


        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID++, builder.build());
    }
}