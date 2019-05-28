package com.fruitjewel.entity;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class BackgroundCell extends ICell {

  // ===========================================================
  // Constructors
  // ===========================================================

  public BackgroundCell(final int pCellX, final int pCellY, final ITextureRegion pTextureRegion,
		  int CELLBG_WIDTH, int CELLBG_HEIGHT, VertexBufferObjectManager pVertexBufferObjectManager) {
    super(pCellX * CELLBG_WIDTH, pCellY * CELLBG_HEIGHT, CELLBG_WIDTH, CELLBG_HEIGHT, pTextureRegion, pVertexBufferObjectManager);
  }  
}