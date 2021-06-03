package ECC;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ECCAlgorithms {

    private Curve curve;

    public ECCAlgorithms(Curve curve) {
        this.curve = curve;
    }

    public BigInteger computeSharedKey(BigInteger privateKey, Point publicKey) {
        return curve.multiplyPoint(publicKey, privateKey).getX();
    }

    public BigInteger[] sign(Object object, BigInteger privateKey) throws NoSuchAlgorithmException, IOException {
        BigInteger s;
        BigInteger r;
        BigInteger k;
        Point curvePoint;

        BigInteger[] signature = new BigInteger[2];

        BigInteger e = this.hash(object);
        BigInteger z = this.calculateZ(e);

        do {
            do {
                k = generateRandomKey(this.curve.getOrder());
                curvePoint = this.curve.multiplyPoint(this.curve.getBasePoint(), k);
                r = curvePoint.getX().mod(this.curve.getOrder());
            }
            while (r.compareTo(BigInteger.ZERO) == 0);
            BigInteger kInv = k.modInverse(this.curve.getOrder());
            s = (kInv.multiply(z.add(privateKey.multiply(r)))).mod(this.curve.getOrder());
        } while (s.compareTo(BigInteger.ZERO) == 0);

        signature[0] = r;
        signature[1] = s;
        return signature;
    }

    public boolean verify(Object object, BigInteger[] signature, Point publicKey) throws NoSuchAlgorithmException, IOException {
        BigInteger r = signature[0];
        BigInteger s = signature[1];

        if (r.compareTo(this.curve.getOrder()) >= 0 || s.compareTo(this.curve.getOrder()) >= 0) {
            return false;
        }

        BigInteger e = this.hash(object);
        BigInteger z = this.calculateZ(e);
        BigInteger signatureInv = s.modInverse(this.curve.getOrder());

        BigInteger u1 = (z.multiply(signatureInv)).mod(this.curve.getOrder());
        BigInteger u2 = (r.multiply(signatureInv)).mod(this.curve.getOrder());

        Point curvePoint = curve.addPoints(curve.multiplyPoint(curve.getBasePoint(), u1), curve.multiplyPoint(publicKey, u2));

        if (curvePoint.getX().equals(BigInteger.ZERO) || curvePoint.getY().equals(BigInteger.ZERO)) {
            return false;
        }

        BigInteger v = curvePoint.getX().mod(this.curve.getOrder());

        if (v.compareTo(r) == 0) {
            System.out.println("Message VERIFIED");
            return true;
        }

        System.out.println("Message NOT VERIFIED");
        return false;
    }

    private BigInteger hash(Object object) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes;

        if (object instanceof String) {
            bytes = ((String) object).getBytes();
        } else if (object instanceof File) {
            bytes = Files.readAllBytes(((File) object).toPath());
        } else {
            return null;
        }

        md.update(bytes);
        byte[] hashByte = md.digest();
        return new BigInteger(hashByte).abs();
    }

    private BigInteger calculateZ(BigInteger e) {
        return e.shiftRight(e.bitLength() - this.curve.getMod().bitLength());
    }

    private BigInteger generateRandomKey(BigInteger n) {
        BigInteger r;
        Random rnd = new Random();
        do {
            r = new BigInteger(n.bitLength(), rnd);
        } while (r.compareTo(n) >= 0);
        return r;
    }
}