/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threadedphysics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

// things to change, array location, add methods to wheel and pendulum to add to arrays, save, load, collision pen, 
/**
 *
 * @author Alex
 */
public class ThreadedPhysics  {
    
    static final boolean Run = true; // Thread run boolean
    
    static ArrayList<Thread> ballThreads = new ArrayList<Thread>();// array responsible for all threads
    static ArrayList<Wheel> wheels = new ArrayList<Wheel>();//array responsible for all wheels
    
    final static int HEIGHT = 720; //standard frame height
    final static int WIDTH = 1024; //standard frame width
    final static int CHEIGHT = HEIGHT/2; // standard control frame height
    final static int CWIDTH = WIDTH/5; // standard control panel width
    final static double damp = .009; // damping coefficient
    
    JFrame frame = new JFrame();//physics frame
    JFrame PFrame = new JFrame(); // pendulum frame
    JFrame control = new JFrame();//control for pure physics module
    JFrame Pcontrol = new JFrame(); // control for pendulum module
    JFrame menu = new JFrame(); // frame for first menu
    JFrame EFrame = new JFrame(); // pendulum edit frame
    
    JPanel EPanel = new JPanel(); // pendulum edit panel
    JPanel panel = new JPanel();//physics panel
    JPanel Mpanel = new JPanel();//menu panel
    JPanel Ppanel = new JPanel(); // pendulum panel
    JPanel controlPanel = new JPanel();//panel for phyiscs control frame
    JPanel PcontrolPanel = new JPanel();//panel for pendulum control frame
    
    
    JButton addBall =  new JButton("Add ball!");//adds new ball
    JButton addVel = new JButton("Add random velocity!");//adds velocity to all balls
    JButton toMenu = new JButton("To Menu");
    JButton start = new JButton("Start Pure Physics!");
    JButton startPen = new JButton("Start pendulums!");
    JButton addPen = new JButton("Add Pendulum");
    
    
    public class savePenBtn implements ActionListener{ // creates and saves pendulum
        JTextField[] j = new JTextField[3];
        public savePenBtn(JTextField[] j){
            this.j = j;
        }
        public void actionPerformed(ActionEvent e) {
            try {
                wheels.add(new Pendulum(new VectorMath2D((double)(ThreadedPhysics.WIDTH/2),(double)(ThreadedPhysics.HEIGHT/2 - 200)),0,200));
            } catch (IOException ex) {
                Logger.getLogger(ThreadedPhysics.class.getName()).log(Level.SEVERE, null, ex);
            }
            Ppanel.add(wheels.get(wheels.size()-1));
            ballThreads.add(new Thread(wheels.get(wheels.size()-1)));
            ballThreads.get(ballThreads.size()-1).start();
            
        }
        
    }
    
    public class ballListener implements ActionListener{ // adds balls to physics sim
        
        public void actionPerformed(ActionEvent e) {
            try {
                wheels.add(Wheel.newWheel());
            } catch (IOException ex) {
                Logger.getLogger(ThreadedPhysics.class.getName()).log(Level.SEVERE, null, ex);
            }
            panel.add(wheels.get(wheels.size()-1));
            ballThreads.add(new Thread(wheels.get(wheels.size()-1)));
            ballThreads.get(ballThreads.size()-1).start();
        }
        
    }
    
    public class penListener implements ActionListener{ // adds pendulums to sim
        
        public void actionPerformed(ActionEvent e) {
            try {
                 wheels.add(new Pendulum(new VectorMath2D((double)(WIDTH/2),(double)(HEIGHT/2 - 200)),Math.random()*.08,(int)(Math.random() * 200 + 100)));
            } catch (IOException ex) {
                Logger.getLogger(ThreadedPhysics.class.getName()).log(Level.SEVERE, null, ex);
            }
            Ppanel.add(wheels.get(wheels.size()-1));
            ballThreads.add(new Thread(wheels.get(wheels.size()-1)));
            ballThreads.get(ballThreads.size()-1).start();
            EFrame.dispose();
            EPanel.removeAll();
            PenEditor();
        }
        
    }
    
