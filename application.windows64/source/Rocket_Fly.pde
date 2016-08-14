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

void setup() {
  size(displayWidth, displayHeight);

  Images = new Images();
  Keys = new Keys();
  background = new Background();

  player = new Player();

  GuiSetup();
  CometsSetup();
}

void draw() {
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

void keyPressed() {
  Keys.pressed(key);
}

void keyReleased() {
  Keys.released(key);
}

void mousePressed() {
  GuiMousePressed();
}