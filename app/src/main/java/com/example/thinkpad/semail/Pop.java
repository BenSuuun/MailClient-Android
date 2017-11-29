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
		String test= "Content-Type: multipart/alternative; BOUNDARY=\"=_Part_854971_716788961.1510304382627\"";
		String ta[];
		ta = test.split("[: ;]");
		for (int i = 0; i < ta.length; i++)
		{
			System.out.println(ta[i]);
		}
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
		result = pop3Client.input("RETR 2174");
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
		String message_plain = "";
		int read_line=0;
		String line = null, s1 = null, s2 = null, charset = "Loading", s3 = null;
		String m_date = "Loading...",m_from="Loading...",m_subject="Loading...";
		String[] res,res_g;
		String boundary = "";
		String end_boundary;
		String general_type = "";
		int read_num=0, read_date=0;
		Decode decoding_b64 = new Decode();
		QuotedPrintableCodec decoding_qp = new QuotedPrintableCodec();
		try{
			line=in.readLine().toString();
			while (read_num+read_date < 4)
			{
				if (".".equalsIgnoreCase(line))
				{
					break;
				}
				System.out.println(line);
				res_g = line.split("[:| ]");
				if ("Date".equalsIgnoreCase(res_g[0]))
				{
					if (" ".equalsIgnoreCase(line.substring(5,6)))
						m_date = line.substring(6);
					else
						m_date = line.substring(5);
					read_date = 1;
					//System.out.println(m_date);
					line=in.readLine().toString();
					continue;
				}
				else if ("From".equalsIgnoreCase(res_g[0]))
				{
					res = line.split("\\<");
					if (res.length == 1)
					{
						m_from = res_g[res_g.length-1];
						read_num++;
						line=in.readLine().toString();
						continue;
					}
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
						if (line.length()>=1)
						{
							s1 = line.substring(0,1);
							while(" ".equalsIgnoreCase(s1)){
								res = line.split("\\?");
								s2 = s2 + decoding_b64.decodes(res[1], res[3]);
								line=in.readLine().toString();
								s1 = line.substring(0,1);
							}
						}
						m_subject = s2;
						read_num++;
						line=in.readLine().toString();
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

				else if ("Content-Type".equalsIgnoreCase(res_g[0]))
				{
					res = line.split("[: ;]");
					for (int l = 1; l < res.length; l++)
						if (res[l].length()>= 5)
						{
							general_type = res[l];
							read_num ++;
							if (res.length - l <= 2)
							{
								line=in.readLine().toString();
								System.out.println(line);
							}
							break;
						}
					res = line.split("\"");
					/*for (int l = 0; l < res.length; l++)
					{
						System.out.println(res[l]);
					}*/
					if (res.length > 1)
					{
						for (int l = 1; l < res.length; l++)
							if (res[l].length()>= 5)
							{
								boundary = res[l];
								break;
							}
					}
				}
				line=in.readLine().toString();
			}
			boundary = "--"+boundary;
			end_boundary = boundary+"--";
			System.out.println(boundary);
			System.out.println(general_type);
			message = message + m_date + "\n" + m_from + "\n" + m_subject + "\n";
			message_plain = message_plain + m_date + "\n" + m_from + "\n" + m_subject + "\n";
			res = general_type.split("/");
			if (!"multipart".equalsIgnoreCase(res[0]))
			{
				message = message + "base64\nUTF-8\n";
				message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
				return message;
			}
			System.out.println(message);
			while (!".".equalsIgnoreCase(line))
			{
				if (".".equalsIgnoreCase(line))
				{
					message = message + "base64\nUTF-8\n";
					message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
					return message;
				}
				line=in.readLine().toString();
				System.out.println("begin:"+line);
				if (".".equalsIgnoreCase(line))
				{
					message = message + "base64\nUTF-8\n";
					message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
					return message;
				}
				while (!boundary.equalsIgnoreCase(line) && !end_boundary.equalsIgnoreCase(line))
				{
					//System.out.println(line);
					line=in.readLine().toString();
					if (".".equalsIgnoreCase(line))
						break;
				}
				/*if (line.length()>=2)
					s1 = line.substring(0,2);
				else
					s1 = null;
				while(!"--".equalsIgnoreCase(s1)){
					line=in.readLine().toString();
					if (line.length()>=2)
						s1 = line.substring(0,2);
					else
						s1 = null;
				}*/
				if (".".equalsIgnoreCase(line))
				{
					message = message + "base64\nUTF-8\n";
					message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
					return message;
				}
				line=in.readLine().toString();
				if (".".equalsIgnoreCase(line))
				{
					message = message + "base64\nUTF-8\n";
					message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
					return message;
				}
				res_g = line.split("[:|;|=| |\"]"); //正则表达式取出type 和 charset
				System.out.println(line);
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
						int charset_flag = 0;
						for (int j = res_g.length-1; j >= 0; j --)
							if ("charset".equalsIgnoreCase(res_g[j]))
							{
								charset_flag = 1;
								break;
							}
						if (charset_flag == 0)
						{
							if (".".equalsIgnoreCase(line))
							{
								message = message + "base64\nUTF-8\n";
								message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
								return message;
							}
							line=in.readLine().toString();
							res = line.split("[:|;|=| |\"]");
							for (int j = res.length-1; j >= i; j --)
								if (res[j].length()>=3)
								{
									charset = res[j];
									break;
								}
						}
						System.out.println(charset);
						s1 = "";
						if (".".equalsIgnoreCase(line))
						{
							message = message + "base64\nUTF-8\n";
							message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
							return message;
						}
						if (".".equalsIgnoreCase(line))
						{
							message = message + "base64\nUTF-8\n";
							message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
							return message;
						}
						line=in.readLine().toString();
						res = line.split("[:| ]");
						while (!"Content-Transfer-Encoding".equalsIgnoreCase(res[0]))
						{
							if (".".equalsIgnoreCase(line))
							{
								message = message + "base64\nUTF-8\n";
								message = message + decoding_qp.decode("Loading...", "UTF-8")+"\n";
								return message;
							}
							line=in.readLine().toString();
							res = line.split("[:| ]");
							if (".".equalsIgnoreCase(s2))
								break;
						}
						int read_line_num = 0;
						for (int k = 0; k< res.length;k++)
							if ("quoted-printable".equalsIgnoreCase(res[k]))
							{
								line=in.readLine().toString();
								while (!boundary.equalsIgnoreCase(line) && !end_boundary.equalsIgnoreCase(line))
								{
									s1 = s1 + line + "\n";
									line=in.readLine().toString();
									read_line_num ++;
									if (read_line_num > 1000)
									{
										message = "Loading...\nLoading...\nLoading...\n";
										break;
									}
									if (".".equalsIgnoreCase(line))
										break;
								}
								/*
								if (line.length()>=2)
									s2 = line.substring(0,2);
								else
									s2 = null;
								while (!"--".equalsIgnoreCase(s2))
								{
									s1 = s1 + line + "\n";
									line=in.readLine().toString();
									if (line.length()>=2)
										s2 = line.substring(0,2);
									else
										s2 = null;
									if (".".equalsIgnoreCase(s2))
										break;
								}*/
								message = message + res_g[i] + "\n" + charset + "\n";
								System.out.println(s1);
								//byte[] s1_b = s1.getBytes(charset);
								message = message + decoding_qp.decode(s1, charset)+"\n";
								System.out.println(message);
								return message;
							}
							else if ("base64".equalsIgnoreCase(res[k]))
							{
								line=in.readLine().toString();
								while (!boundary.equalsIgnoreCase(line) && !end_boundary.equalsIgnoreCase(line))
								{
									s1 = s1 + line;
									line=in.readLine().toString();
									if (".".equalsIgnoreCase(line))
										break;
								}
								/*
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
								}*/
								message = message + res_g[i] + "\n" + charset + "\n";
								message = message + decoding_b64.decodes(charset, s1)+"\n";
								System.out.println(message);
								return message;
							}
					}
					else if ("text/plain_wait".equalsIgnoreCase(res_g[i])) //
					{
						for (int j = res_g.length-1; j >= i; j --)
							if (res_g[j].length()>=3)
							{
								charset = res_g[j];
								break;
							}
						int charset_flag = 0;
						for (int j = res_g.length-1; j >= 0; j --)
							if ("charset".equalsIgnoreCase(res_g[j]))
							{
								charset_flag = 1;
								break;
							}
						if (charset_flag == 0)
						{
							line=in.readLine().toString();
							res = line.split("[:|;|=| |\"]");
							for (int j = res.length-1; j >= i; j --)
								if (res[j].length()>=3)
								{
									charset = res[j];
									break;
								}
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
								while (!boundary.equalsIgnoreCase(line) && !end_boundary.equalsIgnoreCase(line))
								{
									s1 = s1 + line + "\n";
									System.out.println(line);
									line=in.readLine().toString();
									if (".".equalsIgnoreCase(line))
										break;
								}
								/*
								line=in.readLine().toString();
								if (line.length()>=2)
									s2 = line.substring(0,2);
								else
									s2 = null;
								while (!"--".equalsIgnoreCase(s2))
								{

									s1 = s1 + line + "\n";
									line=in.readLine().toString();
									if (line.length()>=2)
										s2 = line.substring(0,2);
									else
										s2 = null;
									if (".".equalsIgnoreCase(s2))
										break;
								}*/
								message_plain = message_plain + res_g[i] + "\n" + charset + "\n";
								message_plain = message_plain + decoding_qp.decode(s1, charset)+"\n";
								//return message_plain;
							}
							else if ("base64".equalsIgnoreCase(res[k]))
							{

								line=in.readLine().toString();
								System.out.println(line);
								while (!boundary.equalsIgnoreCase(line) && !end_boundary.equalsIgnoreCase(line))
								{
									s1 = s1 + line;
									line=in.readLine().toString();
									System.out.println(line);
									if (".".equalsIgnoreCase(line))
										break;
								}
								/*
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
								}*/
								message_plain = message_plain + res_g[i] + "\n" + charset + "\n";
								message_plain = message_plain + decoding_b64.decodes(charset, s1)+"\n";
								//return message_plain;
							}
					}
					else if ("multipart/alternative".equalsIgnoreCase(res_g[i]) || "multipart/related".equalsIgnoreCase(res_g[i]))
					{
						res = line.split("[: ;]");
						for (int l = 1; l < res.length; l++)
							if (res[l].length()>= 5)
							{
								general_type = res[l];
								read_num ++;
								if (res.length - l <= 2)
								{
									line=in.readLine().toString();
									System.out.println(line);
								}
								break;
							}
						res = line.split("\"");
						/*for (int l = 0; l < res.length; l++)
						{
							System.out.println(res[l]);
						}*/
						if (res.length > 1)
						{
							for (int l = 1; l < res.length; l++)
								if (res[l].length()>= 5)
								{
									boundary = res[l];
									boundary = "--"+boundary;
									end_boundary = boundary+"--";
									System.out.println("new_boundary"+boundary);
									break;
								}
						}
					}

			}
		}catch(Exception e){
			e.printStackTrace();
			//return (message_plain);
		}

		return (message+"Loading...");
	}
	//retr命令
	public String retr(int mailNum,BufferedReader in,BufferedWriter out) throws IOException, InterruptedException{
		String result = null;
		result=getResult(sendServer("RETR "+mailNum,in,out));
		if(!"+OK".equals(result)){
			throw new IOException("接收邮件出错!");
		}
		return getMessagedetail(in);
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