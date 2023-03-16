package com.revature.GameLogic.AllGames;

import java.security.SecureRandom;

public class IdGenerator {
    private static final String URL_CHARS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
    private static final int GENERATED_STRING_LENGTH = 64;
    private static final SecureRandom rand = new SecureRandom();

    private IdGenerator(){ }

    public static String generate_id(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < GENERATED_STRING_LENGTH; i++){
            int pos = rand.nextInt() % URL_CHARS.length();
            if(pos < 0) pos *= -1;
            sb.append(URL_CHARS.charAt(pos));
        }
        return sb.toString();
    }
}
