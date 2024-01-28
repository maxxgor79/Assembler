package ru.assembler.zxspectrum.io.loader;

import lombok.NonNull;

public class Filter {
    private final double[] a;

    private final double[] b;

    private final double[] s;

    private Filter() {
        a = null;
        b = null;
        s = null;
    }

    public Filter(final double[] a, final double[] b) {
        this(a, b, null);
    }

    public Filter(@NonNull final double[] a, @NonNull final double[] b, final double[] s) {
        int n = Math.max(a.length, b.length);
        this.a = new double[n];
        this.b = new double[n];
        this.s = new double[n];
        update(a, b);
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                this.s[i] = s[i];
            }
        }
    }

    // Digital Filter Implementation II, in & out overlapables.
    public void filter(@NonNull final double[] in, @NonNull final double[] out) {
        if (in.length != out.length) {
            throw new IllegalArgumentException("in out different length");
        }
        for (int i = 0; i < in.length; i++) {
            double w = in[i];
            double y = 0.0;
            for (int j = a.length - 2; j >= 0; j--) {
                w -= a[j + 1] * s[j];
                y += b[j + 1] * s[j];
                s[j + 1] = s[j];
            }
            y += w * b[0];
            s[0] = w;
            out[i] = y;
        }
    }

    public double filter(final double in) // single sample filtering
    {
        final double[] result = new double[1];
        filter(new double[]{in}, result);
        return result[0];
    }

    public void update(@NonNull final double[] a, @NonNull final double[] b) {
        if (a.length > this.a.length) {
            throw new ArrayIndexOutOfBoundsException("a");
        }
        if (b.length > this.b.length) {
            throw new ArrayIndexOutOfBoundsException("b");
        }
        for (int i = 0; i < a.length; i++) {
            this.a[i] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            this.b[i] = b[i];
        }
        for (int i = 0; i < a.length; i++) {
            this.a[i] /= a[0]; // normalization.
            this.b[i] /= a[0];
        }
    }
}
