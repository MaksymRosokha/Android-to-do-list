package com.rosokha.todolist.authorization;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.AuthorizationDao;
import com.rosokha.todolist.users.HomePageOfLeaderActivity;
import com.rosokha.todolist.users.HomePageOfWorkerActivity;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.Worker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Представляє собою графічний користувацький інтерфейс реєстрації
 *
 * @see Activity
 * @see AbleToAuthorization
 */
public class SignUpActivity extends Activity implements AbleToAuthorization {

    /**
     * Кнопка, після натиску якої здійснюється реєстрація
     */
    private Button btnSignUp;
    /**
     * ПІБ користувача
     */
    private EditText etUserName;
    /**
     * Логін користувача
     */
    private EditText etLogin;
    /**
     * Пароль користуача
     */
    private EditText etPassword;
    /**
     * Повторений пароль користувача
     */
    private EditText etRepeatedPassword;
    /**
     * Тип аккаунту керівник
     */
    private RadioButton rbLeader;
    /**
     * Тип аккаунту працівник
     */
    private RadioButton rbWorker;

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
        setContentView(R.layout.activity_sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану

        btnSignUp = findViewById(R.id.btnSignUp);
        etUserName = findViewById(R.id.etUserName);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        etRepeatedPassword = findViewById(R.id.etRepeatedPassword);
        rbLeader = findViewById(R.id.rbLeader);
        rbWorker = findViewById(R.id.rbWorker);

        btnSignUp.setOnClickListener((View view) -> toRegister());
    }

    /**
     * Реєструє користувача
     *
     * @see SignUpActivity#checkData()
     * @see AuthorizationDao
     * @see AuthorizationDao#doRegister(String, String, String, String)
     * @see AuthorizationDao#doLogIn(String, String)
     * @see Intent
     * @see Intent#putExtra(String, int)
     * @see Activity#startActivity(Intent)
     * @see Activity#finish()
     */
    private void toRegister() {
        if (checkData() == false) {
            return;
        }
        try {
            AuthorizationDao.doRegister(etUserName.getText().toString(), etLogin.getText().toString(),
                    hashPassword(etPassword.getText().toString()), rbLeader.isChecked() ? "Leader" : "Worker");
            writeUserToFile(etLogin.getText().toString(), hashPassword(etPassword.getText().toString()));
        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося зареєструвати користувача", Toast.LENGTH_LONG).show();
            return;
        }

        if (rbLeader.isChecked()) {
            try {
                Intent intent = new Intent(SignUpActivity.this, HomePageOfLeaderActivity.class);
                intent.putExtra(Leader.class.getSimpleName(), AuthorizationDao.doLogIn(etLogin.getText().toString(),
                        hashPassword(etPassword.getText().toString())));
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(this, "Не вдалося увійти", Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                Intent intent = new Intent(SignUpActivity.this, HomePageOfWorkerActivity.class);
                intent.putExtra(Worker.class.getSimpleName(), AuthorizationDao.doLogIn(etLogin.getText().toString(),
                        hashPassword(etPassword.getText().toString())));
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(this, "Не вдалося увійти", Toast.LENGTH_LONG).show();
            }
        }
        Toast.makeText(this, "Користувач успішно зареєстрований", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Перевіряє на коректність введених даних
     *
     * @return повертає true якщо дані введені коректно. Плвертає false якщо дані введені не коректно
     * @see AuthorizationDao
     * @see AuthorizationDao#checkForLoginUniqueness(String)
     * @see Toast#makeText(Context, int, int)
     * @see Toast#show()
     */
    @Override
    public boolean checkData() {
        if (etUserName.getText().length() == 0) {
            Toast.makeText(this, "ПІБ користувача не може бути пустим", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etLogin.getText().length() < 3) {
            Toast.makeText(this, "Логін повинен містити від трьох символів", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etPassword.getText().length() < 8) {
            Toast.makeText(this, "Пароль повинен містити щонайменше вісім символів", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etPassword.getText().toString().equals(etRepeatedPassword.getText().toString())) {
            Toast.makeText(this, "Паролі не співпадають", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!rbLeader.isChecked() && !rbWorker.isChecked()) {
            Toast.makeText(this, "Не вибрано тип аккаунту", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            if (AuthorizationDao.checkForLoginUniqueness(etLogin.getText().toString())) { return true; }
        } catch (SQLException ex){
            Toast.makeText(this, "Користувач з таким логіном вже існує", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Записує дані користувача у файл, щоб повторно не авторизуватися
     * @param login логін
     * @param password пароль
     */
    private void writeUserToFile(String login, String password){
        String text = login + separator + password;
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(userFile, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Не вдалося знайти файл", Toast.LENGTH_LONG).show();
        }
        try {
            fos.write(text.getBytes());
        } catch (IOException e) {
            Toast.makeText(this, "Не вдалося записати у файл", Toast.LENGTH_LONG).show();
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                Toast.makeText(this, "Не вдалося закрити FileOutputStream", Toast.LENGTH_LONG).show();
            }
        }
    }
}