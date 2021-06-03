package AES;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class AESAlgorithms {

    public static IvParameterSpec ivParam;

    public static void generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        ivParam = new IvParameterSpec(iv);
    }

    public static SecretKeySpec convertKey(BigInteger key) throws NoSuchAlgorithmException {
        byte[] newKey = key.toByteArray();
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        newKey = sha.digest(newKey);
        newKey = Arrays.copyOf(newKey, 16);

        return new SecretKeySpec(newKey,"AES");
    }

    public static void encryptFile(File inputFile, File outputFile, BigInteger key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, convertKey(key), ivParam);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static void decryptFile(File inputFile, File outputFile, BigInteger key) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, convertKey(key), ivParam);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static String encrypt(String msg, BigInteger key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, convertKey(key), ivParam);
        byte[] cipherText = cipher.doFinal(msg.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String msg, BigInteger key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, convertKey(key), ivParam);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(msg));
        return new String(plainText);
    }
}
