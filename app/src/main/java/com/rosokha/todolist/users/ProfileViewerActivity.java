package com.rosokha.todolist.users;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.ViewProfileDao;

/**
 * Представляє собою графічний користувацький інтерфейс перегляду профіля користувача
 *
 * @see Activity
 */
public class ProfileViewerActivity extends Activity {

    /**
     * Заголовок
     */
    TextView tvProfile;
    /**
     * Логін користувача
     */
    EditText etLogin;
    /**
     * ПІБ користувача
     */
    EditText etUserName;
    /**
     * Кнопка збереження змін
     */
    Button btnSaveChanges;
    /**
     * CheckBox вибору користувача до вибраних
     */
    CheckBox chkFavoriteUser;
    /**
     * Працівник, аакаунт якого потрібно переглянути
     */
    Worker worker = null;
    /**
     * Керівник, акаунт якого потрібно переглянути
     */
    Leader leader = null;
    /**
     * Працівник який переглядає акаунт
     */
    Worker currentWorker = null;
    /**
     * Керівник який переглядає акаунт
     */
    Leader currentLeader = null;

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see Nullable
     * @see Activity#onCreate(Bundle)
     * @see Activity#setContentView(View)
     * @see Activity#setRequestedOrientation(int)
     * @see ProfileViewerActivity#inicialComponents()
     * @see ProfileViewerActivity#setThisUser()
     * @see ProfileViewerActivity#checkUser()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();
        setThisUser();
        checkUser();
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

        currentWorker = HomePageOfWorkerActivity.getCurrentWorker();
        currentLeader = HomePageOfLeaderActivity.getCurrentLeader();

        tvProfile = findViewById(R.id.tvProfileTitle);
        etLogin = findViewById(R.id.etLogin);
        etUserName = findViewById(R.id.etUserName);
        Button btnGoBack = findViewById(R.id.btnGoBack);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        chkFavoriteUser = findViewById(R.id.chkFavoriteUser);

