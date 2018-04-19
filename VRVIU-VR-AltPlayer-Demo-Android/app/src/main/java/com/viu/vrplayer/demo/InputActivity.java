package com.viu.vrplayer.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.viu.vrplayer.demo.R;

/**
 * Created by VRVIU on 2018/3/4.
 */

public class InputActivity extends BaseActivity {
    private Context mContext;
    private String defaultUrl = "http://";
    public static String LAST_PLAY_URL = "last_play_url";
    public static String KEY_PLAY_URL = "play_url";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        mContext = this;
        final SharedPreferences sp = getSharedPreferences(LAST_PLAY_URL , MODE_PRIVATE);
        String lastPlayUrl = sp.getString(KEY_PLAY_URL,"");
        Button playBtn = (Button)findViewById(R.id.play_btn);
        final EditText editText = (EditText)findViewById(R.id.input_ev);
        if (lastPlayUrl.isEmpty()) {
            editText.setText(defaultUrl);
        }else{
            editText.setText(lastPlayUrl);
        }
        editText.setSelection(defaultUrl.length());

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = null;
                if (editText.getText() != null){
                    url = editText.getText().toString().trim();
                }
                if (url != null ){
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(KEY_PLAY_URL,url);
                    edit.commit();
                    Intent intent = new Intent(mContext,VRPlayerActivity.class);
                    intent.putExtra(VRPlayerActivity.URI_LIST_EXTRA,url);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
