package com.rosokha.todolist.users;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.database.*;
import android.os.*;
import android.text.*;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;

import com.rosokha.todolist.*;
import com.rosokha.todolist.dao.*;

import java.util.*;

/**
 * Представляє собою графічний користувацький інтерфейс пошуку користувачів
 *
 * @see Activity
 */
public class SearchUsersActivity extends Activity {

    /**
     * Користувач, який шукає інших користувачів
     */
    User user;

    /**
     * Список знайдених користувачів
     */
    List<User> foundUsers = new ArrayList<>();

    /**
     * Контейнер у якому відображуються знайдені користувачі
     */
    LinearLayout llFoundUsers;

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see Activity#onCreate(Bundle)
     * @see Activity#setContentView(View)
     * @see Activity#setRequestedOrientation(int)
     * @see SearchUsersActivity#inicialComponents()
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();
    }

    /**
     * Викликається при поверненні користувача в дане activity
     *
     * @see Activity#onResume()
     * @see SearchUsersActivity#findUsers(String)
     */
    @Override
    protected void onResume() {
        super.onResume();
        EditText etSearch = findViewById(R.id.etSearch);
        findUsers(etSearch.getText().toString());
    }

    /**
     * Ініціалізує компоненти
     *
     * @see HomePageOfLeaderActivity
     * @see HomePageOfWorkerActivity
     * @see HomePageOfLeaderActivity#getCurrentLeader()
     * @see HomePageOfWorkerActivity#getCurrentWorker()
     */
    private void inicialComponents() {

        if (HomePageOfLeaderActivity.getCurrentLeader() != null) {
            user = HomePageOfLeaderActivity.getCurrentLeader();
        } else {
            user = HomePageOfWorkerActivity.getCurrentWorker();
        }

        EditText etSearch = findViewById(R.id.etSearch);
        llFoundUsers = findViewById(R.id.llFoundUsers);
        Button btnGoBack = findViewById(R.id.btnGoBack);
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
     * @see SearchDao
     * @see SearchDao#findUsers(String)
     * @see Toast
     * @see User
     */
    @SuppressLint("Range")
    private void findUsers(String login) {

        llFoundUsers.removeAllViews();
        try {
            foundUsers = SearchDao.findUsers(login);
        } catch (SQLException ex) {
            Toast.makeText(this, "Пошук не працює", Toast.LENGTH_LONG).show();
            return;
        }

        for (User tempUser : foundUsers) {
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundResource(R.drawable.round_user);
            linearLayout.setOnClickListener((View v) -> openProfile(tempUser));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, height);
            layoutParams.setMargins(0, 5, 0, 0);
            linearLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setLayoutParams(layoutParams);

            TextView tv = new TextView(this);
            tv.setText(tempUser.getLogin());
            tv.setTextSize(20);
            tv.setAllCaps(false);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setChecked(ViewProfileDao.checkUserForFavorite(user.getId(), tempUser.getId()));

            checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                if (isChecked) {
                    ViewProfileDao.addFavoriteUser(user.getId(), tempUser.getId());
                } else {
                    ViewProfileDao.deleteFavoriteUser(user.getId(), tempUser.getId());
                }
            });

            if(user.getId() == tempUser.getId()) {
                checkBox.setVisibility(View.INVISIBLE);
            }

            linearLayout.addView(checkBox);
            linearLayout.addView(tv);
            llFoundUsers.addView(linearLayout);
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
     */
    private void openProfile(User user) {
        if (user instanceof Leader) {
            Leader tempLeader = (Leader) user;
            Intent intent = new Intent(SearchUsersActivity.this, ProfileViewerActivity.class);
            intent.putExtra(Leader.class.getSimpleName(), tempLeader);
            startActivity(intent);
        } else {
            Worker tempWorker = (Worker) user;
            Intent intent = new Intent(SearchUsersActivity.this, ProfileViewerActivity.class);
            intent.putExtra(Worker.class.getSimpleName(), tempWorker);
            startActivity(intent);
        }
    }
}