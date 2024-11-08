package com.rosokha.todolist.task;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.DrawableRes;

import com.rosokha.todolist.R;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Являє собою обє'кт завдання
 *
 * @see Serializable
 */
public class Task implements Serializable {

    /**
     * Ідентифікатор завдання
     */
    private int id;

    /**
     * Назва завдання
     */
    private String name;

    /**
     * Зміст завдання
     */
    private String content;

    /**
     * Дата і час публікації
     */
    private LocalDateTime dateTimeOfPublication;

    /**
     * Дата і час, до якого потрібно виконати завдання
     */
    private LocalDateTime endDateTime;

    /**
     * Дата і час, в який виконано завдання
     */
    private LocalDateTime dateTimeExecution;

    /**
     * Керівник завдання
     */
    private Leader leader;

    /**
     * Чи є завдання виконаним?
     */
    private boolean isDone;

    /**
     * Колекція з коментарями до завдання
     */
    private List<Comment> comments = null;

    /**
     * Список виконавців
     */
    private Set<User> executors = null;

    /**
     * Повідомлення про створення завдання
     */
    private boolean notificationAboutCreatingTask = true;

    /**
     * Повідомлення з попередженням
     */
    private boolean notificationTaskCompletionWarning = true;

    /**
     * Повідомлення про закінчення завдання
     */
    private boolean notificationLateCompletionOfTask = true;

    /**
     * Конструктор
     *
     * @param name    назва завдання
     * @param content зміст завдання
     * @param date    дата, до якої потрібно виконати завдання
     */
    public Task(String name, String content, LocalDateTime date) {
        this.name = name;
        this.content = content;
        this.endDateTime = date;
    }

    /**
     * Конструктор
     */
    public Task() {
    }

    /**
     * Повертає колір завдання, в залежності від дати закінчення і виконання
     *
     * @param tabPosition позиція сторінки яку переглядає користувач
     * @return колір завдання
     * @see Color
     * @see LocalDateTime#isAfter(ChronoLocalDateTime) LocalDateTime#isAfter(ChronoLocalDateTime)
     * @see LocalDateTime#isBefore(ChronoLocalDateTime) LocalDateTime#isBefore(ChronoLocalDateTime)
     */
    public int getColorTask(@DrawableRes int tabPosition) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (tabPosition == 2) {
                if (!isDone) {
                    if (LocalDateTime.now().isAfter(endDateTime)) {
                        return R.drawable.round_red_task;
                    }
                    if (LocalDateTime.now().isBefore(endDateTime)) {
                        return R.drawable.round_green_task;
                    }
                }
                if (isDone) {
                    if (dateTimeExecution.isAfter(endDateTime)) {
                        return R.drawable.round_red_task;
                    }
                    if (dateTimeExecution.isBefore(endDateTime)) {
                        return R.drawable.round_green_task;
                    }
                }
            }

