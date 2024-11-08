package com.rosokha.todolist.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.LoaderOfFavoriteUsersDao;
import com.rosokha.todolist.dao.SearchDao;
import com.rosokha.todolist.dao.TaskDao;
import com.rosokha.todolist.users.HomePageOfLeaderActivity;
import com.rosokha.todolist.users.HomePageOfWorkerActivity;
import com.rosokha.todolist.users.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Являє собою графічний користувацький інтерфейс створення завдання
 *
 * @see Activity
 */
public class TaskCreatorActivity extends Activity {

    /**
     * Користувач, який вибиратиме виконавців
     */
    private User currentUser;

    /**
     * Заголовок
     */
    private TextView tvTitle;

    /**
     * Назва завдання
     */
    private EditText etTaskName;
    /**
     * Зміст завдання
     */
    private EditText etTaskContent;
    /**
     * Дата закінчення завдання
     */
    private DatePicker dpDate;
    /**
     * Час закінчення завдання
     */
    private TimePicker tpTime;
    /**
     * Кнопка, яка відкриває введення дати і часу
     */
    private Button btnInputDateAndTime;
    /**
     * Кнопк,а яка створює завданя
     */
    private Button btnCreateTask;
    /**
     * Кнопка, яка здійснює вихід з вікна
     */
    private Button btnGoBack;
    /**
     * Контейней у якому розміщені введені дані
     */
    private ScrollView sclInputData;
    /**
     * Сторінка пошуку
     */
    private View search;
    /**
     * Сторінка вибраних користувачів
     */
    private View favoritesUsers;

