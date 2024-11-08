package com.rosokha.todolist.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.rosokha.todolist.authorization.LogInActivity;
import com.rosokha.todolist.authorization.MainActivity;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;
import com.rosokha.todolist.users.Worker;

/**
 * Клас авторизації, який працює з базою даних
 */
public class AuthorizationDao {

    /**
     * Об'єкт бази даних
     */
    private static SQLiteDatabase DB;


    static {
        DB = MainActivity.getDB();
    }

    /**
     * Реєструє користувача вносячи дані у базу даних
     *
     * @param name     ПІБ користувача
     * @param login    логін користувача
     * @param password пароль користувача
     * @param role     роль користувача
     * @throws SQLException помилка під час реєстрації
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String)
     * @see SQLException
     */
    public static void doRegister(String name, String login, String password, String role) throws SQLException {
        DB.execSQL("INSERT INTO users (name, login, password, role) " +
                        "       VALUES (?,?,?,?)",
                new String[]{name, login, password, role});
    }


    /**
     * Перевіряє унікальність логіну користувача у базі даних
     *
     * @param login the login
     * @return поверта true, якщо такого логіну ще не має. Повертає false якщо такий логін вже існує
     * @throws SQLException the sql exception
     * @see Cursor
     * @see Cursor#moveToFirst() Cursor#moveToFirst()
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see SQLException
     */
    public static boolean checkForLoginUniqueness(String login) throws SQLException {
        Cursor query = DB.rawQuery("SELECT login" +
                        "                 FROM users" +
                        "                WHERE login = ?;",
                new String[]{login});

        return query.moveToFirst() ? false : true;
    }

    /**
     * Здійснює вхід користувача
     *
     * @param login    логін користувача
     * @param password пароль користувача
     * @return користувача user
     * @throws SQLException помилка під час входу
     * @see LogInActivity#checkData() LogInActivity#checkData()
     * @see Cursor
     * @see Cursor#moveToFirst() Cursor#moveToFirst()
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see SQLException
     */
    @SuppressLint("Range")
    public static User doLogIn(String login, String password) throws SQLException {
        Cursor query = DB.rawQuery("SELECT *" +
                        "                 FROM users" +
                        "                WHERE login = ?" +
                        "                  AND password = ?" +
                        "                LIMIT 1;",
                new String[]{login, password});

        if (query.moveToFirst()) {
            if (query.getString(query.getColumnIndex("role")).equals("Leader")) {
                Leader leader = new Leader();
                leader.setId(query.getInt(query.getColumnIndex("id")));
                leader.setName(query.getString(query.getColumnIndex("name")));
                leader.setLogin(query.getString(query.getColumnIndex("login")));
                query.close();
                return leader;
            }
            if (query.getString(query.getColumnIndex("role")).equals("Worker")){
                Worker worker = new Worker();
                worker.setId(query.getInt(query.getColumnIndex("id")));
                worker.setName(query.getString(query.getColumnIndex("name")));
                worker.setLogin(query.getString(query.getColumnIndex("login")));
                query.close();
                return worker;
            }
        } else {
            throw new SQLException();
        }
        return null;
    }
}