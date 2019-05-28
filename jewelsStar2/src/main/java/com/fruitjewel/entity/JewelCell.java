package com.fruitjewel.entity;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class JewelCell extends ICell {

  // ===========================================================
  // Constructors
  // ===========================================================

  public JewelCell(final int pCellX, final int pCellY, final ITextureRegion pTextureRegion,
		  int CELL_WIDTH, int CELL_HEIGHT, int PADDINGX, int PADDINGY, 
		  VertexBufferObjectManager pVertexBufferObjectManager) {		
	super(pCellX+PADDINGX, pCellY+PADDINGY, CELL_WIDTH, CELL_HEIGHT, pTextureRegion, pVertexBufferObjectManager);	
  }    
}