    /**
     * Логіни виконавців
     */
    private Set<String> executorsLogins = new LinkedHashSet<>();

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
        setContentView(R.layout.activity_create_task);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();
    }

    /**
     * Ініціалізує компоненти
     */
    private void inicialComponents() {

        tvTitle = findViewById(R.id.tvTitle);
        etTaskName = findViewById(R.id.etTaskName);
        etTaskContent = findViewById(R.id.etTaskContent);
        btnInputDateAndTime = findViewById(R.id.btnInputDateAndTime);
        Button btnSelectExecutors = findViewById(R.id.btnSelectExecutors);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        btnGoBack = findViewById(R.id.btnGoBack);
        sclInputData = findViewById(R.id.sclInputData);

        ConstraintLayout clInputDateAndTime = findViewById(R.id.activity_input_date_and_time);
        dpDate = clInputDateAndTime.findViewById(R.id.dpDate);
        tpTime = clInputDateAndTime.findViewById(R.id.tpTime);
        tpTime.setIs24HourView(true);
        Button btnSaveDate = clInputDateAndTime.findViewById(R.id.btnSaveDate);
        Button btnSaveTime = clInputDateAndTime.findViewById(R.id.btnSaveTime);
        ConstraintLayout clDate = clInputDateAndTime.findViewById(R.id.clDate);
        ConstraintLayout clTime = clInputDateAndTime.findViewById(R.id.clTime);

        Button btnSaveExecutors = findViewById(R.id.btnSaveExecutors);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnSelectFromFavorites = findViewById(R.id.btnSelectFromFavorites);
        ConstraintLayout clSelectExecutors = findViewById(R.id.clSelectExecutors);


        search = findViewById(R.id.activity_search);
        EditText etSearch = search.findViewById(R.id.etSearch);

        Button btnGoBackFromSearch = search.findViewById(R.id.btnGoBack);

        favoritesUsers = findViewById(R.id.activity_favorite_users);
        Button btnGoBackFromFavoritesUsers = favoritesUsers.findViewById(R.id.btnGoBack);

        btnGoBackFromFavoritesUsers.setText("OK");
        btnGoBackFromSearch.setText("OK");

        if (HomePageOfLeaderActivity.getCurrentLeader() != null) {
            currentUser = HomePageOfLeaderActivity.getCurrentLeader();
        } else {
            currentUser = HomePageOfWorkerActivity.getCurrentWorker();
        }

        btnInputDateAndTime.setOnClickListener((View v) -> {
            hideMainViews();
            clInputDateAndTime.setVisibility(View.VISIBLE);
            clDate.setVisibility(View.VISIBLE);
            clTime.setVisibility(View.GONE);
            btnInputDateAndTime.setText("Змінити дату");
        });

        btnSelectExecutors.setOnClickListener((View v) -> {
            hideMainViews();
            clSelectExecutors.setVisibility(View.VISIBLE);
        });

        btnCreateTask.setOnClickListener((View v) -> createTask());

        btnSaveDate.setOnClickListener((View v) -> {
            clDate.setVisibility(View.GONE);
            clTime.setVisibility(View.VISIBLE);
        });

        btnSaveTime.setOnClickListener((View v) -> {
            clInputDateAndTime.setVisibility(View.GONE);
            showMainViews();
        });

        btnSaveExecutors.setOnClickListener((View v) -> {
            clSelectExecutors.setVisibility(View.GONE);
            showMainViews();
        });

        btnSearch.setOnClickListener((View v) -> search.setVisibility(View.VISIBLE));

        btnSelectFromFavorites.setOnClickListener((View v) -> {
            favoritesUsers.setVisibility(View.VISIBLE);
            favoritesUsers.findViewById(R.id.btnFindNewUsers).setVisibility(View.GONE);
            showFavoriteUsers();
        });

        btnGoBackFromSearch.setOnClickListener((View v) -> search.setVisibility(View.GONE));

        btnGoBackFromFavoritesUsers.setOnClickListener((View v) -> favoritesUsers.setVisibility(View.GONE));

        btnGoBack.setOnClickListener((View v) -> finish());

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
     * @see TaskCreatorActivity#addOrRemoveExecutor(CheckBox)
     */
    @SuppressLint("Range")
    private void findUsers(String login) {

        LinearLayout llFoundUsers = search.findViewById(R.id.llFoundUsers);
        llFoundUsers.removeAllViews();
        List<User> foundUsers = null;

        try {
            foundUsers = SearchDao.findUsers(login);
        } catch (SQLException ex) {
            Toast.makeText(this, "Пошук не працює", Toast.LENGTH_LONG).show();
        }
        User foundCurrentUser = null;
        for (User tempUser : foundUsers) {
            if (tempUser.getId() == currentUser.getId()) {
                foundCurrentUser = tempUser;
            }
        }
        foundUsers.remove(foundCurrentUser);

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
     * Додає або видаляє користувача до списку виконавців
     *
     * @param chkExecutor checkBox виконавця
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
     * @see TaskCreatorActivity#addOrRemoveExecutor(CheckBox)
     */
    @SuppressLint("Range")
    private void showFavoriteUsers() {
        List<User> favoriteUsers = null;

        LinearLayout llFavoriteUsers = favoritesUsers.findViewById(R.id.llFavoriteUsers);
        llFavoriteUsers.removeAllViews();

        try {
            favoriteUsers = LoaderOfFavoriteUsersDao.loadFavoriteUsers(currentUser.getId());
        } catch (SQLException ex) {
            Toast.makeText(this, "Неможливо завантажити вибраних користувачів", Toast.LENGTH_LONG).show();
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

    /**
     * Створює завдання
     *
     * @see TaskCreatorActivity#checkData()
     * @see TaskDao
     * @see TaskDao#createTask(int, String, String, String)
     * @see Toast
     * @see Activity#finish()
     */
    private void createTask() {
        if (!checkData()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime endDateTime = LocalDateTime.of(dpDate.getYear(), dpDate.getMonth() + 1, dpDate.getDayOfMonth(), tpTime.getHour(), tpTime.getMinute());
                try {
                    TaskDao.createTask(currentUser.getId(), etTaskName.getText().toString(),
                            etTaskContent.getText().toString(), endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } catch (SQLException ex) {
                    Toast.makeText(this, "Не вдалося створити завдання", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        try {
            TaskDao.addExecutors(TaskDao.getIDLastTask(), executorsLogins);
        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося додати виконавців до завдання", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Завдання успішно створено", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Перевіряє на коректність введених даних
     *
     * @return повертає true якщо дані введені коректно. Плвертає false якщо дані введені не коректно
     * @see Toast
     */
    private boolean checkData() {
        if (etTaskName.getText().toString().equals("")) {
            Toast.makeText(this, "Назва завдання не може бути порожньою", Toast.LENGTH_LONG).show();
            return false;
        }
        if (btnInputDateAndTime.getText().toString().equals("Ввести дату")) {
            Toast.makeText(this, "Введіть дату і час", Toast.LENGTH_LONG).show();
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (LocalDateTime.of(dpDate.getYear(), dpDate.getMonth() + 1, dpDate.getDayOfMonth(), tpTime.getHour(), tpTime.getMinute()).isBefore(LocalDateTime.now())) {
                    Toast.makeText(this, "Дата виконання не може бути раніше теперішньої", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        if (executorsLogins.isEmpty()) {
            Toast.makeText(this, "Виберіть виконавців", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /**
     * Сховує головні елементи activity при появі вікна вводу
     */
    private void hideMainViews() {
        tvTitle.setVisibility(View.GONE);
        sclInputData.setVisibility(View.GONE);
        btnCreateTask.setVisibility(View.GONE);
        btnGoBack.setVisibility(View.GONE);
    }

    /**
     * Показує головні елементи activity при закритті вікна вводу
     */
    private void showMainViews() {
        tvTitle.setVisibility(View.VISIBLE);
        sclInputData.setVisibility(View.VISIBLE);
        btnCreateTask.setVisibility(View.VISIBLE);
        btnGoBack.setVisibility(View.VISIBLE);
    }
}