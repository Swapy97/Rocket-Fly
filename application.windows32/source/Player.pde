class Player {

  public float x;
  public float y;
  public float xSpd = 0;
  public float ySpd = 0;
  public float spdMax = 10;
  public float acl = 0.25;
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
      this.direction -= 0.1;
    } else if (Keys.D) {
      this.direction += 0.1;
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
    if (this.xSpd != 0 && (this.xSpd < 0.5 && this.xSpd > -0.5)) { //Friction X
      this.xSpd = gotoZero(this.xSpd, 0.0025);
    }
    if (this.ySpd != 0 && (this.ySpd < 0.5 && this.ySpd > -0.5)) {
      this.ySpd = gotoZero(this.ySpd, 0.0025);
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