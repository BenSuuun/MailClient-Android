package com.example.thinkpad.semail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Date;

public class SMTP {
    private Socket socket = null;
    private boolean debug=true;
    String username="";//用户名，填写自己的邮箱用户名
    String password="";//密码，填写自己的密码
    String to_mail = "";
    String subject = "";
    BufferedReader in;
    BufferedWriter out;
    /*public static void main(String[] args) throws UnknownHostException, IOException {
        String server="smtp.sem.tsinghua.edu.cn";//POP3服务器地址
        SMTP smtpClient=new SMTP(server,25,"sunwb.14","SWB0109huidan");
        smtpClient.input("HELO");
        smtpClient.input("AUTH");
        smtpClient.input("MAIL");
        smtpClient.input("RCPT qianw.14@sem.tsinghua.edu.cn");
        smtpClient.input("SUBJ 测试java邮箱");
        smtpClient.input("DATA 就是测试一下，看看能不能达到收到");
        //smtpClient.sendMail();
    }*/
    /*构造函数*/
    public SMTP(String server,int port, String user, String pass) throws UnknownHostException, IOException{
        try{
            socket=new Socket(server,port);//在新建socket的时候就已经与服务器建立了连接
            username = user+"@sem.tsinghua.edu.cn";
            password = pass;
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            System.out.println("建立连接！");
        }
    }

