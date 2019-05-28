package com.fruitjewel.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fruitjewel.constants.IConstants;


/**
 * Initialize the Cell X/Y position and the Width, Height
 */
public abstract class ICell extends Sprite implements IConstants {

	public ICell(final int pCellX, final int pCellY, final int pWidth, final int pHeight, 
			final ITextureRegion pTextureRegion, final VertexBufferObjectManager  pVertexBufferObjectManager) {
	    super(pCellX, pCellY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);	    
	  }
}