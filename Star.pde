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