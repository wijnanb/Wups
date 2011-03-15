import processing.opengl.*;

ArrayList pool;
int numCircles = 100;
color[] koalalumpur = { #302100, #7F7E52, #BDAB64, #FFD45D, #D99C29 };
color[] katrien = { #A6A498, #818C69, #594251, #F0F2E9, #BF4E4E };
color[] eiffel65 = { #1C1F24, #2C3D51, #356781, #24AABC, #3CFFEE };
color[] gotohell = { #D94800, #1A2533, #731F3D, #B2152F, #151419 };
color[] nemo = { #0D1F30, #3B6670, #8BADA3, #F0E3C0, #DB6C0F };
color[] destiny = { #17705C, #F2EAD1, #B19977, #F2EAD1, #8EB833 };
color[] artdeco = { #991408, #7F7C68, #BAA279, #FFBD7B, #323836 };
color[][] themes = { koalalumpur, katrien, eiffel65, gotohell , nemo, destiny, artdeco };

color[] colorScheme;
color[] newColorScheme;
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

void setup()
{
  size(1920, 1078, OPENGL);
  frameRate(60);
  //smooth();
  
  currentTheme = (int) Math.round( Math.random() * themes.length );
  newColorScheme = colorScheme = themes[currentTheme];
  imageMode(CENTER);
  
  pool = new ArrayList();
  
  boid = new Boid(new PVector(width/4,height/4),7.0,0.1);
  target = new PVector(width/2,height/2);
  
  head = new Head(50);
  
  offset_x = -step_x;
}

void draw() {
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

void keyPressed() {
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
   color c;
   float a;
   
   Circle(float x, float y, float r) {
     this.x = x;
     this.y = y;
     this.r = r; 
     
     if ( ringCnt == 0 ) {
       ringColor = (color) colorScheme[ (int) Math.floor( Math.random()*(colorScheme.length-1) ) + 1 ];
       ringCnt = 1 + (int) Math.floor( Math.random()*2 );
     }
     else {
       ringCnt--; 
     }
     c = ringColor;
     
     
     t = 0;
     a = 255;
   }
   
   void display(int index) {   
     a = (t > 3.6) ? 255-32*(t-3.6) : 255;
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
     
     t += 0.1;
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
  
  void update(float x, float y) {
    this.x = x;
    this.y = y; 
  }
  
  void display() {
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
