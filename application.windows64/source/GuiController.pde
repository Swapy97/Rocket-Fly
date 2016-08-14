GuiMainMenu guiMainMenu;

void GuiSetup() {
  this.guiMainMenu = new GuiMainMenu();
  this.guiMainMenu.show = true;
}

void drawMainMenu(){
  background(0);
  this.guiMainMenu.draw();
}

void GuiDraw(){
}

void GuiMousePressed(){
  this.guiMainMenu.mousePressed();
}