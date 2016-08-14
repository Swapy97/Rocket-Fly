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