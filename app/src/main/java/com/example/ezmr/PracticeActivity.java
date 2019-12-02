package com.example.ezmr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.Map;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.writer.WriterProcessor;

public class PracticeActivity extends AppCompatActivity {
    final static String[] littleStar = {
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
            "spaceone","la","spaceone","la","spaceone","sollong","spaceone","noteheadNull","spacetwo","barlineSingle",
            "spaceone","fa","spaceone","fa","spaceone","mi","spaceone","mi","spacetwo","barlineSingle",
            "spaceone","re","spaceone","re","spaceone","dolong","spacetwo","spacetwo","barlineSingle"
    };
//    final static String[] littleStar = {"staff5LinesWide",
//            "spaceone","do","staff5LinesWide","spaceone","do","staff5LinesWide","spaceone","staff5LinesWide","sol","spaceone","staff5LinesWide","sol","staff5LinesWide","spacetwo","staff5LinesWide","barlineSingle",
//            "spaceone","la","staff5LinesWide","spaceone","la","staff5LinesWide","spaceone","sollong","staff5LinesWide","spacetwo","staff5LinesWide","spacetwo","barlineSingle","staff5LinesWide",
//            "spaceone","fa","staff5LinesWide","spaceone","fa","staff5LinesWide","spaceone","mi","staff5LinesWide","spaceone","mi","staff5LinesWide","spacetwo","barlineSingle","staff5LinesWide",
//            "spaceone","re","staff5LinesWide","spaceone","re","staff5LinesWide","spaceone","dolong","staff5LinesWide","spacetwo","staff5LinesWide","spacetwo","barlineSingle"
//    };
    int[] colorPos =new int[littleStar.length*2];
    double[] noteFreInHz = new double[littleStar.length];
    double delayTime=0.0;
    int colorindex=0;
    TextView tv_notes_display;
    TextView tv_notes_generate;
    Button btn_detectnotes;
    Button btn_Save_PlayActivity;
    Button btn_Playback_PlayActivity;
    ImageButton ibtn_Stop_PlayActivity;
    ImageButton ibtn_Play_PlayActivity;
    ImageButton ibtn_Setting_PlayActivity;
    HorizontalScrollView hsv_display;
    HorizontalScrollView hsv_generate;

    Spinner spinner_speed;

