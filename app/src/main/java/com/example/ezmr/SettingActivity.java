package com.example.ezmr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    RadioGroup rg1;
    RadioGroup rg2;
    RadioGroup rg3;
    int rg1_mode;
    int rg2_mode;
    int rg3_mode;
    int from_page;
    int beatup;
    int beatlow;
    Button btn_apply;
    Button btn_cancel;
    Intent intent;
    RadioButton rg1_piano;
    RadioButton rg1_violin;
    RadioButton rg1_saxophone;
    RadioButton rg2_recording;
    RadioButton rg2_generate;
    RadioButton rg3_gclef;
    RadioButton rg3_fclef;
    TextView tv_clef_setting;
    TextView tv_beat_setting;
    EditText edt_beatup_setting;
    EditText edt_beatlow_setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Settings:");

        intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        rg1_mode = bundle.getInt("rg1");
        rg2_mode = bundle.getInt("rg2");
        rg3_mode = bundle.getInt("rg3",0);
        from_page = bundle.getInt("page");
        beatup = bundle.getInt("beatup");
        beatlow = bundle.getInt("beatlow");



        rg1 = findViewById(R.id.radioGroup1);
        rg2 = findViewById(R.id.radioGroup2);
        rg3 = findViewById(R.id.radioGroup3);
        btn_apply = findViewById(R.id.btn_apply_SettingActivity);
        btn_cancel = findViewById(R.id.btn_cancel_SettingActivity);
        rg1_piano = findViewById(R.id.rg1_piano);
        rg1_violin = findViewById(R.id.rg1_violin);
        rg1_saxophone = findViewById(R.id.rg1_saxophone);
        rg2_recording = findViewById(R.id.rg2_recording);
        rg2_generate = findViewById(R.id.rg2_generate);
        rg3_gclef = findViewById(R.id.rg3_gclef);
        rg3_fclef = findViewById(R.id.rg3_fclef);
        tv_clef_setting = findViewById(R.id.tv_clef_setting);
        tv_beat_setting = findViewById(R.id.tv_beat_setting);
        edt_beatup_setting = findViewById(R.id.edt_beatup_setting);
        edt_beatlow_setting = findViewById(R.id.edt_beatlow_setting);

        edt_beatup_setting.setText(beatup+"");
        edt_beatlow_setting.setText(beatlow+"");

        switch (from_page){
            case 0:
                rg3.setVisibility(View.INVISIBLE);
                tv_clef_setting.setVisibility(View.INVISIBLE);
                tv_beat_setting.setVisibility(View.INVISIBLE);
                edt_beatup_setting.setVisibility(View.INVISIBLE);
                edt_beatlow_setting.setVisibility(View.INVISIBLE);
                break;
            case 1:
                rg3.setVisibility(View.VISIBLE);
                tv_clef_setting.setVisibility(View.VISIBLE);
                tv_beat_setting.setVisibility(View.VISIBLE);
                edt_beatup_setting.setVisibility(View.VISIBLE);
                edt_beatlow_setting.setVisibility(View.VISIBLE);
                break;
        }
        switch (rg1_mode){
            case 0:
                rg1_piano.setChecked(true);
                break;
            case 1:
                rg1_violin.setChecked(true);
                break;
            case 2:
                rg1_saxophone.setChecked(true);
                break;
        }
        switch (rg2_mode){
            case 0:
                rg2_recording.setChecked(true);
                break;
            case 1:
                rg2_generate.setChecked(true);
                break;
        }
        switch (rg3_mode){
            case 0:
                rg3_gclef.setChecked(true);
                break;
            case 1:
                rg3_fclef.setChecked(true);
                break;
        }

        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rg1_piano:
                        rg1_mode=0;
                        break;
                    case R.id.rg1_violin:
                        rg1_mode=1;
                        break;
                    case R.id.rg1_saxophone:
                        rg1_mode=2;
                        break;
                }
            }
        });

        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rg2_recording:
                        rg2_mode=0;
                        break;
                    case R.id.rg2_generate:
                        rg2_mode=1;
                        break;
                }
            }
        });

        rg3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rg3_gclef:
                        rg3_mode=0;
                        break;
                    case R.id.rg3_fclef:
                        rg3_mode=1;
                        break;
                }
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backWithResult();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backWithoutResult();
            }
        });
    }

    void backWithResult(){
        Intent i = getIntent();
        i.putExtra("rg1",rg1_mode);
        i.putExtra("rg2",rg2_mode);
        i.putExtra("rg3",rg3_mode);
        i.putExtra("beatup",edt_beatup_setting.getText().toString());
        i.putExtra("beatlow",edt_beatlow_setting.getText().toString());
        setResult(RESULT_OK,i);
        finish();
    }

    void backWithoutResult(){
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        backWithoutResult();
    }
}
