package com.rosokha.todolist.task;

import com.rosokha.todolist.users.User;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Клас Comment представляє собою коментар до завдання
 *
 * @see Serializable
 */
public class Comment implements Serializable {


    /**
     * Ідентифікатор коментаря
     */
    private int id;

    /**
     * Текст коментаря
     */
    private String content;

    /**
     * Дата написання коментаря
     */
    private LocalDateTime dateTimeOfWriting;

    /**
     * Користувач, який відправив коментар
     */
    private User user;

    /**
     * Завдання до якого написаний коментар
     */
    private Task task;

    /**
     * Конструктор з пустими параметрами
     */
    public Comment() {
    }

    /**
     * Конструктор зі всіма параметрами
     *
     * @param id                ідентифікатор
     * @param content           зміст
     * @param dateTimeOfWriting дата написання
     * @param user              користувач, який написав коментар
     * @param task              завдання, до якого написаний коментар
     */
    public Comment(int id, String content, LocalDateTime dateTimeOfWriting, User user, Task task) {
        this.id = id;
        this.content = content;
        this.dateTimeOfWriting = dateTimeOfWriting;
        this.user = user;
        this.task = task;
    }

    /**
     * Getter для поля id
     *
     * @return повертає ідентифікатор коментаря
     */
    public int getId() {
        return id;
    }

    /**
     * Setter для id
     *
     * @param id ідентифікатор коментаря
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter для поля content
     *
     * @return повертає текст коментаря
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter для поля content
     *
     * @param content зміст коментаря
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Getter для поля dateTimeOfWriting
     *
     * @return повертає дату написання
     */
    public LocalDateTime getDateTimeOfWriting() {
        return dateTimeOfWriting;
    }

    /**
     * Setter для поля dateTimeOfWriting
     *
     * @param dateTimeOfWriting дата написання
     */
    public void setDateTimeOfWriting(LocalDateTime dateTimeOfWriting) {
        this.dateTimeOfWriting = dateTimeOfWriting;
    }

    /**
     * Getter для поля task
     *
     * @return повертає завдання, до якого написаний коментар
     * @see Task
     */
    public Task getTask() {
        return task;
    }

    /**
     * Setter для поля task
     *
     * @param task азавдання, до якого написаний коментар
     * @see Task
     */
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * Getter для поля user
     *
     * @return повертає користувача, який написав коментар
     * @see User
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter для поля user
     *
     * @param user користувач, який написав коментар
     * @see User
     */
    public void setUser(User user) {
        this.user = user;
    }
}