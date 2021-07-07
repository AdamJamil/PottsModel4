package com.company;

class Rational
{
    long p = 0, q = 1;

    Rational copy()
    {
        return new Rational(p, q);
    }

    double value()
    {
        return p / (double) q;
    }

    void multiply(Rational other)
    {
        long gcd = gcd(other.p, other.q);
        p *= other.p / gcd;
        q *= other.q / gcd;
    }

    void add(Rational other)
    {
        p = (p * other.q) + (other.p * q);
        q *= other.q;
        long gcd = gcd(p, q);
        p /= gcd;
        q /= gcd;
    }

    private static long gcd(long a, long b)
    {
        a = Math.abs(a);
        b = Math.abs(b);
        return b == 0 ? a : gcd(b, a % b);
    }
    
    Rational(){}

    Rational(long a, long b)
    {
        p = a;
        q = b;
    }

    @Override
    public String toString()
    {
        if (p == 1 && q == 1) return "";
        if (q == 1) return "" + p;
        return "(" + p + "/" + q + ")";
    }
}
