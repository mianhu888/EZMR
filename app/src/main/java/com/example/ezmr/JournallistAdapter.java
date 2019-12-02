package com.example.ezmr;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class JournallistAdapter extends ArrayAdapter<String> {
    Context context;
    int resource;
    List<String> journals;
    SharedPreferences reminderPreferences;
    SharedPreferences notePreferences;
    SharedPreferences.Editor reminderEditor;
    SharedPreferences.Editor noteEditor;
    List<ReminderBean> reminderBeanList;
    List<String> noteList;

    public JournallistAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.journals = objects;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, parent,false);
        ImageButton ib_delete = convertView.findViewById(R.id.ib_delete_list);

        if(this.context instanceof MainActivity){
            ib_delete.setVisibility(View.GONE);
        }


        reminderPreferences = context.getSharedPreferences("reminders",Context.MODE_PRIVATE);
        notePreferences = context.getSharedPreferences("notes",Context.MODE_PRIVATE);
        reminderEditor = reminderPreferences.edit();
        noteEditor = notePreferences.edit();
        if(parent.getId()==R.id.lv1_JournalsActivity){
            //LV1
            String reminderJson = reminderPreferences.getString("reminder","");
            Gson gson = new Gson();
            if(reminderJson.equals("")){
                reminderBeanList = new ArrayList<>();
            }else{
                reminderBeanList = gson.fromJson(reminderJson,new TypeToken<List<ReminderBean>>(){}.getType());
            }

            ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    journals.remove(position);
                    reminderBeanList.remove(position);
                    Gson gson = new Gson();
                    String strJson = gson.toJson(reminderBeanList);
                    reminderEditor.putString("reminder",strJson);
                    reminderEditor.commit();
                    JournallistAdapter.this.notifyDataSetChanged();
                }
            });




        } else if(parent.getId()==R.id.lv2_JournalsActivity){
            //LV2
            String notesJson = notePreferences.getString("note","");
            Gson gson = new Gson();
            if(notesJson.equals("")){
                noteList = new ArrayList<>();
            }else{
                noteList = gson.fromJson(notesJson,new TypeToken<List<String>>(){}.getType());
            }

            ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    journals.remove(position);
                    noteList.remove(position);
                    Gson gson = new Gson();
                    String strJson = gson.toJson(noteList);
                    noteEditor.putString("note",strJson);
                    noteEditor.commit();
                    JournallistAdapter.this.notifyDataSetChanged();
                }
            });



        } else if(parent.getId()==R.id.lv3_JournalsActivity){
            System.out.println("lv3");



        }


        TextView tvJournalMain = convertView.findViewById(R.id.tv_journal_main);
        String journal = journals.get(position);
        tvJournalMain.setText(journal);

        return convertView;
    }


}
