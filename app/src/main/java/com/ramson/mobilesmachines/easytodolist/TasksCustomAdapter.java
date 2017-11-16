package com.ramson.mobilesmachines.easytodolist;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;


public class TasksCustomAdapter extends BaseAdapter implements Comparator {

    private Context context;
    private List<Task> tasks;

    private LayoutInflater inflater = null;


    public TasksCustomAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_content, null);
        }

        final LinearLayout ll_task = (LinearLayout) view.findViewById(R.id.ll_task);
        TextView main_date = (TextView) view.findViewById(R.id.main_date);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView due_date = (TextView) view.findViewById(R.id.due_date);
        final ImageView img_checkbox = (ImageView) view.findViewById(R.id.img_checkbox);

        main_date.setText(tasks.get(position).getDueDate());
        title.setText(tasks.get(position).getTitle());
        description.setText(tasks.get(position).getDescription());
        due_date.setText(tasks.get(position).getDueDate());

        if (tasks.get(position).getStatus() == 0) { // if task is incomplete
            showTaskAsIncomplete(img_checkbox, position, ll_task);
        }
        else {
            showTaskAsComplete(img_checkbox, position, ll_task);
        }

        img_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tasks.get(position).getStatus() == 0) {
                    showTaskAsComplete(img_checkbox, position, ll_task);
                    updateTaskStatus(tasks.get(position), 1);
                } else {
                    showTaskAsIncomplete(img_checkbox, position, ll_task);
                    updateTaskStatus(tasks.get(position), 0);
                }
            }
        });


        return view;
    }

    private void showTaskAsIncomplete(ImageView img_checkbox, int position, LinearLayout ll_task) {
        img_checkbox.setImageResource(android.R.drawable.checkbox_off_background);
        tasks.get(position).setStatus(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ll_task.setBackgroundColor(context.getResources().getColor(R.color.colorIncompleteTaskBkg));
        }
    }


    private void showTaskAsComplete(ImageView img_thunb, int position, LinearLayout ll_task) {
        img_thunb.setImageResource(android.R.drawable.checkbox_on_background);
        tasks.get(position).setStatus(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ll_task.setBackgroundColor(context.getResources().getColor(R.color.colorCompletedTaskBkg));
        }
    }


    private void updateTaskStatus(Task task, int newStatus) {
        DB db = new DB(context);
        db.updateTaskStatus(task.getId(), newStatus);
    }

    @Override
    public int compare(Object o1, Object o2) {
        return o2.toString().compareTo(o1.toString());
    }
}
