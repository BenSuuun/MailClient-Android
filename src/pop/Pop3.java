package pop;

import java.util.Base64;

public class Pop3 {
	public static void main(String[] args) throws Exception {
	final Base64.Decoder decoder = Base64.getDecoder();
	final Base64.Encoder encoder = Base64.getEncoder();
	final String text = "HELLO WORLD!\n"
			+ "小钱钱加油加油加油"
			+ "TEST";
	final byte[] textByte = text.getBytes("GBK");
	//编码
	String encodedText = encoder.encodeToString(textByte);
	System.out.println(encodedText);
	//解码
	encodedText = "0KHHrseu";
	System.out.println(new String(decoder.decode(encodedText), "GBK"));
	}
}
