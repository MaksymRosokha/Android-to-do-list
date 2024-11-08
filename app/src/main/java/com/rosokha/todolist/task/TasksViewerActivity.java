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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.tabs.TabLayout;
import com.rosokha.todolist.R;
import com.rosokha.todolist.dao.LoaderOfTasksDao;
import com.rosokha.todolist.dao.TaskDao;
import com.rosokha.todolist.users.Leader;
import com.rosokha.todolist.users.User;

/**
 * Представляє собою графічний користувацький інтерфейс перегляду завдань
 *
 * @see Activity
 */
public class TasksViewerActivity extends Activity {

    /**
     * Перемикач між типами завдань
     */
    private TabLayout tabLayout;

    /**
     * Користувач, який переглядає завдання
     */
    private User user;

    /**
     * Активні завдання
     */
    private Task[] activeTasks;

    /**
     * Виконані завдання
     */
    private Task[] completedTasks;

    /**
     * Завдання, які створив користувач
     */
    private Task[] createdTasks;

    /**
     * Тип, по якому потрібно філльтрувати завдання
     */
    private String filterType = "Даті публікації (зростання)";

    /**
     * Проводиться початкове налаштування activity
     *
     * @param savedInstanceState попередня властивість activity, якщо вона була збережена
     * @see Bundle
     * @see Nullable
     * @see Activity#onCreate(Bundle)
     * @see Activity#setContentView(View)
     * @see Activity#setRequestedOrientation(int)
     * @see TasksViewerActivity#inicialComponents()
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//блокує поворот екрану
        inicialComponents();
    }

    /**
     * Викликається при поверненні користувача в дане activity
     *
     * @see Activity#onResume()
     * @see TasksViewerActivity#setTabLayout(int)
     */
    @Override
    protected void onResume() {
        super.onResume();
        setTabLayout(tabLayout.getSelectedTabPosition());
    }

    /**
     * Ініціалізує компоненти
     *
     * @see Bundle
     * @see Spinner
     * @see TasksViewerActivity#setTabLayout(int)
     */
    private void inicialComponents() {
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            user = (User) arguments.getSerializable(User.class.getSimpleName());
        } else {
            Toast.makeText(this, "Сталася помилка при передачі аргументів", Toast.LENGTH_LONG).show();
            finish();
        }

        tabLayout = findViewById(R.id.tabLayout);

