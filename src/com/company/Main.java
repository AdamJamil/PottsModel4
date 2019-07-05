package com.company;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class Main extends Application
{
    //number of vertices
    static int n = 5;
    static boolean printTM = false;
    static Driver d;

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("Potts Model");
        Group root = new Group();
        Canvas canvas = new Canvas(GUtil.width, GUtil.height);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);

        new GUtil(d, gc);
    }

    static InputHandler ih;

    public static void main(String[] args)
    {
        ArrayList<LinkedHashSet<Integer>[]> graphs = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        int m;

        while ((m = sc.nextInt()) != -1)
        {
            ih = new InputHandler();
            for (int i = 0; i < m; i++)
                link(sc.nextInt() - 1, sc.nextInt() - 1);
            TransitionMatrix tm = new TransitionMatrix(ih);
            if (new Driver().run(tm))
                System.out.println("kanryo!");
            else
                System.out.println("mata shippai :/");
        }
    }

    static void link(int a, int b)
    {
        ih.graph[a].add(b);
        ih.graph[b].add(a);
    }
}