    public class velListener implements ActionListener{ // adds random velocity to balls

        
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < wheels.size(); i++){
                wheels.get(i).vel.x -= 10 * Math.cos(Math.random() * (2*Math.PI));
                wheels.get(i).vel.y -= 10 * Math.sin(Math.random() * (2*Math.PI)) ;
            }
        }
        
    }
    
    
     class startPhysicsListener implements ActionListener{ // starts phyics frame
           
            public void actionPerformed(ActionEvent e) {
                 wheels.clear();//reset
            for(int i = 0; i < ballThreads.size(); i ++){
                ballThreads.get(i).interrupt();
            }
            ballThreads.clear();
                menu.dispose();
                
               // menu.removeAll();
                Mpanel.removeAll();
                Mpanel.invalidate();
                panel.removeAll();
                controlPanel.removeAll();
                try {
                    //                wheels.clear();//reset
//                ballThreads.get(0).interrupt();
//                ballThreads.clear();//reset
//                menu.dispose();
//               // menu.removeAll();
//                Mpanel.removeAll();
                    purePhysics();
                } catch (IOException ex) {
                    Logger.getLogger(ThreadedPhysics.class.getName()).log(Level.SEVERE, null, ex);
                }
            }    
        }
     
      class startPendulum implements ActionListener{ // starts phyics frame
           
            public void actionPerformed(ActionEvent e) {
//                wheels.clear();//reset
//                ballThreads.get(0).interrupt();
//                ballThreads.clear();//reset
//                menu.dispose();
//                panel.removeAll();
                wheels.clear();//reset
            for(int i = 0; i < ballThreads.size(); i ++){
                ballThreads.get(i).interrupt();
            }
            ballThreads.clear();//reset
           
            menu.dispose();
            Mpanel.removeAll();
            Mpanel.invalidate();
            Ppanel.removeAll();
                try {
                    RunPendulum();
                } catch (IOException ex) {
                    Logger.getLogger(ThreadedPhysics.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
     
     class ToMenu implements ActionListener{ //sends user back to main menu

        
        public void actionPerformed(ActionEvent e) {
          /*  try {
                Pendulum.SavePendulums("test.txt");
            } catch (IOException ex) {
                Logger.getLogger(ThreadedPhysics.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            wheels.clear();//reset
            for(int i = 0; i < ballThreads.size(); i ++){
                ballThreads.get(i).interrupt();
            }
            ballThreads.clear();//reset
           
            frame.dispose();
            panel.removeAll();
            panel.invalidate();
            control.dispose();
            controlPanel.removeAll();
            controlPanel.invalidate();
            PFrame.dispose();
            Ppanel.removeAll();
            Ppanel.invalidate();
            Pcontrol.dispose();
            PcontrolPanel.removeAll();
            PcontrolPanel.invalidate();
            try {
                startMenu();
            } catch (IOException ex) {
                Logger.getLogger(ThreadedPhysics.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
         
     }
    
    public void startMenu() throws IOException{
        
        menu.setResizable(false);
        JButton start = new JButton("Start Pure Physics!");
        JButton startPen = new JButton("Start pendulums!");
        start.setBounds(WIDTH/2 - 100, HEIGHT/2 - 100, 200, 50);
        startPen.setBounds(WIDTH/2 - 100, HEIGHT/2 + 100, 200, 50);
        Wheel startWheel = Wheel.newWheel();
        wheels.add(startWheel);
        ballThreads.add(new Thread(wheels.get(0)));
        menu.setBounds(0, 0, WIDTH, HEIGHT);
        Mpanel.setBounds(0, 0, WIDTH, HEIGHT);
        ActionListener listen = new startPhysicsListener();
        ActionListener penListen = new startPendulum();
        Mpanel.setLayout(new BorderLayout());
        start.addActionListener(listen);
        startPen.addActionListener(penListen);
        Mpanel.add(start);
        Mpanel.add(startPen);
        Mpanel.add(wheels.get(0));
        ballThreads.get(0).start();
        menu.setVisible(true);
        Mpanel.setVisible(true);
        menu.add(Mpanel);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
    }
    
    public void purePhysics() throws IOException{
        frame.setResizable(false);
       JButton addBall =  new JButton("Add ball!");
        JButton addVel = new JButton("Add random velocity!");
         Wheel wheel2 = Wheel.newWheel();
        JButton toMenu = new JButton("To Menu");
        frame.setBounds(0,0,WIDTH, HEIGHT);
        ActionListener listener = new ballListener();
        ActionListener listen2 = new velListener();
        ActionListener listen3 = new ToMenu();
        addBall.addActionListener(listener);
        addVel.addActionListener(listen2);
        toMenu.addActionListener(listen3);
        panel.setLayout(new BorderLayout());
        panel.add(addBall, BorderLayout.SOUTH);
        panel.add(addVel,BorderLayout.NORTH);
        
        panel.setBounds(0, 0, WIDTH, HEIGHT);
        wheels.add(wheel2);
        ballThreads.add(new Thread(wheels.get(0)));
        panel.add(wheels.get(0));
        ballThreads.get(0).start();
        frame.add(panel);
        frame.setVisible(true);
        panel.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        control.setBounds(0,0,CWIDTH, CHEIGHT);
        control.setVisible(true);
        //control.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controlPanel.add(addBall,BorderLayout.SOUTH);
        controlPanel.add(addVel,BorderLayout.NORTH);
        controlPanel.add(toMenu,BorderLayout.CENTER);
        control.add(controlPanel);
        
        
    }
    
    
    public void RunPendulum() throws IOException{
        PFrame.setResizable(false);
        JButton toMenu = new JButton("To Menu");
        JButton addPen = new JButton("Add Pendulum");
        ActionListener listen = new ToMenu();
        ActionListener listen2 = new penListener();
        toMenu.addActionListener(listen);
        addPen.addActionListener(listen2);
        Pendulum p = new Pendulum(new VectorMath2D((double)(ThreadedPhysics.WIDTH/2),(double)(ThreadedPhysics.HEIGHT/2 - 200)),Math.random()*.08,300);
        PFrame.setBounds(0,0,WIDTH, HEIGHT);
        Ppanel.setBounds(0, 0, WIDTH, HEIGHT);
        Ppanel.setLayout(new BorderLayout());
        wheels.add(p);
        ballThreads.add(new Thread(p));
        Ppanel.add(p);
        PFrame.add(Ppanel);
        PFrame.setVisible(true);
        Ppanel.setVisible(true);
        ballThreads.get(0).start();
        PFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Pcontrol.setBounds(0,0,CWIDTH, CHEIGHT);
        Pcontrol.setVisible(true);
        PcontrolPanel.add(toMenu,BorderLayout.CENTER);
        PcontrolPanel.add(addPen,BorderLayout.CENTER);
        Pcontrol.add(PcontrolPanel);
    }
    
    public void PenEditor(){
        
        JButton save = new JButton("Save");
        JTextField rad = new JTextField("Set Radian"); // radian input
        JTextField length = new JTextField("Set rope length"); // rope length input
        JTextField iv = new JTextField("Set initial velocity"); // initial velocity
        JTextField filename = new JTextField("Set file name"); 
        EFrame.setBounds(0, 0, CWIDTH, CHEIGHT);
        EPanel.setBounds(0,0,CWIDTH,CHEIGHT);
        rad.setBounds(10, CHEIGHT - 10, CWIDTH-10, CHEIGHT/5 - 10);
        length.setBounds(10, CHEIGHT - 10 - CHEIGHT/5, CWIDTH-10, CHEIGHT/5 - 10);
        iv.setBounds(10, CHEIGHT - 10 - (CHEIGHT/5)*2, CWIDTH-10, CHEIGHT/5 - 10);
        filename.setBounds(10, CHEIGHT - 10 - (CHEIGHT/5)*3, CWIDTH-10, CHEIGHT/5 - 10);
        save.setBounds(10, CHEIGHT - 10 - (CHEIGHT/5)*4, CWIDTH-10, CHEIGHT/5 - 10);
        EPanel.add(rad);
        EPanel.add(length);
        EPanel.add(iv);
        EPanel.add(filename);
        EPanel.add(save);
        EFrame.add(EPanel);
        EFrame.setVisible(true);
        EPanel.setVisible(true);
        
    }
    
    public ThreadedPhysics() throws IOException{
            startMenu();
           
        
    }
    
    public static void main(String[] args) throws IOException {
       ThreadedPhysics x = new ThreadedPhysics();
    }
    
}
