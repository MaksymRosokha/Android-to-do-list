package com.rosokha.todolist.authorization;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.AuthorizationDao;
import com.rosokha.todolist.users.HomePageOfLeaderActivity;
import com.rosokha.todolist.users.HomePageOfWorkerActivity;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;
import com.rosokha.todolist.users.Worker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Представляє собою графічний користувацький інтерфейс входу в програму
 *
 * @see Activity
 * @see AbleToAuthorization
 */
public class LogInActivity extends Activity implements AbleToAuthorization {

    /**
     * Кнопка, після натиску якої здійснюється вхід
     */
    private Button btnLogIn;
    /**
     * Логін користувача
     */
    private EditText etLogin;
    /**
     * Пароль користуача
     */
    private EditText etPassword;

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see Nullable
     * @see Activity#onCreate(Bundle)
     * @see Activity#setContentView(View)
     * @see Activity#setRequestedOrientation(int)
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану

        btnLogIn = findViewById(R.id.btnLogin);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);

        btnLogIn.setOnClickListener((View view) -> doLogIn(etLogin.getText().toString(), hashPassword(etPassword.getText().toString())));
    }

    /**
     * Здійснює вхід користувача
     *
     * @see AuthorizationDao
     * @see AuthorizationDao#doLogIn(String, String)
     * @see LogInActivity#checkData()
     * @see Toast
     * @see Toast#makeText(Context, int, int)
     * @see Toast#show()
     * @see Intent
     * @see Intent#putExtra(String, int)
     * @see Activity#startActivity(Intent)
     * @see Activity#finish()
     * @see User
     * @see Leader
     * @see Worker
     */
    @SuppressLint("Range")
    private void doLogIn(String login, String password) {
        if (checkData() == false) {
            return;
        }
        User user = null;
        try {
            user = AuthorizationDao.doLogIn(login, password);
            writeUserToFile(login, password);
        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося увійти", Toast.LENGTH_LONG).show();
            return;
        }
        if (user instanceof Leader) {
            Intent intent = new Intent(this, HomePageOfLeaderActivity.class);
            intent.putExtra(Leader.class.getSimpleName(), user);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, HomePageOfWorkerActivity.class);
            intent.putExtra(Worker.class.getSimpleName(), user);
            startActivity(intent);
        }

        Toast.makeText(this, "Вхід успішно здійснений", Toast.LENGTH_LONG).show();
        finish();
    }


    /**
     * Перевіряє на коректність введених даних
     *
     * @return повертає true якщо дані введені коректно. Плвертає false якщо дані введені не коректно
     * @see Toast#makeText(Context, int, int)
     * @see Toast#show()
     */
    @Override
    public boolean checkData() {
        if (etLogin.getText().length() < 3) {
            Toast.makeText(this, "Логін повинен містити від трьох символів", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etPassword.getText().length() < 8) {
            Toast.makeText(this, "Пароль повинен містити щонайменше вісім символів", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Записує дані користувача у файл, щоб повторно не авторизуватися
     *
     * @param login    логін
     * @param password пароль
     */
    private void writeUserToFile(String login, String password) {
        String text = login + separator + password;
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(userFile, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            return;
        }
        try {
            fos.write(text.getBytes());
        } catch (IOException e) {
            return;
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                return;
            }
        }
    }
}