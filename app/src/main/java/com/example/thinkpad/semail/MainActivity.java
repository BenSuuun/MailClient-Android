package com.example.thinkpad.semail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {

    private EditText  username=null;
    private EditText  password=null;

    private Button login;
    int counter = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
        login = (Button)findViewById(R.id.button1);

    }

    public void login(View view){
        login.setText("Please waiting...");
        String server="pop3.sem.tsinghua.edu.cn";//POP3服务器地址
        String result;
        String input_string;
        try
        {
            Pop pop3Client=new Pop(server,110);
            input_string = "USER "+username.getText().toString()+"@sem.tsinghua.edu.cn";
            result = pop3Client.input(input_string);
            System.out.println(result);
            if ("SUCCESS".equals(result))
            {
                input_string = "PASS "+password.getText().toString();
                result = pop3Client.input(input_string);
                System.out.println(result);
                if ("SUCCESS".equals(result))
                {
                    Toast.makeText(getApplicationContext(), "Login Successfully...",
                            Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("user", username.getText().toString());
                    bundle.putString("pass", password.getText().toString());
                    bundle.putString("no", "1");
                    Intent intent = new Intent(MainActivity.this, MyListView4.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else
                {

                    Toast.makeText(getApplicationContext(), "Wrong Password",
                            Toast.LENGTH_SHORT).show();
                    login.setText("Please waiting...");
                }
            }
            else
            {
                System.out.println(result);
                Toast.makeText(getApplicationContext(), "User NOT Existing",
                        Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            System.out.println("HERE!");
        }

    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

}
