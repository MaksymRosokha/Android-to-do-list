package com.rosokha.todolist.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.rosokha.todolist.authorization.MainActivity;
import com.rosokha.todolist.task.Task;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;
import com.rosokha.todolist.users.Worker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Представляє собою сутність, яка створює завдання і додає виконавців до нього
 */
public class TaskDao {

    /**
     * Об'єкт бази даних
     */
    private static SQLiteDatabase DB;

    static {
        DB = MainActivity.getDB();
    }

    /**
     * Створює завдання
     *
     * @param idCurrentUser ідентифікатор керівника, який створив завдання
     * @param nameTask      назва завдання
     * @param contentTask   зміст завдання
     * @param endDateTime   дата і час закінчення завдання
     * @throws SQLException помилка при створенні завдання
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createTask(int idCurrentUser, String nameTask, String contentTask, String endDateTime) throws SQLException {
        DB.execSQL(" INSERT INTO tasks (id_user_leader, name, content, date_of_publication, end_date, done) " +
                        "VALUES (?,?,?,?,?,?);",
                new Object[]{idCurrentUser, nameTask, contentTask,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), endDateTime, 0});
    }

    /**
     * Повертає ідентифікатор останнього створеного завдання
     *
     * @return ідентифікатор завдання
     * @throws SQLException помилка під час отримання ідентифікатору останнього завдання
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToFirst() Cursor#moveToFirst()
     */
    @SuppressLint("Range")
    public static int getIDLastTask() throws SQLException {
        Cursor query = DB.rawQuery("SELECT id" +
                "                         FROM tasks " +
                "                        ORDER BY id DESC LIMIT 1;",
                null);
        if (query.moveToFirst()) {
            return query.getInt(query.getColumnIndex("id"));
        } else {
            throw new SQLException();
        }
    }

    /**
     * Додає виконавців до завдання
     *
     * @param idTask          ідентифікатор завдання, до якого потрібно додати виконавців
     * @param executorsLogins логіни виконавців завдання
     * @throws SQLException помилка при додаванні виконавців до завдання
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     * @see TaskDao#getUserIDsByLogins(Set) TaskDao#getUserIDsByLogins(Set)
     */
    public static void addExecutors(int idTask, Set<String> executorsLogins) throws SQLException {
        List<Integer> idsExecutors = getUserIDsByLogins(executorsLogins);

        for (int idExecutor : idsExecutors) {
            DB.execSQL("INSERT INTO executors(id_user, id_task)" +
                    "       VALUES (?,?);",
                    new Object[]{idExecutor, idTask});
        }
    }

