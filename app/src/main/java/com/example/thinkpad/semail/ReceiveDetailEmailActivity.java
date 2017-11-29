package com.example.thinkpad.semail;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileOutputStream;

public class ReceiveDetailEmailActivity extends Activity{
    private String sender;
    private String content;
    String username;
    String password;
    Bundle bundle;
    String mailno;

    private TextView receive_sender = null;
    private TextView receive_title = null;
    private WebView receive_content = null;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        receive_sender = (TextView) findViewById(R.id.receive_sender);
        receive_title = (TextView) findViewById(R.id.receive_title);
        receive_content = (WebView) findViewById(R.id.receive_webView);
        bundle = this.getIntent().getExtras();
        username = bundle.getString("user");
        password = bundle.getString("pass");
        mailno = bundle.getString("no");

        String server = "pop3.sem.tsinghua.edu.cn";//POP3服务器地址
        String result;
        String input_string;
        String charset_c;
        String type_c;
        String[] res;
        String htmlString;

        Pop pop3Client = new Pop(server, 110);
        input_string = "USER " + username + "@sem.tsinghua.edu.cn";
        result = pop3Client.input(input_string);
        System.out.println(result);
        input_string = "PASS " + password;
        result = pop3Client.input(input_string);
        System.out.println(result);
        input_string = "RETR " + mailno;
        result = pop3Client.input(input_string);
        System.out.println(result);
        //日期set
        res = result.split("\n");
        System.out.println(res[0]);
        receive_sender.setText(res[1]);
        System.out.println(res[1]);
        receive_title.setText(res[2]);
        System.out.println(res[2]);
        htmlString = "";
        if (res.length >= 5) {
            type_c = res[3];
            charset_c = res[4];
            for (int i = 5; i < res.length; i++)
                htmlString = htmlString + "\n" + res[i];
            receive_content.getSettings().setDefaultTextEncodingName(charset_c);
            //需要考虑无html编码的情况
            System.out.println(htmlString);
            mWebSettings = receive_content.getSettings();
            try {
                receive_content.loadData(htmlString, type_c + "; charset=UTF-8", null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else
        {
            receive_content.loadData("Loading", "text/plain; charset=UTF-8", null);
        }


    }

}
