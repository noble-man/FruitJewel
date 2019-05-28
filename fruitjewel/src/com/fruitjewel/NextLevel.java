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
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class NextLevel extends BaseGameActivity implements IOnAreaTouchListener {

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
	private BitmapTextureAtlas mMenuNextGameTexture, mMenuMenuTexture;
	protected TextureRegion mMenuNextGameTextureRegion,mMenuMenuTextureRegion;

	// Define the menu Items
	private Sprite mNextGame, mMenu;
	private Text mNextGameText, mMenuText;

	// Define the font
	private ITexture mFontTexture;
	private Font mFont;
	
	/** App Preferences **/
	AppPreferences _appPrefs;

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
		
		mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0, 0, 0);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.mCamera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engineOptions.getRenderOptions().setDithering(true);	
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		// Background Image resoures
		this.mBackGroundTexture = new BitmapTextureAtlas(getTextureManager(),720, 1280,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBackGroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mBackGroundTexture, this, "gfx/background.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mBackGroundTexture);
		// New Game button image resource
		this.mMenuNextGameTexture = new BitmapTextureAtlas(getTextureManager(),266, 75,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuNextGameTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mMenuNextGameTexture, this, "gfx/next.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuNextGameTexture);
		// Continue Game button image resource
		this.mMenuMenuTexture = new BitmapTextureAtlas(getTextureManager(),266, 75,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mMenuMenuTexture, this, "gfx/menu.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuMenuTexture);			
		// Load font texture
		this.mFontTexture = new BitmapTextureAtlas(getTextureManager(),256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.mFontTexture, 
				this.getAssets(), "fonts/bluehigh.ttf", 38, true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
		
		// resource
		pOnCreateResourcesCallback.onCreateResourcesFinished();
		
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		// Create the main screne
		this.mMainScene = new Scene();
		
		// Set on Touch lister
		this.mMainScene.setOnAreaTouchListener(this);
		// Initialize main Scene
		this.init();
		
		pOnCreateSceneCallback.onCreateSceneFinished(this.mMainScene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback)
			throws Exception {
		// TODO Auto-generated method stub
		pOnPopulateSceneCallback.onPopulateSceneFinished();
		
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, 
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if(pSceneTouchEvent.getAction()==MotionEvent.ACTION_UP)
		{
			if (pTouchArea.equals(mNextGame))
				this.startGame("next");
			else if (pTouchArea.equals(mMenu))
				this.toMenuView();
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
		this.mMainScene.registerTouchArea(this.mNextGame);
		this.mMainScene.registerTouchArea(this.mMenu);		
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
		final float centerX = (CAMERA_WIDTH - this.mMenuNextGameTextureRegion.getWidth()) / 2;
		//
		this.mNextGame = new Sprite(centerX, 230, this.mMenuNextGameTextureRegion,
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(this.mNextGame);
		
/*		this.mNextGameText = new Text(centerX + 47, 236, this.mFont, "Next", 
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(mNextGameText);*/
		//
		this.mMenu = new Sprite(centerX, 320, this.mMenuMenuTextureRegion, 
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(this.mMenu);
		
		/*this.mMenuText = new Text(centerX + 60, 306, this.mFont, "Menu", 
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(mMenuText);*/
	}

	/**
	   * 
	   */
	private void toMenuView() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), Menu.class);
		startActivity(intent);
		NextLevel.this.finish();
	}
	
	/**
	   * 
	   */
	private void startGame(final String model) {
		Intent intent = new Intent();
		intent.putExtra("mode", model);
		intent.setClass(this.getApplicationContext(), JewelsClassic.class);
		startActivity(intent);
		NextLevel.this.finish();
	}
	
}
