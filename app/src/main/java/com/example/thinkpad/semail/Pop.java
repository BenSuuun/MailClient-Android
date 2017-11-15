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
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.codec.DecoderException;

public class Pop {
	private Socket socket = null;
	private boolean debug=true;
	BufferedReader in;
	BufferedWriter out;
	/*public static void main(String[] args) throws UnknownHostException, IOException {
		String server="pop3.sem.tsinghua.edu.cn";//POP3服务器地址
		String user="";//用户名，填写自己的邮箱用户名
		String password="";//密码，填写自己的密码
		String result;
		Pop pop3Client=new Pop(server,110);
		result = pop3Client.input("USER sunwb.14@sem.tsinghua.edu.cn");
		System.out.println(result);
		result = pop3Client.input("PASS SWB0109huidan");
		System.out.println(result);
		result = pop3Client.input("STAT");
		System.out.println(result);
		result = pop3Client.input("RETR 2166");
		System.out.println(result);
		//pop3Client.recieveMail(user, password);
	}*/
	/*构造函数*/
	public Pop(String server,int port) {
		try{
			socket=new Socket(server,port);//在新建socket的时候就已经与服务器建立了连接
			in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(Exception e){
            System.out.println("here1");
			e.printStackTrace();
		}finally{
			System.out.println("建立连接！");
		}
	}
	//接收邮件程序
	public String input(String user_input){
		String operation, v1;
		try {
			StringTokenizer st=new StringTokenizer(user_input," ");
			operation = st.nextToken();
			if ("USER".equalsIgnoreCase(operation))
			{
				v1 = st.nextToken();
				return user(v1, in, out);
			}
			else if ("PASS".equalsIgnoreCase(operation))
			{
				v1 = st.nextToken();
				return pass(v1, in, out);
			}
			else if ("STAT".equalsIgnoreCase(operation))
			{
				return stat(in, out);
			}
        		/*else if ("LIST".equalsIgnoreCase(operation))
        		{
        			if(st.hasMoreTokens())
        			{
        				v1 = st.nextToken();
        	            list_one(Integer.parseInt(v1), in, out);
        			}
        	        else
        	        {
        	            list(in, out);
        	        }
        		}*/  //不会调用list这个语句
			else if ("RETR".equalsIgnoreCase(operation))
			{
				v1 = st.nextToken();
				return retr(Integer.parseInt(v1), in, out);

			}
			else if ("QUIT".equalsIgnoreCase(operation))
			{
				return quit(in, out);
			}
			else
			{
				return ("ERROR");
			}

		}
		catch(Exception e){
            System.out.println("here2");
			e.printStackTrace();
			return "ERROR";
		}
	}
	/*public boolean recieveMail(String user,String password){  //调试使用该段落
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String user_input="";
			String operation, v1, v2, v3;
			InputStreamReader ir = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(ir);
			System.out.println("输入您的操作：");
			user_input = br.readLine();
			while (user_input != null)
			{
				System.out.println("已读入："+user_input);
				StringTokenizer st=new StringTokenizer(user_input," ");
				operation = st.nextToken();
				if ("USER".equalsIgnoreCase(operation))
				{
					v1 = st.nextToken();
					user(v1, in, out);
				}
				else if ("PASS".equalsIgnoreCase(operation))
				{
					v1 = st.nextToken();
					pass(v1, in, out);
				}
				else if ("STAT".equalsIgnoreCase(operation))
				{
					stat(in, out);
				}
				else if ("LIST".equalsIgnoreCase(operation))
				{
					if(st.hasMoreTokens())
					{
						v1 = st.nextToken();
						list_one(Integer.parseInt(v1), in, out);
					}
					else
					{
						list(in, out);
					}
				}
				else if ("RETR".equalsIgnoreCase(operation))
				{
					v1 = st.nextToken();
					retr(Integer.parseInt(v1), in, out);

				}
				else if ("QUIT".equalsIgnoreCase(operation))
				{
					quit(in, out);
				}
				else
				{
					System.out.println(operation);
					System.out.println("错误的指令,请重试");
				}
				user_input = br.readLine();
			}
			ir.close();
			br.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}*/
	//得到服务器返回的一行命令
	public String getReturn(BufferedReader in){
		String line="";
		try{
			line=in.readLine();
			if(debug){
				System.out.println("服务器返回状态:"+line);
			}
		}catch(Exception e){
            System.out.println("here3");
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
	private String sendServer(String str,BufferedReader in,BufferedWriter out) {
		try {
			out.write(str);//发送命令
			out.newLine();//发送空行
			out.flush();//清空缓冲区
			if(debug){
				System.out.println("已发送命令:"+str);
			}
			return getReturn(in);
		}
		catch (Exception e)
		{
            System.out.println("here4");
			return ("ERROR ERROR");
		}
	}
	//user命令
	public String user(String user,BufferedReader in,BufferedWriter out) {
		String result = null;
		result=getResult(getReturn(in));//先检测连接服务器是否已经成功
		if(!"+OK".equals(result)){
			//throw new IOException("连接服务器失败!");
			return ("ERROR");
		}
		result=getResult(sendServer("USER "+user,in,out));//发送USER登录命令
		if(!"+OK".equals(result)){
			//throw new IOException("用户名错误!");
			return ("ERROR");
		}
		return ("SUCCESS");
	}
	//pass命令
	public String pass(String password, BufferedReader in,BufferedWriter out) {
		String result = null;
		result = getResult(sendServer("PASS "+password,in,out));  //发送密码
		if(!"+OK".equals(result)){
			//throw new IOException("密码错误!");
			return ("ERROR");
		}
		return ("SUCCESS");
	}
	//stat命令
	public String stat(BufferedReader in,BufferedWriter out) {
		String result = null;
		String line = null;
		int mailNum = 0;
		line=sendServer("STAT",in,out);   //发送STAT命令，查询邮件数量与容量
		StringTokenizer st=new StringTokenizer(line," ");
		result=st.nextToken();
		if(st.hasMoreTokens())
			mailNum=Integer.parseInt(st.nextToken());
		else{
			mailNum=0;
		}
		if(!"+OK".equals(result)){
			//throw new IOException("查看邮箱状态出错!");
			return ("ERROR");
		}
		System.out.println("共有邮件"+mailNum+"封");
		return ""+mailNum;
	}
	//无参数list命令
	public void list(BufferedReader in,BufferedWriter out) throws IOException{
		String message = "";
		String line = null;
		line=sendServer("LIST",in,out);
		while(!".".equalsIgnoreCase(line)){
			message=message+line+"\n";
			line=in.readLine().toString();
		}
		System.out.println(message); //展示所有的邮件list和识别码
	}
	//带参数list命令
	public void list_one(int mailNumber ,BufferedReader in,BufferedWriter out) throws IOException{
		String result = null;
		result = getResult(sendServer("LIST "+mailNumber,in,out));
		if(!"+OK".equals(result)){
			throw new IOException("list错误!");
		}
	}
	//得到邮件详细信息
	public String getMessage(BufferedReader in) throws UnsupportedEncodingException{
		String message = "";
		String line = null, s1 = null, s2 = null, charset = null;
		String[] res;
		Boolean flag = false;
		try{
			line=in.readLine().toString();
			while(!".".equalsIgnoreCase(line)){
				message=message+line+"\r";
				line=in.readLine().toString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}
	public String getMessagedetail(BufferedReader in) throws UnsupportedEncodingException{
		String message = "";
		int read_line=0;
		String line = null, s1 = null, s2 = null, charset = null, s3 = null;
		String m_date = "",m_from="",m_subject="";
		String[] res,res_g;
		int read_num=0;
		Decode decoding_b64 = new Decode();
		QuotedPrintableCodec decoding_qp = new QuotedPrintableCodec();
		try{
			line=in.readLine().toString();
			while (read_num<3)
			{
				//System.out.println(line);
				res_g = line.split("[:| ]");
				if ("Date".equalsIgnoreCase(res_g[0]))
				{
					if (" ".equalsIgnoreCase(line.substring(5,6)))
						m_date = line.substring(6);
					else
						m_date = line.substring(5);
					read_num ++;
					//System.out.println(m_date);
					line=in.readLine().toString();
					continue;
				}
				else if ("From".equalsIgnoreCase(res_g[0]))
				{
					res = line.split("\\<");
					res = res[1].split("\\>");
					m_from = res[0];
					read_num++;
					//System.out.println(m_from);
					line=in.readLine().toString();
					continue;
				}
				else if ("Subject".equalsIgnoreCase(res_g[0]))
				{
					res = line.split("\\?");
					if (res.length >= 4)
					{
						s2 = decoding_b64.decodes(res[1], res[3]);
						line=in.readLine().toString();
						s1 = line.substring(0,1);
						while(" ".equalsIgnoreCase(s1)){
							res = line.split("\\?");
							s2 = s2 + decoding_b64.decodes(res[1], res[3]);
							line=in.readLine().toString();
							s1 = line.substring(0,1);
						}
						m_subject = s2;
						read_num++;
						//System.out.println(m_subject);
						continue;
					}
					else
					{
						res = line.split("\\:");
						for (int l = 1; l < res.length; l++)
							if (res[l].length()>=3)
							{
								m_subject = res[l];
								read_num++;
								break;
							}
					}
				}
				line=in.readLine().toString();
			}
            
            
            
            /*
            s1 = line.substring(0,5);
            while(!"Date:".equalsIgnoreCase(s1)){                   
                line=in.readLine().toString(); 
                s1 = line.substring(0,5);
            }
            message = line.substring(5)+"\r";
            line=in.readLine().toString(); 
            s1 = line.substring(0,5);
            while(!"From:".equalsIgnoreCase(s1)){                   
                line=in.readLine().toString();   
                s1 = line.substring(0,5);
            }
            res = line.split("\\<");
            res = res[1].split("\\>");    
            message = message+res[0]+"\r";
            line=in.readLine().toString(); 
            s1 = line.substring(0,8);
            while(!"Subject:".equalsIgnoreCase(s1)){                   
                line=in.readLine().toString();   
                s1 = line.substring(0,8);
            }            
            res = line.split("\\?");
            s2 = decoding_b64.decodes(res[1], res[3]);
            line=in.readLine().toString(); 
            s1 = line.substring(0,1);
            System.out.println(message);	
            while(" ".equalsIgnoreCase(s1)){ 
                res = line.split("\\?");
                s2 = s2 + decoding_b64.decodes(res[1], res[3]);            	
                line=in.readLine().toString();  
                s1 = line.substring(0,1);
            }            
            message = message+s2+"\r";  
            System.out.println(message);	
            */
			message = message + m_date + "\r" + m_from + "\r" + m_subject + "\r";
			System.out.println(message);
			while (!".".equalsIgnoreCase(line))
			{
				line=in.readLine().toString();
				if (line.length()>=2)
					s1 = line.substring(0,2);
				else
					s1 = null;
				while(!"--".equalsIgnoreCase(s1)){
					line=in.readLine().toString();
					if (line.length()>=2)
						s1 = line.substring(0,2);
					else
						s1 = null;
				}
				line=in.readLine().toString();
				res_g = line.split("[:|;|=| |\"]"); //正则表达式取出type 和 charset
				// System.out.println(line);
				for (int i= 0 ; i< res_g.length; i++)
					if ("text/html".equalsIgnoreCase(res_g[i]))
					{
						System.out.println(res_g[i]);
						for (int j = res_g.length-1; j >= i; j --)
							if (res_g[j].length()>=3)
							{
								charset = res_g[j];
								break;
							}
						System.out.println(charset);
						s1 = "";
						line=in.readLine().toString();
						res = line.split("[:| ]");
						while (!"Content-Transfer-Encoding".equalsIgnoreCase(res[0]))
						{
							line=in.readLine().toString();
							res = line.split("[:| ]");
							if (".".equalsIgnoreCase(s2))
								break;
						}
						for (int k = 0; k< res.length;k++)
							if ("quoted-printable".equalsIgnoreCase(res[k]))
							{
								line=in.readLine().toString();
								if (line.length()>=2)
									s2 = line.substring(0,2);
								else
									s2 = null;
								while (!"--".equalsIgnoreCase(s2))
								{
									s1 = s1 + line + "\r";
									line=in.readLine().toString();
									if (line.length()>=2)
										s2 = line.substring(0,2);
									else
										s2 = null;
									if (".".equalsIgnoreCase(s2))
										break;
								}
								message = message + res_g[i] + "\r" + charset + "\r";
								message = message + decoding_qp.decode(s1, charset)+"\r";
								return message;
							}
							else if ("base64".equalsIgnoreCase(res[k]))
							{
								line=in.readLine().toString();
								if (line.length()>=2)
									s2 = line.substring(0,2);
								else
									s2 = null;
								while (!"--".equalsIgnoreCase(s2))
								{
									s1 = s1 + line;
									line=in.readLine().toString();
									if (line.length()>=2)
										s2 = line.substring(0,2);
									else
										s2 = null;
									if (".".equalsIgnoreCase(s2))
										break;
								}
								message = message + res_g[i] + "\r" + charset + "\r";
								message = message + decoding_b64.decodes(charset, s1)+"\r";
								return message;
							}
					}
					else if ("text/plain-waiting".equalsIgnoreCase(res_g[i])) //
					{
						for (int j = res_g.length-1; j >= i; j --)
							if (res_g[j].length()>=3)
							{
								charset = res_g[j];
								break;
							}
						System.out.println(charset);
						s1 = "";
						line=in.readLine().toString();
						res = line.split("[:| ]");
						while (!"Content-Transfer-Encoding".equalsIgnoreCase(res[0]))
						{
							line=in.readLine().toString();
							res = line.split("[:| ]");
							System.out.println(res[0]);
							if (".".equalsIgnoreCase(s2))
								break;
						}
						for (int k = 0; k< res.length;k++)
							if ("quoted-printable".equalsIgnoreCase(res[k]))
							{
								line=in.readLine().toString();
								if (line.length()>=2)
									s2 = line.substring(0,2);
								else
									s2 = null;
								while (!"--".equalsIgnoreCase(s2))
								{
									s1 = s1 + line + "\r";
									line=in.readLine().toString();
									if (line.length()>=2)
										s2 = line.substring(0,2);
									else
										s2 = null;
									if (".".equalsIgnoreCase(s2))
										break;
								}
								message = message + res_g[i] + "\r" + charset + "\r";
								message = message + decoding_qp.decode(s1, charset)+"\r";
								return message;
							}
							else if ("base64".equalsIgnoreCase(res[k]))
							{
								line=in.readLine().toString();
								if (line.length()>=2)
									s2 = line.substring(0,2);
								else
									s2 = null;
								while (!"--".equalsIgnoreCase(s2))
								{
									s1 = s1 + line;
									line=in.readLine().toString();
									if (line.length()>=2)
										s2 = line.substring(0,2);
									else
										s2 = null;
									if (".".equalsIgnoreCase(s2))
										break;
								}
								message = message + res_g[i] + "\r" + charset + "\r";
								message = message + decoding_b64.decodes(charset, s1)+"\r";
								return message;
							}
					}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return ("ERROR\r"+message);
        /*
        try{
            line=in.readLine().toString(); 
            while(!".".equalsIgnoreCase(line)){   
                message=message+line+"\r";   
                line=in.readLine().toString();
            }
        }catch(Exception e){  
            e.printStackTrace();
        }
            return message;*/
	}
	//retr命令
	public String retr(int mailNum,BufferedReader in,BufferedWriter out) throws IOException, InterruptedException{
		String result = null;
		result=getResult(sendServer("RETR "+mailNum,in,out));
		if(!"+OK".equals(result)){
			throw new IOException("接收邮件出错!");
		}
		return getMessagedetail(in);
		//return getMessage(in);
           /* System.out.println("第"+mailNum+"封");
            System.out.println(getMessagedetail(in));
            Thread.sleep(3000);*/
	}
	//退出
	public String quit(BufferedReader in,BufferedWriter out) throws IOException{
		String result;
		result=getResult(sendServer("QUIT",in,out));
		if(!"+OK".equals(result)){
			throw new IOException("未能正确退出");
		}
		return "SUCCESS";
	}
}
