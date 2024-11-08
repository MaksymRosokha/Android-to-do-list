package com.rosokha.todolist.dao;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.rosokha.todolist.authorization.MainActivity;

/**
 * Клас перегляду профілю, який працює з базою даних
 */
public class ViewProfileDao {

    /**
     * Об'єкт бази даних
     */
    private static SQLiteDatabase DB;


    static {
        DB = MainActivity.getDB();
    }

    /**
     * Додає користувача до вибраних
     *
     * @param idCurrentUser  ідентифікатор поточного користувача
     * @param idFavoriteUser ідентифікатор вибраного користувача
     * @throws SQLException помилка при додаванні користувача
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     */
    public static void addFavoriteUser(int idCurrentUser, int idFavoriteUser) throws SQLException {
        DB.execSQL("INSERT INTO favorite_users (id_current_user, id_favorite_user) " +
                "       VALUES (?, ?);",
                new Integer[]{idCurrentUser, idFavoriteUser});
    }

    /**
     * Видаляє користувача до вибраних
     *
     * @param idCurrentUser  ідентифікатор поточного користувача
     * @param idFavoriteUser ідентифікатор вибраного користувача
     * @throws SQLException помилка при видаленні користувача
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     */
    public static void deleteFavoriteUser(int idCurrentUser, int idFavoriteUser) throws SQLException {
        DB.execSQL("DELETE FROM favorite_users " +
                "             WHERE id_current_user = ? " +
                "               AND id_favorite_user = ?;",
                new Integer[]{idCurrentUser, idFavoriteUser});
    }

    /**
     * Перевіряє унікальність логіну користувача у базі даних без урахування старого логіну користувача
     *
     * @param newLogin     новий логін
     * @param currentLogin поточний логін
     * @return поверта true якщо такого логіне ще не має. Повертає false якщо такий логін вже існує
     * @throws SQLException помилка при видаленні користувача
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     */
    public static boolean validationLoginWithoutCurrentUser(String newLogin, String currentLogin) throws SQLException{
        Cursor query = DB.rawQuery("SELECT login " +
                        "                 FROM users " +
                        "                WHERE login = ? " +
                        "                  AND login != ?;",
                new String[]{newLogin, currentLogin});

        return !query.moveToFirst();
    }

    /**
     * Перевіряє чи є аккаунт користувача, профіль якого переглядається, у вибраних поточного користувача
     *
     * @param idCurrentUser  ідентифікатор поточного користувача
     * @param idFavoriteUser ідентифікатор вибраного користувача
     * @return поверта true якщо користувач є у вибраних. Повертає false якщо користувача немає у вибраних
     * @throws SQLException помилка при видаленні користувача
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     */
    public static boolean checkUserForFavorite(int idCurrentUser, int idFavoriteUser) throws SQLException{
        Cursor query = DB.rawQuery("SELECT favorite_users.* " +
                "                         FROM favorite_users " +
                "                        WHERE favorite_users.id_current_user = ? " +
                "                          AND favorite_users.id_favorite_user = ?;",
                new String[]{idCurrentUser + "", idFavoriteUser+""});

        return query.moveToFirst();
    }
}
