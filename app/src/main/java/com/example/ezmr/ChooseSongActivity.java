package com.example.ezmr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ChooseSongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_song);
        setTitle("Choose a song:");
        final List<Song> songs = new ArrayList<>();
        songs.add(new Song(1,"Little star",1));
        songs.add(new Song(2,"Genuis",1));
        songs.add(new Song(3,"Wild",2));
        songs.add(new Song(4,"The Phoenix",3));
        ListView lv_songslist = findViewById(R.id.lv_songslist);
        ArrayAdapter<Song> adapter = new SonglistAdapter(this,R.layout.list_adaptor_song, songs);
        lv_songslist.setAdapter(adapter);
        lv_songslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song s = songs.get(position);
                Intent intent = new Intent(ChooseSongActivity.this, PracticeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name",s.getName());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}

class Song{
    private int id;
    private String name;
    private int star;

    Song(int i, String n, int s){
        id = i;
        name = n;
        star = s;
    }
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    public int getStar(){
        return star;
    }

    public void setStar(int star){
        this.star = star;
    }

}