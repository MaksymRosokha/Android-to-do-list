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
 * Представляє собою графічний користувацький інтерфейс кабінету керівника
 *
 * @see Activity
 */
public class HomePageOfLeaderActivity extends Activity {

    /**
     * Об'єкт класу Leader, який представляє собою керівника
     */
    private static Leader currentLeader;

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
     * @see HomePageOfLeaderActivity#inicialComponents()
     * @see Intent
     * @see TasksViewerActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_of_leader);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();

        notificationService = new NotificationService(currentLeader, this);
        notificationService.start();

        Intent intent = new Intent(HomePageOfLeaderActivity.this, TasksViewerActivity.class);
        intent.putExtra(User.class.getSimpleName(), currentLeader);
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
            currentLeader = (Leader) arguments.getSerializable(Leader.class.getSimpleName());
        } else {
            Toast.makeText(this, "Сталася помилка при передачі аргументів", Toast.LENGTH_LONG).show();
            return;
        }

        Button btnViewProfile = findViewById(R.id.btnViewProfile);
        Button btnViewFavoriteUsers = findViewById(R.id.btnFavoriteUsers);
        Button btnViewTasks = findViewById(R.id.btnViewTasks);
        Button btnLogOut = findViewById(R.id.btnLogOut);


        btnViewProfile.setOnClickListener((View v) -> {
            Intent intent = new Intent(HomePageOfLeaderActivity.this, ProfileViewerActivity.class);
            intent.putExtra(Leader.class.getSimpleName(), currentLeader);
            startActivity(intent);
        });
        btnViewFavoriteUsers.setOnClickListener((View v) -> {

            Intent intent = new Intent(HomePageOfLeaderActivity.this, FavoriteUsersViewerActivity.class);
            intent.putExtra(User.class.getSimpleName(), currentLeader);
            startActivity(intent);
        });
        btnViewTasks.setOnClickListener((View v) -> {

            Intent intent = new Intent(HomePageOfLeaderActivity.this, TasksViewerActivity.class);
            intent.putExtra(User.class.getSimpleName(), currentLeader);
            startActivity(intent);
        });
        btnLogOut.setOnClickListener((View v) -> {
            notificationService.interrupt();
            cleanFile();
            currentLeader = null;
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
    public void onBackPressed() {
    }

    /**
     * Getter для поля currentLeader
     *
     * @return поточного керівника
     * @see Leader
     */
    public static Leader getCurrentLeader() {
        return currentLeader;
    }
}