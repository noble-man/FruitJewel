package com.fruitjewel.entity;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fruitjewel.constants.IConstants;

public class JewelSprite implements ISprite, IConstants {

	// ===========================================================
	// Fields
	// ===========================================================

	int mStyle; //
	int mState; //
	final Sprite mSprite; //
	int CELL_WIDTH;
	int CELL_HEIGHT;
	int PADDINGX;
	int PADDINGY;
	int mIndex;

	// ===========================================================
	// Constructors
	// ===========================================================

	public JewelSprite(int row, int col, TextureRegion mJewelTextureRegion,
			int CELL_WIDTH, int CELL_HEIGHT, int PADDINGX, int PADDINGY,
			VertexBufferObjectManager pVertexBufferObjectManager,
			int state, int style) {
		this.CELL_HEIGHT = CELL_HEIGHT;
		this.CELL_WIDTH = CELL_WIDTH;
		this.PADDINGX = PADDINGX;
		this.PADDINGY = PADDINGY;
		this.mSprite = new JewelCell((col * CELL_WIDTH), (row * CELL_HEIGHT),
				mJewelTextureRegion, CELL_WIDTH, CELL_HEIGHT, PADDINGX,
				PADDINGY,pVertexBufferObjectManager);
		this.mSprite.setBlendFunction(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
		// this.mState = STATE_NORMAL;//
		this.mState = state;//
		this.mStyle = style;
	}
	
	public JewelSprite(int row, int col, TextureRegion mJewelTextureRegion,
			int CELL_WIDTH, int CELL_HEIGHT, int PADDINGX, int PADDINGY,
			VertexBufferObjectManager pVertexBufferObjectManager,int state) {
		this.CELL_HEIGHT = CELL_HEIGHT;
		this.CELL_WIDTH = CELL_WIDTH;
		this.PADDINGX = PADDINGX;
		this.PADDINGY = PADDINGY;
		this.mSprite = new JewelCell((col * CELL_WIDTH), (row * CELL_HEIGHT),
				mJewelTextureRegion, CELL_WIDTH, CELL_HEIGHT, PADDINGX,
				PADDINGY, pVertexBufferObjectManager);
		this.mSprite.setBlendFunction(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mState = state;//
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public int getRow() {
		return (int) (this.mSprite.getY() - PADDINGY) / CELL_HEIGHT;
	}

	@Override
	public int getCol() {
		return (int) (this.mSprite.getX() - PADDINGX) / CELL_WIDTH;
	}

	@Override
	public void setMapPosition(int row, int col) {
		this.mSprite.setPosition(col * CELL_WIDTH + PADDINGX, row * CELL_HEIGHT + PADDINGY);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public int getIndex() {
		return this.mIndex;
	}

	public void setIndex(final int index) {
		this.mIndex = index;
	}

	public void setStyle(final int style) {
		this.mStyle = style;
	}

	public int getStyle() {
		return this.mStyle;
	}

	public Sprite getJewel() {
		return this.mSprite;
	}

	public void setState(int state) {
		this.mState = state;
	}

	public int getState() {
		return this.mState;
	}

	// ===========================================================
	// Methods
	// ===========================================================	
	int step = 0; //

	public void doScale() {
		if (this.mState == STATE_SCALEINT) {
			if (step < 5) {
				step++;
				this.mSprite.setBlendFunction(GL10.GL_SRC_ALPHA,
						GL10.GL_ONE_MINUS_SRC_ALPHA);
				this.mSprite.setColor(1, 1, 1);
				switch (step) {
				case 0:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0.5f);
					break;
				case 1:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0.4f);
					break;
				case 2:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0.3f);
					break;
				case 3:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0.2f);
					break;
				case 4:
					this.mSprite.setScale(0.7f);
					this.mSprite.setAlpha(0);
					break;
				default:
					break;
				}
			} else {
				step = 0;
				this.mState = STATE_DEAD;
			}
		}// end if
	}
}