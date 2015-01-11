import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gennadiy on 10.01.2015.
 */
public class OTPResolver {



    public ArrayList<Integer> getotpKey(String message, String hexCipher) throws UnsupportedEncodingException {
        ArrayList<String> cipherBytes = Lists.newArrayList(Splitter.fixedLength(2).split(hexCipher));

        byte[] messageBytes = message.getBytes("UTF-8");

        ArrayList<Integer> key = new ArrayList<Integer>();

        int loopLength = messageBytes.length > cipherBytes.size() ? cipherBytes.size() : messageBytes.length;

        for (int i = 0; i < loopLength; i++) {
            int cipher = Integer.parseInt(cipherBytes.get(i), 16);

            key.add(messageBytes[i] ^ cipher);

        }

        return key;

    }

    public String getCipherString(String message, ArrayList<Integer> key) throws UnsupportedEncodingException {
        byte[] messageBytes = message.getBytes("UTF-8");
        StringBuilder cipher = new StringBuilder();
        for (int i = 0; i < key.size(); i++) {
            int cipherByte = messageBytes[i] ^ key.get(i);
            cipher.append(Integer.toHexString(cipherByte));
        }

        return cipher.toString();
    }

    public List<Integer> getOTPKeyFromMultipleCiphers(String[] ciphers) throws UnsupportedEncodingException {
        Map<Integer, List<Integer>> otpKeyMap = new HashMap<Integer, List<Integer>>();
        for (int i = 0; i < ciphers.length - 1; i++) {
            ArrayList<String> messageBytes1 = Lists.newArrayList(Splitter.fixedLength(2).split(ciphers[i]));
            for (int j = i+1; j < ciphers.length; j++ ) {
                ArrayList<String> messageBytes2 = Lists.newArrayList(Splitter.fixedLength(2).split(ciphers[j]));


                int loopLength = messageBytes1.size() > messageBytes2.size() ? messageBytes2.size() : messageBytes1.size();

                for (int k = 0; k < loopLength; k++) {
                    int sum = Integer.parseInt(messageBytes1.get(k), 16) ^ Integer.parseInt(messageBytes2.get(k), 16);
                    if (Character.isAlphabetic(sum)) {

                        if (!otpKeyMap.containsKey(k)) {
                            otpKeyMap.put(k, new ArrayList<Integer>());
                        }

                        otpKeyMap.get(k).add(Integer.parseInt(messageBytes1.get(k), 16) ^ 32);
                        otpKeyMap.get(k).add(Integer.parseInt(messageBytes2.get(k), 16) ^ 32);


                    }

                }

            }
        }

        Map<Integer, Integer> possibleKey = new HashMap<Integer, Integer>();

        for (int key : otpKeyMap.keySet()) {

            List<Integer> matches = otpKeyMap.get(key);

            int maxCounter = 0;
            int loopMatch = 0;
            for (int i = 0; i < matches.size() - 1; i++) {
                int innerCounter = 1;
                for (int j = i + 1; j < matches.size(); j++) {
                    if (matches.get(i) == matches.get(j)) {
                        innerCounter++;
                    }
                }

                if (innerCounter > maxCounter) {
                    loopMatch = matches.get(i);
                }
            }

            possibleKey.put(key, loopMatch);

        }
        List<Integer> keyList = new ArrayList<Integer>();

        for (int key = 0; key < possibleKey.size(); key++) {
            keyList.add(possibleKey.get(key));

        }
        return keyList;
    }

    public void decrypt(String message, List<Integer> key) {
        ArrayList<String> messageBytes = Lists.newArrayList(Splitter.fixedLength(2).split(message));

        int loopLength = messageBytes.size() > key.size() ? key.size() : messageBytes.size();

        for (int i = 0; i < loopLength; i++) {
            if (key.get(i) != null) {
                int sum = Integer.parseInt(messageBytes.get(i), 16) ^ key.get(i);

//                if (Character.isAlphabetic(sum)) {
                    System.out.print(Character.toChars(sum));
//                }
            }
        }
        System.out.println("");

    }


}
