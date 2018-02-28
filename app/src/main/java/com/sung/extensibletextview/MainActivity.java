package com.sung.extensibletextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshUI();
    }

    private void refreshUI(){
        ExtensibleTextView content = findViewById(R.id.tv_content);
        content.setText(getResources().getString(R.string.content));
    }
}
