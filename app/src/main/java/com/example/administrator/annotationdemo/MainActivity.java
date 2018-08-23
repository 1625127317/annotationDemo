package com.example.administrator.annotationdemo;

import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.viewmodel_annotation.BindView;
import com.example.administrator.viewmodel_annotation.OnClick;
import com.example.administrator.viewmodel_api.ViewModelKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView1;

    @BindView(R.id.textView1)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewModelKnife.bind(this);
    }

    @OnClick(R.id.textView)
    public void textViewOneListener(){
        Toast.makeText(this,"textView----One",Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.textView1)
    public void textViewTwoListener(){
        Toast.makeText(this,"textView----Second",Toast.LENGTH_SHORT).show();
    }
}
