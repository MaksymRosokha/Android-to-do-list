package com.rosokha.todolist.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.rosokha.todolist.authorization.MainActivity;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;
import com.rosokha.todolist.users.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Клас пошуку
 */
public class SearchDao {

    /**
     * Об'єкт бази баних
     */
    private static SQLiteDatabase DB;

    static {
        DB = MainActivity.getDB();
    }

    /**
     * Шукає користувачів
     *
     * @param login логін, по якому здійснюється пошук
     * @return список знайдених користувачів
     * @throws SQLException the sql exception
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToNext() Cursor#moveToNext()
     */
    @SuppressLint("Range")
    public static List<User> findUsers(String login) throws SQLException {

        List<User> foundUsers = new ArrayList<>();
            Cursor query = DB.rawQuery("SELECT * " +
                    "                         FROM users " +
                    "                        WHERE login " +
                    "                         LIKE ? " +
                    "                          AND ? != '';",
                    new String[]{login + "%", login});

            while (query.moveToNext()) {
                if (query.getString(query.getColumnIndex("role")).equals("Leader")) {
                    Leader tempLeader = new Leader();
                    tempLeader.setId(query.getInt(query.getColumnIndex("id")));
                    tempLeader.setName(query.getString(query.getColumnIndex("name")));
                    tempLeader.setLogin(query.getString(query.getColumnIndex("login")));
                    foundUsers.add(tempLeader);
                } else {
                    Worker tempWorker = new Worker();
                    tempWorker.setId(query.getInt(query.getColumnIndex("id")));
                    tempWorker.setName(query.getString(query.getColumnIndex("name")));
                    tempWorker.setLogin(query.getString(query.getColumnIndex("login")));
                    foundUsers.add(tempWorker);
                }
            }
            query.close();

        return foundUsers;
    }
}