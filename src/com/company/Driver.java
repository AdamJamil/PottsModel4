package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Driver
{
    boolean[][] partialOrder;
    int[][] neighbors;
    ArrayList<State> states;
    private TransitionMatrix tm;
    private int s;
    private boolean[][] badPair;
    private ArrayList<Integer>[] checklist;
    private static final double err = 0.000000001;
    private HashMap<Long, Boolean> result = new HashMap<>();
    private HashMap<Integer, RESum> probability = new HashMap<>();
    private int maxNeighbors = 0;
    private Node root = new Node();

    boolean run(TransitionMatrix in)
    {
        tm = in;
        states = tm.states;
        s = states.size();
        initializeNeighbors();
        partialOrder = guessMaxPartialOrder();

        return refinePartialOrder();
    }

    private boolean  refinePartialOrder()
    {
        while (true)
        {
            long time = System.nanoTime();

            init();
            checkUpsets();

            for (int i = 0; i < s; i++)
                if (badPair[0][i])
                    return false;

            boolean done = true;

            for (int i = 1; i < s; i++)
            {
                int idx = -1;
                for (int j = 0; j < s; j++)
                    if (badPair[i][j])
                        partialOrder[i][idx = j] = (badPair[i][j] = false);
                done &= idx == -1;
            }

            if (done)
                return true;

            System.out.println(((double) (System.nanoTime() - time)) / 1000000000 + "s");
        }
    }

    private boolean[][] guessMaxPartialOrder()
    {
        boolean[][] geq = new boolean[s][s];

        for (int i = 0; i < s; i++)
            for (int j = 0; j < s; j++)
                geq[i][j] = true;

//        if (Main.n == 2)
//            geq[s - 2][s - 1] = false;
//        if (Main.n == 1)
//            geq[s - 1][s - 2] = false;

        for (double lambda = 1.01; lambda < 100; lambda *= 1.01)
        {
            double[][] arr = tm.evaluate(lambda), temp = new double[s][s];
            for (int i = 0; i < s; i++)
                System.arraycopy(arr[i], 0, temp[i], 0, s);
            for (int pow = 0; pow < 8; pow++)
            {
                arr = multiply(arr, arr);

                for (int i = 0; i < s; i++)
                    for (int j = 0; j < s; j++)
                        geq[i][j] &= arr[i][0] + err >= arr[j][0];
            }
        }

        for (int i = 0; i < s; i++)
            if (!geq[0][i])
                System.out.println("!!");

        return geq;
    }

    private void checkUpsets()
    {
        boolean[] allowed = new boolean[s];
        for (int i = 0; i < s; i++)
            allowed[i] = true;

        generateAntichains(new boolean[s], allowed);
    }

    private void generateAntichains(boolean[] curr, boolean[] allowed)
    {
        int idx = -1;
        for (int i = 0; i < s; i++)
            if (allowed[i])
            {
                idx = i;
                break;
            }

        if (idx == -1)
        {
            boolean[] upset = new boolean[s];
            for (int i = 0; i < s; i++)
                if (curr[i])
                    for (int j = 0; j < s; j++)
                        upset[j] |= partialOrder[j][i];

            root.contains(upset);

            for (int i = 0; i < s; i++)
            {
                int key1 = i << maxNeighbors;
                for (int j = 0; j < neighbors[i].length; j++)
                    if (upset[neighbors[i][j]])
                        key1 += (1L << j);

                for (int w = checklist[i].size() - 1; w >= 0; w--)
                {
                    int j = checklist[i].get(w);

                    int key2 = j << maxNeighbors;
                    for (int k = 0; k < neighbors[j].length; k++)
                        if (upset[neighbors[j][k]])
                            key2 += (1L << k);

                    boolean temp;
                    long key = (key1 << 16) + key2;

                    if (result.containsKey(key))
                        temp = result.get(key);
                    else
                        result.put(key, temp = getProbability(i, key1).geq(getProbability(j, key2)));

                    if (!temp)
                    {
                        badPair[i][j] = true;
                        checklist[i].remove(w);
                    }
                }
            }

            return;
        }

        allowed[idx] = false;
        boolean[] other = new boolean[s], otherAllowed = new boolean[s];
        System.arraycopy(curr, 0, other, 0, s);
        System.arraycopy(allowed, 0, otherAllowed, 0, s);
        generateAntichains(curr, allowed);

        other[idx] = true;
        for (int i = 0; i < s; i++)
            if (partialOrder[idx][i] || partialOrder[i][idx])
                otherAllowed[i] = false;

        generateAntichains(other, otherAllowed);
    }

    private void initializeNeighbors()
    {
        neighbors = new int[s][];

        for (int i = 0; i < s; i++)
        {
            int count = 0;

            for (int j = 0; j < s; j++)
                if (!tm.arr[i][j].terms.isEmpty())
                    count++;

            if (count > maxNeighbors)
                maxNeighbors = count;

            neighbors[i] = new int[count];
            count = 0;

            for (int j = 0; j < s; j++)
                if (!tm.arr[i][j].terms.isEmpty())
                    neighbors[i][count++] = j;
        }

        System.out.println(maxNeighbors + " neighbors max");
    }

    private void init()
    {
        badPair = new boolean[s][s];
        boolean[][] minRel = minRel();

        checklist = new ArrayList[s];

        for (int i = 0; i < s; i++)
        {
            checklist[i] = new ArrayList<>(s);
            for (int j = 0; j < s; j++)
                if (i != j && partialOrder[i][j] && minRel[j][i])
                    checklist[i].add(j);
        }
    }

    private boolean[][] minRel()
    {
        boolean[][] out = new boolean[s][s];

        for (int i = 0; i < s; i++)
            for (int j = 0; j < s; j++)
                out[i][j] = partialOrder[j][i];

        for (int i = 0; i < s; i++)
        {
            boolean[] set = out[i];
            set[i] = false;

            outer:
            for (int j = 0; j < s; j++)
            {
                if (!set[j])
                    continue;

                for (int k = 0; k < s; k++)
                {
                    if (!set[k])
                        continue;

                    if (j == k)
                        continue;

                    if (partialOrder[j][k])
                    {
                        set[j] = false;
                        continue outer;
                    }
                }
            }
        }

        return out;
    }

    private RESum getProbability(int state, int key)
    {
        RESum p;

        if (probability.containsKey(key))
            p = probability.get(key);
        else
        {
            p = new RESum();
            for (int k = 0; k < maxNeighbors; k++)
                if ((key & (1 << k)) != 0)
                    p.add(tm.arr[state][neighbors[state][k]]);
            if (p.terms.isEmpty())
                p.terms.add(RationalExpression.zero.copy());
            probability.put(key, p);
        }

        return p;
    }

    private double[][] multiply(double[][] arr1, double[][] arr2)
    {
        double[][] out = new double[s][s];

        for (int i = 0; i < s; i++)
            for (int j = 0; j < s; j++)
                for (int k = 0; k < s; k++)
                    out[i][j] += arr1[i][k] * arr2[k][j];

        return out;
    }
}