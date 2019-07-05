package com.company;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import static com.company.Main.n;

class TransitionMatrix
{
    //map.get(s1).get(s2) -> gives P(s1 -> s2)
    ArrayList<State> states;
    private int s;
    RESum[][] arr;

    double[][] evaluate(double lambda)
    {
        double[][] out = new double[s][s];

        for (int i = 0; i < s; i++)
            for (int j = 0; j < s; j++)
                out[i][j] = arr[i][j].evaluate(lambda);

        return out;
    }

    TransitionMatrix(InputHandler ih)
    {
        LinkedHashSet<Integer>[] graph = ih.graph;
        states = ih.computeStates();
        s = states.size();

        arr = new RESum[s][s];

        for (int i = 0; i < s; i++)
            for (int j = 0; j < s; j++)
                arr[i][j] = new RESum();

        for (int s1 = 0; s1 < s; s1++)
        {
            int[] startColoring = states.get(s1).colorings.get(0);

            for (int src = 0; src < n; src++)
            {
                Rational pickSrc = new Rational(1, Main.n);
                Polynomial denom = Polynomial.zero.copy();

                int[] neighbors = new int[3];

                for (Integer n : graph[src])
                    neighbors[startColoring[n]]++;

                for (int dst = 0; dst < 3; dst++)
                    denom.add(Polynomial.pow.get(neighbors[dst]));

                for (int dst = 0; dst < 3; dst++)
                {
                    int[] endColoring = new int[n];

                    System.arraycopy(startColoring, 0, endColoring, 0, n);
                    endColoring[src] = dst;

                    RationalExpression p = new RationalExpression();
                    p.denom = denom.copy();
                    p.num = Polynomial.pow.get(neighbors[dst]).copy();
                    p.num.multiply(pickSrc);

                    for (int s2 = 0; s2 < s; s2++)
                        if (states.get(s2).equal(endColoring))
                        {
                            arr[s1][s2].add(p);
                            break;
                        }
                }
            }
        }

        for (RESum[] temp1 : arr)
            for (RESum temp2 : temp1)
                temp2.simplify();

        if (Main.printTM)
            print();
    }

    private void print()
    {
        for (State state : states)
            System.out.print(" & " + (state.toString().replace("†", "\\dagger")));

        for (int s1 = 0; s1 < s; s1++)
        {
            System.out.print(" \\\\ \n" + states.get(s1).toString().replace("†", "\\dagger") + " & ");

            for (int s2 = 0; s2 < s; s2++)
            {
                boolean first = true;
                for (RationalExpression rE : arr[s1][s2].terms)
                {
                    if (rE.num.equals(Polynomial.zero))
                        continue;

                    if (first)
                    {
                        System.out.print("\\frac{" + rE.num.LaTeX() + "}{" + rE.denom.LaTeX() + "}");
                        first = false;
                    }
                    else
                        System.out.print(" + \\frac{" + rE.num.LaTeX() + "}{" + rE.denom.LaTeX() + "}");
                }

                if (arr[s1][s2].terms.isEmpty())
                    System.out.print(0);

                System.out.print(" & ");
            }
        }

        System.out.println();
        System.out.println();
    }
}