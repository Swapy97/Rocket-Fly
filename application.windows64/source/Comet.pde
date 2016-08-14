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

    this.xSpd = xSpd * random(0.9, 1.1);
    this.ySpd = ySpd * random(0.9, 1.1);

    this.spawn = new PVector(x, y);
    this.x = floor(this.spawn.x);
    this.y = floor(this.spawn.y);
  }

  Comet() {
    this.radius = floor(random(30, 60));
    
    this.xSpd = this.xSpd * random(0.9, 1.1);
    this.ySpd = this.ySpd * random(0.9, 1.1);
    
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