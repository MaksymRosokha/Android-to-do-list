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
 * Клас працюючий з вибраними користувачами у базі даних
 */
public class LoaderOfFavoriteUsersDao {

    /**
     * Об'єкт бази даних
     */
    private static SQLiteDatabase DB;

    static {
        DB = MainActivity.getDB();
    }

    /**
     * Завантажує вибраних користувачів
     *
     * @param idCurrentUser id поточного користувача, для якого потрібно загрузити вибраних користувачів
     * @return список вибраних користувачів
     * @throws SQLException the sql exception
     * @see User
     * @see Leader
     * @see Worker
     * @see SQLException
     * @see Cursor
     * @see Cursor#moveToNext() Cursor#moveToNext()Cursor#moveToNext()Cursor#moveToNext()
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])SQLiteDatabase#rawQuery(String, String[])SQLiteDatabase#rawQuery(String, String[])
     */
    @SuppressLint("Range")
    public static List<User> loadFavoriteUsers(int idCurrentUser) throws SQLException{
        List<User> favoriteUsers = new ArrayList<>();

        try {
            Cursor query = DB.rawQuery("SELECT favorite_users.*, users.*" +
                    "                         FROM favorite_users " +
                    "                              JOIN users" +
                    "                              ON favorite_users.id_favorite_user = users.id" +
                    "                        WHERE favorite_users.id_current_user = " + idCurrentUser +
                    "                        GROUP BY favorite_users.id_favorite_user;",
                    null);


            while (query.moveToNext()) {

                if (query.getString(query.getColumnIndex("role")).equals("Leader")) {
                    Leader tempLeader = new Leader();
                    tempLeader.setId(query.getInt(query.getColumnIndex("id")));
                    tempLeader.setName(query.getString(query.getColumnIndex("name")));
                    tempLeader.setLogin(query.getString(query.getColumnIndex("login")));
                    favoriteUsers.add(tempLeader);
                } else {
                    Worker tempWorker = new Worker();
                    tempWorker.setId(query.getInt(query.getColumnIndex("id")));
                    tempWorker.setName(query.getString(query.getColumnIndex("name")));
                    tempWorker.setLogin(query.getString(query.getColumnIndex("login")));
                    favoriteUsers.add(tempWorker);
                }
            }
            query.close();
        } catch (SQLException ex){
            throw new SQLException();
        }
        return favoriteUsers;
    }
}
