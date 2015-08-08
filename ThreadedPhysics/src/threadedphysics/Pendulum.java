/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threadedphysics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import static threadedphysics.Wheel.image;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author Alex
 */
public class Pendulum extends Wheel  {
    
    private double LRope = 300; // length of rope
   // ropePos = new VectorMath2D((double)(ThreadedPhysics.WIDTH/2),(double)(ThreadedPhysics.HEIGHT/2 - 200)); //rope anchor position
    public double radian = 0; // radian of wheel
    protected double velRad = 0; // radian velocity of wheel
    private double circumferance = 2 * Math.PI * LRope;
    
    public Pendulum(VectorMath2D pos, double velocity, double rlength) throws IOException {
        super(50,0,0,0,0);
        //ropePos = new VectorMath2D((double)(ThreadedPhysics.WIDTH/2),(double)(ThreadedPhysics.HEIGHT/2 - 200));
        this.ropePos = pos;
       // ropePos = new VectorMath2D((double)(ThreadedPhysics.WIDTH/2),(double)(ThreadedPhysics.HEIGHT/2 - 200));
        this.velRad = velocity;
        LRope = rlength;
        position.x = ropePos.x + LRope*Math.cos(radian) + Math.sin(radian)*(diameter/2); // starting x position of wheel
        position.y = ropePos.y + LRope*Math.sin(radian) - Math.cos(radian)*(diameter/2)*diameter/2 ; // starting y position of wheel
        picFile = "sacklerphysicscropped.jpg";
        image = ImageIO.read(new File(picFile));
        circumferance = 2 * Math.PI * LRope;
        accel.y = ((accel.y)/circumferance )* 2 * Math.PI;// pendulums of different length rotate faster
    }
   
    
   
   
  @Override  public void paint(Graphics c){
     //   picFile = "sacklerphysicscropped.jpeg";
        super.paint(c);
        Graphics2D g = (Graphics2D)c;
        CenterA = this.position.add(new VectorMath2D(this.diameter/2,this.diameter/2));
        MainLock.lock();
        try{
           
        for (int i = 0; i < ThreadedPhysics.wheels.size(); i++) {
            
           /* g.drawLine((int)(ThreadedPhysics.wheels.get(i).ropePos.x), (int)(ThreadedPhysics.wheels.get(i).ropePos.y), 
                    (int)(ThreadedPhysics.wheels.get(i).CenterA.x), (int)(ThreadedPhysics.wheels.get(i).CenterA.y));*/ // points rope in direction of circle  
            g.drawLine((int)(ThreadedPhysics.wheels.get(i).ropePos.x), (int)(ThreadedPhysics.wheels.get(i).ropePos.y), 
                    (int)(ThreadedPhysics.wheels.get(i).ropeEnd.x), (int)(ThreadedPhysics.wheels.get(i).ropeEnd.y));
        }
       /* g.drawLine((int)(ropePos.x), (int)(ropePos.y), 
                    (int)(CenterA.x), (int)(CenterA.y));*/
        }
        finally{
           MainLock.unlock();
       }
    }
    
    public double calcRadian(){
      return Math.acos((position.x - ropePos.x)/LRope);
    }
    
    public double calcRadian(VectorMath2D c){  // calc radian using desired position
        return Math.acos((c.x - ropePos.x)/LRope);
    }
    
    
    @Override
    public void move(double damp){
        CenterA = this.position.add(new VectorMath2D(this.diameter / 2,this.diameter / 2)); //  center of circle
        ropeEnd = new VectorMath2D(ropePos.x + LRope*Math.cos(radian), ropePos.y + LRope*Math.sin(radian));//end of rope position
        position = ropeEnd.sub(new VectorMath2D(diameter/2 - (diameter/2)*Math.cos(radian),
                diameter/2 - (diameter/2)*Math.sin(radian))); // position of wheel
        velRad += (accel.y ) * Math.cos(radian); // differential of radian velocity
        velRad -= velRad * (damp/4); // damping of velocity
        super.vel = new VectorMath2D(velRad / (2 * Math.PI) * circumferance * Math.sin(radian),
                velRad / (2 * Math.PI) * circumferance * Math.cos(radian)); // controls rotation of spindals from super class
        radian += velRad; // differntial increment of radian position
    }
    
    public static void SavePendulums(String filename) throws IOException{
        try
        {
            FileWriter writer = new FileWriter(filename,true);
            PrintWriter print = new PrintWriter(writer);
            for(Wheel w: ThreadedPhysics.wheels){
                print.printf( "%f," + " %f" + "%n" , w.ropePos.x, w.ropePos.y);
                print.printf( "%f," + " %f" + "%n" , w.vel.x, w.vel.y); // used to find velRad
                print.printf( "%f," + " %f" + "%n" , w.position.x, w.position.y); // use ball position to figure out distance to ropePos, getting rope length
                
            }
            
            print.close();
        }
        catch (IOException e)
        {
            System.out.println( "Error saving "  + filename );
            System.exit( 0 );
        }
    }
    
    public static void LoadPendulums(){
        // would load save file
    }
    
    public void setVelocity(double v){
        velRad = v;
    }
    
    public void setRopeLength(double l){
        LRope = l;
    }
    
    public void setRadian(double r){
        radian = r;
    }
    
    public void autospace(){
        //auto spaces pendulums
    }
    
    
    
//    public double Distance(Pendulum other){
//        double radius = this.diameter/2 + other.diameter/2;
//        VectorMath2D CenterA = this.position.add(new VectorMath2D(this.diameter/2,this.diameter/2));
//        VectorMath2D CenterB = other.position.add(new VectorMath2D(this.diameter/2,this.diameter/2));
//        return Math.sqrt((CenterB.x - CenterA.x)*(CenterB.x - CenterA.x) + (CenterB.y - CenterA.y)*(CenterB.y - CenterA.y)) - radius /*- this.vel.magnitude() - other.vel.magnitude()*/;
//    }
    /*
    public void collisionDetection(Wheel w, int i){
        double hold;
        w = (Pendulum)w;
        System.out.println("new col");
         if(Distance(w) <= 0 && w.thisID != this.thisID){
             hold = this.VelRad;
             this.VelRad = w.VelRad;
             w.VelRad = hold;
         }
    }*/

   
    
}
