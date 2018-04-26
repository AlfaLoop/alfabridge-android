package com.alfaloop.android.alfabridge.utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class GenUtils {
    public static String makeConnectionHash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        byte[] code = new byte[4];
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        // divide the digest
        code[0] = (byte)(digest[0] ^ digest[15] ^ digest[1] ^ digest[14] ^ digest[19] );
        code[1] = (byte)(digest[2] ^ digest[13] ^ digest[3] ^ digest[12] ^ digest[18] );
        code[2] = (byte)(digest[4] ^ digest[11] ^ digest[5] ^ digest[10] ^ digest[17]);
        code[3] = (byte)(digest[6] ^ digest[9] ^ digest[7] ^ digest[8] ^ digest[16]);

        String hexStr = "";
        for (int i = 0; i < code.length; i++) {
            hexStr += Integer.toString( ( code[i] & 0xff ) + 0x100, 16).substring( 1 );
        }

//        for (int i = 0; i < digest.length; i++) {
//            hexStr += Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
//        }
        return hexStr;
    }
}