        Spinner spnFilter = findViewById(R.id.spnFilter);
        String[] dataForFilter = new String[]{"Даті публікації (зростання)", "Даті публікації (спадання)",
                "Даті закінчення (зростання)", "Даті закінчення (спадання)",
                "Даті виконання (зростання)", "Даті виконання (спадання)"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dataForFilter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilter.setAdapter(adapter);
        spnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterType = (String) parent.getItemAtPosition(position);
                setTabLayout(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnGoBack = findViewById(R.id.btnGoBack);
        Button btnCreateNewTask = findViewById(R.id.btnCreateNewTask);

        btnGoBack.setOnClickListener((View v) -> finish());
        btnCreateNewTask.setOnClickListener((View v) -> {
            Intent intent = new Intent(TasksViewerActivity.this, TaskCreatorActivity.class);
            startActivity(intent);
        });

        tabLayout.addTab(tabLayout.newTab().setText("Активні"));
        tabLayout.addTab(tabLayout.newTab().setText("Виконані"));
        if (user instanceof Leader) {
            tabLayout.addTab(tabLayout.newTab().setText("Створені"));
        }
        tabLayout.setBackgroundColor(Color.rgb(0, 0, 55));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabLayout(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    /**
     * Встановлює activity перегляду завдань залежно від позиції перемикача
     *
     * @param position позиція перемикача
     */
    private void setTabLayout(int position) {
        ConstraintLayout clActiveTasks = findViewById(R.id.clActiveTasks);
        ConstraintLayout clСompletedTasks = findViewById(R.id.clСompletedTasks);
        ConstraintLayout clСreatedTasks = findViewById(R.id.clСreatedTasks);

        switch (position) {
            case 0: {
                clActiveTasks.setVisibility(View.VISIBLE);
                clСompletedTasks.setVisibility(View.GONE);
                clСreatedTasks.setVisibility(View.GONE);
                showActiveTasks();
                break;
            }
            case 1: {
                clActiveTasks.setVisibility(View.GONE);
                clСompletedTasks.setVisibility(View.VISIBLE);
                clСreatedTasks.setVisibility(View.GONE);
                showСompletedTasks();
                break;
            }
            case 2: {
                clActiveTasks.setVisibility(View.GONE);
                clСompletedTasks.setVisibility(View.GONE);
                clСreatedTasks.setVisibility(View.VISIBLE);
                showСreatedTasks();
                break;
            }
        }
    }

    /**
     * Показує активні завдання
     *
     * @see LoaderOfTasksDao
     * @see LoaderOfTasksDao#getListOfActiveTasks(int)
     * @see TasksViewerActivity#createDesignForTask(Task)
     */
    private void showActiveTasks() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                activeTasks = LoaderOfTasksDao.getListOfActiveTasks(user.getId()).toArray(new Task[0]);
                filterTasks(activeTasks);
            } else {
                throw new SQLException();
            }

            LinearLayout llActiveTasks = findViewById(R.id.llActiveTasks);
            llActiveTasks.removeAllViews();
            for (Task task : activeTasks) {
                llActiveTasks.addView(createDesignForTask(task));
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося завантажити завдання", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Показує виконані завдання
     *
     * @see LoaderOfTasksDao
     * @see LoaderOfTasksDao#getListOfСompletedTasks(int)
     * @see TasksViewerActivity#createDesignForTask(Task)
     */
    private void showСompletedTasks() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                completedTasks = LoaderOfTasksDao.getListOfСompletedTasks(user.getId()).toArray(new Task[0]);
                filterTasks(completedTasks);
            } else {
                throw new SQLException();
            }

            LinearLayout llСompletedTasks = findViewById(R.id.llСompletedTasks);
            llСompletedTasks.removeAllViews();
            for (Task task : completedTasks) {
                llСompletedTasks.addView(createDesignForTask(task));
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося завантажити завдання", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Показує створені користувачем завдання
     *
     * @see LoaderOfTasksDao
     * @see LoaderOfTasksDao#getListOfСreatedTasks(int)
     * @see TasksViewerActivity#createDesignForTask(Task)
     */
    private void showСreatedTasks() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                createdTasks = LoaderOfTasksDao.getListOfСreatedTasks(user.getId()).toArray(new Task[0]);
                filterTasks(createdTasks);
            } else {
                throw new SQLException();
            }

            LinearLayout llСreatedTasks = findViewById(R.id.llСreatedTasks);
            llСreatedTasks.removeAllViews();
            for (Task task : createdTasks) {
                llСreatedTasks.addView(createDesignForTask(task));
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "Не вдалося завантажити завдання", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Створює дизайн для завдання
     *
     * @param task завданння, для якого потрібно створити дизайн
     * @return дизайн завдання
     * @see Task
     * @see Intent
     */
    public View createDesignForTask(Task task) {
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics());
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundResource(task.getColorTask(tabLayout.getSelectedTabPosition()));
            linearLayout.setOnClickListener((View v) -> {
                Intent intent = new Intent(TasksViewerActivity.this, TaskViewerActivity.class);
                intent.putExtra(Task.class.getSimpleName(), task);
                intent.putExtra(User.class.getSimpleName(), user);
                startActivity(intent);
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, height);
            layoutParams.setMargins(5, 5, 5, 5);
            linearLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            linearLayout.setLayoutParams(layoutParams);

            TextView tv = new TextView(this);
            tv.setText(task.getName());
            tv.setTextSize(20);
            tv.setAllCaps(false);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setChecked(task.getDone());

            checkBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                if (isChecked) {
                    TaskDao.setDone(task, true);
                } else {
                    TaskDao.setDone(task, false);
                }
                onResume();
            });

            linearLayout.addView(checkBox);
            linearLayout.addView(tv);
            return linearLayout;
    }

    /**
     * Фільтрує масив завдань
     *
     * @param tasks масив із завданнями
     * @see Task
     */
    private void filterTasks(Task[] tasks) {
        switch (filterType) {
            case "Даті публікації (зростання)": {
                for (int i = 0; i < tasks.length; i++) {
                    for (int j = 1; j < tasks.length; j++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (tasks[j - 1].getDateTimeOfPublication().isAfter(tasks[j].getDateTimeOfPublication())) {
                                Task temp = tasks[j - 1];
                                tasks[j - 1] = tasks[j];
                                tasks[j] = temp;
                            }
                        }
                    }
                }
                break;
            }
            case "Даті публікації (спадання)": {
                for (int i = 0; i < tasks.length; i++) {
                    for (int j = 1; j < tasks.length; j++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (tasks[j - 1].getDateTimeOfPublication().isBefore(tasks[j].getDateTimeOfPublication())) {
                                Task temp = tasks[j - 1];
                                tasks[j - 1] = tasks[j];
                                tasks[j] = temp;
                            }
                        }
                    }
                }
                break;
            }
            case "Даті закінчення (зростання)": {
                for (int i = 0; i < tasks.length; i++) {
                    for (int j = 1; j < tasks.length; j++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (tasks[j - 1].getEndDateTime().isAfter(tasks[j].getEndDateTime())) {
                                Task temp = tasks[j - 1];
                                tasks[j - 1] = tasks[j];
                                tasks[j] = temp;
                            }
                        }
                    }
                }
                break;
            }
            case "Даті закінчення (спадання)": {
                for (int i = 0; i < tasks.length; i++) {
                    for (int j = 1; j < tasks.length; j++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (tasks[j - 1].getEndDateTime().isBefore(tasks[j].getEndDateTime())) {
                                Task temp = tasks[j - 1];
                                tasks[j - 1] = tasks[j];
                                tasks[j] = temp;
                            }
                        }
                    }
                }
                break;
            }
            case "Даті виконання (зростання)": {
                for (int i = 0; i < tasks.length; i++) {
                    for (int j = 1; j < tasks.length; j++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (tasks[j - 1].getDateTimeExecution() != null && tasks[j].getDateTimeExecution() != null) {
                                if (tasks[j - 1].getDateTimeExecution().isAfter(tasks[j].getDateTimeExecution())) {
                                    Task temp = tasks[j - 1];
                                    tasks[j - 1] = tasks[j];
                                    tasks[j] = temp;
                                }
                            }
                        }
                    }
                }
                break;
            }
            case "Даті виконання (спадання)": {
                for (int i = 0; i < tasks.length; i++) {
                    for (int j = 1; j < tasks.length; j++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (tasks[j - 1].getDateTimeExecution() != null && tasks[j].getDateTimeExecution() != null) {
                                if (tasks[j - 1].getDateTimeExecution().isBefore(tasks[j].getDateTimeExecution())) {
                                    Task temp = tasks[j - 1];
                                    tasks[j - 1] = tasks[j];
                                    tasks[j] = temp;
                                }
                            }
                        }
                    }
                }
                break;
            }
            default: {
                Toast.makeText(this, "Помилка під час фільтрацій", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}