    public void input(String user_input)
    {
        String operation, v1, v2, v3;
        String temp_s;
        Encode encoding = new Encode();
        try {
            StringTokenizer st=new StringTokenizer(user_input," ");
            operation = st.nextToken();
            if ("HELO".equalsIgnoreCase(operation)) //输入HELO打招呼
            {
                helo("smtp.sem.tsinghua.edu.cn", in, out);
            }
            else if ("AUTH".equalsIgnoreCase(operation)) //输入AUTH认证
            {
                auth("LOGIN", in, out);
                temp_s = encoding.encode("UTF-8", username);
                System.out.println(temp_s);
                user(temp_s, in, out);
                temp_s = encoding.encode("UTF-8", password);
                System.out.println(temp_s);
                pass(temp_s, in, out);
            }
            else if ("MAIL".equalsIgnoreCase(operation)) //输入 MAIL xx可识别
            {
                mail(in, out);
            }
            else if ("RCPT".equalsIgnoreCase(operation)) //输入 RCPT email
            {
                v1 = st.nextToken();
                to_mail = v1;
                rcpt(v1, in, out);
            }
            else if ("SUBJ".equalsIgnoreCase(operation))
            {
                subject = user_input.substring(5);
            }

            else if ("DATA".equalsIgnoreCase(operation)) //输入 DATA 开始传输发送文字
            {
                // Encode encoding;
                String content = "";
                content ="From: <"+username+">\n";
                content = content + "To: <"+to_mail+">\n";
                temp_s = encoding.encode("UTF-8", subject);
                content = content + "Subject: =?UTF-8?B?"+temp_s+"?=\n";
                content = content + "MIME-Version: 1.0\n";
                //content = content + "Date: \n";
                Date date = new Date();
                content = content + "Date: "+ date.toString()+"\n";
                content = content + "Content-Type: multipart/mixed;\n";
                content = content + "\tboundary=\"=boundaryhere\"\n\n";
                temp_s = encoding.encode("UTF-8","<html><body>"+user_input.substring(5)+"</body></html>");
                content = content + "--=boundaryhere\n";
                content = content + "Content-Type: text/html; charset=UTF-8\n";
                content = content + "Content-Transfer-Encoding: base64\n\n";
                content = content + temp_s+"\n";
                content = content + "--=boundaryhere\n";
                content = content + "Content-Type: text/plain; charset=UTF-8\n";
                content = content + "Content-Transfer-Encoding: base64\n\n";
                temp_s = encoding.encode("UTF-8",user_input.substring(5));
                content = content + temp_s+"\n";
                content = content + "--=boundaryhere--\n";
                content = content + ".\n";
                System.out.println(content);
                send(content, in, out);
            }
            else if ("QUIT".equalsIgnoreCase(operation)) //输入 RCPT email
            {
                quit(in, out);
            }
            else
            {
                System.out.println(operation);
                System.out.println("错误的指令,请重试");
            }

        }
        catch(Exception e){
            System.out.println("here2");
            e.printStackTrace();
            //return "ERROR";
        }
    }
    //得到服务器返回的一行命令
    public String getReturn(BufferedReader in){
        String line="";
        try{
            line=in.readLine();
            if(debug){
                System.out.println("服务器返回状态:"+line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return line;
    }
    //从返回的命令中得到第一个字段,也就是服务器的返回状态码(+OK或者-ERR)
    public String getResult(String line){
        StringTokenizer st=new StringTokenizer(line," ");
        return st.nextToken(); // 返回切割后的第一块值
    }
    //发送命令
    private String sendServer(String str,BufferedReader in,BufferedWriter out) throws IOException{
        out.write(str);//发送命令
        out.newLine();//发送空行
        out.flush();//清空缓冲区
        if(debug){
            System.out.println("已发送命令:"+str);
        }
        return getReturn(in);
    }
    //helo命令
    public void helo(String site, BufferedReader in,BufferedWriter out) throws IOException{
        String result = null;
        result=getResult(getReturn(in));//先检测连接服务器是否已经成功
        if(!"220".equals(result)){
            throw new IOException("连接服务器失败!");
        }
        result = getResult(sendServer("HELO "+site,in,out));  //打招呼
        if(!"250".equals(result)){
            throw new IOException("连接错误!");
        }
    }
    //auth命令
    public void auth(String addition, BufferedReader in,BufferedWriter out) throws IOException{
        String result = null;
        result = getResult(sendServer("AUTH "+addition,in,out));  //发送认证请求
        if(!"334".equals(result)){
            throw new IOException("连接错误!");
        }
    }
    //auth-user命令
    public void user(String user,BufferedReader in,BufferedWriter out) throws IOException{
        String result = null;
        result=getResult(sendServer(user ,in ,out ));//发送USER登录
        if(!"334".equals(result)){
            throw new IOException("用户名错误!");
        }
    }
    //auth-pass命令
    public void pass(String password, BufferedReader in,BufferedWriter out) throws IOException{
        String result = null;
        result = getResult(sendServer(password,in,out));  //发送PASS密码
        if(!"235".equals(result)){
            throw new IOException("密码错误!");
        }
    }
    //mail-from命令
    public void mail(BufferedReader in,BufferedWriter out) throws IOException{
        String result = null;
        result = getResult(sendServer("MAIL FROM: <"+username+">",in,out));  //发送的邮箱
        if(!"250".equals(result)){
            throw new IOException("错误!");
        }
    }
    //rcpt命令
    public void rcpt(String to_mail, BufferedReader in,BufferedWriter out) throws IOException{
        String result = null;
        result = getResult(sendServer("RCPT TO: <"+to_mail+">",in,out));  //目标邮箱
        if(!"250".equals(result)){
            throw new IOException("错误!");
        }
    }
    //send-mail命令
    public void send(String content, BufferedReader in,BufferedWriter out) throws IOException{
        String result = null;
        result = getResult(sendServer("DATA",in,out));  //data表示传输数据
        if(!"354".equals(result)){
            throw new IOException("错误!");
        }
        result = getResult(sendServer(content,in,out));  //发送所有整理好的内容
        if(!"250".equals(result)){
            throw new IOException("错误!");
        }
    }
    //退出
    public void quit(BufferedReader in,BufferedWriter out) throws IOException{
        String result;
        result=getResult(sendServer("QUIT",in,out));
        if(!"221".equals(result)){
            throw new IOException("未能正确退出");
        }
    }
}