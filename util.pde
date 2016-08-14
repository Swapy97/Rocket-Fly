float gotoZero(float value, float stepsize) {
  if (value > 0 && value > stepsize) {
    value -= stepsize;
  } else if (value < 0 && value < stepsize) {
    value += stepsize;
  } else {
    value = 0;
  }

  return value;
}

void drawLoadingScreen() {
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
   text("Lädt. . .", width/2, height/2+250);
   */
}

void drawDeathScreen() {
  background(51);
  textAlign(CENTER);
  textSize(32);
  text("Game Over!", width/2, height/2 - 20);
  text("Leertaste zum Neustarten!", width/2, height/2 + 20);
}

void drawDebugScreen() {
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