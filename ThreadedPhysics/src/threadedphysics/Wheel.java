/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threadedphysics;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 *
 * @author Alex
 */
public class Wheel extends JComponent implements Runnable {
    static int diameter = 100;//diameter of wheel
    public VectorMath2D position = new VectorMath2D(0,0); // position of whee;
    public VectorMath2D vel = new VectorMath2D(0,0); // velocity of wheel
    public VectorMath2D accel = new VectorMath2D(0,.67); // acceleration constant vector
    public VectorMath2D CenterA = this.position.add(new VectorMath2D(this.diameter/2,this.diameter/2)); // center of wheel
    public VectorMath2D ropePos = new VectorMath2D((double)(ThreadedPhysics.WIDTH/2),(double)(ThreadedPhysics.HEIGHT/2 - 200)); // draw position of rope
    public VectorMath2D ropeEnd = new VectorMath2D(0,0); // end of rope
    protected double velRad = 0; // radian velocity of wheel

    int recWidth = 6;
    int recX = (int)(position.x+diameter/2 + recWidth/2 - diameter/20);
    static int floorHeight = 50;
    final static int floor = ThreadedPhysics.HEIGHT - 40 - floorHeight;
    protected static int ID = -1;
    protected int thisID = 0;
    double spokePos = 0;
    
    Rectangle rec = new Rectangle((int)(position.x+diameter/2 - 3) ,(int)(position.y),6,diameter/2);
    Graphics2D g = null;
    AffineTransform orig = null;
    AffineTransform trans = new AffineTransform();
    
    Color[] colors = {Color.BLACK,Color.BLUE,Color.CYAN,Color.DARK_GRAY,Color.GREEN,Color.ORANGE,Color.RED,Color.YELLOW};
    Color mycolor = Color.BLUE;
    
    static Lock MainLock = new ReentrantLock();//main lock used in wheel class
    
    public static BufferedImage image;//image for each panel
    public String picFile = "physics.jpeg";//image file name
    
    
    public Wheel(int diameter,double velX,double velY,double x, double y) throws IOException { // for initialization of array
        this.diameter = diameter;
        this.vel.x = velX;
        this.vel.y = velY;
        position.x = x;
        position.y = y;
        this.mycolor = colors[(int)(Math.random() * 7)];
        image = ImageIO.read(new File(picFile));
        ID++;
        thisID = ID;
    }
   
    @Override
   public void paint(Graphics c){
      MainLock.lock();
      
       try{
       g = (Graphics2D)c;
       orig = g.getTransform();
       
       g.drawImage(image,0,0,null); // draws background pic
       for (int j = 0; j < ThreadedPhysics.wheels.size();j++){
            g.setColor(ThreadedPhysics.wheels.get(j).mycolor);
            
            g.drawLine(0, floor, ThreadedPhysics.WIDTH, floor);
           // g.setPaint(ThreadedPhysics.wheels.get(j).mycolor);
            g.setStroke(new BasicStroke(5));
            g.drawOval((int)(ThreadedPhysics.wheels.get(j).position.x ), (int)(ThreadedPhysics.wheels.get(j).position.y) , 
                    ThreadedPhysics.wheels.get(j).diameter, ThreadedPhysics.wheels.get(j).diameter); // draws largest circle
            g.fillOval((int)(ThreadedPhysics.wheels.get(j).position.x + ThreadedPhysics.wheels.get(j).diameter/2 - ThreadedPhysics.wheels.get(j).diameter/20), 
                    (int)(ThreadedPhysics.wheels.get(j).position.y + ThreadedPhysics.wheels.get(j).diameter/2 - ThreadedPhysics.wheels.get(j).diameter/20),
                    (ThreadedPhysics.wheels.get(j).diameter / 10), (ThreadedPhysics.wheels.get(j).diameter/10));
            
            ThreadedPhysics.wheels.get(j).recX = (int)(ThreadedPhysics.wheels.get(j).position.x+ThreadedPhysics.wheels.get(j).diameter/2 + 
                    ThreadedPhysics.wheels.get(j).recWidth/2 - ThreadedPhysics.wheels.get(j).diameter/20);
            for (int i = 0; i < 3; i++){
                g.rotate(Math.toRadians(ThreadedPhysics.wheels.get(j).spokePos + 120*i),ThreadedPhysics.wheels.get(j).recX + 
                        ThreadedPhysics.wheels.get(j).diameter/40 , (int)(ThreadedPhysics.wheels.get(j).position.y + ThreadedPhysics.wheels.get(j).diameter/2-1));
                g.fillRect(ThreadedPhysics.wheels.get(j).recX ,(int)(ThreadedPhysics.wheels.get(j).position.y), ThreadedPhysics.wheels.get(j).recWidth, 
                        ThreadedPhysics.wheels.get(j).diameter/2);
                g.setTransform(orig);   
            }
        }
       
   }
       
       finally{
           MainLock.unlock();
       }
    }
    
    
    
