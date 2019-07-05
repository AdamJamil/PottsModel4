package com.company;

import java.util.ArrayList;

class State
{
    ArrayList<int[]> colorings;
    int hashCode = -1;

    State(ArrayList<int[]> in)
    {
        colorings = in;
    }

    @Override
    public int hashCode()
    {
        if (hashCode != -1)
            return hashCode;

        hashCode = 1;

        int[] a = new int[3];

        for (int i : colorings.get(0))
            a[i]++;

        if (a[1] < a[2])
        {
            int temp = a[1];
            a[1] = a[2];
            a[2] = temp;
        }

        hashCode *= Math.pow(2, a[0]);
        hashCode *= Math.pow(3, a[1]);
        hashCode *= Math.pow(5, a[2]);

        return hashCode;
    }

    boolean equal(int[] other)
    {
        int otherHash = 1;

        int[] a = new int[3];

        for (int i : colorings.get(0))
            a[i]++;

        if (a[1] < a[2])
        {
            int temp = a[1];
            a[1] = a[2];
            a[2] = temp;
        }

        otherHash *= Math.pow(2, a[0]);
        otherHash *= Math.pow(3, a[1]);
        otherHash *= Math.pow(5, a[2]);

        if (otherHash != hashCode())
            return false;

        outer: for (int[] coloring : colorings)
        {
            for (int i = 0; i < coloring.length; i++)
                if (other[i] != coloring[i])
                    continue outer;
            return true;
        }

        return false;
    }

    @Override
    public String toString()
    {
        int[] a = new int[3];

        for (int i : colorings.get(0))
            a[i]++;

        if (a[1] < a[2])
        {
            int temp = a[1];
            a[1] = a[2];
            a[2] = temp;
        }
        return "(" + a[0] + "," + a[1] + "," + a[2] + ")";
    }
}
