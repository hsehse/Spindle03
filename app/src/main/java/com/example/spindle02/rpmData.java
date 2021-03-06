package com.example.spindle02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class rpmData extends AppCompatActivity {

    private ListView listView;
    private DataListAdapter adapter;
    private List<Data> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        Intent intent = getIntent();
        listView = (ListView)findViewById(R.id.listView);
        dataList = new ArrayList<Data>();

        adapter = new DataListAdapter(getApplicationContext(),dataList);
        listView.setAdapter(adapter);

        try{
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("dataList"));

            //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;

            String value;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미
                JSONObject object = jsonArray.getJSONObject(count);

                value = object.getString("value");//여기서 ID가 대문자임을 유의


                //값들을 User클래스에 묶어줍니다
                Data data=new Data(value);
                dataList.add(data);
                count++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
