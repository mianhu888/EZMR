package com.example.ezmr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JournalActivity extends AppCompatActivity {

    ListView lv1;
    ListView lv2;
    ListView lv3;
    ImageButton addReminders;
    ImageButton addNotes;
    View li_new_dialog;
    TimePicker timePicker;
    DatePicker datePicker;
    EditText edtDialog;
    SharedPreferences reminderPreferences;
    SharedPreferences notePreferences;
    SharedPreferences.Editor reminderEditor;
    SharedPreferences.Editor noteEditor;
    List<ReminderBean> reminderBeanList;
    List<String> notesList;
    List<String> reminders;
    ArrayAdapter<String> reminderAdapter;
    ArrayAdapter<String> notesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        setTitle("Journal");
        lv1 = findViewById(R.id.lv1_JournalsActivity);
        lv2 = findViewById(R.id.lv2_JournalsActivity);
        lv3 = findViewById(R.id.lv3_JournalsActivity);
        addReminders = findViewById(R.id.add_reminder);
        addNotes = findViewById(R.id.add_notes);
        li_new_dialog = View.inflate(this,R.layout.alertdialog_reminder_new, null);
        reminderPreferences = getSharedPreferences("reminders",Context.MODE_PRIVATE);
        notePreferences = getSharedPreferences("notes",Context.MODE_PRIVATE);
        reminderEditor = reminderPreferences.edit();
        noteEditor = notePreferences.edit();

        String reminderJson = reminderPreferences.getString("reminder","");
        Gson gson = new Gson();
        if(reminderJson.equals("")){
            reminderBeanList = new ArrayList<>();
        }else{
            reminderBeanList = gson.fromJson(reminderJson,new TypeToken<List<ReminderBean>>(){}.getType());
        }
        reminders = new ArrayList<>();
        for (ReminderBean reminder:reminderBeanList){
            reminders.add(reminder.toString());
        }
        reminderAdapter = new JournallistAdapter(this, R.layout.list_adaptor_main, reminders);
        lv1.setAdapter(reminderAdapter);
        addReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder newReminderDialog = new AlertDialog.Builder(JournalActivity.this);

                newReminderDialog.setTitle("Please enter reminder:").setView(li_new_dialog);
                edtDialog = li_new_dialog.findViewById(R.id.edt_content_dialog);
                timePicker = li_new_dialog.findViewById(R.id.timePicker);
                datePicker = li_new_dialog.findViewById(R.id.datePicker);
                timePicker.setVisibility(View.VISIBLE);
                datePicker.setVisibility(View.VISIBLE);
                timePicker.setIs24HourView(true);

                newReminderDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reminderEditor.clear();
                        Gson gson = new Gson();
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        String strTime = year+"-"+month+"-"+day+" "+hour+":"+minute;
                        Long longTime = getLongTime(strTime);
                        ReminderBean reminderBean = new ReminderBean(edtDialog.getText().toString(),longTime, strTime);
                        int j = reminderBeanList.size();

                        if(reminderBeanList.size()>0){

                            for(int k = 0; k<j;k++){
                                if(longTime<reminderBeanList.get(k).getLongTime()){
                                    reminderBeanList.add(k,reminderBean);
                                }
                                if(k == reminderBeanList.size()-1){
                                    reminderBeanList.add(k,reminderBean);
                                }
                            }
                        }else{
                            reminderBeanList.add(reminderBean);
                        }
                        String strJson = gson.toJson(reminderBeanList);
                        reminderEditor.putString("reminder",strJson);
                        reminderEditor.commit();
                        reminders = new ArrayList<>();
                        for (ReminderBean reminder:reminderBeanList){
                            reminders.add(reminder.toString());
                        }
                        reminderAdapter = new JournallistAdapter(JournalActivity.this, R.layout.list_adaptor_main, reminders);
                        lv1.setAdapter(reminderAdapter);
                    }
                });

                newReminderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                newReminderDialog.show();
            }
        });

        String notesJson = notePreferences.getString("note","");
        if(notesJson.equals("")){
            notesList = new ArrayList<>();
        }else{
            notesList = gson.fromJson(notesJson,new TypeToken<List<String>>(){}.getType());
        }
        notesAdapter = new JournallistAdapter(this, R.layout.list_adaptor_main, notesList);
        lv2.setAdapter(notesAdapter);
        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder newNotesDialog = new AlertDialog.Builder(JournalActivity.this);

                newNotesDialog.setTitle("Please enter notes:").setView(li_new_dialog);
                edtDialog = li_new_dialog.findViewById(R.id.edt_content_dialog);
                timePicker = li_new_dialog.findViewById(R.id.timePicker);
                datePicker = li_new_dialog.findViewById(R.id.datePicker);
                timePicker.setVisibility(View.GONE);
                datePicker.setVisibility(View.GONE);

                newNotesDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteEditor.clear();
                        Gson gson = new Gson();
                        notesList.add(edtDialog.getText().toString());
                        String strJson = gson.toJson(notesList);
                        noteEditor.putString("note",strJson);
                        noteEditor.commit();
                        notesAdapter = new JournallistAdapter(JournalActivity.this, R.layout.list_adaptor_main, notesList);
                        lv2.setAdapter(notesAdapter);
                    }
                });

                newNotesDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                newNotesDialog.show();
            }
        });

        List<String> archive = new ArrayList<>();
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        ArrayList<File> list = new ArrayList<File>();
        File[] allFiles = new File(dirPath).listFiles();
        if(allFiles != null){
            for(int i=0; i<allFiles.length;i++){
                File file = allFiles[i];
                if(file.isFile()){
                    list.add(file);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            // full name
            String mPicName = list.get(i).getName();

            // suffix name
            String substring = mPicName.substring(mPicName.lastIndexOf(".") + 1);

            if(substring.equals("wav")&&(!mPicName.equals("record.wav"))){
                archive.add(mPicName);
            }
        }
        ArrayAdapter<String> archiveAdapter = new JournallistAdapter(this, R.layout.list_adaptor_main, archive);
        lv3.setAdapter(archiveAdapter);
    }

    //get String longTime from long longTime
    String getStringTime(Long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    Long getLongTime(String time){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0L;
    }
}

class ReminderBean{
    String content;
    Long longTime;
    String strTime;

    @Override
    public String toString() {
        return strTime + " - " + content;
    }

    public ReminderBean(String content, Long longTime, String strTime) {
        this.content = content;
        this.longTime = longTime;
        this.strTime = strTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getLongTime() {
        return longTime;
    }

    public void setLongTime(Long longTime) {
        this.longTime = longTime;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }
}
