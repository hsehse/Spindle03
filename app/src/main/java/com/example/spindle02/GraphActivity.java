package com.example.spindle02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.toolbox.HttpResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GraphActivity extends AppCompatActivity {

    ArrayList<String> mArrayList=new ArrayList<>();
    int data_count = 0;
    int value_fa = 1; //판별 0이면 멈추는거 1이면 하는거?
    String value; //값
    int degree;
    private LineChart chart;
    MediaPlayer mp=null;
    Timer timer=null;
    String mJsonString;
    int i = 0;
    int sign=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        ImageButton play=(ImageButton)findViewById(R.id.play_t);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GraphgroundTask().execute();
            }
        });

        ImageButton menub=(ImageButton)findViewById(R.id.menubar);
        menub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(
                        getApplicationContext(), // 현재 화면의 제어권자
                        v); // anchor : 팝업을 띄울 기준될 위젯
                getMenuInflater().inflate(R.menu.graphmenu, p.getMenu());
                // 이벤트 처리
                p.show();
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()){
                            case R.id.temp:
                                intent=new Intent(GraphActivity.this,GraphActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.rpm:
                                intent=new Intent(GraphActivity.this,Graph_rpm.class);
                                startActivity(intent);
                                return true;

                            case R.id.gap_x:
                                intent=new Intent(GraphActivity.this, Graph_gapx.class);
                                startActivity(intent);
                                return true;

                            case R.id.gap_y:
                                intent=new Intent(GraphActivity.this, Graph_gapy.class);
                                startActivity(intent);
                                return true;
                        }
                        return true;
                    }
                });
                 // 메뉴를 띄우기
            }
        });

        Button showButton=(Button)findViewById(R.id.btn_show_data);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });

        chart = (LineChart) findViewById(R.id.chart);

        chart.setDrawGridBackground(true);
        chart.setBackgroundColor(Color.BLACK);
        chart.setGridBackgroundColor(Color.BLACK);

        // description text
        chart.getDescription().setEnabled(true);
        Description des = chart.getDescription();
        des.setEnabled(true);
        des.setText("Real-Time DATA");
        des.setTextSize(15f);
        des.setTextColor(Color.WHITE);

        chart.setMaxVisibleValueCount(30);

        // touch gestures (false-비활성화)
        chart.setTouchEnabled(false);

        // scaling and dragging (false-비활성화)
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        //auto scale
        chart.setAutoScaleMinMaxEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        //X축
        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setDrawGridLines(false);

        //Legend
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setTextSize(12f);
        l.setTextColor(Color.WHITE);

        //Y축
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setTextColor(getResources().getColor(R.color.colorAccent));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(getResources().getColor(R.color.colorAccent));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);


        // don't forget to refresh the drawing
        chart.invalidate();

        //init();
        //threadStart();
        Button startbtn = (Button)findViewById(R.id.btn_start);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value_fa = 1;
                threadStart();
            }
        });

        Button stopbtn = (Button)findViewById(R.id.btn_stop);
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value_fa = 0;
            }
        });

    }

    private void addEntry(double num) {
        LineData data = chart.getData();
        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }
        // set.addEntry(...); // can be called as well
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        data.addEntry(new Entry((float)set.getEntryCount(), (float)num), 0);
        data.notifyDataChanged();
        data_count++;
        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

        chart.setVisibleXRangeMaximum(150);
        // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);

    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Real-time Line Data");
        set.setLineWidth(1f);
        set.setDrawValues(false);
        set.setValueTextColor(getResources().getColor(android.R.color.white));
        set.setColor(getResources().getColor(android.R.color.white));
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(190, 190, 190));

        return set;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 0) {
                if(mArrayList.size() == i) {
                    handler.removeMessages(0);
                }
                else {
                    double a = Double.parseDouble(mArrayList.get(i++));
                    addEntry(a);
                    if (a >= 27) {
                        sign++;
                        if(sign >= 5) {
                            value_fa = 0;
                            soundplay();
                            showdiag();
                        }

                    }else {
                        sign = 0;
                    }
                }

                if(data_count > 20) {
                    chart.moveViewToX(data_count - 15);
                }
            }
        }
    };

    void soundplay(){
        if(timer!=null){
            timer.cancel();
            timer.purge();
            timer=null;}
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
        mp = MediaPlayer.create(getApplicationContext(), R.raw.alram);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mp.stop();
            }
        }, 80000);

        return;
    }

    void showdiag(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Dangerous!").setMessage("Check and Stop machine!");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Yes..",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"No..",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    class MyThread extends Thread{
        Handler myhandler = handler;
        @Override
        public void run() {
            while(value_fa == 1){
                myhandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void threadStart(){
        final MyThread thread=new MyThread();
        thread.setDaemon(true);
        thread.start();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.graphmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.temp:
                intent=new Intent(GraphActivity.this,GraphActivity.class);
                startActivity(intent);
                return true;

            case R.id.rpm:
                intent=new Intent(GraphActivity.this,Graph_rpm.class);
                startActivity(intent);
                return true;

            case R.id.gap_x:
                intent=new Intent(GraphActivity.this, Graph_gapx.class);
                startActivity(intent);
                return true;

            case R.id.gap_y:
                intent=new Intent(GraphActivity.this, Graph_gapy.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
     */
    //showdata 통신
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://13.59.96.134/tempData.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);//URL 객체 생성

                //URL을 이용해서 웹페이지에 연결하는 부분
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //바이트단위 입력스트림 생성 소스는 httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //웹페이지 출력물을 버퍼로 받음 버퍼로 하면 속도가 더 빨라짐
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //문자열 처리를 더 빠르게 하기 위해 StringBuilder클래스를 사용함
                StringBuilder stringBuilder = new StringBuilder();

                //한줄씩 읽어서 stringBuilder에 저장함
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }

                //사용했던 것도 다 닫아줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim은 앞뒤의 공백을 제거함

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
                Intent intent = new Intent(GraphActivity.this, Management.class);
                intent.putExtra("dataList", result);//파싱한 값을 넘겨줌
                GraphActivity.this.startActivity(intent);//ManagementActivity로 넘어감
        }
    }

    //그래프 통신
    class GraphgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://13.59.96.134/tempData.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);//URL 객체 생성

                //URL을 이용해서 웹페이지에 연결하는 부분
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //바이트단위 입력스트림 생성 소스는 httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //웹페이지 출력물을 버퍼로 받음 버퍼로 하면 속도가 더 빨라짐
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //문자열 처리를 더 빠르게 하기 위해 StringBuilder클래스를 사용함
                StringBuilder stringBuilder = new StringBuilder();

                //한줄씩 읽어서 stringBuilder에 저장함
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }

                //사용했던 것도 다 닫아줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim은 앞뒤의 공백을 제거함

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            mJsonString=result;
            showGraph();
        }
    }

    void showGraph(){
        try{
            JSONObject jsonObject=new JSONObject(mJsonString);
            JSONArray jsonArray=jsonObject.getJSONArray("response");
            int count=0;
            String value;

            while(count<jsonArray.length()){
                JSONObject object=jsonArray.getJSONObject(count);
                value=object.getString("value");

                mArrayList.add(value);
                count++;
            }

            threadStart();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
