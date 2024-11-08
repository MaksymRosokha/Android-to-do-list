package com.rosokha.todolist.users;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rosokha.todolist.R;
import com.rosokha.todolist.notifications.NotificationService;
import com.rosokha.todolist.task.TasksViewerActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Представляє собою графічний користувацький інтерфейс кабінету працівника
 *
 * @see Activity
 */
public class HomePageOfWorkerActivity extends Activity {

    /**
     * Об'єкт класу Worker, який представляє собою працівника
     */
    private static Worker currentWorker;

    /**
     * Сервіс для перевірки повідомлень
     */
    private NotificationService notificationService;

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see Nullable
     * @see Activity#onCreate(Bundle)
     * @see Activity#setContentView(View)
     * @see Activity#setRequestedOrientation(int
     * @see HomePageOfWorkerActivity#inicialComponents()
     * @see Intent
     * @see TasksViewerActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_of_worker);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();

        notificationService = new NotificationService(currentWorker, this);
        notificationService.start();

        Intent intent = new Intent(HomePageOfWorkerActivity.this, TasksViewerActivity.class);
        intent.putExtra(User.class.getSimpleName(), currentWorker);
        startActivity(intent);
    }


    /**
     * Метод, який ініціалізує компоненти
     *
     * @see Bundle
     * @see Leader
     * @see Toast
     * @see Intent
     * @see ProfileViewerActivity
     * @see FavoriteUsersViewerActivity
     * @see TasksViewerActivity
     * @see Activity#finish()
     */
    private void inicialComponents() {
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            currentWorker = (Worker) arguments.getSerializable(Worker.class.getSimpleName());
        } else {
            Toast.makeText(this, "Сталася помилка при передачі аргументів", Toast.LENGTH_LONG).show();
            return;
        }

        Button btnViewProfile = findViewById(R.id.btnViewProfile);
        Button btnViewFavoriteUsers = findViewById(R.id.btnFavoriteUsers);
        Button btnViewTasks = findViewById(R.id.btnViewTasks);
        Button btnLogOut = findViewById(R.id.btnLogOut);


        btnViewProfile.setOnClickListener((View v) -> {
            Intent intent = new Intent(HomePageOfWorkerActivity.this, ProfileViewerActivity.class);
            intent.putExtra(Worker.class.getSimpleName(), currentWorker);
            startActivity(intent);
        });
        btnViewFavoriteUsers.setOnClickListener((View v) -> {
            Intent intent = new Intent(HomePageOfWorkerActivity.this, FavoriteUsersViewerActivity.class);
            intent.putExtra(User.class.getSimpleName(), currentWorker);
            startActivity(intent);
        });
        btnViewTasks.setOnClickListener((View v) -> {
            Intent intent = new Intent(HomePageOfWorkerActivity.this, TasksViewerActivity.class);
            intent.putExtra(User.class.getSimpleName(), currentWorker);
            startActivity(intent);
        });

        btnLogOut.setOnClickListener((View v) -> {
            notificationService.interrupt();
            cleanFile();
            currentWorker = null;
            finish();
        });
    }

    /**
     * Очищує файл з даними користувача
     *
     * @see FileOutputStream
     * @see Activity#openFileOutput(String, int)
     * @see FileOutputStream#write(int)
     */
    private void cleanFile(){
        String text = "";
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("user.txt", MODE_PRIVATE);
        } catch (FileNotFoundException e) {
        }
        try {
            fos.write(text.getBytes());
        } catch (IOException e) {
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Викликається при натиску клавіші "Назад"
     */
    @Override
    public void onBackPressed() { }

    /**
     * Getter для поля currentWorker
     *
     * @return поточного працівника
     * @see Worker
     */
    public static Worker getCurrentWorker() {
        return currentWorker;
    }
}
