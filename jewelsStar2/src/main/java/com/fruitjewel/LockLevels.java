package com.fruitjewel;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class LockLevels extends BaseGameActivity implements
		IOnAreaTouchListener {

	// ===========================================================
	// Constants
	// ===========================================================

	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 1280;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mMainScene; // Main Scene

	// Background Texture
	private BitmapTextureAtlas mBackGroundTexture;
	protected TextureRegion mBackGroundTextureRegion;

	// Menu button Texture
	private BitmapTextureAtlas mMenuLevelTexture, mMenuLockTexture;
	protected TextureRegion mMenuLevelTextureRegion, mMenuLockTextureRegion;

	// Define the menu Items
	private Sprite[][] mLevels;
	private Sprite[][] mLocks;
	private Text[][] mLevelsText;

	// Define the font
	private ITexture mFontTexture;
	private Font mFont;

	/** App Preferences **/
	AppPreferences _appPrefs;

	/****/
	public static int CELL_WIDTH = 45;
	public static int CELL_HEIGHT = CELL_WIDTH;
	public static int CELLS_HORIZONTAL = 3;
	public static int CELLS_VERTICAL = 4;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		_appPrefs = new AppPreferences(getApplicationContext());
		initConstants();

		mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0, 0, 0);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.mCamera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engineOptions.getRenderOptions().setDithering(true);
		
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		// Background Image resoures
		this.mBackGroundTexture = new BitmapTextureAtlas(getTextureManager(),
				720, 1280, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBackGroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBackGroundTexture, this,
						"gfx/bg_nextlevel.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mBackGroundTexture);
		// New Game button image resource

		this.mMenuLevelTexture = new BitmapTextureAtlas(getTextureManager(),
				161, 153, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuLevelTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mMenuLevelTexture, this,
						"gfx/brick_bg.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuLevelTexture);

		// Continue Game button image resource
		this.mMenuLockTexture = new BitmapTextureAtlas(getTextureManager(),
				159, 151, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuLockTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mMenuLockTexture, this, "gfx/lock.png",
						0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuLockTexture);

		// Load font texture
		this.mFontTexture = new BitmapTextureAtlas(getTextureManager(), 256,
				256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = FontFactory.createFromAsset(this.getFontManager(),
				this.mFontTexture, this.getAssets(), "fonts/SuperMario256.ttf",
				38, true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);

		// resource
		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		// Create the main screne
		this.mMainScene = new Scene();

		// Set on Touch lister
		this.mMainScene.setOnAreaTouchListener(this);
		// Initialize main Scene
		this.init();

		pOnCreateSceneCallback.onCreateSceneFinished(this.mMainScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO Auto-generated method stub
		pOnPopulateSceneCallback.onPopulateSceneFinished();

	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
			for (int i = 0; i < CELLS_VERTICAL; i++) {
				for (int j = 0; j < CELLS_HORIZONTAL; j++) {
					if (pTouchArea.equals(mLevels[i][j])) {
						_appPrefs.setChapter(i * CELLS_HORIZONTAL + j + 1);
						this.startGame("next");
					}
						
//					else if (pTouchArea.equals(mLocks))
//						this.toMenuView();
				}
			}
		}
		return false;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Initialize everything.
	 */
	private void init() {
		this.initBG();
		this.initMenu();
		this.initAreaTouch();
	}

	/**
	 * Initialize touch area.
	 */
	private void initAreaTouch() {
		// this.mMainScene.registerTouchArea(this.mLevels);
		// this.mMainScene.registerTouchArea(this.mLocks);
	}

	/**
	 * Initialize everything.
	 */
	private void initBG() {
		// Background
		Sprite bg = new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
				mBackGroundTextureRegion,
				this.mEngine.getVertexBufferObjectManager());
		this.mMainScene.setBackground(new SpriteBackground(bg));
	}

	/**
	 * Initialize menu buttons.
	 */
	private void initMenu() {
		//
		int length = _appPrefs.getUnlockLevels();

		

		this.mLevels = new Sprite[CELLS_VERTICAL][CELLS_HORIZONTAL];
		this.mLevelsText = new Text[CELLS_VERTICAL][CELLS_HORIZONTAL];
		this.mLocks = new Sprite[CELLS_VERTICAL][CELLS_HORIZONTAL];

		float padding_left = (CELL_WIDTH - mMenuLevelTextureRegion.getWidth())/2;
		float padding_top = (CELL_HEIGHT - mMenuLevelTextureRegion.getHeight())/2;
		
		for (int i = 0; i < CELLS_VERTICAL; i++) {
			for (int j = 0; j < CELLS_HORIZONTAL; j++) {
				if (i * CELLS_HORIZONTAL + j < length) {
					this.mLevels[i][j] = new Sprite(padding_left + CELL_WIDTH
							* j, padding_top + i * CELL_HEIGHT,
							this.mMenuLevelTextureRegion,
							this.mEngine.getVertexBufferObjectManager());//
					this.mMainScene.attachChild(this.mLevels[i][j]);
					this.mMainScene.registerTouchArea(this.mLevels[i][j]);

					this.mLevelsText[i][j] = new Text(padding_left + CELL_WIDTH
							* j + this.mLevels[i][j].getWidth() / 2 - 10,
							padding_top + i * CELL_HEIGHT
									+ this.mLevels[i][j].getHeight() / 2 - 10,
							this.mFont, String.valueOf(i * CELLS_HORIZONTAL + j
									+ 1),
							this.mEngine.getVertexBufferObjectManager());//
					this.mMainScene.attachChild(mLevelsText[i][j]);
				} else {
					this.mLocks[i][j] = new Sprite(padding_left + CELL_WIDTH
							* j, padding_top + i * CELL_HEIGHT,
							this.mMenuLockTextureRegion,
							this.mEngine.getVertexBufferObjectManager());//
					this.mMainScene.attachChild(this.mLocks[i][j]);
				}
			}
		}

	}

	/**
	   * 
	   */
	private void toMenuView() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), Menu.class);
		startActivity(intent);
		LockLevels.this.finish();
	}

	/**
	   * 
	   */
	private void startGame(final String model) {
		Intent intent = new Intent();
		intent.putExtra("mode", model);
		intent.setClass(this.getApplicationContext(), JewelsArcade.class);
		startActivity(intent);
		overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
//		LockLevels.this.finish();
	}

	// ===========================================================
	// Additional MEthods
	// ===========================================================

	private void initConstants() {
		CELL_WIDTH = CAMERA_WIDTH / CELLS_HORIZONTAL;
		CELL_HEIGHT = CELL_WIDTH;
	}
}
