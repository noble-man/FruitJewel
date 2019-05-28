package com.fruitjewel.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fruitjewel.constants.IConstants;

public class BorderSprite implements ISprite, IConstants {

	// ===========================================================
	// Fields
	// ===========================================================

	final Sprite mSprite;//
	int CELL_WIDTH;
	int CELL_HEIGHT;
	int PADDINGX;
	int PADDINGY;
	// ===========================================================
	// Constructors
	// ===========================================================

	public BorderSprite(int row, int col, ITextureRegion mTextureRegion,
			int CELL_WIDTH, int CELL_HEIGHT, int PADDINGX, int PADDINGY, 
			VertexBufferObjectManager pVertexBufferObjectManager) {
		this.CELL_HEIGHT = CELL_HEIGHT;
		this.CELL_WIDTH = CELL_WIDTH;
		this.PADDINGX = PADDINGX;
		this.PADDINGY = PADDINGY;
		this.mSprite = new JewelCell((col*CELL_WIDTH), (row*CELL_HEIGHT), mTextureRegion, 
				CELL_WIDTH,CELL_HEIGHT,PADDINGX,PADDINGY,pVertexBufferObjectManager);
	}

	 // ===========================================================
	  // Methods for/from SuperClass/Interfaces
	  // ===========================================================  
	  
	  @Override
	  public int getRow() {
		  return (int)(this.mSprite.getY()-PADDINGY)/CELL_HEIGHT;
	  }  
	  
	  @Override
	  public int getCol() {	    
	    return (int)(this.mSprite.getX()-PADDINGX)/CELL_WIDTH;
	  }
	  
	  @Override
	  public void setMapPosition(int row, int col){
	    this.mSprite.setPosition(col * CELL_WIDTH+PADDINGX, row * CELL_HEIGHT+PADDINGY);
	  }
	  
	  // ===========================================================
	  // Getter & Setter
	  // ===========================================================
	  
	  public Sprite getSprite(){
	    return this.mSprite;
	  }
	  
	  // ===========================================================
	  // Methods
	  // ===========================================================
	}