package com.ramson.mobilesmachines.easytodolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity
        extends AppCompatActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView list_all_tasks;
    private DB db;
    private ArrayList<Task> tasks;
    private TasksCustomAdapter customTasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        list_all_tasks = (ListView) findViewById(R.id.list_all_tasks);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTasks();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        editTask(adapterView, i);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        deleteTask(adapterView, i);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        popupMenu.show();

        return true;
    }

    private void showTasks(String... args) {
        if (db == null) {
            db = new DB(this);
        }

        if (db.getAllTasks().size() == 0) {
            db.insertNewTask(getString(R.string.sample_task_title), getString(R.string.sample_task_desc), getString(R.string.sample_task_date));
            Toast.makeText(this, getString(R.string.sample_task_created_toast), Toast.LENGTH_SHORT).show();
        }

        if (args.length != 0 && args[0].toLowerCase().equals("completed")) {
            tasks = db.getAllCompletedTasks();

        } else if (args.length != 0 && args[0].toLowerCase().equals("incomplete")) {
            tasks = db.getAllInCompleteTasks();

        } else {
            tasks = db.getAllTasks();
        }

        Collections.sort(tasks, new DateSorter());
        customTasksAdapter = new TasksCustomAdapter(getApplicationContext(), tasks);
        list_all_tasks.setAdapter(customTasksAdapter);
        list_all_tasks.setOnItemClickListener(this);
        list_all_tasks.setOnItemLongClickListener(this);
    }

    private void newTask() {
        View view = getLayoutInflater().inflate(R.layout.dialog_new_task, null);
        final EditText edt_title = (EditText) view.findViewById(R.id.edt_title);
        final EditText edt_description = (EditText) view.findViewById(R.id.edt_description);
        final DatePicker date_due = (DatePicker) view.findViewById(R.id.date_due);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = edt_title.getText() != null ? edt_title.getText().toString() : "";
                String description = edt_description.getText() != null ? edt_description.getText().toString() : "";
                String dueDate = date_due.getDayOfMonth() + "/" + (date_due.getMonth() + 1) + "/" + date_due.getYear();

                if (title.equals("") && description.equals("")) {
                    Toast.makeText(MainActivity.this, R.string.no_changes, Toast.LENGTH_SHORT).show();
                } else {
                    db.insertNewTask(title, description, dueDate);
                    Toast.makeText(MainActivity.this, getString(R.string.task_created_toast), Toast.LENGTH_SHORT).show();
                    showTasks();
                }

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void editTask(AdapterView<?> adapterView, int i) {
        final Task task = (Task) adapterView.getAdapter().getItem(i);
        View view = getLayoutInflater().inflate(R.layout.dialog_new_task, null);

        final EditText edt_title = (EditText) view.findViewById(R.id.edt_title);
        edt_title.setText(task.getTitle());

        final EditText edt_description = (EditText) view.findViewById(R.id.edt_description);
        edt_description.setText(task.getDescription());

        final DatePicker date_due = (DatePicker) view.findViewById(R.id.date_due);
        String dueDate = task.getDueDate();
        int dayOfMonth = Integer.parseInt(dueDate.split("/", 3)[0]);
        int month = Integer.parseInt(dueDate.split("/", 3)[1]) - 1;
        int year = Integer.parseInt(dueDate.split("/", 3)[2]);
        date_due.init(year, month, dayOfMonth, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = edt_title.getText() != null ? edt_title.getText().toString() : "";
                String description = edt_description.getText() != null ? edt_description.getText().toString() : "";
                String dueDate = date_due.getDayOfMonth() + "/" + (date_due.getMonth() + 1) + "/" + date_due.getYear();

                if (title.equals("") && description.equals("")) {
//                    dialogInterface.dismiss();
                    Toast.makeText(MainActivity.this, R.string.no_changes, Toast.LENGTH_SHORT).show();
                } else {
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setDueDate(dueDate);
                    db.updateTask(task);
                    showTasks();
                }

            }
        });
        builder.setCancelable(false);
        builder.show();

        db.updateTask(task);
        showTasks();
    }

    private void deleteTask(AdapterView<?> adapterView, int i) {
        db.deleteTask(((Task) adapterView.getAdapter().getItem(i)).getId());
        Toast.makeText(this, getString(R.string.delete_toast), Toast.LENGTH_SHORT).show();
        showTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_task:
                newTask();
                break;

            case R.id.show_incomplete:
                showTasks("incomplete");
                break;

            case R.id.show_completed:
                showTasks("completed");
                break;

            case R.id.show_all:
                showTasks();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class DateSorter implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {

            String date1 = dateToNumber(((Task) o1).getDueDate());
            String date2 = dateToNumber(((Task) o2).getDueDate());
            return date1.compareTo(date2);
        }
    }

    private static String dateToNumber(String date) {
        return date.split("/")[2] + date.split("/")[1] + date.split("/")[0];
    }
}