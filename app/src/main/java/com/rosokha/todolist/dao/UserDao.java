package com.rosokha.todolist.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.rosokha.todolist.authorization.MainActivity;
import com.rosokha.todolist.task.Task;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;
import com.rosokha.todolist.users.Worker;

/**
 * Користувач взаємодіючий з базою даних
 */
public class UserDao {

    /**
     * Об'єкт бази даних
     */
    private static SQLiteDatabase DB;

    static {
        DB = MainActivity.getDB();
    }

    /**
     * Оновлює логін користувача
     *
     * @param user     користувач, якому потрібно оновити логін
     * @param newLogin новий логін
     * @throws SQLException помилка під час оновлення
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     */
    public static void updateLogin(User user, String newLogin) throws SQLException {
        DB.execSQL("UPDATE users " +
                        "  SET login = ? " +
                        "WHERE id = ?;",
                new String[]{newLogin, user.getId() + ""});
    }

    /**
     * Оновлює ПІБ користувача
     *
     * @param user    користувач, якому потрібно оновити ПІБ
     * @param newName новий ПШБ
     * @throws SQLException помилка під час оновлення
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     */
    public static void updateName(User user, String newName) throws SQLException {
        DB.execSQL("UPDATE users " +
                        "  SET name = ? " +
                        "WHERE id = ?;",
                new String[]{newName, user.getId() + ""});
    }


    /**
     * Завантажує користувача по ідентифікатору
     *
     * @param id ідентифікатор користувача
     * @return користувача user by id
     * @throws SQLException помилка при отриманні користувача
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToFirst() Cursor#moveToFirst()
     * @see User
     */
    @SuppressLint("Range")
    public static User getUserById(int id) throws SQLException {
        User user = null;

        Cursor query = DB.rawQuery("SELECT * " +
                        "                 FROM users " +
                        "                WHERE id = ?;",
                new String[]{id + ""});

        if (query.moveToFirst()) {
            if (query.getString(query.getColumnIndex("role")).equals("Leader")) {
                user = new Leader();
                user.setId(id);
                user.setLogin(query.getString(query.getColumnIndex("login")));
                user.setName(query.getString(query.getColumnIndex("name")));
            } else {
                user = new Worker();
                user.setId(id);
                user.setLogin(query.getString(query.getColumnIndex("login")));
                user.setName(query.getString(query.getColumnIndex("name")));
            }
        } else {
            throw new SQLException();
        }
        return user;
    }

    /**
     * Завантажує керівника по ідентифікатору
     *
     * @param id ідентиифікатор керівника
     * @return керівника leader by id
     * @throws SQLException помилка при отриманні керівника
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToFirst() Cursor#moveToFirst()
     * @see Leader
     */
    @SuppressLint("Range")
    public static Leader getLeaderById(int id) throws SQLException {
        Leader leader = null;

        Cursor query = DB.rawQuery("SELECT * " +
                        "                 FROM users " +
                        "                WHERE id = ?" +
                        "                  AND role = 'Leader';",
                new String[]{id + ""});

        if (query.moveToFirst()) {
            leader = new Leader();
            leader.setId(id);
            leader.setLogin(query.getString(query.getColumnIndex("login")));
            leader.setName(query.getString(query.getColumnIndex("name")));
        }
        return leader;
    }

    /**
     * Завантажує працівника по ідентифікатору
     *
     * @param id ідентиифікатор працівника
     * @return працівника worker by id
     * @throws SQLException помилка при отриманні працівника
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToFirst() Cursor#moveToFirst()
     * @see Worker
     */
    @SuppressLint("Range")
    public static Worker getWorkerById(int id) throws SQLException {
        Worker worker = null;

        Cursor query = DB.rawQuery("SELECT * " +
                        "                 FROM users " +
                        "                WHERE id = ?" +
                        "                  AND role = 'Worker';",
                new String[]{id + ""});
        if (query.moveToFirst()) {
            worker = new Worker();
            worker.setId(id);
            worker.setLogin(query.getString(query.getColumnIndex("login")));
            worker.setName(query.getString(query.getColumnIndex("name")));
        }
        return worker;
    }

    /**
     * Завантажує користувача по логіну
     *
     * @param login логін користувача
     * @return користувача
     * @throws SQLException помилка при отриманні користувача
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToFirst()
     * @see User
     */
    @SuppressLint("Range")
    public static User getUserByLogin(String login) throws SQLException {
        User user = null;

        Cursor query = DB.rawQuery("SELECT * " +
                        "                 FROM users " +
                        "                WHERE login = ?;",
                new String[]{login});

        if (query.moveToFirst()) {
            if (query.getString(query.getColumnIndex("role")).equals("Leader")) {
                user = new Leader();
                user.setId(query.getInt(query.getColumnIndex("id")));
                user.setLogin(login);
                user.setName(query.getString(query.getColumnIndex("name")));
            } else {
                user = new Worker();
                user.setId(query.getInt(query.getColumnIndex("id")));
                user.setLogin(login);
                user.setName(query.getString(query.getColumnIndex("name")));
            }
        } else {
            throw new SQLException();
        }
        return user;
    }

    /**
     * Позначає підправлення повідомлення про нове завдання
     *
     * @param executor виконавець, якому повинно прийти повідомлення
     * @param task завдання, про яке ідеться в повідомленні
     * @param isSent чи воно відправлене?
     * @throws SQLException помилка при позначення повідомлення
     */
    public static void markNotificationAboutCreatingTask(User executor, Task task, boolean isSent) throws SQLException {
        DB.execSQL("UPDATE executors" +
                        "  SET about_creating_task = ?" +
                        "WHERE id_user = ?" +
                        "  AND id_task = ?;",
                new Object[]{isSent ? 1 : 0, executor.getId(), task.getId()});
    }

    /**
     * Позначає підправлення повідомлення з попередженням про невелику кількість часу до кінця завдання
     *
     * @param executor виконавець, якому повинно прийти повідомлення
     * @param task завдання, про яке ідеться в повідомленні
     * @param isSent чи воно відправлене?
     * @throws SQLException помилка при позначення повідомлення
     */
    public static void markNotificationTaskCompletionWarning(User executor, Task task, boolean isSent) throws SQLException {
        DB.execSQL("UPDATE executors" +
                        "  SET task_completion_warning = ?" +
                        "WHERE id_user = ?" +
                        "  AND id_task = ?;",
                new Object[]{isSent ? 1 : 0, executor.getId(), task.getId()});
    }

    /**
     * Позначає підправлення повідомлення про несвоєчасне виконання завдання
     *
     * @param executor виконавець, якому повинно прийти повідомлення
     * @param task завдання, про яке ідеться в повідомленні
     * @param isSent чи воно відправлене?
     * @throws SQLException помилка при позначення повідомлення
     */
    public static void markNotificationLateCompletionOfTask(User executor, Task task, boolean isSent) throws SQLException {
        DB.execSQL("UPDATE executors" +
                        "  SET late_completion_of_task = ?" +
                        "WHERE id_user = ?" +
                        "  AND id_task = ?;",
                new Object[]{isSent ? 1 : 0, executor.getId(), task.getId()});
    }
}