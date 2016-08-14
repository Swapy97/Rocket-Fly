import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Rocket_Fly extends PApplet {

/*
 TODO:
 - Fix OuterBackground draw TOP RIGHT CORNER
 
 - Make a GUI Class
 - Make a Button Class
 - Make a Main Menu Class which extends the GUI class
 - Seperate KeyPresses in own Class
 - Player can split a comet into 2 new comets with half the size each by shooting it 
 (if size is under a certain value new Comets should not spawn)
 - Improve Displaying of Comets (without images because of lags)
 - Improve Comet Collision (delete comet, which is the bigger one, isCollidingComet() should return null if not colliding, otherwise the comet we are colliding with)
 - Shop with Rocket Upgrades
 - Save function (Dumping Variables like bullets or the player variable into json ans then to base64, then save to file.
 - Loading Screen
 - Do not use mass assignment in save function ( dont save methods and constants; only save changing things like x, y, xSpd, ySpd, direction....)
 
 DONE:
 - Rework Collision Functions
 - Debug Screen and Debug Mode seperation (Debug Screen should not affect gameplay)
 - Moving Comets with Comet Collision on other Comets
 - Player can shoot 
 - Make X / Y Positions Float and round the numbers on drawing function like ellipse() or image() if necessary
 - Player does not spawn in Comet
 - Comet does not overlap with other comet on spawn
 - Background with Stars
 - Rocket cant go out of border
 - Comets are spawning completely inside of the border
 */

// CONSTANTS
static final int playFieldWidth = 2000;
static final int playFieldHeight = 2000;
static final boolean debugMode = false;

// VARIABLE DECLARATIONS
boolean gameStarted = false;
boolean debugScreen = false;
int cometAmount = 25;
Background background;
Player player;
ArrayList<Comet> comets = new ArrayList(cometAmount);
ArrayList<Bullet> bullets = new ArrayList();

//Images
Images Images;

//Key Presses
Keys Keys;

public void setup() {
  

  Images = new Images();
  Keys = new Keys();
  background = new Background();

  player = new Player();

  GuiSetup();
  CometsSetup();
}

public void draw() {
  if(!gameStarted){
    drawMainMenu();
    return;
  }
  
  if (player.died) {
    drawDeathScreen();

    if (Keys.SPACE) {
      player.respawn();
    }
    return;
  }

  background.draw();
  CometsUpdate();

  player.update(); 
  player.draw();

  BulletsUpdate();

  background.drawOuterBackground();

  player.drawScore();
  drawDebugScreen();

  GuiDraw();
}

public void keyPressed() {
  Keys.pressed(key);
}

public void keyReleased() {
  Keys.released(key);
}

public void mousePressed() {
  GuiMousePressed();
}
class Background {
  private int starAmount = 1000;  
  private ArrayList<Star> stars = new ArrayList(starAmount);

  Background() {
    for (int i=0; i<this.starAmount; i++) {
      stars.add(new Star());
    }
  }

  public void draw() {
    background(0);
    for (int i=0; i<stars.size(); i++) {
      stars.get(i).update();
      stars.get(i).draw();
    }

    /*int x = -player.x + width/2;
     int y = -player.y + height/2;    
     imageMode(CORNER);
     image(background, x, y, playFieldWidth, playFieldHeight);*/
  }

  private void drawBorder() {
    float x = -player.x + width/2;
    float y = -player.y + height/2;

    stroke(255);
    noFill();
    strokeWeight(2);
    rect(x, y, playFieldWidth, playFieldHeight);
  }
  
  public void drawOuterBackground(){
    float x = -player.x + width/2;
    float y = -player.y + height/2;

    fill(0);
    noStroke();
    rect( x                  - width/2, y                   - height/2,  playFieldWidth + width/2,                    height/2); // TOP
    rect( x + playFieldWidth          , y                   - height/2,                   width/2,  playFieldHeight + height/2); // RIGHT
    rect( x + playFieldWidth + width/2, y + playFieldHeight           , -playFieldWidth - width/2,                    height/2); // BOTTOM
    rect( x                           , y + playFieldHeight + height/2,                 - width/2, -playFieldHeight - height/2 ); // LEFT
    
    this.drawBorder();
  }
}
class Bullet {
  private float x;
  private float y;
  private int radius = 10;
  private float direction;
  private float speed = 10;
  private float xSpd;
  private float ySpd;
  
  public int lifespan = 200;
  public boolean remove = false;

  Bullet() {
    this.direction = player.direction;
    
    this.x = player.x + (player.radius * cos(this.direction - HALF_PI));
    this.y = player.y + (player.radius * sin(this.direction - HALF_PI));

    // Bullet starts with velocity
    this.xSpd = player.xSpd + speed * cos(this.direction-HALF_PI);
    this.ySpd = player.ySpd + speed * sin(this.direction-HALF_PI);
  }

  public void update() {
    this.updatePosition();
    this.checkCollision();
    
    this.lifespan--;
  }

  private void updatePosition() {
    this.x += this.xSpd;
    this.y += this.ySpd;
  }

  public void draw() { 
    float x = this.x - player.x;
    float y = this.y - player.y;

    pushMatrix();

    translate(width/2, height/2);
    fill(255);
    noStroke();
    ellipse(x, y, this.radius, this.radius);

    popMatrix();
  }
  
  private void checkCollision(){
    Comet comet = this.isColliding();
    if(comet != null){
      comet.remove = true;
      this.remove = true;
      
      player.score++;
    }
  }
  
  private Comet isColliding(){
    Comet collider = null;
    
    for(int i=0;i<comets.size();i++){
      Comet comet = comets.get(i);
      
      float dx = this.x - comet.x;
      float dy = this.y - comet.y;
      
      float distance = sqrt( pow(dx, 2) + pow(dy, 2) );
      if(distance < this.radius + comet.radius){
        collider = comet;
      }
    }
    
    return collider;
  }
}
class Comet {
  public int id;
  public int x;
  public int y;
  public boolean remove = false;
  private float xSpd = 2;
  private float ySpd = 2;
  private PVector spawn;
  private int radius;
  private boolean dontReset = true;

  Comet(int radius, int x, int y, float xSpd, float ySpd) {
    this.id = CometID;
    CometID++;
    this.radius = radius;

    this.xSpd = xSpd * random(0.9f, 1.1f);
    this.ySpd = ySpd * random(0.9f, 1.1f);

    this.spawn = new PVector(x, y);
    this.x = floor(this.spawn.x);
    this.y = floor(this.spawn.y);
  }

  Comet() {
    this.radius = floor(random(30, 60));
    
    this.xSpd = this.xSpd * random(0.9f, 1.1f);
    this.ySpd = this.ySpd * random(0.9f, 1.1f);
    
    this.spawn = this.pickSpawnLocation();
    this.x = floor(this.spawn.x);
    this.y = floor(this.spawn.y);
  }

  public void update() {
    this.reset();
    this.updatePosition();

    this.checkCollision();
  }

  private void reset() {
    if ( this.x > this.radius && this.x < playFieldWidth-this.radius && this.y > this.radius && this.y > playFieldHeight-this.radius ) {
      this.dontReset = false;
    }

    /*if (this.x-this.radius < 0 || this.x+this.radius > playFieldWidth) {
     this.xSpd = -this.xSpd;
     }
     if (this.y-this.radius < 0 || this.y+this.radius > playFieldHeight){
     this.ySpd = -this.ySpd;
     }*/


    if ( (this.x < -this.radius || this.x > playFieldWidth+this.radius || this.y < -this.radius || this.y > playFieldHeight+this.radius) && !this.dontReset ) {
      this.spawn = this.pickSpawnLocation();
      this.x = floor(this.spawn.x);
      this.y = floor(this.spawn.y);
      this.dontReset = true;
    }
  }

  private void updatePosition() {
    this.x += this.xSpd;
    this.y += this.ySpd;
  }

  public void draw() {
    float x = this.x - player.x + width/2;
    float y = this.y - player.y + height/2;
    
    //imageMode(CENTER);
    //image(Images.comet, x, y, this.radius*2, this.radius*2);
    
    //if (debugScreen) {
      noFill();
      stroke(255, 0, 0);
      ellipseMode(CENTER);
      ellipse(x, y, this.radius*2, this.radius*2);
    //}
  }

  private void checkCollision(){
    boolean collideComet = isCollidingComet(this.x, this.y, this.radius);
    if (collideComet) {
      this.remove = true;
    }
  }
  
  private PVector pickSpawnLocation(){
    float x = random(this.radius, playFieldWidth-this.radius);
    float y = random(this.radius, playFieldHeight-this.radius);
    
    while( (x > -this.radius && x < playFieldWidth+this.radius) || (y > -this.radius && y < playFieldHeight+this.radius) ){
      x -= this.xSpd;
      y -= this.ySpd;
    }
    
    while(!isNiceSpawn(x, y, this.radius)){
      x = random(playFieldWidth);
      y = random(playFieldHeight);
      
      while( (x > -this.radius && x < playFieldWidth+this.radius) || (y > -this.radius && y < playFieldHeight+this.radius) ){
        x -= this.xSpd;
        y -= this.ySpd;
      }
    }
    
    PVector spawn = new PVector(x, y);
    return spawn;
  }
}
int CometID = 0;

public void CometsSetup() {
  for (int i=0; i<cometAmount; i++) {
    comets.add(new Comet());
  }
}

public void CometsUpdate() {
  if( frameCount % 250 == 0 ){
    cometAmount += round(cometAmount*0.05f);
  }
  
  while(comets.size() < cometAmount){
    comets.add(new Comet());
  }
  
  for (int i = 0; i < comets.size(); i++) {
    Comet comet = comets.get(i);
    
    if(comet.xSpd == 0 && comet.ySpd == 0){
      comet.remove = true;
    }
    
    comet.update();
    comet.draw();
    
    if (comet.remove) {
      comets.remove(i);
    }
  }
}






public void BulletsUpdate(){
  for (int i = 0; i < bullets.size(); i++) {
    Bullet bullet = bullets.get(i);
    
    bullet.update();
    bullet.draw();
    
    if(bullet.remove || bullet.lifespan == 0){
      bullets.remove(i);
    }
  }
}
class Gui {
  public boolean show = false;
  private ArrayList<GuiText> textList = new ArrayList();
  private ArrayList<GuiButton> buttonList = new ArrayList();

  public void draw() {
    if(!this.show){
      return;
    }
    
    for ( int i=0; i<this.textList.size(); i++) {
      GuiText text = this.textList.get(i);
      text.draw();
    }

    for ( int i=0; i<this.buttonList.size(); i++) {
      GuiButton button = this.buttonList.get(i);
      button.draw();
    }
  }

  public void mousePressed() {
    for ( int i=0; i<this.buttonList.size(); i++) {
      GuiButton button = this.buttonList.get(i);
      if(mouseX > button.x && mouseX < button.x+button.w && mouseY > button.y && mouseY < button.y+button.h){
        this.buttonPressed(button);
      }    }
  }
  
  public void buttonPressed(GuiButton button){}

  public GuiText addText(int id, String text, int x, int y, int textSize) {
    GuiText guiText = new GuiText(id, text, x, y, textSize, CORNER);
    this.textList.add(guiText);
    return guiText;
  }
  public GuiText addCenteredText(int id, String text, int x, int y, int textSize) {
    GuiText guiText = new GuiText(id, text, x, y, textSize, CENTER);
    this.textList.add(guiText);
    return guiText;
  }


  public GuiButton addButton(int id, String text, int x, int y, int w, int h) {
    GuiButton guiButton = new GuiButton(id, text, x, y, w, h, false);
    this.buttonList.add(guiButton);
    return guiButton;
  }
  public GuiButton addCenteredButton(int id, String text, int x, int y, int w, int h) {
    GuiButton guiButton = new GuiButton(id, text, x, y, w, h, true);
    this.buttonList.add(guiButton);
    return guiButton;
  }
}
class GuiButton {
  private int id;
  private String text;
  public int x;
  public int y;
  public int w;
  public int h;
  private boolean isHovered = false;
  
  GuiButton(int id, String text, int x, int y, int w, int h, boolean isCentered) {
    this.id = id;
    this.text = text;
    this.x = isCentered ? x-w/2 : x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  
  public void draw(){
    fill(255);
    stroke(255, 0, 0);
    textSize(16);
    textAlign(CORNER);
    
    rect(this.x, this.y, this.w, this.h);
    
    this.updateHover();
    if(this.isHovered){
      this.drawHover();
    }
    
    
    fill(0);   
    text(this.text, this.x+4, this.y+this.h-5);
  }
  
  private void drawHover(){
    noStroke();
    fill(255, 255, 0, 100);
    rect(this.x, this.y, this.w, this.h);
  }
  
  private void updateHover(){
    this.isHovered = mouseX > this.x && mouseX < this.x+this.w && mouseY > this.y && mouseY < this.y+this.h;
  }
}
GuiMainMenu guiMainMenu;

public void GuiSetup() {
  this.guiMainMenu = new GuiMainMenu();
  this.guiMainMenu.show = true;
}

public void drawMainMenu(){
  background(0);
  this.guiMainMenu.draw();
}

public void GuiDraw(){
}

public void GuiMousePressed(){
  this.guiMainMenu.mousePressed();
}
class GuiMainMenu extends Gui {
  
  private GuiText textTitle;
  
  private GuiButton buttonStart;
  private GuiButton buttonEnd;
  
  GuiMainMenu() {
    this.textTitle = this.addCenteredText(0, "Rocket Fly" , width/2, 100, 64);
    
    this.buttonStart = this.addButton(1, "Play Game" , 200, 250, 200, 25);
    this.buttonEnd = this.addButton(2, "Exit", 200, 300, 200, 25);
  }
  
  public void buttonPressed(GuiButton button){
    if(button.id == this.buttonStart.id){
      gameStarted = true;
      player.god = false;
    }
    
    if(button.id == this.buttonEnd.id){
      exit();
    }
  }
}
class GuiText {
  private int id;
  private String text;
  private int x;
  private int y;
  private int textSize;
  private int textAlign;
  
  GuiText(int id, String text, int x, int y, int textSize, int textAlign) {
    this.id = id;
    this.text = text;
    this.x = x;
    this.y = y;
    this.textSize = textSize;
    this.textAlign = textAlign;
  }
  
  public void draw(){
    fill(255);
    textAlign(this.textAlign);
    textSize(this.textSize);
    text(this.text, this.x, this.y);
  }
}
class Images {
  public PImage rocket_standby;
  public PImage rocket_action;
  public PImage comet;
  
  Images() {
    this.rocket_standby = loadImage("rocket_standby.png");
    this.rocket_action  = loadImage("rocket_action.png");
    this.comet = loadImage("comet.png");    
  }
}
class Keys {
  public boolean W     = false;
  public boolean A     = false;
  public boolean S     = false;
  public boolean D     = false;
  public boolean SPACE = false;
  public boolean H     = false;
  public boolean J     = false;

  public void pressed(char key) {
    switch(key) {
    case 'w':
      this.W = true;
      break;
    case 'a':
      this.A = true;
      break;
    case 's':
      this.S = true;
      break;
    case 'd':
      this.D = true;
      break;
    case ' ':
      this.SPACE = true;
      break;

    case 'h':
      debugScreen = debugScreen ? false : true;
      break;
    case 'j':
      print("You pressed J!");
      break;
    }
  }

  public void released(char key) {
    switch(key) {
    case 'w':
      this.W = false;
      break;
    case 'a':
      this.A = false;
      break;
    case 's':
      this.S = false;
      break;
    case 'd':
      this.D = false;
      break;
    case ' ':
      this.SPACE = false;
      break;
    }
  }
}
class Player {

  public float x;
  public float y;
  public float xSpd = 0;
  public float ySpd = 0;
  public float spdMax = 10;
  public float acl = 0.25f;
  public boolean died = false;
  public int radius = 75;
  public float direction = 0;
  public int score = 0;
  public boolean god = true;

  public PVector spawn;

  // Private Stuff
  private int shootingCooldown = 0;
  private int shootingCooldownMax = 25;

  Player() {
    this.spawn = this.pickSpawnLocation();
    this.x = spawn.x;
    this.y = spawn.y;
  }

  public void update() {
    if(debugMode){
      this.god = true;
    }
    
    this.updateDirection();
    this.updateSpeed();
    this.updatePosition();

    this.collideComet();

    this.shoot();
  }

  private void updateDirection() {
    if (Keys.A) {
      this.direction -= 0.1f;
    } else if (Keys.D) {
      this.direction += 0.1f;
    }
  }

  private void updateSpeed() {
    float speed = 0;
    if (Keys.W) {
      speed = this.acl;
    }

    this.xSpd += speed * cos(this.direction-HALF_PI);
    this.ySpd += speed * sin(this.direction-HALF_PI);


    // Friction applies between 0.5 and -0.5 speed
    if (this.xSpd != 0 && (this.xSpd < 0.5f && this.xSpd > -0.5f)) { //Friction X
      this.xSpd = gotoZero(this.xSpd, 0.0025f);
    }
    if (this.ySpd != 0 && (this.ySpd < 0.5f && this.ySpd > -0.5f)) {
      this.ySpd = gotoZero(this.ySpd, 0.0025f);
    }
  }

  private void updatePosition() {
    boolean updateX = true;
    boolean updateY = true;

    //check border collision X-axis
    if ( !(this.xSpd > 0 || this.x - this.radius > 0) ) {
      updateX = false;
      this.x = 0 + this.radius;
    } else if ( !(this.xSpd < 0 || this.x + this.radius < playFieldWidth) ) {
      updateX = false;
      this.x = playFieldWidth - this.radius;
    }

    //check border collision Y-axis
    if ( !(this.ySpd > 0 || this.y - this.radius > 0) ) {
      updateY = false;
      this.y = 0 + this.radius;
    } else if ( !(this.ySpd < 0 || this.y + this.radius < playFieldHeight) ) {
      updateY = false;
      this.y = playFieldHeight - this.radius;
    }

    if (updateX) {
      this.x += this.xSpd;
    } else {
      this.xSpd = 0;
    }


    if (updateY) {
      this.y += this.ySpd;
    } else {
      this.ySpd = 0;
    }
  }

  public void draw() {
    PImage rocket;
    if (Keys.W) {
      rocket = Images.rocket_action;
    } else {
      rocket = Images.rocket_standby;
    }
    pushMatrix();
    translate(width/2, height/2);
    rotate(this.direction);
    imageMode(CENTER);
    if(this.god){
      tint(255, 255, 255, 50);
    }
    else{
      tint(255, 255, 255);
    }
    image(rocket, 0, 0, this.radius*2, this.radius*2);
    popMatrix();

    stroke(255, 0, 0);
    strokeWeight(4);
    noFill();

    if (debugScreen) {
      ellipseMode(CENTER);
      ellipse(width/2, height/2, this.radius*2, this.radius*2);
    }
  }

  public void respawn() {
    if (this.died) {
      this.xSpd = 0;
      this.ySpd = 0;
      this.spawn = this.pickSpawnLocation();
      this.x = spawn.x;
      this.y = spawn.y;
      this.score = 0;
      this.died = false;
    }
  }

  private PVector pickSpawnLocation() {
    float x = random(this.radius, playFieldWidth - this.radius);
    float y = random(this.radius, playFieldHeight - this.radius);

    while (!isNiceSpawn(x, y, this.radius)) {
      x = random(this.radius, playFieldWidth - this.radius);
      y = random(this.radius, playFieldHeight - this.radius);
    }

    PVector spawn = new PVector(x, y);
    return spawn;
  }

  private void shoot() {
    this.shootingCooldown -= 1;
    this.shootingCooldown  = constrain(this.shootingCooldown, 0, this.shootingCooldownMax);

    if (Keys.SPACE && this.shootingCooldown == 0) {
      this.shootingCooldown = this.shootingCooldownMax;
      bullets.add(new Bullet());
    }
  }
  
  private void collideComet(){
    boolean collision = isCollidingComet(this.x, this.y, this.radius);
    if (collision) {
      if (debugScreen) {
        stroke(255, 0, 0);
        text("Colliding with Comet", 5, 75);
      }
      if(!this.god){
        this.died = true;
      }
    }
  }
  
  public void drawScore(){
    fill(255);
    textSize(24);
    textAlign(RIGHT);
    text(this.score, width, 20);
  }
}
class Star {
  public int x;
  public int y;
  public int radius = 3;

  Star(){
    this.x = floor(random(playFieldWidth));
    this.y = floor(random(playFieldHeight));
  }

  public void update() {
  }

  public void draw() {
    float x = this.x - player.x + width/2;
    float y = this.y - player.y + height/2;
    
    noStroke();
    fill(255);
    ellipse(x, y, this.radius, this.radius);
  }
}
public float gotoZero(float value, float stepsize) {
  if (value > 0 && value > stepsize) {
    value -= stepsize;
  } else if (value < 0 && value < stepsize) {
    value += stepsize;
  } else {
    value = 0;
  }

  return value;
}

public void drawLoadingScreen() {
  background(0);
  /*
  print("loading screen. . .");
   PImage loadingImage = loadImage("rocket_action.png");
   
   pushMatrix();
   translate(width/2, height/2);
   rotate(HALF_PI/2);
   imageMode(CENTER);
   image(loadingImage, 0, 0);
   popMatrix();
   
   textAlign(CENTER);
   textSize(24);
   text("L\u00e4dt. . .", width/2, height/2+250);
   */
}

public void drawDeathScreen() {
  background(51);
  textAlign(CENTER);
  textSize(32);
  text("Game Over!", width/2, height/2 - 20);
  text("Leertaste zum Neustarten!", width/2, height/2 + 20);
}

public void drawDebugScreen() {
  if (debugScreen) {
    fill(255);
    stroke(255);
    textAlign(CORNER);
    textSize(12);
    text("X: " + round(player.x), 5, 15);
    text("Y: " + round(player.y), 5, 25);

    text("Speed-X: " + player.xSpd, 75, 15);
    text("Speed-Y: " + player.ySpd, 75, 25);

    text("Shooting Cooldown: " + player.shootingCooldown +" / "+ player.shootingCooldownMax, 5, 45);
    
    text( round(frameRate) + " FPS", 5, 65 );
    
    if(isCollidingComet(player.x, player.y, player.radius)){
      text("Colliding with Comet", 5, 75);
    }
  }
}
public boolean isNiceSpawn(float x, float y, int radius) {
  boolean collision;

  // COMETS
  collision = isCollidingComet(x, y, radius);
  if (collision) {
    return false;
  }

  // PLAYER
  collision = isCollidingPlayer(x, y, radius);
  if (collision) {
    return false;
  }

  // BULLETS
  collision = isCollidingBullet(x, y, radius);
  if (collision) {
    return false;
  }
  
  return true;
}


public boolean testCollision(float x1, float y1, int radius1, float x2, float y2, int radius2) {
  return testCollision(x1, y1, radius1, round(x2), round(y2), radius2);
}
public boolean testCollision(float x1, float y1, int radius1, int x2, int y2, int radius2) {
  float dx = x1 - x2;
  float dy = y1 - y2;

  float distance = sqrt( pow(dx, 2) + pow(dy, 2) );

  if (dx == 0 && dy == 0) { // make sure the object does not collide with itself
    return false;
  }

  return distance < radius1+radius2;
}

public boolean isCollidingComet(float x, float y, int radius) {
  return isCollidingComet(round(x), round(y), radius);
}
public boolean isCollidingComet(int x, int y, int radius) {
  for (int i=0; i<comets.size(); i++) {
    Comet comet = comets.get(i);

    boolean collision = testCollision(x, y, radius, comet.x, comet.y, comet.radius);
    if (collision) {
      return true;
    }
  }
  return false;
}

public boolean isCollidingPlayer(float x, float y, int radius) {
  if (player != null) {
    boolean collision = testCollision(x, y, radius, player.x, player.y, player.radius);
    return collision;
  }
  return false;
}


public boolean isCollidingBullet(float x, float y, int radius) {
  for (int i=0; i<bullets.size(); i++) {
    Bullet bullet = bullets.get(i);

    boolean collision = testCollision(x, y, radius, bullet.x, bullet.y, bullet.radius);
    if (collision) {
      return true;
    }
  }
  return false;
}
  public void settings() {  size(displayWidth, displayHeight); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--hide-stop", "Rocket_Fly" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
