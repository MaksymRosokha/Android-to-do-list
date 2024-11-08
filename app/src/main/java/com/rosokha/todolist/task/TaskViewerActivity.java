package com.rosokha.todolist.task;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.CommentDao;
import com.rosokha.todolist.dao.TaskDao;
import com.rosokha.todolist.dao.ViewProfileDao;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.ProfileViewerActivity;
import com.rosokha.todolist.users.User;
import com.rosokha.todolist.users.Worker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Представляє собою графічний користувацький інтерфейс перегляду завдання
 *
 * @see Activity
 */
public class TaskViewerActivity extends Activity {

    /**
     * Назва завдання
     */
    private EditText etName;
    /**
     * Зміст завдання
     */
    private EditText etContent;
    /**
     * Керівник завдання
     */
    private TextView tvLeader;
    /**
     * Дата публікації завдання
     */
    private EditText etDateOfPublication;
    /**
     * Дата закінчення завдання
     */
    private EditText etEndDate;
    /**
     * Дата виконання завдання
     */
    private EditText etDateExecution;
    /**
     * Кнопка виходу з activity
     */
    private Button btnGoBack;
    /**
     * Кнопка позначення завдання як виконане
     */
    private Button btnMarkAsDone;
    /**
     * Кнопка відкриття сторінки створення завдання
     */
    private Button btnOpenPageCreateComment;
    /**
     * Кнопка редагування завдання
     */
    private Button btnEditTask;
    /**
     * Кнопка перегляду виконавців
     */
    private Button btnViewExecutors;
    /**
     * Кнопка додавання нових користувачів
     */
    private Button btnAddNewExecutors;
    /**
     * Кнопка зміни кінцевої дати і часу
     */
    private Button btnChangeEndDate;
    /**
     * Кнопка видалення завдання
     */
    private Button btnDeleteTask;
    /**
     * Контейнер з інформаціїєю про завдання
     */
    private LinearLayout llTaskInformation;
    /**
     * Контейнер створення коментаря
     */
    private ConstraintLayout clCreateComment;
    /**
     * Контейнер перегляду виконавців
     */
    private ConstraintLayout clExecutors;
    /**
     * Контейнер вводу дати і часу
     */
    private ConstraintLayout clInputDateAndTime;
    /**
     * Календар
     */
    private DatePicker dpDate;
    /**
     * Годинник
     */
    private TimePicker tpTime;
    /**
     * Завдання яке переглядається
     */
    private Task task = null;
    /**
     * Користувач який переглядає завдання
     */
    private User user = null;

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see Nullable
     * @see Activity#onCreate(Bundle)
     * @see Activity#setContentView(View)
     * @see Activity#setRequestedOrientation(int)
     * @see TaskViewerActivity#inicialComponents()
     * @see TaskViewerActivity#setTaskValuesForActivity()
     * @see TaskViewerActivity#checkTaskDone()
     * @see TaskViewerActivity#showMainViews()
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();
        setTaskValuesForActivity();
        checkTaskDone();
        showMainViews();
    }

    /**
     * Ініціалізує компоненти
     *
     * @see Bundle
     * @see Task
     * @see User
     */
    private void inicialComponents() {

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            user = (User) arguments.getSerializable(User.class.getSimpleName());
        }

        etName = findViewById(R.id.etName);
        etContent = findViewById(R.id.etContent);
        tvLeader = findViewById(R.id.tvLeader);
        etDateOfPublication = findViewById(R.id.tvDateOfPublication);
        etEndDate = findViewById(R.id.tvEndDate);
        etDateExecution = findViewById(R.id.tvDateExecution);

        btnGoBack = findViewById(R.id.btnGoBack);
        btnMarkAsDone = findViewById(R.id.btnMarkAsDone);
        btnOpenPageCreateComment = findViewById(R.id.btnOpenPageCreateComment);
        Button btnCreateComment = findViewById(R.id.btnCreateComment);
        Button btnGoToViewTask = findViewById(R.id.btnGoToViewTask);
        btnEditTask = findViewById(R.id.btnEditTask);
        btnViewExecutors = findViewById(R.id.btnViewExecutors);
        btnAddNewExecutors = findViewById(R.id.btnAddNewExecutors);
        Button btnGoBackFromExecutors = findViewById(R.id.btnGoBackFromExecutors);
        btnChangeEndDate = findViewById(R.id.btnChangeEndDate);
        btnDeleteTask = findViewById(R.id.btnDeleteTask);


        llTaskInformation = findViewById(R.id.llTaskInformation);
        clCreateComment = findViewById(R.id.clCreateComment);
        clExecutors = findViewById(R.id.clExecutors);
        clInputDateAndTime = findViewById(R.id.activity_input_date_and_time);

        dpDate = clInputDateAndTime.findViewById(R.id.dpDate);
        tpTime = clInputDateAndTime.findViewById(R.id.tpTime);
        tpTime.setIs24HourView(true);

        btnGoBack.setOnClickListener((View v) -> finish());

        btnMarkAsDone.setOnClickListener((View v) -> markAsDone());

        btnOpenPageCreateComment.setOnClickListener((View v) -> {
            hideMainViews();
            clCreateComment.setVisibility(View.VISIBLE);
        });

        btnCreateComment.setOnClickListener((View v) -> createComment());

        btnGoToViewTask.setOnClickListener((View v) -> showMainViews());

        tvLeader.setOnClickListener((View v) -> {
            Intent intent = new Intent(TaskViewerActivity.this, ProfileViewerActivity.class);
            intent.putExtra(Leader.class.getSimpleName(), task.getLeader());
            startActivity(intent);
        });

        btnEditTask.setOnClickListener((View v) -> updateTask());

        btnViewExecutors.setOnClickListener((View v) -> loadExecutors());

        btnAddNewExecutors.setOnClickListener((View v) -> {
            Intent intent = new Intent(TaskViewerActivity.this, FinderExecutorsActivity.class);
            intent.putExtra(Task.class.getSimpleName(), task);
            startActivity(intent);
            showMainViews();
        });

        btnGoBackFromExecutors.setOnClickListener((View v) -> showMainViews());

        btnChangeEndDate.setOnClickListener((View v) -> changeEndDate());

        btnDeleteTask.setOnClickListener((View v) -> deleteTask());
    }

    /**
     * Встановлює значення завдання для activity
     *
     * @see CommentDao
     * @see CommentDao#getListOfComments(Task)
     * @see TaskViewerActivity#createDesignForComment(Comment)
     */
    private void setTaskValuesForActivity() {
        etName.setText(task.getName());
        etContent.setText(task.getContent() == null ? "Зміст не доданий" : task.getContent());
        tvLeader.setText(task.getLeader().getLogin());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etDateOfPublication.setText(task.getDateTimeOfPublication().format(DateTimeFormatter.ofPattern("Дата:  yyyy.MM.dd\nЧас:    HH:mm")));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etEndDate.setText((task.getEndDateTime().format(DateTimeFormatter.ofPattern("Дата:  yyyy.MM.dd\nЧас:    HH:mm"))));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            etDateExecution.setText(task.getDateTimeExecution() == null
                    ? "Завдання не виконане"
                    : task.getDateTimeExecution().format(DateTimeFormatter.ofPattern("Дата:  yyyy.MM.dd\nЧас:    HH:mm")));
        }

        task.setComments(CommentDao.getListOfComments(task));
        LinearLayout llComments = findViewById(R.id.llComments);
        llComments.removeAllViews();
        for (Comment comment : task.getComments()) {
            llComments.addView(createDesignForComment(comment));
        }
    }

    /**
     * Перевіряє чи завдання є виконаним
     */
    private void checkTaskDone() {
        if (task.getDone()) {
            btnMarkAsDone.setText("Відмінити виконання");
            btnMarkAsDone.setBackgroundResource(R.drawable.round_delete_button);
        } else {
            btnMarkAsDone.setText("Позначити як виконане");
            btnMarkAsDone.setBackgroundResource(R.drawable.round_green_button);
        }
    }

    /**
     * Створює коментар
     *
     * @see CommentDao
     * @see CommentDao#createComment(Task, User, String)
     * @see Toast
     * @see TaskViewerActivity#setTaskValuesForActivity()
     * @see TaskViewerActivity#showMainViews()
     */
    private void createComment() {
        EditText etComment = findViewById(R.id.etComment);
        try {
            if(etComment.getText().toString().length() > 0) {
                CommentDao.createComment(task, user, etComment.getText().toString());
                Toast.makeText(this, "Коментар успішно створений", Toast.LENGTH_SHORT).show();
                setTaskValuesForActivity();
                clCreateComment.setVisibility(View.GONE);
                showMainViews();
                etComment.setText("");
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Не вдалося створити коментар", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Завантажує виконавців
     *
     * @see TaskViewerActivity#hideMainViews()
     * @see User
     */
    private void loadExecutors() {
        hideMainViews();
        clExecutors.setVisibility(View.VISIBLE);
        LinearLayout llExecutors = findViewById(R.id.llExecutors);
        llExecutors.removeAllViews();
        task.setExecutors(TaskDao.getListOfExecutorsForTask(task));
        for (User executor : task.getExecutors()) {
            if (task.getExecutors().size() == 1) {
                llExecutors.addView(createDesignForExecutor(executor, false));
            } else {
                llExecutors.addView(createDesignForExecutor(executor, true));
            }

        }

        btnAddNewExecutors.setVisibility(user.getId() == task.getLeader().getId() ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Створює дизайн для коментаря
     *
     * @param comment коментар
     * @return елемент з оформленим коментарем
     * @see Comment
     * @see CommentDao
     * @see CommentDao#deleteComment(Comment) CommentDao#deleteComment(Comment)
     * @see Toast
     */
    public View createDesignForComment(Comment comment) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setPadding(5, 15, 5, 15);
        linearLayout.setBackgroundResource(R.drawable.round_comment);

        TextView tvText = new TextView(this);
        tvText.setTextSize(18);
        tvText.setTextColor(Color.WHITE);
        layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tvText.setLayoutParams(layoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvText.setText(comment.getContent());
        }

        linearLayout.addView(tvText);

        TextView tvAboutComment = new TextView(this);
        tvAboutComment.setTextSize(18);
        tvAboutComment.setTextColor(Color.YELLOW);
        layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tvAboutComment.setLayoutParams(layoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvAboutComment.setText("Автор: " + comment.getUser().getLogin() + "\n" +
                    "Дата написання:\n" +
                    comment.getDateTimeOfWriting().format(DateTimeFormatter.ofPattern("Дата:  yyyy.MM.dd\nЧас:    HH:mm")));
        }

        linearLayout.addView(tvAboutComment);


        if (user.getId() == comment.getUser().getId()) {
            Button btn = new Button(this);
            btn.setText("Видалити");

            layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5, 0, 5);
            btn.setLayoutParams(layoutParams);
            btn.setBackgroundResource(R.drawable.round_delete_button);
            btn.setOnClickListener((View v) -> {
                try {
                    CommentDao.deleteComment(comment);
                    setTaskValuesForActivity();
                } catch (SQLException ex) {
                    Toast.makeText(this, "Не вдалося видалити коментар", Toast.LENGTH_LONG).show();
                }
            });
            linearLayout.addView(btn);
        }
        return linearLayout;
    }

    /**
     * Створює дизайн для відображення виконавця
     *
     * @param executor виконавець
     * @return елемент дизайну виконавця
     * @see User
     * @see Intent
     * @see Toast
     * @see TaskDao
     * @see TaskDao#deleteExecutorById(int, int) TaskDao#deleteExecutorById(int, int)
     * @see TaskViewerActivity#loadExecutors() TaskViewerActivity#loadExecutors()
     */
    public View createDesignForExecutor(User executor, boolean isCheckBox) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundResource(R.drawable.round_user);
        linearLayout.setOnClickListener((View v) -> {
            Intent intent = new Intent(TaskViewerActivity.this, ProfileViewerActivity.class);
            if (executor instanceof Leader) {
                intent.putExtra(Leader.class.getSimpleName(), executor);
            } else {
                intent.putExtra(Worker.class.getSimpleName(), executor);
            }
            startActivity(intent);
        });
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, height);
        layoutParams.setMargins(0, 5, 0, 0);
        linearLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
        linearLayout.setLayoutParams(layoutParams);

        TextView tv = new TextView(this);
        tv.setText(executor.getLogin());
        tv.setTextSize(20);
        tv.setAllCaps(false);

        if (user.getId() == task.getLeader().getId()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        TaskDao.deleteExecutorById(executor.getId(), task.getId());
                        Toast.makeText(TaskViewerActivity.this, "Виконавець успішно видалений", Toast.LENGTH_SHORT).show();
                        task.setExecutors(TaskDao.getListOfExecutorsForTask(task));
                        loadExecutors();
                    } catch (SQLException ex) {
                        Toast.makeText(TaskViewerActivity.this, "Не вдалося видалити виконавця", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (isCheckBox) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.INVISIBLE);
            }

            linearLayout.addView(checkBox);
        }
        linearLayout.addView(tv);

        return linearLayout;
    }

    /**
     * Помічає завдання як виконане
     *
     * @see TaskDao#setDone(Task, boolean)
     * @see TaskViewerActivity#checkTaskDone()
     * @see TaskViewerActivity#setTaskValuesForActivity()
     * @see Toast
     */
    private void markAsDone() {
        try {
            if (btnMarkAsDone.getText().toString().equals("Позначити як виконане")) {
                TaskDao.setDone(task, true);
            } else {
                TaskDao.setDone(task, false);
            }
            task = TaskDao.getTaskByID(task.getId());
            checkTaskDone();
            setTaskValuesForActivity();

        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося позначити завдання як виконане", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Змінює завдання
     *
     * @see Toast
     * @see TaskDao
     * @see TaskDao#updateTask(Task)
     */
    private void updateTask() {
        if (etName.getText().toString().equals("")) {
            Toast.makeText(this, "Навзва завдання не може бути пустою", Toast.LENGTH_LONG).show();
            return;
        }



        task.setName(etName.getText().toString());
        task.setContent(etContent.getText().toString());
        try {
            TaskDao.updateTask(task);
            Toast.makeText(this, "Зміни збережено", Toast.LENGTH_LONG).show();
        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося зберегти зміни", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Видаляє завдання
     *
     * @see Toast
     * @see TaskDao
     * @see TaskDao#deleteTask(Task)
     * @see Activity#finish()
     */
    private void deleteTask() {
        try {
            TaskDao.deleteTask(task);
            Toast.makeText(this, "Завдання успішно видалено", Toast.LENGTH_LONG).show();
            finish();
        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося видалити завдання", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Змінює дату закінчення завдання
     *
     * @see TaskViewerActivity#hideMainViews()
     * @see TaskViewerActivity#showMainViews()
     */
    private void changeEndDate() {
        hideMainViews();
        clInputDateAndTime.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dpDate.init(task.getEndDateTime().getYear(), task.getEndDateTime().getMonthValue() - 1,
                    task.getEndDateTime().getDayOfMonth(), null);
            tpTime.setHour(task.getEndDateTime().getHour());
            tpTime.setMinute(task.getEndDateTime().getMinute());
        }

        ConstraintLayout clDate = clInputDateAndTime.findViewById(R.id.clDate);
        ConstraintLayout clTime = clInputDateAndTime.findViewById(R.id.clTime);
        Button btnSaveDate = clInputDateAndTime.findViewById(R.id.btnSaveDate);
        Button btnSaveTime = clInputDateAndTime.findViewById(R.id.btnSaveTime);

        btnSaveDate.setOnClickListener((View v) -> {
            clDate.setVisibility(View.GONE);
            clTime.setVisibility(View.VISIBLE);
        });

        btnSaveTime.setOnClickListener((View v) -> {
            clDate.setVisibility(View.VISIBLE);
            clTime.setVisibility(View.GONE);
            clInputDateAndTime.setVisibility(View.GONE);
            showMainViews();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime newEndDateTime = LocalDateTime.of(dpDate.getYear(), dpDate.getMonth() + 1, dpDate.getDayOfMonth(),
                            tpTime.getHour(), tpTime.getMinute());
                    if(newEndDateTime.isBefore(task.getDateTimeOfPublication())) {
                        Toast.makeText(this, "Дата виконання не може бути раніше дати публікації", Toast.LENGTH_LONG).show();
                        return;
                    }
                    task.setEndDateTime(newEndDateTime);
                    etEndDate.setText(newEndDateTime.format(DateTimeFormatter.ofPattern("Дата:  yyyy.MM.dd\nЧас:    HH:mm")));
                }
            }
        });

    }


    /**
     * Приховує головні елементи activity при появі вікна вводу
     */
    private void hideMainViews() {
        llTaskInformation.setVisibility(View.GONE);
        btnGoBack.setVisibility(View.GONE);
        btnMarkAsDone.setVisibility(View.GONE);
        btnOpenPageCreateComment.setVisibility(View.GONE);
        btnEditTask.setVisibility(View.GONE);
        btnViewExecutors.setVisibility(View.GONE);
    }

    /**
     * Показує головні елементи activity при закритті вікна вводу
     */
    private void showMainViews() {
        if (task.getLeader().getId() == user.getId()) {
            btnEditTask.setVisibility(View.VISIBLE);
            btnMarkAsDone.setVisibility(View.INVISIBLE);
            etName.setEnabled(true);
            etContent.setEnabled(true);
            btnChangeEndDate.setVisibility(View.VISIBLE);
            btnDeleteTask.setVisibility(View.VISIBLE);
        } else {
            btnEditTask.setVisibility(View.INVISIBLE);
            btnMarkAsDone.setVisibility(View.VISIBLE);
            etName.setEnabled(false);
            etContent.setEnabled(false);
            btnChangeEndDate.setVisibility(View.GONE);
            btnDeleteTask.setVisibility(View.GONE);
        }
        llTaskInformation.setVisibility(View.VISIBLE);
        btnGoBack.setVisibility(View.VISIBLE);
        btnOpenPageCreateComment.setVisibility(View.VISIBLE);
        btnViewExecutors.setVisibility(View.VISIBLE);
        clCreateComment.setVisibility(View.GONE);
        clExecutors.setVisibility(View.GONE);
    }
}