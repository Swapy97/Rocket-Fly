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