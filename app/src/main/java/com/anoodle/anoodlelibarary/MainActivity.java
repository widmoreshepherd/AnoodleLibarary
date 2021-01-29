package com.anoodle.anoodlelibarary;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anoodle.webapi.controller.CnQAController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Context context;
    private EditText queryEt;
    private Button queryBt;
    private TextView robotTv;
    private TextView personTv;

    private class MyHandler extends Handler{

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        queryEt = findViewById(R.id.et_query);
        queryBt = findViewById(R.id.bt_query);
        robotTv = findViewById(R.id.tv_robot);
        personTv = findViewById(R.id.tv_person);
        queryBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Pair<String, String>> bags = new ArrayList<>();
                bags.add(new Pair<>("答错咯", "0"));
                bags.add(new Pair<>("额闹米嗯是厨余垃圾", "1"));
                //CnQAController.getInstance(context).batchSpeak(bags);
                CnQAController.getInstance(context).speekText("答错咯");
                /*CnQAController.getInstance(context).setCnAnswerListener(new CnAnswerListener() {
                    @Override
                    public void onAnswer(String answer) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                robotTv.setText(answer);
                            }
                        });
                    }

                    @Override
                    public void onSpeechProgress(String s, String text, int progress) {

                    }

                    @Override
                    public void onRecogText(String text) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                personTv.setText(text);
                            }
                        });
                    }

                    @Override
                    public void onPlayTip() {

                    }

                    @Override
                    public void onTrashClassify(String trashType) {

                    }

                    @Override
                    public void onEnd() {

                    }
                });
                CnQAController.getInstance(context).getAnswer("123456",queryEt.getText().toString());*/
            }
        });

    }

}
