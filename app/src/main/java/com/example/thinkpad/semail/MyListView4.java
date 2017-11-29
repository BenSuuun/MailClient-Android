package com.example.thinkpad.semail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyListView4 extends Activity {


    private List<Map<String, Object>> mData;
    private ListView myListView;
    private Button sender_btn;
    String username;
    String password;
    Bundle bundle;
    int mail_stat;
    String mailno;
    String[] title = new String[30];
    String[] sender = new String[30];
    String[] date = new String[30];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivers);
        String result;
        String input_string;
        String[] res;
        bundle = this.getIntent().getExtras();
        username = bundle.getString("user");
        password = bundle.getString("pass");
        mailno = bundle.getString("no");

        //setContentView(R.layout.my_list_view);
        String server="pop3.sem.tsinghua.edu.cn";//POP3服务器地址
        Pop pop3Client = new Pop(server, 110);
        input_string = "USER " + username + "@sem.tsinghua.edu.cn";
        result = pop3Client.input(input_string);
        System.out.println(result);
        input_string = "PASS " + password;
        result = pop3Client.input(input_string);
        System.out.println(result);
        input_string = "STAT";
        result = pop3Client.input(input_string);
        mail_stat = Integer.valueOf(result);
        //mail_stat = 2200;
        for (int i = 0; i < 10; i++)
        try
            {
                pop3Client = new Pop(server, 110);
                input_string = "USER " + username + "@sem.tsinghua.edu.cn";
                result = pop3Client.input(input_string);
                input_string = "PASS " + password;
                result = pop3Client.input(input_string);
                input_string = "RETR "+(mail_stat - i);
                result = pop3Client.input(input_string);
                res = result.split("\n");
                System.out.println("VIEW---------------"+result);
                date[i] = res[0];
                sender[i] = res[1];
                title[i] = res[2];
            }
        catch (Exception e)
        {
            date[i] = "Loading...";
            sender[i] = "Loading...";
            title[i] = "Loading...";
            System.out.println("INPUT ERROR!");
        }

        mData = getData();
        sender_btn = (Button)findViewById(R.id.sender_btn);
        sender_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyListView4.this,WriteEmailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        myListView = (ListView)findViewById(R.id.id_lv);
        MyAdapter adapter = new MyAdapter(this);
        myListView.setAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map;
        for (int i = 0; i < 10; i++)
        {
            map = new HashMap<String, Object>();
            map.put("title", title[i]);
            map.put("sender", sender[i]);
            map.put("date",date[i]);
            map.put("img", R.drawable.email);
            list.add(map);
        }

        return list;
    }



    public final class ViewHolder{
        public ImageView img;
        public TextView title;
        public TextView sender;
        public TextView date;
        public Button viewBtn;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.test_layout, null);
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                holder.title = (TextView)convertView.findViewById(R.id.title);
                holder.sender = (TextView)convertView.findViewById(R.id.sender);
                holder.date = (TextView)convertView.findViewById(R.id.date);
                holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
            holder.title.setText((String) mData.get(position).get("title"));
            holder.sender.setText((String)mData.get(position).get("sender"));
            holder.date.setText((String)mData.get(position).get("date"));

            holder.viewBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //showInfo();
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!"+position);
                    bundle.putString("no", ""+(mail_stat-position));
                    Intent intent = new Intent(MyListView4.this, ReceiveDetailEmailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return convertView;
        }

    }

}