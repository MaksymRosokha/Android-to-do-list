package com.rosokha.todolist.users;

import android.database.SQLException;

import com.rosokha.todolist.dao.UserDao;

/**
 * Клас Leader представляє собою керівника, який створює завдання
 *
 * @see User
 */
public class Leader extends User {

    /**
     * Оновлює логін керівника
     *
     * @param newLogin новий логін
     * @throws SQLException помилка під час оновлення логіну
     * @see UserDao
     * @see UserDao#updateLogin(User, String)
     */
    @Override
    public void updateLogin(String newLogin) throws SQLException {
        UserDao.updateLogin(this, newLogin);
        setLogin(newLogin);
    }

    /**
     * Оновлює ПІБ керівника
     *
     * @param newUserName новий ПІБ
     * @throws SQLException помилка під час оновлення ПІБ
     * @see UserDao
     * @see UserDao#updateName(User, String)
     */
    @Override
    public void updateUserName(String newUserName) throws SQLException {
        UserDao.updateName(this, newUserName);
        setName(newUserName);
    }
}