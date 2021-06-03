package ECC;

import java.math.BigInteger;
import java.util.Objects;

public class Point {

    BigInteger x;
    BigInteger y;

    public Point(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public void setPointInFiniteField(BigInteger mod) {
        this.x = this.x.remainder(mod);
        this.y = this.y.remainder(mod);

        while (this.x.compareTo(new BigInteger("0")) < 0) {
            this.x = this.x.add(mod);
        }

        while (this.y.compareTo(new BigInteger("0")) < 0) {
            this.y = this.y.add(mod);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Objects.equals(x, point.x) && Objects.equals(y, point.y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
