package pcm;

import javax.swing.JFrame;

import pcm.DrawPanel;
import pcm.GUI;

public class GUI
{
     JFrame window;
     DrawPanel panel;

    public GUI()
    {
        window = new JFrame("PCM");
        panel = new DrawPanel();
        window.setSize(810,630);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        return;
    }
}