            if (!isDone) {
                if (LocalDateTime.now().isAfter(endDateTime)) {
                    return R.drawable.round_red_task;
                }
                if (endDateTime.minusDays(1).isBefore(LocalDateTime.now())) {
                    return R.drawable.round_yellow_tasl;
                }
                if (LocalDateTime.now().isBefore(endDateTime)) {
                    return R.drawable.round_green_task;
                }
            }
            if (isDone) {
                if (dateTimeExecution.isAfter(endDateTime)) {
                    return R.drawable.round_red_task;
                }
                if (dateTimeExecution.isBefore(endDateTime)) {
                    return R.drawable.round_green_task;
                }
            }
        }
        return 0;
    }

    /**
     * Getter для поля leader
     *
     * @return керівника, який створив завдання
     * @see Leader
     */
    public Leader getLeader() {
        return leader;
    }

    /**
     * Setter для поля leader
     *
     * @param leader керівник, який створив завдання
     * @see Leader
     */
    public void setLeader(Leader leader) {
        this.leader = leader;
    }

    /**
     * Getter для поля dateTimeExecution
     *
     * @return дату виконання
     */
    public LocalDateTime getDateTimeExecution() {
        return dateTimeExecution;
    }

    /**
     * Setter для поля dateTimeExecution
     *
     * @param dateTimeExecution дата і час виконання
     */
    public void setDateTimeExecution(LocalDateTime dateTimeExecution) {
        this.dateTimeExecution = dateTimeExecution;
    }

    /**
     * Getter для поля isDone
     *
     * @return чи є завдання виконаним?
     */
    public boolean getDone() {
        return isDone;
    }

    /**
     * Setter для поля isDone
     *
     * @param isDone чи є завдання виконаним?
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Getter для поля comments
     *
     * @return список коментарів
     * @see Comment
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * Setter для поля comments
     *
     * @param comments список коментарів
     * @see Comment
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Getter для поля id
     *
     * @return ідентифікатор завдання
     */
    public int getId() {
        return id;
    }

    /**
     * Setter для поля id
     *
     * @param id ідентифікатор завдання
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter для поля name
     *
     * @return назву завдання
     */
    public String getName() {
        return name;
    }

    /**
     * Setter для поля name
     *
     * @param name назва завдання
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter для поля content
     *
     * @return зміст завдання
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter для поля content
     *
     * @param content зміст завдання
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Getter для поля endDateTime
     *
     * @return дату закінчення виконання завдання
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Setter для поля endDateTime
     *
     * @param endDateTime дата закінчення виконання завдання
     */
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * Getter для поля dateTimeOfPublication
     *
     * @return дату публікації завдання
     */
    public LocalDateTime getDateTimeOfPublication() {
        return dateTimeOfPublication;
    }

    /**
     * Setter для поля dateTimeOfPublication
     *
     * @param dateTimeOfPublication дата публікації завдання
     */
    public void setDateTimeOfPublication(LocalDateTime dateTimeOfPublication) {
        this.dateTimeOfPublication = dateTimeOfPublication;
    }

    /**
     * Getter для поля executors
     *
     * @return список виконавців завдання
     * @see User
     */
    public Set<User> getExecutors() {
        return executors;
    }

    /**
     * Setter для поля executors
     *
     * @param executors список виконавців завдання
     * @see User
     */
    public void setExecutors(Set<User> executors) {
        this.executors = executors;
    }

    /**
     * Getter для поля notificationAboutCreatingTask
     *
     * @return чи відправлене повідомлення про створення завдання
     */
    public boolean isNotificationAboutCreatingTask() {
        return notificationAboutCreatingTask;
    }

    /**
     * Setter для поля notificationAboutCreatingTask
     *
     * @param notificationAboutCreatingTask повідомлення про створення завдання
     */
    public void setNotificationAboutCreatingTask(boolean notificationAboutCreatingTask) {
        this.notificationAboutCreatingTask = notificationAboutCreatingTask;
    }

    /**
     * Getter для поля notificationTaskCompletionWarning
     *
     * @return чи відправлене повідомлення з попередженням
     */
    public boolean isNotificationTaskCompletionWarning() {
        return notificationTaskCompletionWarning;
    }

    /**
     * Setter для поля notificationTaskCompletionWarning
     *
     * @param notificationTaskCompletionWarning повідомлення з попередженням
     */
    public void setNotificationTaskCompletionWarning(boolean notificationTaskCompletionWarning) {
        this.notificationTaskCompletionWarning = notificationTaskCompletionWarning;
    }

    /**
     * Getter для поля notificationLateCompletionOfTask
     *
     * @return чи відправлене повідомлення про не виконання завдання
     */
    public boolean isNotificationLateCompletionOfTask() {
        return notificationLateCompletionOfTask;
    }

    /**
     * Setter для поля notificationLateCompletionOfTask
     *
     * @param notificationLateCompletionOfTask повідомлення про не виконання завдання
     */
    public void setNotificationLateCompletionOfTask(boolean notificationLateCompletionOfTask) {
        this.notificationLateCompletionOfTask = notificationLateCompletionOfTask;
    }
}