import processing.core.*; 
import processing.xml.*; 

import processing.opengl.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class wups extends PApplet {



ArrayList pool;
int numCircles = 100;
int[] koalalumpur = { 0xff302100, 0xff7F7E52, 0xffBDAB64, 0xffFFD45D, 0xffD99C29 };
int[] katrien = { 0xffA6A498, 0xff818C69, 0xff594251, 0xffF0F2E9, 0xffBF4E4E };
int[] eiffel65 = { 0xff1C1F24, 0xff2C3D51, 0xff356781, 0xff24AABC, 0xff3CFFEE };
int[] gotohell = { 0xffD94800, 0xff1A2533, 0xff731F3D, 0xffB2152F, 0xff151419 };
int[] nemo = { 0xff0D1F30, 0xff3B6670, 0xff8BADA3, 0xffF0E3C0, 0xffDB6C0F };
int[] destiny = { 0xff17705C, 0xffF2EAD1, 0xffB19977, 0xffF2EAD1, 0xff8EB833 };
int[] artdeco = { 0xff991408, 0xff7F7C68, 0xffBAA279, 0xffFFBD7B, 0xff323836 };
int[][] themes = { koalalumpur, katrien, eiffel65, gotohell , nemo, destiny, artdeco };

int[] colorScheme;
int[] newColorScheme;
int currentTheme = 0;

PImage b;
Boid boid;
PVector target;
Head head;
Boolean debug = false;
int ringCnt = 0;
int ringColor;
int _a = 200;
int step_x = 40;
int step_y = 40;
float offset_x;
int theme_cnt = 0;

public void setup()
{
  size(1920, 1078, OPENGL);
  frameRate(60);
  //smooth();
  
  currentTheme = (int) Math.round( Math.random() * themes.length );
  newColorScheme = colorScheme = themes[currentTheme];
  imageMode(CENTER);
  
  pool = new ArrayList();
  
  boid = new Boid(new PVector(width/4,height/4),7.0f,0.1f);
  target = new PVector(width/2,height/2);
  
  head = new Head(50);
  
  offset_x = -step_x;
}

public void draw() {
  int cnt = 0;
  offset_x = (offset_x + (float) boid.vel.x) % ((colorScheme.length-1)*step_x) ;
  
  float x=0, y=0;
  int bound_x = (int)width/step_x + (colorScheme.length-1);
  int bound_y = (int)height/step_y;
  
  for ( int i=-4 ; i<=bound_x; i++ ) {
    for ( int j=0; j<=bound_y; j++ ) {
      
      x = - offset_x + (step_x * i);
      y = step_y * j;
      
      noStroke();
      if ( 2*i+j < theme_cnt )  fill(newColorScheme[cnt %(newColorScheme.length-1)]);
      else                  fill(colorScheme[cnt %(colorScheme.length-1)]);
      rect(x,y,step_x,step_y);
      cnt++;
    }
  }
  
  if ( theme_cnt > 0 )    theme_cnt++;
  if ( theme_cnt >= 2*bound_x+bound_y ){
    theme_cnt = 0;
    colorScheme = newColorScheme;
  }
 
  for ( int i=0; i<pool.size(); i++ ) {
    ((Circle) pool.get(i)).display(i);
  }
   
  //boid.wander();
  boid.run();
  boid.arrive(target);
   
  Circle c = new Circle(boid.loc.x, boid.loc.y, 20);
  pool.add(c);
   
  head.update(boid.loc.x, boid.loc.y);
  head.display();
   
  if (mousePressed) {
    target = new PVector(mouseX,mouseY);
  }
}

public void keyPressed() {
  if (keyCode == ENTER) {
    currentTheme = (currentTheme + 1) % themes.length;
    newColorScheme = themes[currentTheme];
    theme_cnt = 1;
  }
  else if (key == CODED) {
    if (keyCode == RIGHT) {
    currentTheme = (currentTheme + 1) % themes.length;
    newColorScheme = themes[currentTheme];
    theme_cnt = 1;
    } 
    else if (keyCode == LEFT) {
      currentTheme = ((currentTheme - 1) + themes.length) % themes.length;
      newColorScheme = themes[currentTheme];
      theme_cnt = 1;
    } 
  }
}






class Circle {
   float r;
   float x;
   float y;
   float t;
   int c;
   float a;
   
   Circle(float x, float y, float r) {
     this.x = x;
     this.y = y;
     this.r = r; 
     
     if ( ringCnt == 0 ) {
       ringColor = (int) colorScheme[ (int) Math.floor( Math.random()*(colorScheme.length-1) ) + 1 ];
       ringCnt = 1 + (int) Math.floor( Math.random()*2 );
     }
     else {
       ringCnt--; 
     }
     c = ringColor;
     
     
     t = 0;
     a = 255;
   }
   
   public void display(int index) {   
     a = (t > 3.6f) ? 255-32*(t-3.6f) : 255;
     float r_declining = r * a/255;
     double delta = 20 + r * Math.sin(t);
     
     stroke(colorScheme[0],50);
     fill(c, _a);
     
     if ( index > (pool.size()-6) ) fill(colorScheme[4], 100);
     if ( index == (pool.size()-2) ) fill(colorScheme[3], 100);
     
     ellipse(x, y, (float)(r_declining+delta), (float)(r_declining+delta));
     
     if ( r_declining <= 0 ) {
       pool.remove(index); 
     }
     
     t += 0.1f;
   }
}




class Head {
  float x;
  float y;
  float r;
  float theta;
  
  Head(float r) {
    this.r = r; 
  }
  
  public void update(float x, float y) {
    this.x = x;
    this.y = y; 
  }
  
  public void display() {
    // head
    stroke(colorScheme[0],50);
    fill(colorScheme[4], 255);
    ellipse(x, y, r, r);

    // angles
    double angle = Math.atan2(boid.vel.x, boid.vel.y);
    double a = angle + Math.PI/3;
    double a2 = angle - Math.PI/3;
    
    if ( debug ) {
      // target
      stroke(255);
      strokeWeight(1);
      line(x, y, target.x, target.y);
      
      // direction
      stroke(255);
      strokeWeight(4);
      line(x, y, x + 30*boid.vel.x, y + 30*boid.vel.y);
      
      /*
      // eyes
      strokeWeight(5);
      stroke(255,0,0);
      line(x, y - 12, x + 12 * (float) Math.sin(a), y - 12 + 12 * (float) Math.cos(a));
      
      stroke(0,255,0);
      line(x, y - 12, x + 12 * (float) Math.sin(a2), y - 12 + 12 * (float) Math.cos(a2));
      */
    }
    
    
    // eyes
    float r2 = 12;
    float eye1_x = x + r2 * (float) Math.sin(a);
    float eye1_y = y - r2 + r2 * (float) Math.cos(a);
    float eye2_x = x + r2 * (float) Math.sin(a2);
    float eye2_y = y - r2 + r2 * (float) Math.cos(a2);
    float dx =  4 * (float) Math.sin(angle);
    float dy =  4 * (float) Math.cos(angle);

    // wit    
    noStroke();
    fill(255, 255);
    ellipse(eye1_x, eye1_y, r2, r2);
    ellipse(eye2_x, eye2_y, r2, r2);
    
    // pupil
    fill(0, 255);
    ellipse(eye1_x + dx, eye1_y + dy, 5, 5);
    ellipse(eye2_x + dx, eye2_y + dy, 5, 5);
  }
}
// Wander
// Daniel Shiffman <http://www.shiffman.net>
// The Nature of Code

// The "Boid" class (for wandering)

class Boid {

  PVector loc;
  PVector vel;
  PVector acc;
  float r;
  float wandertheta;
  float maxforce;    // Maximum steering force
  float maxspeed;    // Maximum speed

  Boid(PVector l, float ms, float mf) {
    acc = new PVector(0,0);
    vel = new PVector(0,0);
    loc = l.get();
    r = 3.0f;
    wandertheta = 0.0f;
    maxspeed = ms;
    maxforce = mf;
  }
  
  public void run() {
    update();
    //borders();
    //render();
  }
  
  // Method to update location
  public void update() {
    // Update velocity
    vel.add(acc);
    // Limit speed
    vel.limit(maxspeed);
    loc.add(vel);
    // Reset accelertion to 0 each cycle
    acc.mult(0);
  }

  public void seek(PVector target) {
    acc.add(steer(target,false));
  }
 
  public void arrive(PVector target) {
    acc.add(steer(target,true));
  }
  
  public void wander() {
    float wanderR = 16.0f;         // Radius for our "wander circle"
    float wanderD = 60.0f;         // Distance for our "wander circle"
    float change = 0.25f;
    wandertheta += random(-change,change);     // Randomly change wander theta

    // Now we have to calculate the new location to steer towards on the wander circle
    PVector circleloc = vel.get();  // Start with velocity
    circleloc.normalize();            // Normalize to get heading
    circleloc.mult(wanderD);          // Multiply by distance
    circleloc.add(loc);               // Make it relative to boid's location
    
    PVector circleOffSet = new PVector(wanderR*cos(wandertheta),wanderR*sin(wandertheta));
    PVector target = PVector.add(circleloc,circleOffSet);
    acc.add(steer(target,false));  // Steer towards it
    
    // Render wandering circle, etc. 
    //if (debug) drawWanderStuff(loc,circleloc,target,wanderR);
    
  }  
  
  // A method that calculates a steering vector towards a target
  // Takes a second argument, if true, it slows down as it approaches the target
  public PVector steer(PVector target, boolean slowdown) {
    PVector steer;  // The steering vector
    PVector desired = PVector.sub(target,loc);  // A vector pointing from the location to the target
    float d = desired.mag(); // Distance from the target is the magnitude of the vector
    // If the distance is greater than 0, calc steering (otherwise return zero vector)
    if (d > 0) {
      // Normalize desired
      desired.normalize();
      // Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
      if ((slowdown) && (d < 100.0f)) desired.mult(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
      else desired.mult(maxspeed);
      // Steering = Desired minus Velocity
      steer = PVector.sub(desired,vel);
      steer.limit(maxforce);  // Limit to maximum steering force
    } else {
      steer = new PVector(0,0);
    }
    return steer;
  }
  
  public void render() {
    // Draw a triangle rotated in the direction of velocity
    float theta = vel.heading2D() + radians(90);
    fill(175);
    stroke(0);
    pushMatrix();
    translate(loc.x,loc.y);
    rotate(theta);
    beginShape(TRIANGLES);
    vertex(0, -r*2);
    vertex(-r, r*2);
    vertex(r, r*2);
    endShape();
    popMatrix();
  }
  
  // Wraparound
  public void borders() {
    if (loc.x < -r) loc.x = width+r;
    if (loc.y < -r) loc.y = height+r;
    if (loc.x > width+r) loc.x = -r;
    if (loc.y > height+r) loc.y = -r;
  }

}


// A method just to draw the circle associated with wandering
public void drawWanderStuff(PVector loc, PVector circle, PVector target, float rad) {
  stroke(0); 
  noFill();
  ellipseMode(CENTER);
  ellipse(circle.x,circle.y,rad*2,rad*2);
  ellipse(target.x,target.y,4,4);
  line(loc.x,loc.y,circle.x,circle.y);
  line(circle.x,circle.y,target.x,target.y);
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--stop-color=#cccccc", "wups" });
  }
}
