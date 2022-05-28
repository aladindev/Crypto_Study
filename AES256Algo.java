import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AES256Algo {

	/**
	 *  양방향 암호화 알고리즘 AES256
	 */
	
	private final static String alg = "AES/CBC/PKCS5Padding";				//특정크기보다 부족한 부분의 공간을 채워 비트수를 맞추는 방식
	private final static String key = "AAAABBBBCCCCDDDDEEEEFFFFGGGGHHHH"; 	//암호화 키 값 32byte
	private final static String iv = key.substring(0, 16); 					// 벡터값 16byte
	
	
    private static String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }
    
    /**
     * 입력된 문자열 배열을 암호화 후 전문으로 반환하는 함수
     * @param arg
     * @return
     * @throws Exception 
     */
    private static String arrayToTelegram(String[] arg) throws Exception {
    	
    	//양방향 암호화에서는 반드시 암호화 할 때의 키 값과 복호화 할 때의 키 값이 동일해야 한다.
    	StringBuilder telegram = new StringBuilder();
    	
    	System.out.println("\n-------input---------");
    	for(String str : arg) {
    		System.out.println("\n" + str);
    		telegram.append( encrypt(str) + " ");
    	}
    	System.out.println("\n-----input end -------");
    	
    	return telegram.toString();
    }
    
    private static ArrayList<Map<Integer, String>> telegramToMap(String arg) throws Exception {
    	
    	ArrayList<Map<Integer, String>> result = new ArrayList<>();
    	
    	String[] plainStr = arg.split(" ");
    	int index = 0;
    	for(String str : plainStr) {
    		//복호화
    		String decrptResult = decrypt(str);
    		Map<Integer, String> map = new HashMap<Integer, String>();
    		map.put(index, decrptResult);
    		result.add(map);
    		
    		index+=1;
    	}
    	
    	return result;
    }
	
	public static void main(String[] args) throws Exception {
		
		/**
		 * fixed-length 
		 * 암복호화 테스트
		 */
	
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringBuilder sb = new StringBuilder();
		/*
		 * input arguments : 이름 휴대폰번호 생년월일(yyyyMMdd)
		 * test value :'홍길동 01011112222 19700101'
		 */
		String[] inputArr = br.readLine().split(" ");
		/* 입력값 암호화 된 전문으로 반환 */
		String telegram = arrayToTelegram(inputArr);
		
		System.out.println("\n----------telegram---------");
		System.out.println("\n" + telegram);
		System.out.println("\n-------telegram end--------");
		
		ArrayList<Map<Integer, String>> result = telegramToMap(telegram);
		
		int index = 0;
		for(Map map : result) {
			sb.append(map.get(index++) + "\n");
		}
		System.out.println("\n------decrpt result------");
		System.out.println("\n" + sb.toString());
		System.out.println("\n------decrpt result------");
	}
}
