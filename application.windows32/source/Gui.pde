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