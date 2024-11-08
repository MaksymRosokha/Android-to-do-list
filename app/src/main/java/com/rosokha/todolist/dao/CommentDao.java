package com.rosokha.todolist.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.rosokha.todolist.authorization.MainActivity;
import com.rosokha.todolist.task.Comment;
import com.rosokha.todolist.task.Task;
import com.rosokha.todolist.users.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Клас коментаря, який працює з базою даних
 */
public class CommentDao {

    /**
     * Об'єкт бази даних
     */
    private static SQLiteDatabase DB;

    static {
        DB = MainActivity.getDB();
    }

    /**
     * Створює коментар
     *
     * @param task    завдання, до якого створюється коментар
     * @param user    користувач, який пише коментар
     * @param content зміст коментаря
     * @throws SQLException помилка при створенні коментаря
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     * @see Task
     * @see User
     */
    public static void createComment(Task task, User user, String content) throws SQLException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DB.execSQL(" INSERT INTO comments (content, date_of_writing, id_user, id_task)" +
                            "VALUES (?,?,?,?);",
                    new Object[]{content, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            user.getId(), task.getId()});
        }
    }

    /**
     * Повертає список коментарів до завдання
     *
     * @param task завдання до якого потрібно завантажити коментарі
     * @return список коментарів
     * @throws SQLException помилка при створенні списку коментарів
     * @see SQLException
     * @see Comment
     * @see Cursor
     * @see Cursor#moveToNext() Cursor#moveToNext()
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     * @see Task
     */
    @SuppressLint("Range")
    public static List<Comment> getListOfComments(Task task) throws SQLException {
        List<Comment> commentss = new ArrayList<>();
        Cursor query = DB.rawQuery("SELECT * " +
                "                         FROM comments " +
                "                        WHERE id_task = ?;",
                new String[]{task.getId() + ""});

        while (query.moveToNext()) {
            Comment comment = new Comment();
            comment.setId(query.getInt(query.getColumnIndex("id")));
            comment.setContent(query.getString(query.getColumnIndex("content")));
            comment.setTask(task);
            comment.setUser(UserDao.getUserById(query.getInt(query.getColumnIndex("id_user"))));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                comment.setDateTimeOfWriting(LocalDateTime.parse(query.getString(query.getColumnIndex("date_of_writing")),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            commentss.add(comment);
        }
        return commentss;
    }

    /**
     * Видаляє коментар
     *
     * @param comment коментар, який потрібно видалити
     * @throws SQLException помилка під час видалення коментаря
     * @see SQLException
     * @see SQLiteDatabase
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase#execSQL(String)
     * @see Comment
     */
    public static void deleteComment(Comment comment) throws SQLException {
        DB.execSQL("DELETE FROM comments " +
                "        WHERE id = ?;",
                new Integer[]{comment.getId()});
    }
}