        btnGoBack.setOnClickListener((View v) -> finish());
        btnSaveChanges.setOnClickListener((View v) -> saveChanges());
        chkFavoriteUser.setOnClickListener((View v) -> addOrDeleteFavoriteUser());
    }

    /**
     * Встановлює профілько користувача якого потрібно переглянути
     *
     * @see Bundle
     */
    private void setThisUser() {
        Bundle arguments = getIntent().getExtras();

        if (arguments == null) {
            Toast.makeText(this, "Сталася помилка при передачі аргументів", Toast.LENGTH_LONG).show();
            return;
        }
        leader = (Leader) arguments.getSerializable(Leader.class.getSimpleName());

        worker = (Worker) arguments.getSerializable(Worker.class.getSimpleName());
    }

    /**
     * Встановлює видимість кнопок та можливість редагування профілю в залежності від
     * користувача який переглядає профіль, та від профілю користувача якого переглядають
     *
     * @see ViewProfileDao
     * @see ViewProfileDao#checkUserForFavorite(int, int)
     */
    private void checkUser() {
        if (leader != null) {
            etLogin.setText(leader.getLogin());
            etUserName.setText(leader.getName());


            if (currentLeader != null) {
                //Перегляд керівником свій профіль
                if (leader.getId() == currentLeader.getId()) {
                    chkFavoriteUser.setVisibility(View.GONE);
                    btnSaveChanges.setVisibility(View.VISIBLE);
                    etLogin.setFocusableInTouchMode(true);
                    etUserName.setFocusableInTouchMode(true);
                    tvProfile.setText("Ваш профіль");
                }
                //Перегляд керівником профіль керівника
                else {
                    chkFavoriteUser.setVisibility(View.VISIBLE);
                    btnSaveChanges.setVisibility(View.GONE);
                    etLogin.setFocusable(false);
                    etUserName.setFocusable(false);
                    tvProfile.setText("Профіль керівника");
                    chkFavoriteUser.setChecked(ViewProfileDao.checkUserForFavorite(currentLeader.getId(), leader.getId()));
                }
            }
            //Перегляд працівником профіль керівника
            else {
                chkFavoriteUser.setVisibility(View.VISIBLE);
                btnSaveChanges.setVisibility(View.GONE);
                etLogin.setFocusable(false);
                etUserName.setFocusable(false);
                tvProfile.setText("Профіль керівника");
                chkFavoriteUser.setChecked(ViewProfileDao.checkUserForFavorite(currentWorker.getId(), leader.getId()));
            }

        }
        if (worker != null) {
            etLogin.setText(worker.getLogin());
            etUserName.setText(worker.getName());

            if (currentWorker != null) {
                //Перегляд працівником свій профіль
                if (worker.getId() == currentWorker.getId()) {
                    chkFavoriteUser.setVisibility(View.GONE);
                    btnSaveChanges.setVisibility(View.VISIBLE);
                    etLogin.setFocusableInTouchMode(true);
                    etUserName.setFocusableInTouchMode(true);
                    tvProfile.setText("Ваш профіль");
                } else {
                    //Перегляд працівником профіль працівника
                    chkFavoriteUser.setVisibility(View.VISIBLE);
                    btnSaveChanges.setVisibility(View.GONE);
                    etLogin.setFocusable(false);
                    etUserName.setFocusable(false);
                    tvProfile.setText("Профіль працівника");
                    chkFavoriteUser.setChecked(ViewProfileDao.checkUserForFavorite(currentWorker.getId(), worker.getId()));
                }
            }
            //Перегляд керівником профіль працівника
            else {
                chkFavoriteUser.setVisibility(View.VISIBLE);
                btnSaveChanges.setVisibility(View.GONE);
                etLogin.setFocusable(false);
                etUserName.setFocusable(false);
                tvProfile.setText("Профіль працівника");
                chkFavoriteUser.setChecked(ViewProfileDao.checkUserForFavorite(currentLeader.getId(), worker.getId()));
            }
        }
    }

    /**
     * Зберігає зміни логіну та імені користувача
     *
     * @see Leader#updateLogin(String)
     * @see Leader#updateUserName(String)
     * @see Worker#updateLogin(String)
     * @see Worker#updateUserName(String)
     * @see Toast
     * @see Activity#finish()
     */
    private void saveChanges() {

        if (checkData() == false) {
            return;
        }

        if (currentLeader != null) {
            currentLeader.updateLogin(etLogin.getText().toString());
            currentLeader.updateUserName(etUserName.getText().toString());
        }
        if (currentWorker != null) {
            currentWorker.updateLogin(etLogin.getText().toString());
            currentWorker.updateUserName(etUserName.getText().toString());
        }
        Toast.makeText(this, "Ваші дані успішно збережені", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Перевіряє на коректність введених даних
     *
     * @return повертає true, якщо дані введені коректно. Плвертає false, якщо дані введені не коректно
     * @see Toast
     * @see ViewProfileDao
     * @see ViewProfileDao#validationLoginWithoutCurrentUser(String, String)
     */
    private boolean checkData() {
        if (etLogin.length() < 3) {
            Toast.makeText(this, "Логін повинен містити від трьох символів", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etUserName.length() < 1) {
            Toast.makeText(this, "ПІБ користувача не може бути пустим", Toast.LENGTH_LONG).show();
            return false;
        }
        if(currentLeader != null) {
            if (!ViewProfileDao.validationLoginWithoutCurrentUser(etLogin.getText().toString(), currentLeader.getLogin())) {
                Toast.makeText(this, "Користувач з таким логіном вже існує", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            if (!ViewProfileDao.validationLoginWithoutCurrentUser(etLogin.getText().toString(), currentWorker.getLogin())) {
                Toast.makeText(this, "Користувач з таким логіном вже існує", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    /**
     * Додає або видаляє користувача з вибраних
     *
     * @see ViewProfileDao
     * @see ViewProfileDao#addFavoriteUser(int, int)
     * @see ViewProfileDao#deleteFavoriteUser(int, int)
     */
    private void addOrDeleteFavoriteUser() {
        if (chkFavoriteUser.isChecked()) {
            if (currentLeader != null) {
                if (leader != null) {
                    ViewProfileDao.addFavoriteUser(currentLeader.getId(), leader.getId());
                } else {
                    ViewProfileDao.addFavoriteUser(currentLeader.getId(), worker.getId());
                }
            }
            if (currentWorker != null) {
                if (leader != null) {
                    ViewProfileDao.addFavoriteUser(currentWorker.getId(), worker.getId());
                } else {
                    ViewProfileDao.addFavoriteUser(currentWorker.getId(), leader.getId());
                }
            }
        } else {
            if (currentLeader != null) {
                if (leader != null) {
                    ViewProfileDao.deleteFavoriteUser(currentLeader.getId(), leader.getId());
                } else {
                    ViewProfileDao.deleteFavoriteUser(currentLeader.getId(), worker.getId());
                }
            }
            if (currentWorker != null) {
                if (leader != null) {
                    ViewProfileDao.deleteFavoriteUser(currentWorker.getId(), worker.getId());
                } else {
                    ViewProfileDao.deleteFavoriteUser(currentWorker.getId(), leader.getId());
                }
            }
        }
    }
}