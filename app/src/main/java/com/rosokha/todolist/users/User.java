package com.rosokha.todolist.users;

import java.io.Serializable;

/**
 * Являє собою коритсувача
 */
public abstract class User implements Serializable {


    /**
     * Ідентифікатор користувача
     */
    private int id = 0;

    /**
     * ПІБ користувача
     */
    private String name;

    /**
     * Логін користувача
     */
    private String login;

    /**
     * Конструктор без параметрів
     */
    public User() {
    }

    /**
     * Конструктор з параметрами
     *
     * @param id    ідентифікатор користувача
     * @param name  ПІБ користувача
     * @param login логін користувача
     */
    public User(int id, String name, String login) {
        this.id = id;
        this.name = name;
        this.login = login;
    }

    /**
     * Оновлює логін керівника
     *
     * @param newLogin новий логін користувача
     */
    public abstract void updateLogin(String newLogin);

    /**
     * Оновлює ПІБ керівника
     *
     * @param newUserName новий ПІБ користувача
     */
    public abstract void updateUserName(String newUserName);

    /**
     * Getter для поля id
     *
     * @return ідентифікар користувача
     */
    public int getId() {
        return id;
    }

    /**
     * Setter для поля id
     *
     * @param id ідентифікатор користувача
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter для поля name
     *
     * @return ПІБ користувача
     */
    public String getName() {
        return name;
    }

    /**
     * Setter для поля name
     *
     * @param name ПІБ користувача
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter для поля login
     *
     * @return логін користувача
     */
    public String getLogin() {
        return login;
    }

    /**
     * Setter для поля login
     *
     * @param login логін користувача
     */
    public void setLogin(String login) {
        this.login = login;
    }
}