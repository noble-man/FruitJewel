package com.fruitjewel;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.MotionEvent;
import android.widget.Toast;

import com.appbrain.AppBrain;
import com.google.analytics.tracking.android.EasyTracker;
import com.swarmconnect.Swarm;

public class Menu extends BaseGameActivity implements IOnAreaTouchListener {

	// ===========================================================
	// Constants
	// ===========================================================

	private static int CAMERA_WIDTH = 720;
	private static  int CAMERA_HEIGHT = 1280;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	/** Swarm **/
	public static final int SWARM_APP_ID = 6662;
	public static final String SWARM_APP_KEY = "3a96bfb6d29fa36e09da8563e6130e1e";
	
	protected Scene mMainScene; // Main Scene

	// Background Texture
	private BitmapTextureAtlas mBackGroundTexture;
	protected TextureRegion mBackGroundTextureRegion;

	// Menu button Texture
	private BitmapTextureAtlas mMenuNewGameTexture, mMenuContinueTexture, mMenuLeaderBoardTexture,
			mMenuRateTexture, mMenuSoundTexture, mMenuMusicTexture;
	protected TextureRegion mMenuNewGameTextureRegion,
			mMenuContinueTextureRegion, mMenuLeaderBoardTextureRegion, mMenuRateTextureRegion,
			mMenuSoundTextureRegion, mMenuMusicTextureRegion;

	// Define the menu Items
	private Sprite mNewGame, mContinue, mLeaderBoard, mRate, mMusic, mSound;
	private Text mNewGameText, mContinueText;

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
		
		Intent intent = getIntent();
		String showLeaderBoard = intent.getStringExtra("showLeaderBoard");		

		if ((showLeaderBoard != null) && (showLeaderBoard.equals("yes"))) {
			Swarm.setActive(this);
			Swarm.setAllowGuests(true);
			Swarm.init(Menu.this, SWARM_APP_ID, SWARM_APP_KEY);
			Swarm.showLeaderboards();
		}
		// Background Image resoures
		this.mBackGroundTexture = new BitmapTextureAtlas(getTextureManager(),720, 1280);
		this.mBackGroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBackGroundTexture, this,"gfx/background_menu.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mBackGroundTexture);
		
