package com.example.thinkpad.semail;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.commons.codec.binary.Base64;


public class Decode {
	public String decodedText;
	
	public Decode()  {
	}

	public String decodes(String type, String encoded_text) throws UnknownHostException, IOException{
        Base64 base64 = new Base64();
        byte[] b;
		System.out.println(encoded_text);
        b = base64.decode(encoded_text.getBytes());
        String s = new String(b, type);
        return s;
    }
	
	public String get()
	{
		return decodedText;
	}
	
	/*public static void main(String[] args) throws UnknownHostException, IOException { 
		String content;
        String result;
        // Decode decoding = new Decode("UTF-8", content);
        QuotedPrintableCodec decoding = new QuotedPrintableCodec();
        try{
        		result = decoding.decode(content);
        		System.out.println(result);
        }
        catch(Exception e)
        {
        	System.out.println("Decoding error!");
        }        
        //System.out.println(decoding.get());
 }*/

}
