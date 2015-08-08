/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threadedphysics;

/**
 *
 * @author Alex
 */
public class VectorMath2D {
    
    double x,y = 0;
    public VectorMath2D(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public VectorMath2D add(VectorMath2D other){
        
        return new VectorMath2D((this.x + other.x), (this.y + other.y));
        
    }
    
    public VectorMath2D sub(VectorMath2D other){
        return new VectorMath2D((this.x - other.x),(this.y - other.y));
    }
    
    public VectorMath2D mult(VectorMath2D other){
        return new VectorMath2D((this.x*other.x),(this.y*other.y));
    }
    
    public VectorMath2D div(VectorMath2D other){
        
        return new VectorMath2D((this.x/other.x),(this.y/other.y));
        
    }
    
    public boolean GreaterOrLess(VectorMath2D other){
            //returns true for greater and less otherwise
        if (this.x > other.y && this.y > other.y){
            return true;
        }
        return false;
    }
    
    public double magnitude(){
        return Math.sqrt(this.x*this.x + this.y*this.y);
    }
    
    public VectorMath2D normalize(){
        double m = magnitude();
        if(m>0){
            this.x /= m;
            this.y /= m;
        }
        return new VectorMath2D(this.x,this.y);
    }
    
    public VectorMath2D get_normal(VectorMath2D other){
        return this.sub(other).normalize();
    }
    
    public String toString(){
        return Double.toString(x)+','+Double.toString(y);
    }/*
    public VectorMath2D cross(VectorMath2D other){
        return new VectorMath2D(this.x*other)
    }*/
    public VectorMath2D reflect(VectorMath2D normal){
        double d = 2*(this.x * normal.x + this.y * normal.y);
        return new VectorMath2D((this.x - d * normal.x), (this.y - d * normal.y));
        
        
    }
    
}