		// New Game button image resource
		this.mMenuNewGameTexture = new BitmapTextureAtlas(getTextureManager(),268, 85,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuNewGameTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mMenuNewGameTexture, this, "gfx/arcade.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuNewGameTexture);
		
		// Continue Game button image resource
		this.mMenuContinueTexture = new BitmapTextureAtlas(getTextureManager(),266, 75,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuContinueTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mMenuContinueTexture, this, "gfx/classic.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuContinueTexture);
		
		// LeaderBoard Game button image resource
		this.mMenuLeaderBoardTexture = new BitmapTextureAtlas(getTextureManager(),267, 76,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuLeaderBoardTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mMenuLeaderBoardTexture, this, "gfx/leaderboard.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuLeaderBoardTexture);
				
		// Rate button image resource
		this.mMenuRateTexture = new BitmapTextureAtlas(getTextureManager(),70, 70,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuRateTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mMenuRateTexture, this, "gfx/rate.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuRateTexture);	
		
		// Sound button image resource
		this.mMenuSoundTexture = new BitmapTextureAtlas(getTextureManager(),70, 70,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.mMenuSoundTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuSoundTexture, this, "gfx/soundon.png", 0, 0);
		this.mMenuSoundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mMenuSoundTexture, this, 
				_appPrefs.getSound() ? "gfx/soundon.png" : "gfx/soundoff.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuSoundTexture);	
		
		// Music button image resource
		this.mMenuMusicTexture = new BitmapTextureAtlas(getTextureManager(),70, 70,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuMusicTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.mMenuMusicTexture, this, 
				_appPrefs.getMusic() ? "gfx/musicon.png" : "gfx/musicoff.png", 0, 0); 
		this.mEngine.getTextureManager().loadTexture(this.mMenuMusicTexture);	
		
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


	/**
	 * React on area touched. Recognize the button touch.
	 */
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,float pTouchAreaLocalY) {
		if(pSceneTouchEvent.getAction()==MotionEvent.ACTION_UP)
		{
			if (pTouchArea.equals(mNewGame)) {
				
				AppPreferences appPrefs = new AppPreferences(getApplicationContext());
				if (appPrefs.getUnlockLevels() > 1) {
					Intent intent = new Intent();
					intent.putExtra("mode", "new");
					intent.setClass(this.getApplicationContext(), LockLevels.class);
					startActivity(intent);
					overridePendingTransition(R.anim.animation1, R.anim.animation2);
				} else {				
					Intent intent = new Intent();
					intent.putExtra("mode", "new");
					intent.setClass(this.getApplicationContext(), JewelsArcade.class);
					startActivity(intent);
					overridePendingTransition(R.anim.animation1, R.anim.animation2);
//					Menu.this.finish();
				}
			} else if (pTouchArea.equals(mContinue)) {
				Intent intent = new Intent();
				intent.putExtra("mode", "new");
				intent.setClass(this.getApplicationContext(), JewelsClassic.class);
				startActivity(intent);
				overridePendingTransition(R.anim.animation1, R.anim.animation2);
//				Menu.this.finish();
			} else if (pTouchArea.equals(mLeaderBoard)) {
				
				Swarm.setActive(this);
				Swarm.setAllowGuests(true);
				Swarm.init(Menu.this, SWARM_APP_ID, SWARM_APP_KEY);
				Swarm.showLeaderboards();
			}
			else if(pTouchArea.equals(mSound))
			{
				_appPrefs.setSound(!_appPrefs.getSound());
				this.mMenuSoundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						this.mMenuSoundTexture, this, 
						_appPrefs.getSound() ? "gfx/soundon.png" : "gfx/soundoff.png", 0, 0);			
			}
			else if(pTouchArea.equals(mMusic))
			{
				_appPrefs.setMusic(!_appPrefs.getMusic());
				this.mMenuMusicTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						this.mMenuMusicTexture, this, 
						_appPrefs.getMusic() ? "gfx/musicon.png" : "gfx/musicoff.png", 0, 0); 
			}
			else if(pTouchArea.equals(mRate))
			{
				Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
				  startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
				  Toast.makeText(this, "Couldn't launch the market", Toast.LENGTH_LONG).show();
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
		this.mMainScene.registerTouchArea(this.mNewGame);
		this.mMainScene.registerTouchArea(this.mContinue);
		this.mMainScene.registerTouchArea(this.mLeaderBoard);
		this.mMainScene.registerTouchArea(this.mRate);
		this.mMainScene.registerTouchArea(this.mSound);
		this.mMainScene.registerTouchArea(this.mMusic);
		
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
		
		final float centerX = (CAMERA_WIDTH - this.mMenuNewGameTextureRegion.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mMenuNewGameTextureRegion.getHeight()) / 3;
		final float iconDim = 60;
		final int padding = 10;
		
		float buttonWidth = 269;		
		float buttonHeigh = 85;
						
		this.mNewGame = new Sprite(centerX, centerY, buttonWidth, buttonHeigh, this.mMenuNewGameTextureRegion,
		this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(this.mNewGame);

		this.mContinue = new Sprite(centerX, centerY + buttonHeigh + padding, buttonWidth, buttonHeigh, this.mMenuContinueTextureRegion,
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(this.mContinue);

		this.mLeaderBoard = new Sprite(centerX, centerY + 2*buttonHeigh + 2*padding, buttonWidth, buttonHeigh, this.mMenuLeaderBoardTextureRegion,
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(this.mLeaderBoard);
		
		
		// Sound Button
		this.mSound = new Sprite(centerX, centerY + 3*buttonHeigh + 3*padding, iconDim,iconDim, this.mMenuSoundTextureRegion,
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(this.mSound);
				
		// Rate Button
		this.mRate = new Sprite(centerX + 100, centerY + 3*buttonHeigh + 3*padding, iconDim,iconDim, this.mMenuRateTextureRegion,
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(this.mRate);
		
		
		// Music Button
		this.mMusic = new Sprite(centerX + 200, centerY + 3*buttonHeigh + 3*padding, iconDim,iconDim, this.mMenuMusicTextureRegion,
				this.mEngine.getVertexBufferObjectManager());//
		this.mMainScene.attachChild(this.mMusic);

	}

	/**
	   * 
	   */
	private void startGame(final String model) {
		Intent intent = new Intent();
		intent.putExtra("mode", model);
		intent.setClass(this.getApplicationContext(), JewelsArcade.class);
		startActivity(intent);
		Menu.this.finish();
	}
	// ===========================================================
	// Additional MEthods
	// ===========================================================			
	
	@Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
}
