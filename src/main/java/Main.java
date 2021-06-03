import AES.AESAlgorithms;
import ECC.Curve;
import ECC.ECCAlgorithms;
import ECC.Point;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {

        BigInteger order = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);

        Point basePoint = new Point(new BigInteger("55066263022277343669578718895168534326250603453777594175500187360389116729240"),
                new BigInteger("32670510020758816978083085130507043184471273380659243275938904335757337482424"));

        Curve curve = new Curve(new BigInteger("0"),new BigInteger("7"), generatePrimeModulo(), order, basePoint);

        BigInteger privateKeyAlice = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140", 16);
        Point publicKeyAlice = curve.multiplyPoint(basePoint, privateKeyAlice);

        BigInteger privateKeyBob = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48AC3ABFF20E8CD0364140", 16);
        Point publicKeyBob = curve.multiplyPoint(basePoint, privateKeyBob);

        ECCAlgorithms algorithms = new ECCAlgorithms(curve);
        BigInteger sharedSecretBob = algorithms.computeSharedKey(privateKeyBob,publicKeyAlice);
        BigInteger sharedSecretAlice = algorithms.computeSharedKey(privateKeyAlice,publicKeyBob);

        System.out.println(sharedSecretBob);
        System.out.println(sharedSecretAlice);

        AESAlgorithms.generateIv();

        AESAlgorithms.encryptFile(new File("C:\\Users\\Angel\\Desktop\\file.txt"), new File("C:\\Users\\Angel\\Desktop\\encryptedFile.txt"), sharedSecretAlice);
        BigInteger[] signature = algorithms.sign(new File("C:\\Users\\Angel\\Desktop\\encryptedFile.txt"), privateKeyAlice);
        boolean isVerified = algorithms.verify(new File("C:\\Users\\Angel\\Desktop\\encryptedFile.txt"), signature, publicKeyAlice);
        if (isVerified) {
            AESAlgorithms.decryptFile(new File("C:\\Users\\Angel\\Desktop\\encryptedFile.txt"), new File("C:\\Users\\Angel\\Desktop\\decryptedFile.txt"), sharedSecretBob);
        }

    }

    public static BigInteger generatePrimeModulo(){

        //Secp256k1
        //Recommended 256-bit Elliptic Curve Domain Parameters over Fp (http://www.secg.org/sec2-v2.pdf)
        //modulo for bitcoin
        //2^256 - 2^32 - 2^9 - 2^8 - 2^7 - 2^6 - 2^4 - 2^0

        BigInteger base = new BigInteger("2");

        BigInteger modulus =  base.pow(256)
                .subtract(base.pow(32))
                .subtract(base.pow(9))
                .subtract(base.pow(8))
                .subtract(base.pow(7))
                .subtract(base.pow(6))
                .subtract(base.pow(4))
                .subtract(base.pow(0));

        return modulus;

    }

}
