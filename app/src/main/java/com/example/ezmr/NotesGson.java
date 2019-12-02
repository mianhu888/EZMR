package com.example.ezmr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class NotesGson {

    public String Notes2Gson() {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<String, NotesBean> map = new LinkedHashMap<String, NotesBean>();
        map.put("C1", new NotesBean(1, "\\uEB9D\\uE0A4\\uEB9D\\uE210", true,1,440));
        map.put("C2", new NotesBean(1, "\\uEB9D\\uE0A4\\uEB9D\\uE210", true,1,330));
        map.put("C3", new NotesBean(1, "\\uEB9D\\uE0A4\\uEB9D\\uE210", true,1,220));

        return gson.toJson(map);
    }

    public Map<String, NotesBean> Gson2Notes(String notesjson){
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Type type = new TypeToken<Map<String, NotesBean>>() {}.getType();
        Map<String, NotesBean> map = gson.fromJson(notesjson, type);
        return map;
    }
}
