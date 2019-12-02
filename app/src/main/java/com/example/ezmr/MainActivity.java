package com.example.ezmr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnPlaySong;
    Button btnFreePlay;
    Button btnJournal;
    ListView lvJournal;
    SharedPreferences reminderPreferences;
    List<ReminderBean> reminderBeanList;
    List<String> reminders;
    ArrayAdapter<String> reminderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        btnPlaySong = findViewById(R.id.btnPlaysongMain);
        btnFreePlay = findViewById(R.id.btnFreeplayMain);
        btnJournal = findViewById(R.id.btnJournalMain);
        lvJournal = findViewById(R.id.lv_journal_main);
        reminderPreferences = getSharedPreferences("reminders",MainActivity.MODE_PRIVATE);

        btnPlaySong.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseSongActivity.class);
                startActivity(intent);
            }
        });

        btnFreePlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FreePlayActivity.class);
                startActivity(intent);
            }
        });

        btnJournal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,JournalActivity.class);
                startActivity(intent);
            }
        });

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
        lvJournal.setAdapter(reminderAdapter);

    }
}
