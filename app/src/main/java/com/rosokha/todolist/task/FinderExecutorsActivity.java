package com.rosokha.todolist.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.LoaderOfFavoriteUsersDao;
import com.rosokha.todolist.dao.SearchDao;
import com.rosokha.todolist.dao.TaskDao;
import com.rosokha.todolist.users.HomePageOfLeaderActivity;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Представляє собою графічний користувацький інтерфейс пошуку виконавців до завдання
 *
 * @see Activity
 */
public class FinderExecutorsActivity extends Activity {

    /**
     * Графічний користувацький інтерфейс пошуку користувачів
     */
    private View search;

    /**
     * Графічний користувацький інтерфейс вибору користувачів з вибраних
     */
    private View favoritesUsers;

    /**
     * Контейнер який містить в собі знайдених користувачів
     */
    private LinearLayout llFoundUsers;

    /**
     * Контейнер який містить в собі вибраних користувачів
     */
    private LinearLayout llFavoriteUsers;

    /**
     * Логіни виконавців завдання
     */
    private Set<String> executorsLogins = new LinkedHashSet<>();

    /**
     * Завдання
     */
    private Task task = null;

    /**
     * Поточкий керівник, який вибирає виконавців
     */
    private Leader currentUser = HomePageOfLeaderActivity.getCurrentLeader();

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see Activity#onCreate(Bundle)
     * @see Activity#setContentView(View)
     * @see Activity#setRequestedOrientation(int)
     * @see FinderExecutorsActivity#inicialComponents()
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_executors);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();
    }

    /**
     * Ініціалізує компоненти
     *
     * @see Bundle
     * @see Task
     */
    private void inicialComponents() {
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
        }

        Button btnSaveExecutors = findViewById(R.id.btnSaveExecutors);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnSelectFromFavorites = findViewById(R.id.btnSelectFromFavorites);


        search = findViewById(R.id.activity_search);
        EditText etSearch = search.findViewById(R.id.etSearch);
        llFoundUsers = search.findViewById(R.id.llFoundUsers);
        Button btnGoBackFromSearch = search.findViewById(R.id.btnGoBack);

        favoritesUsers = findViewById(R.id.activity_favorite_users);
        llFavoriteUsers = favoritesUsers.findViewById(R.id.llFavoriteUsers);
        Button btnGoBackFromFavoritesUsers = favoritesUsers.findViewById(R.id.btnGoBack);
        favoritesUsers.findViewById(R.id.btnFindNewUsers).setVisibility(View.GONE);

        btnGoBackFromFavoritesUsers.setText("OK");
        btnGoBackFromSearch.setText("OK");

        btnSaveExecutors.setOnClickListener((View v) -> {
            if (task != null) {
                TaskDao.addExecutors(task.getId(), executorsLogins);
            }
            finish();
        });

        btnSearch.setOnClickListener((View v) -> search.setVisibility(View.VISIBLE));

        btnSelectFromFavorites.setOnClickListener((View v) -> {
            favoritesUsers.setVisibility(View.VISIBLE);
            favoritesUsers.findViewById(R.id.btnFindNewUsers).setVisibility(View.GONE);
            showFavoriteUsers();
        });

        btnGoBackFromSearch.setOnClickListener((View v) -> search.setVisibility(View.GONE));

        btnGoBackFromFavoritesUsers.setOnClickListener((View v) -> favoritesUsers.setVisibility(View.GONE));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                findUsers(etSearch.getText().toString());
            }
        });
    }

    /**
     * Шукає користувачів
     *
     * @param login логін по якому потрібно здійснити пошук
     * @see User
     * @see SearchDao
     * @see SearchDao#findUsers(String)
     * @see Toast
     * @see FinderExecutorsActivity#addOrRemoveExecutor(CheckBox)
     */
    @SuppressLint("Range")
    private void findUsers(String login) {

        llFoundUsers.removeAllViews();
        List<User> foundUsers = null;
        try {
            foundUsers = SearchDao.findUsers(login);
        } catch (SQLException ex) {
            Toast.makeText(this, "Пошук не працює", Toast.LENGTH_LONG).show();
            return;
        }
        User foundCurrentUser = null;
        for (User tempUser : foundUsers) {
            if (tempUser.getId() == currentUser.getId()) {
                foundCurrentUser = tempUser;
            }
        }
        foundUsers.remove(foundCurrentUser);

        if (task != null) {
            for (User tempUser1 : task.getExecutors()) {
                for (User tempUser2 : foundUsers) {
                    if (tempUser1.getLogin().equals(tempUser2.getLogin())) {
                        foundUsers.remove(tempUser2);
                        break;
                    }
                }
            }
        }

        for (User tempUser : foundUsers) {
            CheckBox chkExecutor = new CheckBox(this);
            chkExecutor.setText(tempUser.getLogin());
            chkExecutor.setTextColor(Color.WHITE);
            chkExecutor.setTextSize(20);
            chkExecutor.setOnClickListener((View v) -> addOrRemoveExecutor(chkExecutor));

            for (String checkExecutor : executorsLogins) {
                if (checkExecutor.equals(tempUser.getLogin())) {
                    chkExecutor.setChecked(true);
                    break;
                } else {
                    chkExecutor.setChecked(false);
                }
            }
            llFoundUsers.addView(chkExecutor);
        }
    }

    /**
     * Додає або видаляє користувача з списку виконавців
     *
     * @param chkExecutor checkBox виконавця
     * @see CheckBox
     */
    private void addOrRemoveExecutor(CheckBox chkExecutor) {
        if (chkExecutor.isChecked()) {
            executorsLogins.add(chkExecutor.getText().toString());
        } else {
            for (String executorLogin : executorsLogins) {
                if (executorLogin.equals(chkExecutor.getText())) {
                    executorsLogins.remove(executorLogin);
                    break;
                }
            }
        }
        LinearLayout llViewExecutors = findViewById(R.id.llViewExecutors);
        llViewExecutors.removeAllViews();

        for (String login : executorsLogins) {
            CheckBox executorLogin = new CheckBox(this);
            executorLogin.setText(login);
            executorLogin.setTextSize(20);
            executorLogin.setTextColor(Color.WHITE);
            executorLogin.setChecked(true);
            executorLogin.setOnClickListener((View v) -> addOrRemoveExecutor(executorLogin));
            llViewExecutors.addView(executorLogin);
        }
    }

    /**
     * Показує вибраних користувачів
     *
     * @see User
     * @see Toast
     * @see FinderExecutorsActivity#addOrRemoveExecutor(CheckBox)
     */
    @SuppressLint("Range")
    private void showFavoriteUsers() {
        List<User> favoriteUsers = null;
        llFavoriteUsers.removeAllViews();

        try {
            favoriteUsers = LoaderOfFavoriteUsersDao.loadFavoriteUsers(currentUser.getId());
        } catch (SQLException ex) {
            Toast.makeText(this, "Неможливо завантажити вибраних користувачів", Toast.LENGTH_LONG).show();
            return;
        }

        if (task != null) {
            for (User tempUser1 : task.getExecutors()) {
                for (User tempUser2 : favoriteUsers) {
                    if (tempUser1.getLogin().equals(tempUser2.getLogin())) {
                        favoriteUsers.remove(tempUser2);
                        break;
                    }
                }
            }
        }

        for (User favoriteUser : favoriteUsers) {
            CheckBox chkExecutor = new CheckBox(this);
            chkExecutor.setText(favoriteUser.getLogin());
            chkExecutor.setTextColor(Color.WHITE);
            chkExecutor.setTextSize(20);
            chkExecutor.setOnClickListener((View v) -> addOrRemoveExecutor(chkExecutor));

            for (String checkExecutor : executorsLogins) {
                if (checkExecutor.equals(favoriteUser.getLogin())) {
                    chkExecutor.setChecked(true);
                    break;
                } else {
                    chkExecutor.setChecked(false);
                }
            }
            llFavoriteUsers.addView(chkExecutor);
        }
    }
}