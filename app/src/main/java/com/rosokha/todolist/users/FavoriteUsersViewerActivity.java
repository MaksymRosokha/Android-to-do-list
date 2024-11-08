package com.rosokha.todolist.users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.SQLException;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.LoaderOfFavoriteUsersDao;
import com.rosokha.todolist.dao.ViewProfileDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Представляє собою графічний користувацький інтерфейс перегляду вибраних користувачів
 *
 * @see Activity
 */
public class FavoriteUsersViewerActivity extends Activity {

    /**
     * Користувач, який переглядає вибраних працівників
     */
    User user;
    /**
     * Список вибраних користувачів
     */
    List<User> favoriteUsers = new ArrayList<>();
    /**
     * Контейнер для вибраних користувачів
     */
    LinearLayout llFavoriteUsers;

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see Nullable
     * @see Activity#onCreate(Bundle)
     * @see Activity#setContentView(View)
     * @see Activity#setRequestedOrientation(int)
     * @see FavoriteUsersViewerActivity#inicialComponents()
     * @see FavoriteUsersViewerActivity#showFavoriteUsers()
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorite_users);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();
        showFavoriteUsers();
    }

    /**
     * Викликається при поверненні користувача в дане activity
     *
     * @see Activity#onResume()
     * @see FavoriteUsersViewerActivity#showFavoriteUsers()
     */
    @Override
    protected void onResume() {
        super.onResume();
        showFavoriteUsers();
    }

    /**
     * Ініціалізує компоненти
     *
     * @see Bundle
     * @see Toast
     * @see User
     */
    private void inicialComponents() {
        Button btnFindNewUsers = findViewById(R.id.btnFindNewUsers);
        Button btnGoBack = findViewById(R.id.btnGoBack);
        llFavoriteUsers = findViewById(R.id.llFavoriteUsers);

        btnGoBack.setOnClickListener((View v) -> finish());

        btnFindNewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteUsersViewerActivity.this, SearchUsersActivity.class);
                startActivity(intent);
            }
        });

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            user = (User) arguments.getSerializable(User.class.getSimpleName());
        } else {
            Toast.makeText(this, "Сталася помилка при передачі аргументів", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Показує вибраних користувачів
     *
     * @see LoaderOfFavoriteUsersDao
     * @see LoaderOfFavoriteUsersDao#loadFavoriteUsers(int)
     * @see User
     * @see Toast
     */
    @SuppressLint("Range")
    private void showFavoriteUsers() {
        llFavoriteUsers.removeAllViews();
        try {
            favoriteUsers = LoaderOfFavoriteUsersDao.loadFavoriteUsers(user.getId());
        } catch (SQLException ex) {
            Toast.makeText(this, "Неможливо завантажити вибраних користувачів", Toast.LENGTH_LONG).show();
        }

        for (User favoriteUser : favoriteUsers) {
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundResource(R.drawable.round_user);
            linearLayout.setOnClickListener((View v) -> openProfile(favoriteUser));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, height);
            layoutParams.setMargins(0, 5, 0, 0);
            linearLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setLayoutParams(layoutParams);

            TextView tv = new TextView(this);
            tv.setText(favoriteUser.getLogin());
            tv.setTextSize(20);
            tv.setAllCaps(false);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                if (isChecked) {
                    ViewProfileDao.addFavoriteUser(user.getId(), favoriteUser.getId());
                    showFavoriteUsers();
                } else {
                    ViewProfileDao.deleteFavoriteUser(user.getId(), favoriteUser.getId());
                    showFavoriteUsers();
                }
            });
            linearLayout.addView(checkBox);
            linearLayout.addView(tv);
            llFavoriteUsers.addView(linearLayout);
        }
    }

    /**
     * Відкриває профіль вибраного користувача
     *
     * @param user користувач, профіль якого потрібно відкрити
     * @see User
     * @see Leader
     * @see Worker
     * @see Intent
     * @see ProfileViewerActivity
     */
    private void openProfile(User user) {
        if (user instanceof Leader) {
            Leader tempLeader = (Leader) user;
            Intent intent = new Intent(FavoriteUsersViewerActivity.this, ProfileViewerActivity.class);
            intent.putExtra(Leader.class.getSimpleName(), tempLeader);
            startActivity(intent);
        } else {
            Worker tempWorker = (Worker) user;
            Intent intent = new Intent(FavoriteUsersViewerActivity.this, ProfileViewerActivity.class);
            intent.putExtra(Worker.class.getSimpleName(), tempWorker);
            startActivity(intent);
        }
    }
}