    public void move(double damp){
        //y = vel(t)
        //vel = accel * time
        //accel = 40 pixs/sec
        vel = vel.add(accel);
        if(position.y >= floor - diameter - 5){
          vel = vel.sub(vel.mult(new VectorMath2D(damp,damp/2)));
        }
        else{
            vel = vel.sub(vel.mult(new VectorMath2D(damp/2,damp/2)));
        }
        //vel = vel.sub(vel.mult(new VectorMath2D(damp,damp)));
        position = position.add(vel);
       
        CenterA = this.position.add(new VectorMath2D(this.diameter/2,this.diameter/2));
       
        //repaint();
        
    }
   
    public void bounce(){
        if (position.x <= 0){
            position.x = 0;
            vel = vel.reflect(new VectorMath2D(1,0));
        }
        else if (position.x >= ThreadedPhysics.WIDTH - diameter){
            position.x = ThreadedPhysics.WIDTH - diameter;
            vel = vel.reflect(new VectorMath2D(1,0));
        }
        if (position.y <= 0){
            position.y = 0;
            vel = vel.reflect(new VectorMath2D(0,1));
        }
        else if (position.y >= floor - diameter){
            position.y = floor - diameter;
            vel = vel.reflect(new VectorMath2D(0,1));
        }
    }
    
    public void animateSpokes(){ // animates spokes on wheel
       
     spokePos+=(vel.x/(Math.PI*diameter))*360;  
    }

    public static Wheel newWheel() throws IOException {
        return new Wheel(50,Math.random()*10,Math.random()*20,Math.random()*(ThreadedPhysics.WIDTH - diameter),Math.random()*(floor-diameter));
    }
    
    public void run() {
        try{
            while(ThreadedPhysics.Run){
                move(ThreadedPhysics.damp);
                bounce();
                animateSpokes();
            MainLock.lock();
                try{
                    for (int j = 0; j < ThreadedPhysics.wheels.size();j++){
                        collisionDetection(ThreadedPhysics.wheels.get(j),j);
                    }
                }
                finally{
                  MainLock.unlock();   
                }               
               repaint();
                Thread.sleep(16); // 60 fps
            }
        }
        catch(InterruptedException e){}
    }
    
    
    
    public double Distance(Wheel other){
        double radius = this.diameter/2 + other.diameter/2;
        
        VectorMath2D CenterA = this.accel.mult(new VectorMath2D(.5,.5)).add(this.vel.add(this.position.add(new VectorMath2D(this.diameter/2,this.diameter/2)))); // center of this circle
        VectorMath2D CenterB = this.accel.mult(new VectorMath2D(.5,.5)).add(other.vel.add(other.position.add(new VectorMath2D(this.diameter/2,this.diameter/2)))); // center of other cirlce
        
        return Math.sqrt((CenterB.x - CenterA.x)*(CenterB.x - CenterA.x) + (CenterB.y - CenterA.y)*(CenterB.y - CenterA.y)) - radius /*- this.vel.magnitude() - other.vel.magnitude()*/;
    }
    
    
    
    public void collisionDetection(Wheel w, int i){
        VectorMath2D normal = null;
        double hold = 0;
            if(Distance(w) <= 0 && w.thisID != this.thisID){        
                CenterA = this.position.add(new VectorMath2D(this.diameter/2,this.diameter/2));
                VectorMath2D CenterB = w.position.add(new VectorMath2D(this.diameter/2,this.diameter/2));
                
                normal = CenterA.sub(CenterB);
                normal = normal.normalize();
               
                this.vel = vel.reflect(normal);
                w.vel = w.vel.reflect(normal);
                hold = this.velRad;
                this.velRad =  w.velRad;
                w.velRad = hold;
      
        }
    }
}
