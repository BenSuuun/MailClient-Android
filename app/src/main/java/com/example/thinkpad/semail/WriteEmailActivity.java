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
        write_send.setText("Pleasing waiting...");
        String server="smtp.sem.tsinghua.edu.cn";//POP3服务器地址
        String input_string;
        if ("".equals(write_receiver.getText()))
            return;
        if ("next".equals(write_receiver.getText()))
        {
            Intent intent = new Intent(this, MyListView4.class);
            intent.putExtras(bundle);
            startActivity(intent);
            return;
        }
        if ("".equals(write_title.getText()))
            return;
        if ("".equals(write_content.getText()))
            return;

        try
        {
            SMTP smtpClient = new SMTP(server, 25, username, password);
            input_string = "HELO";
            smtpClient.input(input_string);
            input_string = "AUTH";
            smtpClient.input(input_string);
            input_string = "MAIL";
            smtpClient.input(input_string);
            input_string = "RCPT "+ write_receiver.getText();
            smtpClient.input(input_string);
            input_string = "SUBJ "+ write_title.getText();
            smtpClient.input(input_string);
            input_string = "DATA "+ write_content.getText();
            smtpClient.input(input_string);

            Toast.makeText(getApplicationContext(), "Sent successfully",
                    Toast.LENGTH_SHORT).show();
            //Bundle bundle = new Bundle();
            //bundle.putString("user", username);
            //bundle.putString("pass", password);
            Intent intent = new Intent(this, MyListView4.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        catch (Exception e)
        {
            System.out.println("HERE!");
        }
    }

}