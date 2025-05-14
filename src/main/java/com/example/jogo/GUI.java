package com.example.jogo;

import javax.swing.JFrame;

public class GUI
{
     JFrame window;
     DrawPanel panel;

    public GUI()
    {
        window = new JFrame("Jogo do Nickson");
        window.setSize(810,630);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicializar o painel antes de adicioná-lo à janela
        panel = new DrawPanel();

        window.getContentPane().add(panel);
        window.setVisible(true);
        window.setResizable(false);
    }

    public void go()
    {
        panel.startGame();
        panel=null;
        window.dispose();
        window=null;
    }
    
    public static void main(String[]args)
    {
        GUI game = new GUI();
        game.go();
        //game.panel.startGame();
    }
}
