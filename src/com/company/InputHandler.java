package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import static com.company.Main.n;

class InputHandler
{
    LinkedHashSet<Integer>[] graph = new LinkedHashSet[n];
    private int[][] DS = new int[n][];

    InputHandler()
    {
        for (int i = 0; i < n; i++)
            graph[i] = new LinkedHashSet<>();
    }

    ArrayList<State> computeStates()
    {
        ArrayList<PartialAutomorphism> slns = computeAutomorphisms();
        ArrayList<int[]> automorphisms = new ArrayList<>();

        for (PartialAutomorphism automorphism : slns)
            automorphisms.add(automorphism.f);

        ArrayList<ArrayList<int[]>> coloringClasses = new ArrayList<>();

        for (int[] computeColoring : computeColorings())
        {
            ArrayList<int[]> temp = new ArrayList<>();
            temp.add(computeColoring);
            coloringClasses.add(temp);
        }

        outer: for (int i = coloringClasses.size() - 1; i >= 0; i--)
            for (int[] automorphism : automorphisms)
            {
                int[] newColoring = new int[n];
                int[] coloring = coloringClasses.get(i).iterator().next();

                for (int j = 0; j < n; j++)
                    newColoring[j] = coloring[automorphism[j]];

                for (int j = 0; j < i; j++)
                    if (equal(newColoring, coloringClasses.get(j).get(0)) || equalFlip(newColoring, coloringClasses.get(j).get(0)))
                    {
                        coloringClasses.get(j).addAll(coloringClasses.get(i));
                        coloringClasses.remove(i);
                        continue outer;
                    }
            }

        ArrayList<State> out = new ArrayList<>();

        for (ArrayList<int[]> coloringClass : coloringClasses)
            out.add(new State(coloringClass));

        System.out.println(out.size() + " states");

        return out;
    }

    boolean equalFlip(int[] a, int[] b)
    {
        int[] c = new int[n];

        for (int i = 0; i < n; i++)
            if (b[i] == 1)
                c[i] = 2;
            else if (b[i] == 2)
                c[i] = 1;
            else
                c[i] = 0;

        return equal(a, c);
    }

    boolean equal(int[] a, int[] b)
    {
        if (a.length != b.length)
            return false;

        for (int i = 0; i < a.length; i++)
            if (a[i] != b[i])
                return false;

        return true;
    }

    ArrayList<int[]> computeColorings()
    {
        ArrayList<int[]> out = new ArrayList<>(), temp;

        int[] a = new int[n], b = new int[n], c = new int[n];

        for (int i = 1; i < n; i++)
            a[i] = b[i] = c[i] = -1;

        a[0] = 0;
        b[0] = 1;
        c[0] = 2;

        out.add(a);
        out.add(b);
        out.add(c);

        for (int i = 1; i < n; i++)
        {
            temp = new ArrayList<>();

            for (int[] arr : out)
            {
                int[] arr1 = new int[n], arr2 = new int[n], arr3 = new int[n];
                System.arraycopy(arr, 0, arr1, 0, n);
                System.arraycopy(arr, 0, arr2, 0, n);
                System.arraycopy(arr, 0, arr3, 0, n);
                arr1[i] = 0;
                arr2[i] = 1;
                arr3[i] = 2;
                temp.add(arr1);
                temp.add(arr2);
                temp.add(arr3);
            }

            out = temp;
        }

        return out;
    }

    ArrayList<PartialAutomorphism> computeAutomorphisms()
    {
        for (int i = 0; i < n; i++)
        {
            DS[i] = new int[graph[i].size()];

            for (int j = 0; j < DS[i].length; j++)
                DS[i][j] = graph[j].size();

            Arrays.sort(DS[i]);
        }

        boolean[] picked = new boolean[n], hit = new boolean[n];
        int[] f = new int[n];
        for (int i = 0; i < n; i++)
            f[i] = -1;

        return solve(0, picked, hit, f);
    }

    ArrayList<PartialAutomorphism> solve(int v, boolean[] picked, boolean[] hit, int[] f)
    {
        ArrayList<PartialAutomorphism> out = new ArrayList<>();

        outer: for (int w = 0; w < n; w++)
        {
            if (!hit[w] && graph[v].size() == graph[w].size())
            {
                for (int d = 0; d < DS[v].length; d++)
                    if (DS[v][d] != DS[w][d])
                        continue outer;

                for (Integer n : graph[v])
                    if (picked[n] && !graph[w].contains(f[n]))
                        continue outer;

                boolean[] newPicked = new boolean[n], newHit = new boolean[n];

                System.arraycopy(picked, 0, newPicked, 0, n);
                System.arraycopy(hit, 0, newHit, 0, n);

                int[] newF = new int[n];

                System.arraycopy(f, 0, newF, 0, n);
                newF[v] = w;

                newPicked[v] = true;
                newHit[w] = true;

                ArrayList<PartialAutomorphism> partialSolutions = new ArrayList<>();
                partialSolutions.add(new PartialAutomorphism(newF, newPicked, newHit));

                for (Integer n : graph[v])
                    if (!newPicked[n])
                    {
                        ArrayList<PartialAutomorphism> temp = new ArrayList<>();

                        for (PartialAutomorphism automorphism : partialSolutions)
                        {
                            if (automorphism.picked[n])
                            {
                                temp.add(automorphism);
                                continue;
                            }

                            ArrayList<PartialAutomorphism> temp2 = solve(n, automorphism.picked, automorphism.hit, automorphism.f);
                            if (temp2 == null || temp2.isEmpty())
                                continue;
                            temp.addAll(temp2);
                        }

                        if (temp.isEmpty())
                            continue outer;

                        partialSolutions = temp;
                    }

                out.addAll(partialSolutions);
            }
        }

        return out;
    }

    class PartialAutomorphism
    {
        int[] f;
        boolean[] picked, hit;

        PartialAutomorphism(int[] a, boolean[] b, boolean[] c)
        {
            f = new int[n];
            picked = new boolean[n];
            hit = new boolean[n];

            System.arraycopy(a, 0, f, 0, n);
            System.arraycopy(b, 0, picked, 0, n);
            System.arraycopy(c, 0, hit, 0, n);
        }
    }
}
