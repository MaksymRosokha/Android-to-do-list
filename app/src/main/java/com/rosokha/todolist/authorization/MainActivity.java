package com.rosokha.todolist.authorization;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.AuthorizationDao;
import com.rosokha.todolist.dao.DatabaseHelper;
import com.rosokha.todolist.users.HomePageOfLeaderActivity;
import com.rosokha.todolist.users.HomePageOfWorkerActivity;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;
import com.rosokha.todolist.users.Worker;

import java.io.FileInputStream;


/**
 * Клас з якого починається запуск програми.
 * Представляє собою графічний користувацький інтерфейс з вибором авторизації
 *
 * @see AppCompatActivity
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Об'єкт представляючий собою допоміжний клас для бази даних
     */
    private static DatabaseHelper databaseHelper;

    /**
     * Об'єкт, що представляє собою базу даних
     */
    private static SQLiteDatabase DB;

    /**
     * Кнопка, при клацанні на яку здійснюється перехід до вікна входу у програму
     */
    private Button btnLogin;

    /**
     * Кнопка, при клацанні на яку здійснюється перехід до вікна реєстрації у програму
     */
    private Button btnSignUp;

    /**
     * Перевіряє чи вже був здійснений вхід з файлу
     */
    private static boolean isLoginFromFile = false;

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see AppCompatActivity#onCreate(Bundle)
     * @see AppCompatActivity#setContentView(View)
     * @see AppCompatActivity#setRequestedOrientation(int)
     * @see AppCompatActivity#getSupportActionBar()
     * @see MainActivity#inicialComponents()
     * @see DatabaseHelper
     * @see DatabaseHelper#create_db()
     * @see AppCompatActivity#getApplicationContext()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        inicialComponents();
        createNotificationChannel();

        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.create_db();
    }

    /**
     * Викликається при поверненні користувача в дане activity
     *
     * @see AppCompatActivity#onResume()
     * @see MainActivity#readUserFromFileAndLogIn()
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            DB = databaseHelper.open();
            readUserFromFileAndLogIn();
            isLoginFromFile = false;
        } catch (Exception exception) {
            Toast.makeText(this, "Не вдалося підключитися до бази даних", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Метод, який ініціалізує компоненти файлу activity_main.xml
     *
     * @see Intent
     * @see AppCompatActivity#startActivity(Intent)
     */
    private void inicialComponents() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener((View v) -> {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        });

        btnSignUp.setOnClickListener((View v) -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Читає файл з даними користувача, і здійснює вхід у програму
     */
    private void readUserFromFileAndLogIn() {
        try {
            FileInputStream fin = openFileInput("user.txt");
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            if (text.equals("")) {
                return;
            }
            String[] userData = text.split(AbleToAuthorization.separator);
            if (fin != null) {
                fin.close();
            }
            User user = AuthorizationDao.doLogIn(userData[0], userData[1]);
            if (!isLoginFromFile) {
                isLoginFromFile = true;
                if (user instanceof Leader) {
                    Intent intent = new Intent(MainActivity.this, HomePageOfLeaderActivity.class);
                    intent.putExtra(Leader.class.getSimpleName(), user);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, HomePageOfWorkerActivity.class);
                    intent.putExtra(Worker.class.getSimpleName(), user);
                    startActivity(intent);
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Створю канал для повідомлень
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "rosokha";
            String description = "To do list channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("To Do List channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    /**
     * Setter для об'єкта бази даних
     *
     * @return об 'єкт бази даних
     * @see DatabaseHelper
     */
    public static SQLiteDatabase getDB() {
        return DB;
    }

    /**
     * Викликається при знищенні activity
     *
     * @see AppCompatActivity#onDestroy()
     * @see DatabaseHelper
     * @see DatabaseHelper#close()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        DB.close();
    }
}