    SpannableString spanDisplayString;
    SpannableString spanGenerateString;
    ForegroundColorSpan spanColorStyle;
    ForegroundColorSpan[] spanStyleCollection;
    OneTimeBuzzer buzzer;
    Thread generateThread;
    Thread playThread;
    Thread playbackThread;
    float pitchInHz;
    String[] generateNotes;
    int[] correctness;
    double speedFactor = 1.0;
    int hsv_display_offset;
    double play_time;
    int generateState=0;
    int generateColorPos;
    boolean isRecording=false;
    AudioDispatcher dispatcher;
    int rg1_mode=0;
    int rg2_mode=0;
    int rg3_mode=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String name = bundle.getString("name");
        setTitle(name);
        tv_notes_display = findViewById(R.id.tv_notes_display);
        tv_notes_generate = findViewById(R.id.tv_notes_generate);
        btn_detectnotes = findViewById(R.id.btn_detectnotes);
        btn_Save_PlayActivity = findViewById(R.id.btnSave_PlayActivity);
        btn_Playback_PlayActivity = findViewById(R.id.btnPlayback_PlayActivity);
        ibtn_Stop_PlayActivity = findViewById(R.id.ibStop_PlayActivity);
        ibtn_Play_PlayActivity = findViewById(R.id.ibPlay_PlayActivity);
        ibtn_Setting_PlayActivity = findViewById(R.id.ibSetting_PlayActivity);
        spinner_speed = findViewById(R.id.spinner_speed);
        hsv_display = findViewById(R.id.hsv_display);
        hsv_generate = findViewById(R.id.hsv_generate);



//        String noteUnicode = "E01A\\uE050\\uE014\\uE09E\\uE084\\uE09F\\uE084\\u" +
//                "E01A\\uEB9D\\uE0A4\\uEB9D\\uE210\\uE01A\\uEB9D\\uE0A4\\uEB9D\\uE210\\uEB99\\uE0A4\\uEB99\\uE210\\uE01A\\uEB99\\uE0A4\\uEB99\\uE210\\uE030\\u" +
//                "EB98\\uE0A4\\uEB98\\uE210\\uE01A\\uEB98\\uE0A4\\uEB98\\uE210\\uEB99\\uE0A3\\uEB99\\uE210";
        String notesUnicode = gson2Unicode(littleStar);
        String notesText = unicode2String(notesUnicode);
        spanDisplayString = new SpannableString(notesText);
        spanColorStyle = new ForegroundColorSpan(Color.RED);
        tv_notes_display.setText(notesText);
        spinner_speed.setSelection(1,true);
        spinner_speed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        speedFactor=0.5;
                        break;
                    case 1:
                        speedFactor=1;
                        break;
                    case 2:
                        speedFactor=2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_detectnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (generateState==0){ //not generating
                    dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
                    TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,22050,2*8,1,2*1,22050, ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()));
                    File wavfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), String.format("record.wav"));

                    try {
                        RandomAccessFile wfile = new RandomAccessFile(wavfile,"rw");
                        AudioProcessor p1 = new WriterProcessor(format,wfile);
                        dispatcher.addAudioProcessor(p1);
                    }catch(IOException e){
                        System.out.println("1failed");
                    }
                    dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {
                        @Override
                        public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                            pitchInHz = pitchDetectionResult.getPitch();
                        }
                    }));
                    new Thread(dispatcher,"Audio Dispatcher").start();
                    generateNotes();
                    btn_detectnotes.setText("STOP");
                    generateState=1;
                }else if (generateState==1){ //generating
                    isRecording=false;
                    generateThread.interrupt();
                    btn_detectnotes.setText("RE-DO");
                    btn_Save_PlayActivity.setVisibility(View.VISIBLE);
                    generateState=0;
                    dispatcher.stop();
                }

            }
        });

        btn_Save_PlayActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(PracticeActivity.this);
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(PracticeActivity.this);
                saveDialog.setTitle("Please enter file name:").setView(editText);
                saveDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = editText.getText().toString();
                        File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), String.format("record.wav"));
                        File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),String.format(s+".wav"));
                        from.renameTo(to);
                        btn_Save_PlayActivity.setVisibility(View.INVISIBLE);
                        btn_detectnotes.setText("START");
                    }
                }).show();
            }
        });

        btn_Playback_PlayActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();
                btn_Playback_PlayActivity.setText("STOP");
            }
        });

        ibtn_Play_PlayActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                initplayThread();
            }
        });

        ibtn_Stop_PlayActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playThread.interrupt();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spanDisplayString.setSpan(spanColorStyle,0,0,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        colorindex=0;
                        tv_notes_display.setText(spanDisplayString);
                        hsv_display.smoothScrollTo(0,0);
                    }
                });
            }
        });

        ibtn_Setting_PlayActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PracticeActivity.this, SettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("page",0);
                bundle.putInt("rg1",rg1_mode);
                bundle.putInt("rg2",rg2_mode);
                bundle.putInt("rg3",rg3_mode);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            rg1_mode = data.getIntExtra("rg1",0);
            rg2_mode = data.getIntExtra("rg2",0);
        }
    }

    void playRecord(){
        playbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mediaPlayer = new MediaPlayer();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/record.wav";
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Thread.sleep(mediaPlayer.getDuration()+500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_Playback_PlayActivity.setText("PLAYBACK");
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        playbackThread.start();
    }


    void generateNotes(){
        tv_notes_generate.setText("");

        generateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 1;
                    int pitchNum = 0;
                    double pitchTotal = 0.0;
                    generateColorPos=0;
                    generateNotes = new String[300];
                    correctness = new int[300];
                    spanStyleCollection = new ForegroundColorSpan[150];
                    int notesNum=0;
                    hsv_display.smoothScrollTo(0,0);
                    hsv_generate.smoothScrollTo(0,0);
                    while (true) {
                        int duration = (int)(noteFreInHz[i]+0.25)*10;
                        double maxDelta=0;
                        if(pitchNum < duration){
                            pitchTotal += pitchInHz;
                            pitchNum++;
                            Thread.sleep((long)(100/speedFactor));
                        }else{
                            pitchTotal = pitchTotal/duration;
                            generateNotes[notesNum]="spaceone";
                            generateColorPos++;
                            notesNum++;

                            if(pitchTotal >= 245.6 && pitchTotal < 277.6) {
                                //1, C4
                                generateNotes[notesNum]=("do");
                                maxDelta = 256;
                            }
                            else if(pitchTotal >= 277.6 && pitchTotal < 311.6) {
                                //2, D4
                                generateNotes[notesNum]=("re");
                                maxDelta = 256;
                            }
                            else if(pitchTotal >= 311.6 && pitchTotal < 339.6) {
                                //3, E4
                                generateNotes[notesNum]=("mi");
                                maxDelta = 324;
                            }
                            else if(pitchTotal >= 339.6 && pitchTotal < 370.6) {
                                //4, F4
                                generateNotes[notesNum]=("fa");
                                maxDelta = 457;
                            }
                            else if(pitchTotal >= 370.6 && pitchTotal <= 416.0) {
                                //5, G4
                                generateNotes[notesNum]=("sol");
                                maxDelta = 576;
                            }
                            else if(pitchTotal >= 416.0 && pitchTotal < 466.9) {
                                //6, A4
                                generateNotes[notesNum]=("la");
                                maxDelta = 724;
                            }
                            else if(pitchTotal >= 466.9 && pitchTotal < 521) {
                                //7, B4
                                generateNotes[notesNum]="si";
                                maxDelta = 740;
                            }else {
                                generateNotes[notesNum]="noteheadNull";
                                maxDelta = 1;
                            }
                            double deltaHz = pitchTotal - noteFreInHz[i-1];
                            if(deltaHz*deltaHz>maxDelta){
                                System.out.println("i:"+i);
                                System.out.println("n:"+generateColorPos);
                                correctness[i-1]=generateColorPos;
                                generateColorPos+=2;
                                correctness[i]=generateColorPos;
                            }else {
                                generateColorPos+=2;
                                System.out.println("correct");
                            }
                            notesNum++;
                            if(notesNum%10==8){
                                generateNotes[notesNum]="spacetwo";
                                generateColorPos++;
                                notesNum++;
                                generateNotes[notesNum]="barlineSingle";
                                notesNum++;
                                generateColorPos++;
                            }
                            hsv_display.smoothScrollBy(63,0);
                            hsv_generate.smoothScrollBy(63,0);
                            String text = unicode2String(gson2Unicode(generateNotes));
                            spanGenerateString = new SpannableString(text);
                            for(int q=0;q<300;q+=2){
                                if(q==0||correctness[q]!=0){
                                    spanStyleCollection[q/2]= new ForegroundColorSpan(Color.RED);
                                    spanGenerateString.setSpan(spanStyleCollection[q/2],correctness[q],correctness[q+1],Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                    //spanGenerateString.setSpan(spanStyleCollection[q/2],1,3,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_notes_generate.setText(spanGenerateString);
                                }
                            });
                            pitchNum=0;
                            i+=2;
                            pitchTotal=0.0;

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        generateThread.start();
    }

    void initplayThread(){
        hsv_display_offset = 0;
        play_time=0.0;
        playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    buzzer = new OneTimeBuzzer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_notes_display.setText(spanDisplayString);
                        }
                    });
                    hsv_display.smoothScrollTo(hsv_display_offset,0);
                    for (int i = 0; i<30;i+=2){
                        if (colorPos[colorindex]!=0){
                            spanDisplayString.setSpan(spanColorStyle, colorPos[colorindex], colorPos[colorindex+1],Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            buzzer.setToneFreqInHz(noteFreInHz[colorindex]);
                            buzzer.setDuration(noteFreInHz[colorindex+1]/speedFactor);
                            buzzer.play();
                            delayTime = (noteFreInHz[colorindex+1]+0.25)/speedFactor;
                            colorindex+=2;
                        }else {
                            spanDisplayString.setSpan(spanColorStyle,0,0,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            colorindex=0;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_notes_display.setText(spanDisplayString);
                            }
                        });

                        play_time= play_time + delayTime;
                        Thread.sleep((long)(delayTime*1000));
                        if((play_time)%(4/speedFactor)==0&&play_time!=0){
                            hsv_display_offset+=63;
                        }
                        if(colorindex!=0){
                            hsv_display.smoothScrollTo((hsv_display_offset += 63 * delayTime*speedFactor),0);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        playThread.start();

    }

    public String gson2Unicode(String []gson){
        NotesGson notesGson = new NotesGson();
        Map<String, NotesBean> map = notesGson.Gson2Notes(getJson("notes.json", PracticeActivity.this));

        String noteUnicode = "";
        int tempindex=0;
        int i = 0;
        for (String s : gson){
            if(s!=null){
                noteUnicode = noteUnicode + map.get(s).getUnicode();
                if(map.get(s).isTone()==true){
                    colorPos[i]=tempindex;
                    colorPos[i+1]=tempindex+map.get(s).getLength();
                    noteFreInHz[i]=map.get(s).getToneFreqInHz();
                    noteFreInHz[i+1] = map.get(s).getDuration();
                    i+=2;
                }
                tempindex += map.get(s).getLength();
            }
        }
        return noteUnicode;
    }

    public String unicode2String(String unicode){
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("_u");
        for(int i = 0; i < hex.length; i++){
            try{
                int data = Integer.parseInt(hex[i], 16);
                string.append((char)data);

            }catch (Exception e){
                string.append(hex[i]);
            }
        }
        return string.toString();
    }

    /**
     * 读取assets本地json
     * @param fileName
     * @param context
     * @return
     */
    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }




}
