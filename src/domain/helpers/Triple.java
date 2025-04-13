package domain.helpers;

import java.util.Objects;

public class Triple<A, B, C> {
    public final A x;
    public final B y;
    public final C z;

    public Triple(A x, B y, C z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public A getx() {
        return x;
    }

    public B gety() {
        return y;
    }

    public C getz() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(x, triple.x) &&
               Objects.equals(y, triple.y) &&
               Objects.equals(z, triple.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}