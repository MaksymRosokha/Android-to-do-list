package com.rosokha.todolist.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Допоміжний клас для роботи з базою даних
 *
 * @see SQLiteOpenHelper
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Вікно програми
     */
    private Context myContext;

    /**
     * Назва бази даних
     */
    public static final String DB_NAME = "ToDoList.db";
    /**
     * Повний шлях до внутрішнього файлу бази даних
     */
    public static final String DB_PATH = "/data/data/com.rosokha.todolist/" + DB_NAME;
    /**
     * Версія бази даних
     */
    public static final int VERSION = 1;

    /**
     * Конструктор класу DatabaseHelper
     *
     * @param context вікно програми
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.myContext = context;


    }

    /**
     * Створює бд при необхідності
     *
     * @param db база даних
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * Оновлює бд
     *
     * @param db         база даних
     * @param oldVersion стара версія
     * @param newVersion нова версія
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Створює внутрішній файл бази даних
     */
    public void create_db() {

        File file = new File(DB_PATH);
        if (!file.exists()) {
            //Підключення локальної бд як потік
            try (InputStream myInput = myContext.getAssets().open(DB_NAME);
                 // Відкриваємо порожню бд
                 OutputStream myOutput = new FileOutputStream(DB_PATH)) {

                // Побайтово копіюються данні
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            } catch (IOException ex) {
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }

    /**
     * Відкриває внутрішню базу даних
     *
     * @return sq lite database
     * @throws SQLException          the sql exception
     * @throws IllegalStateException the illegal state exception
     */
    public SQLiteDatabase open() throws SQLException, IllegalStateException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
