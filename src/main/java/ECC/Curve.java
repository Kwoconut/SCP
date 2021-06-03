package ECC;

import java.math.BigInteger;

public class Curve {

    private BigInteger a;
    private BigInteger b;
    private BigInteger mod;
    private BigInteger order;
    private Point basePoint;

    public Curve(BigInteger a, BigInteger b, BigInteger mod, BigInteger order, Point basePoint) {
        this.a = a;
        this.b = b;
        this.mod = mod;
        this.order = order;
        this.basePoint = basePoint;
    }

    public BigInteger getMod() {
        return mod;
    }

    public BigInteger getOrder() {
        return order;
    }

    public Point getBasePoint() {
        return basePoint;
    }

    public Point multiplyPoint(Point P, BigInteger k) {
        Point point = new Point(P.getX(), P.getY());
        String binarySteps = k.toString(2);

        for (int i = 1; i < binarySteps.length(); i++) {
            int currentBit = Integer.parseInt(binarySteps.substring(i, i + 1));
            point = addPoints(point, point);
            if (currentBit == 1) {
                point = addPoints(point, P);
            }
        }
        return point;
    }

    public Point addPoints(Point P, Point Q) {
        if (areSameCoordinates(P, Q)) {
            return pointDouble(P);
        } else {
            return pointAddition(P, Q);
        }
    }

    private Point pointAddition(Point P, Point Q) {
        BigInteger xP = P.getX();
        BigInteger yP = P.getY();

        BigInteger xQ = Q.getX();
        BigInteger yQ = Q.getY();

        BigInteger slope = (yQ.subtract(yP)).multiply(xQ.subtract(xP).modInverse(mod));

        BigInteger xR = calculateXR(xP, xQ, slope);
        BigInteger yR = calculateYR(xP, xR, yP, slope);

        Point R = new Point(xR, yR);
        R.setPointInFiniteField(mod);

        return R;
    }

    private Point pointDouble(Point P) {
        BigInteger xP = P.getX();
        BigInteger yP = P.getY();

        BigInteger slope = (new BigInteger("3").multiply(xP.multiply(xP)).add(this.a)).multiply(new BigInteger("2").multiply(yP).modInverse(mod));

        BigInteger xR = calculateXR(xP, xP, slope);
        BigInteger yR = calculateYR(xP, xR, yP, slope);

        Point R = new Point(xR, yR);
        R.setPointInFiniteField(mod);

        return R;
    }

    private BigInteger calculateXR(BigInteger xP, BigInteger xQ, BigInteger slope) {
        return (slope.multiply(slope)).subtract(xP).subtract(xQ);
    }

    private BigInteger calculateYR(BigInteger xP, BigInteger xR, BigInteger yP, BigInteger slope) {
        return slope.multiply((xP.subtract(xR))).subtract(yP);
    }

    private boolean areSameCoordinates(Point P, Point Q) {
        return P.getX().equals(Q.getX()) && P.getY().equals(Q.getY());
    }

}
