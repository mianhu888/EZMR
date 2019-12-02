package com.example.ezmr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
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

public class FreePlayActivity extends AppCompatActivity {
    TextView tv_freestyle;
    TextView tv_firstfix_freestyle;
    Button btn_start_freestyle;
    Button btn_save_freestyle;
    Button btn_playback_freestyle;
    ImageButton ib_setting_freestyle;
    ScrollView sv_freestyle;

    AudioDispatcher dispatcher;
    int generateState=0;
    float pitchInHz;
    Thread generateThread;
    Thread playbackThread;
    boolean isRecording=false;
    String[] generateNotes;
    String generatenotes;
    double[] noteFreInHz = new double[500];
    int rg3_mode = 0;
    int beatup = 4;
    int beatlow = 4;
    final static String[] testnotes = {
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
            "spaceone","do","spaceone","do","spaceone","sol","spaceone","sol","spacetwo","barlineSingle",
    };

    final static String[] test2 = {
            "staff5LinesWide","noteheadNull","staff5LinesWide","gClef","staff5LinesWide","4/4","staff5LinesWide","spaceone","barlineSingle",
            "staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull",
            "staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull",
            "staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull",
            "staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull","staff5LinesWide","noteheadNull"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_play);
        setTitle("FreeStyle");

        tv_freestyle = findViewById(R.id.tvFreestyle);
        tv_firstfix_freestyle = findViewById(R.id.tv_fistfix_freestyle);
        btn_start_freestyle = findViewById(R.id.btn_start_freestyle);
        btn_save_freestyle = findViewById(R.id.btn_save_freestyle);
        sv_freestyle = findViewById(R.id.sv_freestyle);
        btn_playback_freestyle = findViewById(R.id.btn_playback_freestyle);
        ib_setting_freestyle = findViewById(R.id.ib_setting_freestyle);

        String temp = unicode2String(gson2Unicode(testnotes));
        String temp1 = unicode2String(gson2Unicode(test2));
        tv_freestyle.setText(temp);
        //tv_firstfix_freestyle.setText(temp1);

        btn_start_freestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generateState==0){
                    dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
                    TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,22050,2*8,1,2*1,22050, ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()));
                    File wavfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), String.format("freestyle.wav"));

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
                    btn_start_freestyle.setText("STOP");
                    generateState=1;
                } else if (generateState==1){ //generating
                    isRecording=false;
                    generateThread.interrupt();
                    btn_start_freestyle.setText("RE-DO");
                    btn_save_freestyle.setVisibility(View.VISIBLE);
                    generateState=0;
                    dispatcher.stop();
                }
            }
        });

        btn_playback_freestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();
                btn_playback_freestyle.setText("STOP");
            }
        });

        btn_save_freestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(FreePlayActivity.this);
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(FreePlayActivity.this);
                saveDialog.setTitle("Please enter file name:").setView(editText);
                saveDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = editText.getText().toString();
                        File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), String.format("record.wav"));
                        File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),String.format(s+".wav"));
                        from.renameTo(to);
                        btn_save_freestyle.setVisibility(View.INVISIBLE);
                        btn_start_freestyle.setText("START");
                    }
                }).show();
            }
        });

        ib_setting_freestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreePlayActivity.this, SettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("page",1);
                bundle.putInt("rg3",rg3_mode);
                bundle.putInt("beatup",beatup);
                bundle.putInt("beatlow",beatlow);
                intent.putExtras(bundle);
                startActivityForResult(intent,2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            rg3_mode = data.getIntExtra("rg3",0);
            //System.out.println(data.getIntExtra("beatup",0));

            beatup = Integer.parseInt(data.getStringExtra("beatup"));
            beatlow = Integer.parseInt(data.getStringExtra("beatlow"));

        }
    }

    void playRecord(){
        playbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mediaPlayer = new MediaPlayer();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/freestyle.wav";
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Thread.sleep(mediaPlayer.getDuration()+500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_playback_freestyle.setText("PLAYBACK");
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
        tv_freestyle.setText("");

        generateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int pitchNum = 0;
                    double pitchTotal = 0.0;
                    generateNotes = new String[300];
                    int n=0;
                    sv_freestyle.smoothScrollTo(0,0);
                    while (true) {
                        int duration = 10;
                        if(pitchNum < duration){
                            pitchTotal += pitchInHz;
                            pitchNum++;
//                            Thread.sleep((long)(100/speedFactor));
                            Thread.sleep((long)(100));
                        }else{
                            pitchTotal = pitchTotal/duration;
                            generateNotes[n]="spaceone";
                            n++;

                            if(pitchTotal >= 245.6 && pitchTotal < 277.6) {
                                //1, C4
                                generateNotes[n]=("do");
                            }
                            else if(pitchTotal >= 277.6 && pitchTotal < 311.6) {
                                //2, D4
                                generateNotes[n]=("re");
                            }
                            else if(pitchTotal >= 311.6 && pitchTotal < 339.6) {
                                //3, E4
                                generateNotes[n]=("mi");
                            }
                            else if(pitchTotal >= 339.6 && pitchTotal < 370.6) {
                                //4, F4
                                generateNotes[n]=("fa");
                            }
                            else if(pitchTotal >= 370.6 && pitchTotal <= 416.0) {
                                //5, G4
                                generateNotes[n]=("sol");
                            }
                            else if(pitchTotal >= 416.0 && pitchTotal < 466.9) {
                                //6, A4
                                generateNotes[n]=("la");
                            }
                            else if(pitchTotal >= 466.9 && pitchTotal < 521) {
                                //7, B4
                                generateNotes[n]="si";
                            }else {
                                generateNotes[n]="noteheadNull";
                            }
                            n++;
                            if(n%10==8){
                                generateNotes[n]="spacetwo";
                                n++;
                                generateNotes[n]="barlineSingle";
                                n++;
                            }
                            sv_freestyle.smoothScrollBy(63,0);
                            generatenotes = unicode2String(gson2Unicode(generateNotes));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_freestyle.setText(generatenotes);
                                }
                            });
                            pitchNum=0;
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

    public String gson2Unicode(String []gson){
        NotesGson notesGson = new NotesGson();
        Map<String, NotesBean> map = notesGson.Gson2Notes(getJson("notes.json", FreePlayActivity.this));

        String noteUnicode = "";
        int i = 0;
        for (String s : gson){
            if(s!=null){
                noteUnicode = noteUnicode + map.get(s).getUnicode();
                if(map.get(s).isTone()==true){
                    noteFreInHz[i]=map.get(s).getToneFreqInHz();
                    noteFreInHz[i+1] = map.get(s).getDuration();
                    i+=2;
                }
            }
        }
        return noteUnicode;
    }

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