    /**
     * Знаходить завдання по ідентифікатору
     *
     * @param id ідентифікатор завдання
     * @return завдання task by id
     * @throws SQLException помилка під час отримання завдання по ідентифікатору
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToFirst() Cursor#moveToFirst()
     * @see Task
     */
    @SuppressLint("Range")
    public static Task getTaskByID(int id) throws SQLException {
        Task task = new Task();

        Cursor query = DB.rawQuery("SELECT * " +
                "                         FROM tasks " +
                "                        WHERE id = ?;",
                new String[]{id + ""});

        if (query.moveToFirst()) {
            task.setId(id);
            task.setName(query.getString(query.getColumnIndex("name")));
            task.setContent(query.getString(query.getColumnIndex("content")));
            task.setDone(query.getInt(query.getColumnIndex("done")) == 1);
            task.setLeader(UserDao.getLeaderById(query.getInt(query.getColumnIndex("id_user_leader"))));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                task.setDateTimeOfPublication(LocalDateTime.parse(query.getString(query.getColumnIndex("date_of_publication")),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                task.setEndDateTime(LocalDateTime.parse(query.getString(query.getColumnIndex("end_date")),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                if (query.getString(query.getColumnIndex("execution_date")) != null) {
                    task.setDateTimeExecution(LocalDateTime.parse(query.getString(query.getColumnIndex("execution_date")),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } else {
                    task.setDateTimeExecution(null);
                }
            }
        }
        return task;
    }

    /**
     * Оновлює дані завдання у бд
     *
     * @param task завдання яке потрібно оновити
     * @throws SQLException помилка під час оновлення завдання
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     * @see Task
     */
    public static void updateTask(Task task) throws SQLException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DB.execSQL("UPDATE tasks " +
                    "          SET name = ?, content = ?, end_date = ? " +
                    "        WHERE id = ?",
                    new Object[]{task.getName(), task.getContent(),
                    task.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), task.getId()});
        } else {
            throw new SQLException();
        }
    }

    /**
     * Видаляє завдання
     *
     * @param task завдання яке потрібно видалити
     * @throws SQLException помилка під час видалення завдання
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     * @see Task
     */
    public static void deleteTask(Task task) throws SQLException {
        DB.execSQL("DELETE FROM tasks " +
                "             WHERE id = ?;",
                new Object[]{task.getId()});
    }

    /**
     * Створює список ідентифікаторів виконавців завдання
     *
     * @param executorsLogins логіни, по яким створюєсться список з id виконавців
     * @return список з ідентифікаторами виконавців
     * @throws SQLException помилка при отриманні id
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToFirst()
     * @see Task
     */
    @SuppressLint("Range")
    private static List<Integer> getUserIDsByLogins(Set<String> executorsLogins) throws SQLException {
        List<Integer> ids = new ArrayList<>();

        for (String login : executorsLogins) {
            Cursor query = DB.rawQuery("SELECT id" +
                    "                         FROM users " +
                    "                        WHERE login = ?;",
                    new String[]{login});
            if (query.moveToFirst()) {
                ids.add(query.getInt(query.getColumnIndex("id")));
            }
        }
        return ids;
    }

    /**
     * Встановлює статус виконання для завдання
     *
     * @param task   завдання
     * @param isDone виконане?
     * @throws SQLException помилка при зміні статусу виконання завдання
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     * @see Task
     */
    public static void setDone(Task task, boolean isDone) throws SQLException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isDone) {
                DB.execSQL("UPDATE tasks " +
                        "          SET done = ?, execution_date = ? " +
                        "        WHERE id = ?;",
                        new Object[]{1,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        task.getId()});
            } else {
                DB.execSQL("UPDATE tasks " +
                        "          SET done = ?, execution_date = ? " +
                        "        WHERE id = ?;",
                        new Object[]{0, null, task.getId()});
            }
        } else {
            throw new SQLException();
        }
    }

    /**
     * Створює список виконавців завдання
     *
     * @param task завдання
     * @return список виконавців
     * @throws SQLException промилка при створенні списку виконавців
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see Cursor
     * @see Cursor#moveToNext() Cursor#moveToNext()
     * @see Task
     * @see User
     * @see Leader
     * @see Worker
     */
    @SuppressLint("Range")
    public static Set<User> getListOfExecutorsForTask(Task task) throws SQLException {
        Set<User> executors = new LinkedHashSet<>();

        Cursor query = DB.rawQuery("SELECT users.* " +
                "                         FROM users " +
                "                              JOIN executors " +
                "                              ON users.id = executors.id_user " +
                "                                                              " +
                "                              JOIN tasks " +
                "                              ON tasks.id = executors.id_task " +
                "                         WHERE tasks.id = ?;",
                new String[]{task.getId() + ""});

        while (query.moveToNext()) {
            User user;
            if (query.getString(query.getColumnIndex("role")).equals("Leader")) {
                user = new Leader();
                user.setId(query.getInt(query.getColumnIndex("id")));
                user.setName(query.getString(query.getColumnIndex("name")));
                user.setLogin(query.getString(query.getColumnIndex("login")));
            } else {
                user = new Worker();
                user.setId(query.getInt(query.getColumnIndex("id")));
                user.setName(query.getString(query.getColumnIndex("name")));
                user.setLogin(query.getString(query.getColumnIndex("login")));
            }
            executors.add(user);
        }
        query.close();
        return executors;
    }

    /**
     * Видаляє виконавці завдання по ідентифікатору
     *
     * @param idExecutor ідентифікатор виконавця
     * @param idTask     ідентифікатор завдання
     * @throws SQLException помилка під час видалення виконавці
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     */
    public static void deleteExecutorById(int idExecutor, int idTask) throws SQLException {
        DB.execSQL("DELETE FROM executors " +
                "             WHERE id_user = ? " +
                "               AND id_task = ?;",
                new Integer[]{idExecutor, idTask});
    }
}