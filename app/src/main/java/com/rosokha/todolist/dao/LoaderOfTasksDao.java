package com.rosokha.todolist.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.rosokha.todolist.authorization.MainActivity;
import com.rosokha.todolist.task.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Клас який завантажує завдання з бази даних
 */
public class LoaderOfTasksDao {

    /**
     * Об'єкт бази даних
     */
    private static SQLiteDatabase DB;

    static {
        DB = MainActivity.getDB();
    }

    /**
     * Повертає список активних завдань
     *
     * @param idCurrentUser ідентифікатор користувача, для якого потрібно створити список завдань
     * @return список активних завдань
     * @throws SQLException помилка при створенні списку активних завдань
     * @see SQLException
     * @see Task
     * @see Cursor
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see LoaderOfTasksDao#getListOfTasks(Cursor) LoaderOfTasksDao#getListOfTasks(Cursor)
     */
    @SuppressLint("Range")
    public static List<Task> getListOfActiveTasks(int idCurrentUser) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        Cursor query = DB.rawQuery("SELECT tasks.*, executors.*" +
                        "                 FROM tasks " +
                        "                      JOIN executors" +
                        "                      ON tasks.id = executors.id_task" +
                        "                WHERE executors.id_user = ?" +
                        "                  AND tasks.done = 0;",
                new String[]{idCurrentUser + ""});

        while (query.moveToNext()) {
            Task task = new Task();
            task.setId(query.getInt(query.getColumnIndex("id")));
            task.setName(query.getString(query.getColumnIndex("name")));
            task.setContent(query.getString(query.getColumnIndex("content")));
            task.setDone(query.getInt(query.getColumnIndex("done")) == 1);
            task.setLeader(UserDao.getLeaderById(query.getInt(query.getColumnIndex("id_user_leader"))));
            task.setExecutors(TaskDao.getListOfExecutorsForTask(task));
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
            boolean f = query.getInt(query.getColumnIndex("about_creating_task")) == 1;
            boolean s = query.getInt(query.getColumnIndex("task_completion_warning")) == 1;
            boolean t = query.getInt(query.getColumnIndex("late_completion_of_task")) == 1;
            task.setNotificationAboutCreatingTask(f);
            task.setNotificationTaskCompletionWarning(s);
            task.setNotificationLateCompletionOfTask(t);

            tasks.add(task);
        }

        return tasks;
    }

    /**
     * Повертає список виконаних завдань
     *
     * @param idCurrentUser ідентифікатор користувача, для якого потрібно створити список завдань
     * @return список виконаних завдань
     * @throws SQLException помилка при створенні списку виконаних завдань
     * @see SQLException
     * @see Task
     * @see Cursor
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see LoaderOfTasksDao#getListOfTasks(Cursor) LoaderOfTasksDao#getListOfTasks(Cursor)
     */
    public static List<Task> getListOfСompletedTasks(int idCurrentUser) throws SQLException {
        List<Task> tasks = null;
        Cursor query = DB.rawQuery("SELECT tasks.*" +
                        "                 FROM tasks " +
                        "                      JOIN executors" +
                        "                      ON tasks.id = executors.id_task" +
                        "                WHERE executors.id_user = ?" +
                        "                  AND tasks.done = 1;",
                new String[]{idCurrentUser + ""});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tasks = getListOfTasks(query);
        }
        return tasks;
    }

    /**
     * Повертає список власних завдань
     *
     * @param idCurrentUser ідентифікатор користувача, для якого потрібно створити список завдань
     * @return список створених завдань
     * @throws SQLException помилка при створенні списку завдань
     * @see SQLException
     * @see Task
     * @see Cursor
     * @see SQLiteDatabase
     * @see SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase#rawQuery(String, String[])
     * @see LoaderOfTasksDao#getListOfTasks(Cursor) LoaderOfTasksDao#getListOfTasks(Cursor)
     */
    public static List<Task> getListOfСreatedTasks(int idCurrentUser) throws SQLException {
        List<Task> tasks = null;

        Cursor query = DB.rawQuery("SELECT tasks.*" +
                        "                 FROM tasks " +
                        "                      JOIN users" +
                        "                      ON tasks.id_user_leader = users.id" +
                        "                WHERE users.id = ?" +
                        "                  AND users.role = 'Leader';",
                new String[]{idCurrentUser + ""});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tasks = getListOfTasks(query);
        }
        return tasks;
    }

    /**
     * Створює і повертає список завдання із запиту
     *
     * @param query запит до бази дпних
     * @return список завдань
     * @throws SQLException помилка під час створення списку завдань
     * @see SQLException
     * @see Task
     * @see Cursor
     * @see Cursor#moveToNext()
     */
    @SuppressLint("Range")
    private static List<Task> getListOfTasks(Cursor query) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        while (query.moveToNext()) {
            Task task = new Task();
            task.setId(query.getInt(query.getColumnIndex("id")));
            task.setName(query.getString(query.getColumnIndex("name")));
            task.setContent(query.getString(query.getColumnIndex("content")));
            task.setDone(query.getInt(query.getColumnIndex("done")) == 1);
            task.setLeader(UserDao.getLeaderById(query.getInt(query.getColumnIndex("id_user_leader"))));
            task.setExecutors(TaskDao.getListOfExecutorsForTask(task));
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

            tasks.add(task);
        }
        return tasks;
    }
}