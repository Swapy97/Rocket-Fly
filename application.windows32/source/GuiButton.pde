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