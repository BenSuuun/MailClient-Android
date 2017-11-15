package com.example.thinkpad.semail;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.commons.codec.binary.Base64;

public class Encode {
    public String encodedText;

    public Encode()  {
    }

    public String encode(String type, String decoded_text)  throws UnknownHostException, IOException{
        Base64 base64 = new Base64();
        byte[] ori_b = decoded_text.getBytes(type);
        byte[] fin_b;
        fin_b = base64.encode(ori_b);
        String s = new String(fin_b, type);
        return s;
    }

    public String get()
    {
        return encodedText;
    }

    /*public static void main(String[] args) throws UnknownHostException, IOException {
        String message = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><style> body {direction:ltr; background-color:#fff} .listing td {border-bottom: 1px solid #eee} .listing tr:hover td {background-color:#eee} .listing th {background-color:#f5f5f5 } h4{line-height: 0.2em} </style></head><body style=\"font-family: arial; color:#333; margin:0; padding:0em; font-size:80% \"><table cellspacing=\"0\" cellpadding=\"5\" border=\"0\" style=\"background-color:#e9e9e9;  width:100%; height:30px; text-shadow:1px 1px 1px #fff;\"><tr><td><h1>预约取书通知-图书馆</h1></td><td align=\"right\">2017/11/02</td></tr></table><table cellspacing=\"0\" cellpadding=\"5\" border=\"0\" width=\"100%\"><tr><td width=\"50%\"><table cellspacing=\"0\" cellpadding=\"5\" border=\"0\" style=\"list-style: none; margin:0 0 0 1em; padding:0\"><tr><td><b>孙王斌</b></td></tr><tr><td>经济管理学院</td></tr></table></td></tr></table><div class=\"messageArea\"><div class=\"messageBody\"><table cellspacing=\"0\" cellpadding=\"5\" border=\"0\"><tr><td><b>请您按如下提示信息取书， 逾期将不再为您保留。如果您的Email发生变更，请及时到您的个人记录中修改，以便接收流通通知。</b></td></tr><tr><td><div class=\"recordTitle\"><span class=\"spacer_after_1em\"><b>书名：</b>模式识别</span></div><div class=\"\"><span class=\"spacer_after_1em\"><span class=\"recordAuthor\"><b>作者：</b>张学工</span></span></div></td></tr><tr><td><b>预约取书地 ：</b>主馆 - 北馆总服务台</td></tr><tr><td><b>预约书保留至：： </b>2017/11/05.							 </td></tr><tr><td></td></tr><tr><td><b>【特别说明】</b></td></tr><tr><td>文科馆目前施工闭馆，预约取书安排如下：取书地点：文科馆南侧小门    取书时间：周一至周五 11:00-14:00</td></tr><tr><td>咨询电话：62798676</td></tr></table></div></div><br><table><tr><td>清华大学图书馆</td></tr></table><table style=\"background-color:#444;  width:100%; text-shadow:1px 1px 1px #333; color:#fff; margin-top:1em;  font-weight:700; line-height:2em; font-size:150%;\"><tr style=\"list-style: none; margin:0 0 0 1em; padding:0\"><td align=\"center\">清华大学图书馆&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr></table></body></html>";
        Encode encoding = new Encode();
        System.out.println(encoding.encode("US-ASCII", message));

    }*/
}