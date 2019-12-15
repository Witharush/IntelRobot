package com.example.intelrobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ChatAdapter chatAdapter;
    private List<ChatBean> chatBeanList;    //store all chatting data
    private EditText et_send_msg;
    private Button btn_send;
    //API Address

    private static final String WEB_SITE = "http://openapi.tuling123.com/openapi/api";
    private static final String KEY = "35abac530b89415aa7c43543493ddd35";
    private String sendMsg;
    private String welcome[];
    private MHanler mHanler;
    public static final int MSG_OK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatBeanList = new ArrayList<ChatBean>();
        mHanler = new MHanler();
        welcome = getResources().getStringArray(R.array.welcome);
        initView();
    }

    public void initView() {
        listView = findViewById(R.id.list);
        et_send_msg = findViewById(R.id.et_send_msg);
        btn_send = findViewById(R.id.btn_send);
        chatAdapter = new ChatAdapter(chatBeanList, this);
        listView.setAdapter(chatAdapter);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        et_send_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() ==KeyEvent.ACTION_DOWN){
                    sendData();
                }
                return false;
            }
        });
        int positon = (int)(Math.random() * welcome.length - 1);
        showData(welcome[positon]);
    }

    private void sendData() {
        sendMsg = et_send_msg.getText().toString();
        if(TextUtils.isEmpty(sendMsg)){
            Toast.makeText(this, "You haven't type any information", Toast.LENGTH_SHORT).show();
            return;
        }
        et_send_msg.setText("");
        //replace space and newline('\n')
        sendMsg = sendMsg.replaceAll(" ", "").replaceAll("\n", "").trim();
        ChatBean chatBean = new ChatBean();
        chatBean.setMessage(sendMsg);
        chatBean.setState(ChatBean.SEND);
        chatBeanList.add(chatBean);
        chatAdapter.notifyDataSetChanged();
        getDataFromServer();

    }

    private void getDataFromServer() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(WEB_SITE + "?key=" + KEY + "&info="+ sendMsg).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Message msg = new Message();
                msg.what = MSG_OK;
                msg.obj = res;
                mHanler.sendMessage(msg);
            }
        });
    }

    class MHanler extends Handler {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case MSG_OK:
                    if(msg.obj != null){
                        String vlResult = (String) msg.obj;
                        parseData(vlResult);
                    }
                    break;
            }
        }
    }

    private void parseData(String JsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            String content = jsonObject.getString("text");
            int code = jsonObject.getInt("code");
            updateView(code, content);
        } catch (JSONException e) {
            e.printStackTrace();
            showData("帅标， 你的网络不好哟");
        }
    }
    private void showData(String message) {
        ChatBean chatBean = new ChatBean();
        chatBean.setMessage(message);
        chatBean.setState(ChatBean.RECEIVE);
        chatBeanList.add(chatBean);
        chatAdapter.notifyDataSetChanged();
    }
    private void updateView(int code, String content) {
        switch (code){
            case 4004:
                showData("帅标， 我累了， 明天再来找我玩吧");
                break;
            case 40005:
                showData("帅标， 你在说啥子呀");
                break;
            case 40006:
                showData("帅标， 我去和别人约会啦，不鸟你了");
                break;
            default:
                showData(content);
                break;
        }
    }
    protected long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(MainActivity.this, "Press again to quit IntelChat", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
