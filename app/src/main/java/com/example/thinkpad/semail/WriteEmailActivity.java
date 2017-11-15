package com.example.thinkpad.semail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WriteEmailActivity extends Activity {
    private String[] receivers;
    private String title;
    private String content;

    private EditText write_receiver = null;
    private EditText write_title = null;
    private EditText write_content = null;
    private Button write_send = null;
    String username;
    String password;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        write_receiver = (EditText)findViewById(R.id.write_receiver);
        write_title = (EditText)findViewById(R.id.write_title);
        write_content = (EditText)findViewById(R.id.write_content);
        write_send = (Button)findViewById(R.id.write_send);
        title = write_title.toString();
        content = write_content.toString();
        bundle = this.getIntent().getExtras();
        username = bundle.getString("user");
        password = bundle.getString("pass");
    }

    public void Write(View view){

        String server="pop3.sem.tsinghua.edu.cn";//POP3服务器地址
        String result;
        String input_string;
        try
        {
            Pop pop3Client=new Pop(server,110);
            input_string = "USER "+username+"@sem.tsinghua.edu.cn";
            result = pop3Client.input(input_string);
            System.out.println(result);
            input_string = "PASS "+password;
            result = pop3Client.input(input_string);
            System.out.println(result);



            if ("SUCCESS".equals(result))
            {

                if ("SUCCESS".equals(result))
                {
                    Toast.makeText(getApplicationContext(), "Redirecting...",
                            Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("user", username);
                    bundle.putString("pass", password);
                    Intent intent = new Intent(this, ReceiveDetailEmailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Wrong Password",
                            Toast.LENGTH_SHORT).show();
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



        Toast.makeText(getApplicationContext(), "Sent successfully",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ReceiveDetailEmailActivity.class);
        startActivity(intent);
    }

}