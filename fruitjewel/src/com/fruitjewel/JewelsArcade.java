package com.fruitjewel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontUtils;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.math.MathUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;
import com.google.analytics.tracking.android.EasyTracker;
import com.fruitjewel.constants.IConstants;
import com.fruitjewel.entity.BorderSprite;
import com.fruitjewel.entity.JewelCell;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmLeaderboard;
import com.swarmconnect.SwarmLeaderboard.DateRange;
import com.swarmconnect.SwarmLeaderboard.GotLeaderboardCB;
import com.swarmconnect.SwarmLeaderboard.GotScoresCB;
import com.swarmconnect.SwarmLeaderboardScore;

public class JewelsArcade extends BaseGameActivity implements IConstants,
		IOnAreaTouchListener, IOnSceneTouchListener, IOnMenuItemClickListener, AdListener {

	// ===========================================================
	// Constants
	// ===========================================================

	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 1280;

	/****/
	public static int CELLS_HORIZONTAL = 8;
	public static int CELLS_VERTICAL = CELLS_HORIZONTAL;

	/****/
	public static int CELL_WIDTH = 45;
	public static int CELL_HEIGHT = CELL_WIDTH;

	/****/
	public static int CELLBG_WIDTH = 90;
	public static int CELLBG_HEIGHT = CELLBG_WIDTH;

	/****/
	public static int PADDINGX = 0;
	public static int PADDINGY = 150;
	public static int PADDINGY_ADMOB_TOP = 10;

	/****/
	public static final int CELLBG_HORIZONTAL = 4;
	public static final int CELLBG_VERTICAL = CELLBG_HORIZONTAL;

	/** Swarm **/
	public static final int SWARM_APP_ID = 6662;
	public static final String SWARM_APP_KEY = "3a96bfb6d29fa36e09da8563e6130e1e";
//	public static final int TOP_LEADERBOARD_ID = 11076;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;
	protected Scene mMainScene; // Main Scene

	/** Game Mode **/
	private String mGameModel;

	/** Ad Mob **/
	private ScreenAdvertisement mAdvertisement;
	/** App Preferences **/
	AppPreferences _appPrefs;

	// Background Texture
	private BitmapTextureAtlas mBackGroundTexture, mBackGroundNextLevelTexture;
	protected TextureRegion mBackGroundTextureRegion,
			mBackGroundNextLevelTextureRegion;

	/****/
	private boolean mGameRunning;// ()
	private boolean mIsSwaping;//
	private final int MOVE_UP = 1;//
	private final int MOVE_DOWN = 2;//
	private final int MOVE_LEFT = 3;//
	private final int MOVE_RIGHT = 4;//
	private final int FALL = 5;//
	private final int DEAD = 6;//
	private final int CHECK = 0;//
	private int STATE = CHECK;//

	/** sound **/
	private Sound mSwapErrorSound;//
	private Sound mFallSound;//
	private Sound mRemoveSound;//
	private Sound mStartingSound;//
	/** Music **/
	private Music mMusic;

	/** Jewel Style values **/
	private int mStyleBomb = 10;
	private int mStyleColor = 11;
	private int mStyleLight = 12;

	/** Jewel special values **/
	private boolean mFlagBomb = false;
	private boolean mFlagColor = false;
	private boolean mFlagLight = false;
	private boolean mFlagBombAnimated = false;
	private boolean mFlagFlowerAnimated = false;
	private boolean mFlagRuaAnimated = false;

	private boolean mBombExecution = false;
	private boolean mLightExecution = false;

	/** Special Jewel Active position **/
	private int mBombActiveRow = -2;
	private int mBombActiveCol = -2;
	private int mColorActiveRow = -2;
	private int mColorActiveCol = -2;
	private int mLightActiveRow = -2;
	private int mLightActiveCol = -2;

	/** Values **/
	private int mScore = 0;
	private int mTotalScore = 0;
	private int mGoalScore = 1000;
	private int mChapter = 1;
	private float mChapterStep = 6;// ()
	private int mJewelBombs = 0; // Number of jewel Bombs
	private int mJewelColor = 0; // Number of colour changing jewels
	private int mJewelLightning = 0;// Number of Lightnings earned
	private int mJewelContinuousCount = 0; // Over 20 earns a lightning

	/** Value tracking **/
	private int mCurRow, mCurCol;//
	private int mLastRow, mLastCol;//
	private ArrayList<String> mDeadArrList;//
	private int mTime = 0;// Hint timer
	private int mQuickly = 10;
	private int mQuicklyCount = 0;
	private int mLevelIncrScore = 0;
	private int mLevelBaseScore = 0;

	/** Timer Handlers **/
	private TimerHandler mTimerHandlerProgress;
	private TimerHandler mTimerHandlerHint;

	/****/
	private int SPEED = 4;//
	private int moveValue = 0;//

	/** Jewel textures **/
	private BitmapTextureAtlas[] mJewelTexture;
	protected ITextureRegion[] mJewelTextureRegion;

	/** Brick textures **/
	private BitmapTextureAtlas[] mBrickTexture;
	protected ITextureRegion[] mBrickTextureRegion;

	/** Jewel textures - Bomb **/
	private BitmapTextureAtlas mJewelBombTexture;
	protected ITextureRegion mJewelBombTextureRegion;
	/** Jewel textures - Color **/
	private BitmapTextureAtlas mJewelColorTexture;
	protected ITextureRegion mJewelColorTextureRegion;
	/** Jewel textures - Lightning **/
	private BitmapTextureAtlas mJewelLightTexture;
	protected ITextureRegion mJewelLightTextureRegion;

	/** Selected border sprite **/
	private BorderSprite mBorder;
	private BitmapTextureAtlas mBorderTexture;
	private ITextureRegion mBorderTextureRegion;
	/** board **/
	private BitmapTextureAtlas mBoardTexture;
	private ITextureRegion mBoardTextureRegion;
	/****/
	private BitmapTextureAtlas mBGCellTexture;
	private ITextureRegion mBGCellTextureRegion;
	/** progress **/
	private Sprite mProgress;
	private BitmapTextureAtlas mProgressStaticBGTexture, mProgressBGTexture,
			mProgressTexture;
	private ITextureRegion mProgressStaticBGTextureRegion,
			mProgressBGTextureRegion, mProgressTextureRegion;
	/** Pause **/
	private Sprite mPause;
	private BitmapTextureAtlas mPauseButtonTexture;
	private ITextureRegion mPauseButtonTextureRegion;

	private Sprite btnContinue;
	/** Fonts (value) **/
	private ITexture mValueFontTexture;
	private Font mValueFont;
	private Text mScoreValText, mTotalScoreValText, mGoalScoreValText,
			mChapterValText;

	/** Fonts (title) **/
	private ITexture mTitleFontTexture;
	private Font mTitleFont;
	private Text mScoreTitleText, mTotalScoreTitleText, mGoalScoreTitleText,
			mChapterTitleText;

	// Define the font
	private ITexture mFontTexture;
	private Font mFont;

	/** Arrow Hint sprite **/
	private BitmapTextureAtlas mHintTexture;
	private TiledTextureRegion mHintTextureRegion;
	private AnimatedSprite mHintSprite;

	/** Lightning animated sprite **/
	private BitmapTextureAtlas mLightTexture;
	private TiledTextureRegion mLightTextureRegion;
	private AnimatedSprite mLightSprite;

	/** Bombing animated sprite **/
	private BitmapTextureAtlas mBombTexture;
	private TiledTextureRegion mBombTextureRegion;
	private AnimatedSprite mBombSprite;

	/** Jewel Destroy animated sprite **/
	private BitmapTextureAtlas mJewelDestroyTexture;
	private TiledTextureRegion mJewelDestroyTextureRegion;
	// private AnimatedSprite mJewelDestroySprite;

	/** Animated Star sprite **/
	private BitmapTextureAtlas mAnimatedStar;
	private TiledTextureRegion mAnimatedStarRegion;

	/** Animated Flower sprite **/
	private BitmapTextureAtlas mAnimatedFlower;
	private TiledTextureRegion mAnimatedFlowerRegion;

	/** Animated Rua sprite **/
	private BitmapTextureAtlas mAnimatedRua;
	private TiledTextureRegion mAnimatedRuaRegion;

	/** Menu Pause Screen **/
	private BitmapTextureAtlas pauseResumeTexture, pauseMenuTexture,
			pauseSoundTexture, pauseMusicTexture;
	private TextureRegion pauseResumeRegion, pauseMenuRegion,
			pauseHelpTextureRegion, pauseSoundTextureRegion,
			pauseMusicTextureRegion;

	/** Menu Next Level Screen **/
	private BitmapTextureAtlas nextLevelContinueTexture,
			nextLevelSubmitTexture;
	private TextureRegion nextLevelContinueRegion, nextLevelSubmitRegion;

	/** Sprite data **/
	public ArrayList<Sprite> mSpriteList;
	public int[][] mSpriteStyle;
	public int[][] mSpriteState;
	public int[][] mSpriteEntityPos;

	/** Brick data **/
	public ArrayList<Sprite> mBrickList;
	public int[][] mBrickState;
	public int[][] mBrickStyle;

	public ArrayList<Integer> mBrickStyleList;
	int mBrickCount;

	private InterstitialAd interstitial;
	//
	private HUD mHUD = new HUD();

	private int leaderboardLevels[] = new int[] {11076,11108,11110,11112,11114, 11116, 11118, 11120
												   ,11122, 11124, 11126, 11128};
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		interstitial = new InterstitialAd(this, "a151e93bb58d3ec");
		Swarm.setActive(this);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		_appPrefs = new AppPreferences(getApplicationContext());
		initConstants();		

		mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0, 0, 0);
		mCamera.setHUD(mHUD); // or mCamera.setHUD(hud)
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
		// Load Graphics
		this.loadGfx();
		// Set game mode
		this.initMode();
		// Set initial default values
		this.initFields();

		// resource
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {

		this.mMainScene = new Scene();
		this.mMainScene.setOnSceneTouchListener(this);
		this.mMainScene.setOnAreaTouchListener(this);

		// Set the Background image to full screen
		this.initBG();
		// Set the jewels bacgkround
		// this.initCellBG();

		// Initialize Levels
		this.readLevelJson(mChapter);
		// Initialize brick
		this.initBrick();
		// Initialize top bar (score,level,pause)
		this.initTopBar();
		// Initialize the progress Bar
		this.initProgress();
		// Initialize the border sprite, visible onTouch
		this.initBorderSprite();
		// Initialize jewels and add to HashMap
		this.initJewels();
		// Initialize the Music
		this.initMusic();
		// Initialize the Sound
		this.initSound();				
	    
		pOnCreateSceneCallback.onCreateSceneFinished(this.mMainScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		//
		this.onStarting();
		//
		this.prepareGame();
		//
		this.adjustModel();
		//
		this.gameLoop();
		//
		this.autoTips();

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
			if (pTouchArea.equals(mPause) && JewelsArcade.this.mGameRunning) {
				this.toPauseView();
			}
			if (pTouchArea.equals(btnContinue))
				this.toMenuView(false);
		}
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		//
		if (STATE == MOVE_DOWN || STATE == MOVE_LEFT || STATE == MOVE_RIGHT
				|| STATE == MOVE_UP || STATE == FALL) {
			return false;
		}
		// Eliminate 20 jewels continuous can win 1 lighting.
		// Reset the count, on each Touch
		mJewelContinuousCount = 0;
		// Hide the hint if shown
		if (mHintSprite != null && mHintSprite.isAnimationRunning()) {
			mHintSprite.stopAnimation();
			mHintSprite.setVisible(false);
		}

		//
		if (pSceneTouchEvent.getX() > PADDINGX
				&& pSceneTouchEvent.getX() < (CELLS_HORIZONTAL * CELL_WIDTH)
						+ PADDINGX
				&& pSceneTouchEvent.getY() > PADDINGY
				&& pSceneTouchEvent.getY() < (CELLS_VERTICAL * CELL_HEIGHT)
						+ PADDINGY) {

			if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
				mDeadArrList.clear();// Clear the list of "hints"
				this.mCurCol = (int) ((pSceneTouchEvent.getX() - PADDINGX) / CELL_WIDTH);
				this.mCurRow = (int) ((pSceneTouchEvent.getY() - PADDINGY) / CELL_HEIGHT);

				this.mBorder.setMapPosition(this.mCurRow, this.mCurCol);
				this.mBorder.getSprite().setVisible(true);
				// Check if swap is done by click
				if (this.isNext()) {//
					mTime = 0; // Reset hint timer
					this.mBorder.getSprite().setVisible(false);
					this.setMoveDirection();
				} else {//
					this.mLastRow = this.mCurRow;
					this.mLastCol = this.mCurCol;
					this.mBorder.setMapPosition(this.mCurRow, this.mCurCol);
					this.mBorder.getSprite().setVisible(true);
				}
			}
			if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
				mDeadArrList.clear();// Clear the list of "hints"
				this.mCurCol = (int) ((pSceneTouchEvent.getX() - PADDINGX) / CELL_WIDTH);
				this.mCurRow = (int) ((pSceneTouchEvent.getY() - PADDINGY) / CELL_HEIGHT);

				// Check if slide has been completed (left/right)
				if (this.mCurCol > this.mLastCol) // RIGHT
					this.mCurCol = this.mLastCol + 1;
				else if (this.mCurCol < this.mLastCol) // LEFT
					this.mCurCol = this.mLastCol - 1;
				else if (this.mCurRow > this.mLastRow) // TOP
					this.mCurRow = this.mLastRow + 1;
				else if (this.mCurRow < this.mLastRow) // DOWN
					this.mCurRow = this.mLastRow - 1;

				this.mBorder.setMapPosition(this.mCurRow, this.mCurCol);
				this.mBorder.getSprite().setVisible(true);
				if (this.isNext()) {//
					mTime = 0; // Reset hint timer
					this.mBorder.getSprite().setVisible(false);
					this.setMoveDirection();
				}
			}
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			JewelsArcade.this.onGameOver();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public final void onPause() {
		super.onPause();
		Swarm.setInactive(this);
		if (this.mBrickCount <= 0) {
			this.startNewChapter();
		} else {
			this.toPauseView();
		}
		
		if (_appPrefs.getMusic())
			this.mMusic.pause();
	}

	@Override
	public final void onResume() {
		super.onResume();
		Swarm.setActive(this);
		if (_appPrefs.getMusic())
			if (this.mMusic != null)
				this.mMusic.play();
	}

	// ===========================================================
	// Methods for Ad Mob
	// ===========================================================
	protected void showAd() {
		mAdvertisement.showAdvertisement();
	}

	protected void hideAd() {
		mAdvertisement.hideAdvertisement();
	}

	@Override
	protected void onSetContentView() {

		final FrameLayout frameLayout = new FrameLayout(this);
		final FrameLayout.LayoutParams frameLayoutLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);

		final AdView adView = new AdView(this, AdSize.BANNER, this.getResources().getString(R.string.admob_id));

		adView.refreshDrawableState();
		adView.setVisibility(AdView.VISIBLE);
		final FrameLayout.LayoutParams adViewLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);

		this.mRenderSurfaceView = new RenderSurfaceView(this);
		mRenderSurfaceView.setRenderer(mEngine, this);

		final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams = new FrameLayout.LayoutParams(
				super.createSurfaceViewLayoutParams());

		frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
		frameLayout.addView(adView, adViewLayoutParams);

		this.setContentView(frameLayout, frameLayoutLayoutParams);
	}

	// ===========================================================
	// Additional Methods
	// ===========================================================

	private void initConstants() {

		CELL_WIDTH = CAMERA_WIDTH / CELLS_HORIZONTAL;
		CELL_HEIGHT = CELL_WIDTH;
		CELLBG_WIDTH = CELL_WIDTH * 2;
		CELLBG_HEIGHT = CELLBG_WIDTH;
		SPEED = CELL_WIDTH / 10;		
	}

	private void loadGfx() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// Background Image resoures
		this.mBackGroundTexture = new BitmapTextureAtlas(getTextureManager(),
				720, 1280);
		this.mBackGroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBackGroundTexture, this,
						"background.png", 0, 0);
		mEngine.getTextureManager().loadTexture(this.mBackGroundTexture);

		// Background Image resoures
		this.mBackGroundNextLevelTexture = new BitmapTextureAtlas(
				getTextureManager(), 720, 1280);
		this.mBackGroundNextLevelTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBackGroundNextLevelTexture, this,
						"bg_nextlevel.png", 0, 0);
		mEngine.getTextureManager().loadTexture(
				this.mBackGroundNextLevelTexture);

		/* Jewels */
		this.mJewelTexture = new BitmapTextureAtlas[7];
		this.mJewelTextureRegion = new TextureRegion[7];
		for (int i = 0; i < this.mJewelTexture.length; i++) {
			this.mJewelTexture[i] = new BitmapTextureAtlas(getTextureManager(),
					128, 128, TextureOptions.DEFAULT);
		}
		this.mJewelTextureRegion[0] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelTexture[0], this, "jewel1.png", 0,
						0);
		this.mJewelTextureRegion[1] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelTexture[1], this, "jewel2.png", 0,
						0);
		this.mJewelTextureRegion[2] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelTexture[2], this, "jewel3.png", 0,
						0);
		this.mJewelTextureRegion[3] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelTexture[3], this, "jewel4.png", 0,
						0);
		this.mJewelTextureRegion[4] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelTexture[4], this, "jewel5.png", 0,
						0);
		this.mJewelTextureRegion[5] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelTexture[5], this, "jewel6.png", 0,
						0);
		this.mJewelTextureRegion[6] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelTexture[6], this, "jewel7.png", 0,
						0);
		for (int i = 0; i < this.mJewelTexture.length; i++) {
			this.mEngine.getTextureManager().loadTexture(this.mJewelTexture[i]);
		}

		/* Brick */
		this.mBrickTexture = new BitmapTextureAtlas[3];
		this.mBrickTextureRegion = new TextureRegion[3];
		for (int i = 0; i < this.mBrickTexture.length; i++) {
			this.mBrickTexture[i] = new BitmapTextureAtlas(getTextureManager(),
					128, 128, TextureOptions.DEFAULT);
		}
		this.mBrickTextureRegion[0] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBrickTexture[0], this, "brick1.png", 0,
						0);
		this.mBrickTextureRegion[1] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBrickTexture[1], this, "brick2.png", 0,
						0);
		this.mBrickTextureRegion[2] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBrickTexture[2], this, "brick3.png", 0,
						0);
		for (int i = 0; i < this.mBrickTexture.length; i++) {
			this.mEngine.getTextureManager().loadTexture(this.mBrickTexture[i]);
		}

		/* Jewels Bomb */
		this.mJewelBombTexture = new BitmapTextureAtlas(getTextureManager(),
				128, 128, TextureOptions.DEFAULT);
		this.mJewelBombTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelBombTexture, this, "jewelB.png", 0,
						0);
		this.mEngine.getTextureManager().loadTexture(this.mJewelBombTexture);
		/* Jewels Color */
		this.mJewelColorTexture = new BitmapTextureAtlas(getTextureManager(),
				128, 128, TextureOptions.DEFAULT);
		this.mJewelColorTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelColorTexture, this, "jewelC.png",
						0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mJewelColorTexture);
		/* Jewels Lightning */
		this.mJewelLightTexture = new BitmapTextureAtlas(getTextureManager(),
				128, 128, TextureOptions.DEFAULT);
		this.mJewelLightTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mJewelLightTexture, this, "jewelL.png",
						0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mJewelLightTexture);

		/* Border */
		this.mBorderTexture = new BitmapTextureAtlas(getTextureManager(), 64,
				64, TextureOptions.DEFAULT);
		this.mBorderTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBorderTexture, this, "selection.png", 0,
						0);
		this.mEngine.getTextureManager().loadTexture(this.mBorderTexture);
		/* Board */
		this.mBoardTexture = new BitmapTextureAtlas(getTextureManager(), 512,
				512, TextureOptions.DEFAULT);
		this.mBoardTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBoardTexture, this, "board.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mBoardTexture);
		/* Background cell */
		this.mBGCellTexture = new BitmapTextureAtlas(getTextureManager(), 128,
				128, TextureOptions.DEFAULT);
		this.mBGCellTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBGCellTexture, this, "bg_cell.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mBGCellTexture);

		/* progress */
				this.mProgressTexture = new BitmapTextureAtlas(getTextureManager(),
				698, 102, TextureOptions.DEFAULT);
		this.mProgressTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mProgressTexture, this,
						"progressbar_fill.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mProgressTexture);

		/* Pause */
		this.mPauseButtonTexture = new BitmapTextureAtlas(getTextureManager(),
				64, 64, TextureOptions.DEFAULT);
		this.mPauseButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mPauseButtonTexture, this, "pause.png",
						0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mPauseButtonTexture);

		/* Hint Arrow */
		this.mHintTexture = new BitmapTextureAtlas(getTextureManager(), 128,
				128, TextureOptions.DEFAULT);
		this.mHintTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mHintTexture, this, "arrows.png", 0,
						0, 4, 4);
		this.mEngine.getTextureManager().loadTexture(this.mHintTexture);

		/* Lightning */
		this.mLightTexture = new BitmapTextureAtlas(getTextureManager(), 720,
				1440, TextureOptions.DEFAULT);
		this.mLightTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mLightTexture, this,
						"lightning.png", 0, 0, 1, 8);
		this.mEngine.getTextureManager().loadTexture(this.mLightTexture);

		/* Bombing */
		this.mBombTexture = new BitmapTextureAtlas(getTextureManager(), 1000,
				750, TextureOptions.DEFAULT);
		this.mBombTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBombTexture, this, "bom_m.png", 0,
						0, 5, 4);
		this.mEngine.getTextureManager().loadTexture(this.mBombTexture);

		/* Destroy Jewel */
		this.mJewelDestroyTexture = new BitmapTextureAtlas(getTextureManager(),
				1000, 750, TextureOptions.DEFAULT);
		this.mJewelDestroyTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mJewelDestroyTexture, this,
						"bom_m.png", 0, 0, 5, 4);
		this.mEngine.getTextureManager().loadTexture(this.mJewelDestroyTexture);

		/* Animated Star */
		this.mAnimatedStar = new BitmapTextureAtlas(getTextureManager(), 960,
				900, TextureOptions.DEFAULT);
		this.mAnimatedStarRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mAnimatedStar, this, "sao.png", 0,
						0, 5, 5);
		this.mEngine.getTextureManager().loadTexture(this.mAnimatedStar);

		/* Animated Flower */
		this.mAnimatedFlower = new BitmapTextureAtlas(getTextureManager(), 960,
				768, TextureOptions.DEFAULT);
		this.mAnimatedFlowerRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mAnimatedFlower, this, "hoa.png", 0,
						0, 5, 4);
		this.mEngine.getTextureManager().loadTexture(this.mAnimatedFlower);

		/* Animated Rua */
		this.mAnimatedRua = new BitmapTextureAtlas(getTextureManager(), 960,
				1079, TextureOptions.DEFAULT);
		this.mAnimatedRuaRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mAnimatedRua, this, "rua.png", 0, 0,
						5, 6);
		this.mEngine.getTextureManager().loadTexture(this.mAnimatedRua);

		/************ NEXT LEVEL SCREEN ************/
		// Resume Game
		this.nextLevelContinueTexture = new BitmapTextureAtlas(getTextureManager(),
				268, 85, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.nextLevelContinueRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.nextLevelContinueTexture, this, "continue.png",
						0, 0);
		this.mEngine.getTextureManager().loadTexture(this.nextLevelContinueTexture);

		// Go to Menu
		this.nextLevelSubmitTexture = new BitmapTextureAtlas(getTextureManager(),
				268, 85, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.nextLevelSubmitRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.nextLevelSubmitTexture, this, "submitscore.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.nextLevelSubmitTexture);
		
		/************ PAUSE SCREEN **************/
		// Resume Game
		this.pauseResumeTexture = new BitmapTextureAtlas(getTextureManager(),
				269, 85, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.pauseResumeRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.pauseResumeTexture, this, "resume.png",
						0, 0);
		this.mEngine.getTextureManager().loadTexture(this.pauseResumeTexture);

		// Go to Menu
		this.pauseMenuTexture = new BitmapTextureAtlas(getTextureManager(),
				268, 85, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.pauseMenuRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.pauseMenuTexture, this, "menu.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.pauseMenuTexture);

		// Sound button image resource
		// this.pauseHelpTexture = new BitmapTextureAtlas(getTextureManager(),
		// 64,
		// 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// this.pauseHelpTextureRegion = BitmapTextureAtlasTextureRegionFactory
		// .createFromAsset(this.pauseHelpTexture, this, "help.png", 0, 0);
		// this.mEngine.getTextureManager().loadTexture(this.pauseHelpTexture);

		// Sound button image resource
		this.pauseSoundTexture = new BitmapTextureAtlas(getTextureManager(),
				70, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.pauseSoundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.pauseSoundTexture, this,
						_appPrefs.getSound() ? "soundon.png" : "soundoff.png",
						0, 0);
		this.mEngine.getTextureManager().loadTexture(this.pauseSoundTexture);

		// Music button image resource
		this.pauseMusicTexture = new BitmapTextureAtlas(getTextureManager(),
				70, 70, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.pauseMusicTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.pauseMusicTexture, this,
						_appPrefs.getMusic() ? "musicon.png" : "musicoff.png",
						0, 0);
		this.mEngine.getTextureManager().loadTexture(this.pauseMusicTexture);

		/*** FONTS ***/
		FontFactory.setAssetBasePath("fonts/");

		// Load font texture
		this.mFontTexture = new BitmapTextureAtlas(getTextureManager(), 256,
				256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = FontFactory.createFromAsset(this.getFontManager(),
				this.mFontTexture, this.getAssets(), "UTM Loko.ttf", 35,
				true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);		
		
		this.mTitleFontTexture = new BitmapTextureAtlas(getTextureManager(),
				512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mTitleFont = FontFactory.createFromAsset(this.getFontManager(),
				this.mTitleFontTexture, this.getAssets(), "UTM Loko.ttf",
				28, true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mTitleFontTexture);
		this.mEngine.getFontManager().loadFont(this.mTitleFont);
		
		/* Value font */
		this.mValueFontTexture = new BitmapTextureAtlas(getTextureManager(),
				512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mValueFont = FontFactory.createFromAsset(this.getFontManager(),
				this.mValueFontTexture, this.getAssets(), "Thonburi.otf", 45,
				true, Color.BLACK);

		this.mEngine.getTextureManager().loadTexture(this.mValueFontTexture);
		this.mEngine.getFontManager().loadFont(this.mValueFont);
	}

	/**
	* 
	*/
	private void initMode() {
		Intent intent = getIntent();
		this.mGameModel = intent.getStringExtra("mode");
	}

	private void initFields() {
		this.mLastRow = -2;
		this.mLastCol = -2;
		this.mIsSwaping = false;
		mDeadArrList = new ArrayList<String>();
		mFlagBomb = false;
		mFlagColor = false;
		mFlagLight = false;
		this.mQuickly = this.getResources().getInteger(R.integer.quickly_time);
		this.mLevelBaseScore = this.getResources().getInteger(
				R.integer.level_base_score);
		this.mLevelIncrScore = this.getResources().getInteger(
				R.integer.level_increment_score);

		if (this.mGameModel.equals("new")) {
			_appPrefs.setChapter(1);
			_appPrefs.setTotal(0);
		}

		this.mChapter = _appPrefs.getChapter();
		this.mTotalScore = _appPrefs.getTotal();
		this.mScore = 0;
		// TODO: Set configurable values

		SwarmLeaderboard.getLeaderboardById(leaderboardLevels[mChapter-1], callback);
		
	}

	private void initBG() {
		// Background
		Sprite bg = new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
				mBackGroundTextureRegion,
				this.mEngine.getVertexBufferObjectManager());
		this.mMainScene.setBackground(new SpriteBackground(bg));

	}

	private void initCellBG() {
		// Board Background
		final Sprite board = new Sprite(PADDINGX, PADDINGY, CELL_WIDTH
				* CELLS_HORIZONTAL, CELL_HEIGHT * CELLS_VERTICAL,
				mBoardTextureRegion,
				this.mEngine.getVertexBufferObjectManager());

		board.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		board.setAlpha(0.2f);
		this.mMainScene.attachChild(board);
	}

	private void readLevelJson(int chapter) {
		try {

			this.mBrickStyleList = new ArrayList<Integer>(CELLS_VERTICAL
					* CELLS_HORIZONTAL);

			for (int j = 0; j < CELLS_VERTICAL; j++) {
				for (int i = 0; i < CELLS_HORIZONTAL; i++) {
					this.mBrickStyleList.add(getSpriteArrayPos(j, i), 0);
				}
			}
			InputStream is = this.getAssets().open("arcade.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);
			// getting JSON string from URL
			JSONObject jsonObj = new JSONObject(bufferString);
			JSONArray jsonArray = jsonObj.getJSONArray("levels");
			JSONObject level = jsonArray.getJSONObject(chapter - 1);
			JSONArray brick3s = level.getJSONArray("brick3");
			for (int i = 0; i < brick3s.length(); i++) {
				JSONObject brick3 = brick3s.getJSONObject(i);
				int row = brick3.getInt("j");
				int col = brick3.getInt("i");
				this.mBrickStyleList.set(getSpriteArrayPos(row, col), 2);
			}

			JSONArray brick2s = level.getJSONArray("brick2");
			for (int j = 0; j < brick2s.length(); j++) {
				JSONObject brick2 = brick2s.getJSONObject(j);
				int row = brick2.getInt("j");
				int col = brick2.getInt("i");
				this.mBrickStyleList.set(getSpriteArrayPos(row, col), 1);
			}
		} catch (Exception ex) {
			ex.getMessage();
		}
	}

	private void initBrick() {
		mBrickList = new ArrayList<Sprite>(CELLS_VERTICAL * CELLS_HORIZONTAL);

		mBrickStyle = new int[CELLS_VERTICAL][CELLS_HORIZONTAL];
		for (int j = 0; j < CELLS_VERTICAL; j++) {
			for (int i = 0; i < CELLS_HORIZONTAL; i++) {

				addBrick(j, i,
						this.mBrickStyleList.get(getSpriteArrayPos(j, i)));

				// Add to the Scene
				this.mMainScene.attachChild(this.mBrickList
						.get(getSpriteArrayPos(j, i)));
			}
		}

		mBrickCount = mBrickList.size();

	}

	/**
	 * Border Sprite blinking
	 */
	private void initBorderSprite() {
		//
		this.mBorder = new BorderSprite(-2, -2, mBorderTextureRegion,
				CELL_WIDTH, CELL_HEIGHT, PADDINGX, PADDINGY,
				this.mEngine.getVertexBufferObjectManager());
		this.mBorder.getSprite().setVisible(false);
		this.mBorder.getSprite().setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mBorder.getSprite().registerEntityModifier(
				new LoopEntityModifier(new SequenceEntityModifier(
						new AlphaModifier(0.4f, 1, 0), new AlphaModifier(0.2f,
								0, 1))));
		this.mMainScene.attachChild(this.mBorder.getSprite());
		// this.mMainScene.getLayer(LAYER_JEWELS).addEntity(this.mBorder.getSprite());
	}

	private void initJewels() {

		mSpriteList = new ArrayList<Sprite>(CELLS_VERTICAL * CELLS_HORIZONTAL);
		mSpriteStyle = new int[CELLS_VERTICAL][CELLS_HORIZONTAL];
		mSpriteState = new int[CELLS_VERTICAL][CELLS_HORIZONTAL];
		mSpriteEntityPos = new int[CELLS_VERTICAL][CELLS_HORIZONTAL];

		for (int j = 0; j < CELLS_VERTICAL; j++) {
			for (int i = 0; i < CELLS_HORIZONTAL; i++) {
				addRandomJewel(j, i, STATE_NORMAL);
				// Get next jewel until is NOT 3 in a row
				while (checkHorizontal(j, i) >= 3 || checkVertical(j, i) >= 3) {
					addRandomJewel(j, i, STATE_NORMAL);
				}
				setSpritePosition(j, i);
				// Add to the Scene
				this.mMainScene.attachChild(this.mSpriteList
						.get(getSpriteArrayPos(j, i)));
				mSpriteEntityPos[j][i] = this.mMainScene.getChildCount() - 1;
			}
		}
	}

	/**
	 * Initilize top Bar values (Pause button, score, level, etc.)
	 */
	private void initTopBar() {
		int spacing = (CAMERA_WIDTH - 20) / 3;
		int lineHeight = 50;
		
		// Level Up

		// this.mNextRankText.setScaleY(1.5f);

		// Chapter Title
		this.mChapterTitleText = new Text(5, PADDINGY_ADMOB_TOP + 10,
				this.mTitleFont, "Level",
				this.mEngine.getVertexBufferObjectManager());
		this.mChapterTitleText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mChapterTitleText.setAlpha(1);
		this.mChapterTitleText.setScaleY(1.5f);
		// this.mMainScene.attachChild(this.mChapterTitleText);
		this.mHUD.attachChild(this.mChapterTitleText);

		// Score
		this.mScoreTitleText = new Text(spacing - 20, PADDINGY_ADMOB_TOP + 10,
				this.mTitleFont, "Score",
				this.mEngine.getVertexBufferObjectManager());
		this.mScoreTitleText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mScoreTitleText.setAlpha(1);
		this.mScoreTitleText.setScaleY(1.5f);
		this.mHUD.attachChild(this.mScoreTitleText);

		// Goal Score
		this.mGoalScoreTitleText = new Text(2 * spacing - 20,
				PADDINGY_ADMOB_TOP + 10, this.mTitleFont, "Best",
				this.mEngine.getVertexBufferObjectManager());
		this.mGoalScoreTitleText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mGoalScoreTitleText.setAlpha(1);
		this.mGoalScoreTitleText.setScaleY(1.5f);
		this.mHUD.attachChild(this.mGoalScoreTitleText);

		// Chapter value
		this.mChapterValText = new Text(20, PADDINGY_ADMOB_TOP + lineHeight,
				this.mValueFont, String.valueOf(mChapter), 4,
				this.mEngine.getVertexBufferObjectManager());
		this.mChapterValText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mChapterValText.setAlpha(1);
		this.mChapterValText.setScaleY(1.5f);
		this.mHUD.attachChild(this.mChapterValText);

		// Score value
		this.mScoreValText = new Text(spacing - 10, PADDINGY_ADMOB_TOP + lineHeight,
				this.mValueFont, String.valueOf(mScore), 7,
				this.mEngine.getVertexBufferObjectManager());
		this.mScoreValText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mScoreValText.setAlpha(1);
		this.mScoreValText.setScaleY(1.5f);
		this.mHUD.attachChild(this.mScoreValText);

		// Goal Score value
		this.mGoalScoreValText = new Text(2 * spacing - 10,
				PADDINGY_ADMOB_TOP + lineHeight, this.mValueFont,
				String.valueOf(mGoalScore), 7,
				this.mEngine.getVertexBufferObjectManager());
		this.mGoalScoreValText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mGoalScoreValText.setAlpha(1);
		this.mGoalScoreValText.setScaleY(1.5f);
		this.mHUD.attachChild(this.mGoalScoreValText);


		// Pause Button
		
		this.mPause = new Sprite(CAMERA_WIDTH - 64, PADDINGY_ADMOB_TOP + 20, 
		this.mPauseButtonTextureRegion,
		this.mEngine.getVertexBufferObjectManager());//			

		this.mHUD.attachChild(this.mPause);
		this.mMainScene.registerTouchArea(this.mPause);
	}

	/**
* 
*/
	private void initProgress() {
		int posY = PADDINGY + CELLS_VERTICAL * CELL_HEIGHT;
		this.mProgress = new Sprite(5, posY + 100, this.mProgressTextureRegion,
				this.mEngine.getVertexBufferObjectManager());

		/*
		 * // Text "Progress" final Sprite bonusBG = new Sprite(10,
		 * CAMERA_HEIGHT - 55, this.mProgressStaticBGTextureRegion,
		 * this.mEngine.getVertexBufferObjectManager());
		 * this.mMainScene.attachChild(bonusBG);
		 * 
		 * // Progress Bar (+background) final Sprite bonus = new Sprite(5,
		 * CAMERA_HEIGHT - 20, this.mProgressBGTextureRegion,
		 * this.mEngine.getVertexBufferObjectManager());
		 * this.mMainScene.attachChild(bonus); this.mProgress = new Sprite(7,
		 * CAMERA_HEIGHT - 18, this.mProgressTextureRegion,
		 * this.mEngine.getVertexBufferObjectManager());
		 */
		this.mProgress.setWidth(0);
		this.mMainScene.attachChild(this.mProgress);
	}

	private void initMusic() {
		MusicFactory.setAssetBasePath("raw/");
		try {
			mMusic = MusicFactory.createMusicFromAsset(
					this.mEngine.getMusicManager(), this, "music.ogg");
			this.mMusic.setLooping(true);
			this.mMusic.setVolume(50);
			if (_appPrefs.getMusic())
				this.mMusic.play();
		} catch (final IOException e) {
			// Debug.e("mGoodMusic Error", e);
		}

	}

	private void initSound() {
		try {
			this.mSwapErrorSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this,
					"raw/illegal_move.ogg");
			this.mFallSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "raw/drop1.ogg");
			this.mRemoveSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "raw/remove.ogg");
			this.mStartingSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "raw/nextlevel.ogg");
			this.mSwapErrorSound.setVolume(50);
			this.mFallSound.setVolume(50);
			this.mRemoveSound.setVolume(50);
			this.mStartingSound.setVolume(50);
		} catch (final IOException e) {
			// Debug.e("mGoodMusic Error", e);
		}
	}

	private void onStarting() {
		//
		if (_appPrefs.getSound())
			this.mStartingSound.play();
	}

	private void prepareGame() {
		this.mMainScene.registerUpdateHandler(new TimerHandler(1.0f,
				new ITimerCallback() {
					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						try {
							mMainScene.unregisterUpdateHandler(pTimerHandler);
							JewelsArcade.this.mGameRunning = true;
						} catch (Exception e) {
						}
					}
				}));
	}

	/**
	 * Progress timer update
	 */
	private void adjustModel() {
		//
		this.mProgress.setWidth(CAMERA_WIDTH);
		this.mMainScene
				.registerUpdateHandler(mTimerHandlerProgress = new TimerHandler(
						0.5f, true, new ITimerCallback() {
							@Override
							public void onTimePassed(TimerHandler pTimerHandler) {
								try {
									if (JewelsArcade.this.mGameRunning) {
										if (mProgress.getWidth() > 0.0f) {
											float left = 0.5f;
											left += 0.04 * mChapter;
											mProgress.setWidth(mProgress
													.getWidth() - left);
										} else {
											/*
											 * SwarmLeaderboard.
											 * submitScoreAndShowLeaderboard(
											 * TOP_LEADERBOARD_ID, mTotalScore);
											 */
											toMenuView(false);
											// Message message1 = new Message();
											// message1.what = 4;
											// handler.sendMessage(message1);
											// Message message = new Message();
											// message.what = 1;
											// handler.sendMessage(message);
										}
									}
								} catch (Exception e) {
								}
							}
						}));
	}

	private void gameLoop() {
		this.mMainScene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(float pSecondsElapsed) {

				try {
					if (JewelsArcade.this.mGameRunning) {
						switch (STATE) {
						case MOVE_UP:
							moveUp();
							break;
						case MOVE_DOWN:
							moveDown();
							break;
						case MOVE_LEFT:
							moveLeft();
							break;
						case MOVE_RIGHT:
							moveRight();
							break;
						case CHECK:

							if (!mFlagBombAnimated) {
								checkStarAnimation();
							}

							if (!mFlagFlowerAnimated) {
								checkFlowerAnimation();
							}

							if (!mFlagRuaAnimated) {
								checkRuaAnimation();
							}

							// checkMapDead();
							// Mark as Flag
							if (mFlagColor) {
								mFlagColor = false;
								addProgress();
							}
								
							// Set jewels state to STATE_SCALEINT
							removeHorizontal(); //
							// Set jewels state to STATE_SCALEINT
							removeVrtical(); //

							// Check if the Bomb has been activated
							if (mFlagBomb) {
								executeBombAnimation();
								// removeBomb();
								// mBombActiveCol = -2;
								// mBombActiveRow = -2;
								mFlagBomb = false;
								mBombExecution = true;
							}

							// Check if the Lightning has been activated
							if (mFlagLight) {
								executeLightning();
								mFlagLight = false;
								mLightExecution = true;
							}
							// Check if jewels have state STATE_SCALEINT, and
							// set to FALL
							changeState();
							break;
						case FALL:
							// find all jewels with state STATE_SCALEINT and set
							// scale
							refreshScale(); //

							if (mBrickCount <= 0)							
								startNewChapter();

							// Drop down jewels, and fill empty spots
							fillEmpty(); //
							break;
						case DEAD:
							JewelsArcade.this.mGameRunning = false;
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);
							Message msg1 = new Message();
							msg1.what = 1;
							handler.sendMessage(msg1);
							break;
						default:
							break;
						}
					}
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * Timer for updating the hints. Mark as 3 and display the hint in
	 * Handler();
	 */
	private void autoTips() {
		this.mMainScene
				.registerUpdateHandler(mTimerHandlerHint = new TimerHandler(1f,
						true, new ITimerCallback() {
							@Override
							public void onTimePassed(TimerHandler pTimerHandler) {
								if (mGameRunning) {
									if (STATE == CHECK) {
										mTime++;
										if (mTime >= 10) {
											//
											Message msg = new Message();
											msg.what = 3;
											handler.sendMessage(msg);
											mTime = 0;
										}
									} else {
										mTime = 0;
									}
								}
							}
						}));
	}

	private void moveUp() {
		if (mIsSwaping) {
			if (moveValue < CELL_HEIGHT) {
				moveValue += SPEED;
				final float x = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getX();
				final float curY = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getY();
				final float lastY = this.mSpriteList.get(
						getSpriteArrayPos(mLastRow, mLastCol)).getY();
				this.mSpriteList.get(getSpriteArrayPos(mCurRow, mCurCol))
						.setPosition(x, curY + SPEED);
				this.mSpriteList.get(getSpriteArrayPos(mLastRow, mLastCol))
						.setPosition(x, lastY - SPEED);
			} else {
				swapJewels(mCurRow, mCurCol, mLastRow, mLastCol);
				STATE = CHECK;
				moveValue = 0;
				mIsSwaping = false;
				this.mLastRow = -2;
				this.mLastCol = -2;
			}
		} else {
			if (moveValue < CELL_HEIGHT) {
				moveValue += SPEED;
				final float x = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getX();
				final float curY = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getY();
				final float lastY = this.mSpriteList.get(
						getSpriteArrayPos(mLastRow, mLastCol)).getY();
				this.mSpriteList.get(getSpriteArrayPos(mCurRow, mCurCol))
						.setPosition(x, curY + SPEED);
				this.mSpriteList.get(getSpriteArrayPos(mLastRow, mLastCol))
						.setPosition(x, lastY - SPEED);
			} else {
				swapJewels(mCurRow, mCurCol, mLastRow, mLastCol);
				if (isSwapFall()) { // Check If there is 3+ or special
					if (_appPrefs.getSound())
						this.mFallSound.play();
					STATE = CHECK;
					this.mLastRow = -2;
					this.mLastCol = -2;
				} else {
					if (_appPrefs.getSound())
						this.mSwapErrorSound.play();
					mIsSwaping = true;
				}
				moveValue = 0;
			}
		}
	}

	/**
* 
*/
	private void moveDown() {
		if (mIsSwaping) {
			if (moveValue < CELL_HEIGHT) {
				moveValue += SPEED;
				final float x = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getX();
				final float curY = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getY();
				final float lastY = this.mSpriteList.get(
						getSpriteArrayPos(mLastRow, mLastCol)).getY();
				this.mSpriteList.get(getSpriteArrayPos(mCurRow, mCurCol))
						.setPosition(x, curY - SPEED);
				this.mSpriteList.get(getSpriteArrayPos(mLastRow, mLastCol))
						.setPosition(x, lastY + SPEED);
			} else {
				swapJewels(mCurRow, mCurCol, mLastRow, mLastCol);
				STATE = CHECK;
				moveValue = 0;
				mIsSwaping = false;
				this.mLastRow = -2;
				this.mLastCol = -2;
			}
		} else {
			if (moveValue < CELL_HEIGHT) {
				moveValue += SPEED;
				final float x = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getX();
				final float curY = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getY();
				final float lastY = this.mSpriteList.get(
						getSpriteArrayPos(mLastRow, mLastCol)).getY();
				this.mSpriteList.get(getSpriteArrayPos(mCurRow, mCurCol))
						.setPosition(x, curY - SPEED);
				this.mSpriteList.get(getSpriteArrayPos(mLastRow, mLastCol))
						.setPosition(x, lastY + SPEED);
			} else {
				swapJewels(mCurRow, mCurCol, mLastRow, mLastCol);
				if (isSwapFall()) { // Check If there is 3+ or special
					if (_appPrefs.getSound())
						this.mFallSound.play();
					STATE = CHECK;
					this.mLastRow = -2;
					this.mLastCol = -2;
				} else {
					if (_appPrefs.getSound())
						this.mSwapErrorSound.play();
					mIsSwaping = true;
				}
				moveValue = 0;
			}
		}
	}

	/**
* 
*/
	private void moveLeft() {
		if (mIsSwaping) {
			if (moveValue < CELL_HEIGHT) {
				moveValue += SPEED;
				final float curX = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getX();
				final float lastX = this.mSpriteList.get(
						getSpriteArrayPos(mLastRow, mLastCol)).getX();
				final float y = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getY();
				this.mSpriteList.get(getSpriteArrayPos(mCurRow, mCurCol))
						.setPosition(curX + SPEED, y);
				this.mSpriteList.get(getSpriteArrayPos(mLastRow, mLastCol))
						.setPosition(lastX - SPEED, y);
			} else {
				swapJewels(mCurRow, mCurCol, mLastRow, mLastCol);
				STATE = CHECK;
				moveValue = 0;
				mIsSwaping = false;
				this.mLastRow = -2;
				this.mLastCol = -2;
			}
		} else {
			if (moveValue < CELL_HEIGHT) {
				moveValue += SPEED;
				final float curX = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getX();
				final float lastX = this.mSpriteList.get(
						getSpriteArrayPos(mLastRow, mLastCol)).getX();
				final float y = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getY();
				this.mSpriteList.get(getSpriteArrayPos(mCurRow, mCurCol))
						.setPosition(curX + SPEED, y);
				this.mSpriteList.get(getSpriteArrayPos(mLastRow, mLastCol))
						.setPosition(lastX - SPEED, y);
			} else {
				swapJewels(mCurRow, mCurCol, mLastRow, mLastCol);
				if (isSwapFall()) {
					if (_appPrefs.getSound())
						this.mFallSound.play();
					STATE = CHECK;
					this.mLastRow = -2;
					this.mLastCol = -2;
				} else {
					if (_appPrefs.getSound())
						this.mSwapErrorSound.play();
					mIsSwaping = true;
				}
				moveValue = 0;
			}
		}
	}

	/**
* 
*/
	private void moveRight() {
		if (mIsSwaping) {
			if (moveValue < CELL_HEIGHT) {
				moveValue += SPEED;
				final float curX = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getX();
				final float lastX = this.mSpriteList.get(
						getSpriteArrayPos(mLastRow, mLastCol)).getX();
				final float y = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getY();
				this.mSpriteList.get(getSpriteArrayPos(mCurRow, mCurCol))
						.setPosition(curX - SPEED, y);
				this.mSpriteList.get(getSpriteArrayPos(mLastRow, mLastCol))
						.setPosition(lastX + SPEED, y);
			} else {
				swapJewels(mCurRow, mCurCol, mLastRow, mLastCol);
				STATE = CHECK;
				moveValue = 0;
				mIsSwaping = false;
				this.mLastRow = -2;
				this.mLastCol = -2;
			}
		} else {
			if (moveValue < CELL_HEIGHT) {
				moveValue += SPEED;
				final float curX = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getX();
				final float lastX = this.mSpriteList.get(
						getSpriteArrayPos(mLastRow, mLastCol)).getX();
				final float y = this.mSpriteList.get(
						getSpriteArrayPos(mCurRow, mCurCol)).getY();
				this.mSpriteList.get(getSpriteArrayPos(mCurRow, mCurCol))
						.setPosition(curX - SPEED, y);
				this.mSpriteList.get(getSpriteArrayPos(mLastRow, mLastCol))
						.setPosition(lastX + SPEED, y);
			} else {
				swapJewels(mCurRow, mCurCol, mLastRow, mLastCol);
				if (isSwapFall()) {
					if (_appPrefs.getSound())
						this.mFallSound.play();
					STATE = CHECK;
					this.mLastRow = -2;
					this.mLastCol = -2;
				} else {
					if (_appPrefs.getSound())
						this.mSwapErrorSound.play();
					mIsSwaping = true;
				}
				moveValue = 0;
			}
		}
	}

	// ===========================================================
	// Update Handler
	// ===========================================================
	/**
	 * Handler update
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 0:
					onStopGame();
					break;
				case 1:
					// submitScore();
					break;
				case 2:
					// showLongMessage("");
					break;
				case 3:
					doTips();
					break;
				case 4:
					// showLongMessage("");
					break;
				case 5:
					showLongMessage("Show Help!!!");
					break;
				default:
					break;
				}
			} catch (Exception e) {
			}
		}
	};

	private void onStopGame() {
		Dialog dialog = new AlertDialog.Builder(this).setTitle("")
				.setMessage("")
				.setPositiveButton("", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				}).setNeutralButton("", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						JewelsArcade.this.mGameRunning = true;
					}
				}).create();
		dialog.show();
	}

	/**
	 * 
	 * @param msg
	 */
	private void showLongMessage(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

	/**
* 
*/
	private void onGameOver() {
		if (this.mScore > 0) {
			toPauseView();
		} else {
			toMenuView(false);
		}
	}

	/**
* 
*/
	/**
* 
*/
	private void toPauseView() {
		JewelsArcade.this.mGameRunning = false;
		if (_appPrefs.getMusic())
			this.mMusic.pause();
		mMainScene.setChildScene(pauseScene(), false, true, true);
	}

	/**
	   * Called when an Activity is created in front of the app (e.g. an
	   * interstitial is shown, or an ad is clicked and launches a new Activity).
	   */
	  @Override
	  public void onPresentScreen(Ad ad) {	    
	  }
	  
	  /**
	   * Called when an ad is clicked and going to start a new Activity that will
	   * leave the application (e.g. breaking out to the Browser or Maps
	   * application).
	   */
	  @Override
	  public void onLeaveApplication(Ad ad) {		  
	  }
	  
	@Override
	  public void onReceiveAd(Ad ad) {
	    if (ad == interstitial) {
	      interstitial.show();
	    }
	  }
	
	/** Called when an ad was not received. */
	  @Override
	  public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode error) {	    
	  }
	  
	  /** Called when an ad is clicked and about to return to the application. */
	  @Override
	  public void onDismissScreen(Ad ad) {
	  } 
	/**
* 
*/
	
	private void toMenuView(boolean isShowLeaderBoard) {
		JewelsArcade.this.mGameRunning = false;
		try {
			JewelsArcade.this.mMainScene
					.unregisterUpdateHandler(mTimerHandlerProgress);
			JewelsArcade.this.mMainScene
					.unregisterUpdateHandler(mTimerHandlerHint);
		} catch (Exception e) {
		}

		Intent intent = new Intent();
		if (isShowLeaderBoard) {
			intent.putExtra("showLeaderBoard", "yes");
		}
		intent.setClass(getApplicationContext(), LockLevels.class);
		startActivity(intent);
		JewelsArcade.this.finish();
	}

	// Menu Scene with Play Button
	private MenuScene pauseScene() {
		final MenuScene pauseGame = new MenuScene(mCamera);
		pauseGame.setBackgroundEnabled(false);
		// Set the background as transparent
		Rectangle Rect = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
				this.mEngine.getVertexBufferObjectManager());
		Rect.setColor(0.0f, 0.0f, 0.0f); // Whatever color you fancy
		Rect.setAlpha(0.5f);
		pauseGame.attachChild(Rect);

		// Add Menu items
		final SpriteMenuItem btnResume = new SpriteMenuItem(1,
				this.pauseResumeRegion,
				this.mEngine.getVertexBufferObjectManager());
		btnResume.setPosition(
				((CAMERA_WIDTH - this.pauseResumeRegion.getWidth()) / 2), 200);
		pauseGame.addMenuItem(btnResume);
		// Pause
		final SpriteMenuItem btnMenu = new SpriteMenuItem(2,
				this.pauseMenuRegion,
				this.mEngine.getVertexBufferObjectManager());
		btnMenu.setPosition(
				((CAMERA_WIDTH - this.pauseResumeRegion.getWidth()) / 2), 300);
		pauseGame.addMenuItem(btnMenu);

		final SpriteMenuItem btnSound = new SpriteMenuItem(3,
				this.pauseSoundTextureRegion,
				this.mEngine.getVertexBufferObjectManager());
		btnSound.setPosition(
				(CAMERA_WIDTH / 2 - this.pauseMenuRegion.getWidth() / 2), 390);
		pauseGame.addMenuItem(btnSound);

		final SpriteMenuItem btnMusic = new SpriteMenuItem(4,
				this.pauseMusicTextureRegion,
				this.mEngine.getVertexBufferObjectManager());
		btnMusic.setPosition(
				(2 * (CAMERA_WIDTH / 3) - this.pauseMusicTextureRegion
						.getWidth() / 2), 390);
		pauseGame.addMenuItem(btnMusic);

		pauseGame.setOnMenuItemClickListener(this);
		return pauseGame;
	}

	// Menu Scene with Play Button
	private MenuScene nextLevelScene() {		
	    
		final MenuScene nextLevel = new MenuScene(mCamera);
			
		float topPadding = CAMERA_HEIGHT / 4;
		// Background
		Sprite bg = new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
				mBackGroundNextLevelTextureRegion,
				this.mEngine.getVertexBufferObjectManager());

		nextLevel.setBackground(new SpriteBackground(bg));

		ITexture mLevelUpFontTexture = new BitmapTextureAtlas(
				getTextureManager(), 512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
//		Font mLevelUpFont = FontFactory.createFromAsset(this.getFontManager(),
//				mLevelUpFontTexture, this.getAssets(), "UTM Loko.ttf",
//				100, true, Color.WHITE);
		StrokeFont mLevelUpFont = FontFactory.createStrokeFromAsset(this.getFontManager(), mLevelUpFontTexture, this.getAssets(), "UTM Loko.ttf", 100, true, Color.parseColor("#ffde7d"), 4, Color.parseColor("#99742e"));
		this.mEngine.getTextureManager().loadTexture(mLevelUpFontTexture);
		this.mEngine.getFontManager().loadFont(mLevelUpFont);

		float centerX = (CAMERA_WIDTH - FontUtils.measureText(mLevelUpFont, "level up"))/ 2;
		
		Text mLevelUpText = new Text(centerX, topPadding,
				mLevelUpFont, "level up",
				this.mEngine.getVertexBufferObjectManager());
		mLevelUpText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		mLevelUpText.setAlpha(1);
		mLevelUpText.setScaleY(1.5f);

		nextLevel.attachChild(mLevelUpText);
		
		
		// Your Score

		ITexture mYourScoreFontTexture = new BitmapTextureAtlas(
				getTextureManager(), 512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		Font mYourScoreFont = FontFactory.createFromAsset(
				this.getFontManager(), mYourScoreFontTexture, this.getAssets(),
				"SuperMario256.ttf", 28, true, Color.parseColor("#2bf1ca"));
		this.mEngine.getTextureManager().loadTexture(mYourScoreFontTexture);
		this.mEngine.getFontManager().loadFont(mYourScoreFont);				
		
		centerX = (CAMERA_WIDTH - FontUtils.measureText(mYourScoreFont, "Your Score: " + String.valueOf(this.mScore)) )/ 2;
		Text mYourScoreText = new Text(centerX, topPadding
				+ mLevelUpText.getHeight() + 20, mYourScoreFont, "", 50,
				this.mEngine.getVertexBufferObjectManager());
		
		mYourScoreText.setWidth(centerX);
		mYourScoreText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		mYourScoreText.setAlpha(1);

		nextLevel.attachChild(mYourScoreText);

		mYourScoreText.setText("Your Score: " + String.valueOf(this.mScore));

		// Best Score

		ITexture mBestScoreFontTexture = new BitmapTextureAtlas(
				getTextureManager(), 512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		Font mBestScoreFont = FontFactory.createFromAsset(
				this.getFontManager(), mBestScoreFontTexture, this.getAssets(),
				"SuperMario256.ttf", 28, true, Color.parseColor("#2bf1ca"));
		this.mEngine.getTextureManager().loadTexture(mBestScoreFontTexture);
		this.mEngine.getFontManager().loadFont(mBestScoreFont);
		
		centerX = (CAMERA_WIDTH - FontUtils.measureText(mBestScoreFont, "Best Score: " + String.valueOf(this.mGoalScore)) )/ 2;
		
		Text mBestScoreText = new Text(centerX, topPadding
				+ mLevelUpText.getHeight() + mYourScoreText.getHeight() + 40,
				mBestScoreFont, "", 50,
				this.mEngine.getVertexBufferObjectManager());
		mBestScoreText.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		mBestScoreText.setAlpha(1);

		nextLevel.attachChild(mBestScoreText);

		mBestScoreText
				.setText("Best Score: " + String.valueOf(this.mGoalScore));

		// Add Menu items
		final SpriteMenuItem btnContinue = new SpriteMenuItem(5,
				this.nextLevelContinueRegion,
				this.mEngine.getVertexBufferObjectManager());
		btnContinue.setPosition(
				((CAMERA_WIDTH - this.nextLevelContinueRegion.getWidth()) / 2), topPadding
				+ mLevelUpText.getHeight() + mYourScoreText.getHeight() + mBestScoreText.getHeight() + 90);
		nextLevel.addMenuItem(btnContinue);

		final SpriteMenuItem btnSubmit = new SpriteMenuItem(6,
				this.nextLevelSubmitRegion,
				this.mEngine.getVertexBufferObjectManager());
		btnSubmit.setPosition(
				((CAMERA_WIDTH - this.nextLevelSubmitRegion.getWidth()) / 2), topPadding
				+ mLevelUpText.getHeight() + mYourScoreText.getHeight() + mBestScoreText.getHeight() + btnContinue.getHeight() + 120);
		nextLevel.addMenuItem(btnSubmit);
		
		nextLevel.setOnMenuItemClickListener(this);

		return nextLevel;
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case 1:
			if (mMainScene.hasChildScene()) {
				mMainScene.clearChildScene();
				JewelsArcade.this.mGameRunning = true;
			}
			return true;
		case 2:
			if (mMainScene.hasChildScene()) {
				mMainScene.clearChildScene();
				JewelsArcade.this.toMenuView(false);
			}
			return true;
		case 3:
			_appPrefs.setSound(!_appPrefs.getSound());
			this.pauseSoundTextureRegion = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(this.pauseSoundTexture, this, _appPrefs
							.getSound() ? "soundon.png" : "soundoff.png", 0, 0);
			return true;
		case 4:
			_appPrefs.setMusic(!_appPrefs.getMusic());
			this.pauseMusicTextureRegion = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(this.pauseMusicTexture, this, _appPrefs
							.getMusic() ? "musicon.png" : "musicoff.png", 0, 0);
			if (_appPrefs.getMusic())
				this.mMusic.play();
			else
				this.mMusic.pause();
			return true;
		case 5:
			if (mMainScene.hasChildScene()) {
				mMainScene.clearChildScene();								
				
				this.mChapter += 1;
				_appPrefs.setChapter(this.mChapter);								
				
				int maxUnlockLevel = _appPrefs.getUnlockLevels();
				if (maxUnlockLevel <= this.mChapter)
					_appPrefs.setUnlockLevels(this.mChapter);
				
				this.mScore = 0;
				Intent intent = new Intent();
				intent.putExtra("mode", "next");
				intent.setClass(this.getApplicationContext(), JewelsArcade.class);
				startActivity(intent);
				overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
			}
			return true;
		case 6:			
			Swarm.init(JewelsArcade.this, SWARM_APP_ID, SWARM_APP_KEY);
			SwarmLeaderboard.submitScoreAndShowLeaderboard(leaderboardLevels[mChapter-1], this.mScore);
			return true;
		default:
			return false;
		}
	}

	// ===========================================================
	// Methods for Sprite Jewel handling
	// ===========================================================
	private int getSpriteArrayPos(int row, int col) {
		return row * CELLS_HORIZONTAL + col;
	}

	private int getRow(Sprite jewel) {
		return (int) (jewel.getY() - PADDINGY) / CELL_HEIGHT;
	}

	private int getCol(Sprite jewel) {
		return (int) (jewel.getX() - PADDINGX) / CELL_WIDTH;
	}

	private void setSpritePosition(int row, int col) {
		mSpriteList.get(row * CELLS_HORIZONTAL + col).setPosition(
				col * CELL_WIDTH + PADDINGX, row * CELL_HEIGHT + PADDINGY);
	}

	private void setBrickPosition(int row, int col) {
		mBrickList.get(row * CELLS_HORIZONTAL + col).setPosition(
				col * CELL_WIDTH + PADDINGX, row * CELL_HEIGHT + PADDINGY);
	}

	private void doScale(int row, int col) {

		if (this.mSpriteState[row][col] == STATE_SCALEINT) {
			float alpha = this.mSpriteList.get(getSpriteArrayPos(row, col))
					.getAlpha();

			this.mSpriteList.get(getSpriteArrayPos(row, col)).setBlendFunction(
					GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			this.mSpriteList.get(getSpriteArrayPos(row, col)).setColor(1, 1, 1);

			if (alpha > 0.5f) {
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setScale(0.7f);
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setAlpha(0.5f);
			} else if (alpha > 0.4f) {
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setScale(0.7f);
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setAlpha(0.4f);
			} else if (alpha > 0.3f) {
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setScale(0.7f);
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setAlpha(0.3f);
			} else if (alpha > 0.2f) {
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setScale(0.7f);
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setAlpha(0.2f);
			} else if (alpha > 0.0f) {
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setScale(0.7f);
				this.mSpriteList.get(getSpriteArrayPos(row, col)).setAlpha(0);
				this.mSpriteState[row][col] = STATE_DEAD;
			} else {
				this.mSpriteList.get(getSpriteArrayPos(row, col))
						.setScale(0.7f);
				this.mSpriteList.get(getSpriteArrayPos(row, col)).setAlpha(0);
				this.mSpriteState[row][col] = STATE_DEAD;
			}
		}// end if
	}

	/**
	 * Swap the jewels in all places
	 * 
	 * @param row
	 *            Source row
	 * @param col
	 *            Source col
	 * @param row1
	 *            Target row
	 * @param col1
	 *            Target col
	 */
	private void swapJewels(int row, int col, int row1, int col1) {
		// Swap Sprite
		Sprite tempS = this.mSpriteList.get(getSpriteArrayPos(row, col));
		this.mSpriteList.set(getSpriteArrayPos(row, col),
				this.mSpriteList.get(getSpriteArrayPos(row1, col1)));
		this.mSpriteList.set(getSpriteArrayPos(row1, col1), tempS);

		// Swap Entity
		// this.mMainScene.getLayer(LAYER_JEWELS).swapEntities(mSpriteEntityPos[row][col],
		// mSpriteEntityPos[row1][col1]);

		// Swap Position
		setSpritePosition(row, col);
		setSpritePosition(row1, col1);

		// Swap State
		int state = this.mSpriteState[row][col];
		this.mSpriteState[row][col] = this.mSpriteState[row1][col1];
		this.mSpriteState[row1][col1] = state;

		// Swap Style
		int style = this.mSpriteStyle[row][col];
		this.mSpriteStyle[row][col] = this.mSpriteStyle[row1][col1];
		this.mSpriteStyle[row1][col1] = style;

		// Swap Entity position
		// int ePos = this.mSpriteEntityPos[row][col];
		// this.mSpriteEntityPos[row][col] = this.mSpriteEntityPos[row1][col1];
		// this.mSpriteEntityPos[row1][col1] = ePos;
	}

	/**
	 * Create Random Style Jewel and add it to the matrix array
	 * 
	 * @param row
	 * @param col
	 * @param state
	 */
	private void addRandomJewel(int row, int col, int state) {
		int style = 0;
		Sprite jewelSprite = null;
		ArrayList<Integer> randomStyle = new ArrayList<Integer>();
		for (int i = 0; i < mJewelTextureRegion.length; i++)
			randomStyle.add(i);

		// Check if there are "bombs"
		if (mJewelBombs > 0)
			randomStyle.add(mStyleBomb); // Bomb Style
		if (mJewelColor > 0)
			randomStyle.add(mStyleColor); // Color Style
		if (mJewelLightning > 0)
			randomStyle.add(mStyleLight); // Lightning Style

		// Get the random jewel style
		style = randomStyle.get(MathUtils.random(0, randomStyle.size() - 1));

		// Check if there are "bombs"
		if (style == mStyleBomb) // Bomb Style
		{
			style = mStyleBomb; // Bomb Style
			jewelSprite = new JewelCell((col * CELL_WIDTH),
					(row * CELL_HEIGHT), mJewelBombTextureRegion, CELL_WIDTH,
					CELL_HEIGHT, PADDINGX, PADDINGY,
					this.mEngine.getVertexBufferObjectManager());

			// new JewelSprite(row, col, mJewelBombTextureRegion,
			// CELL_WIDTH, CELL_HEIGHT, PADDINGX, PADDINGY, state);
			mJewelBombs = mJewelBombs - 1;
		} else if (style == mStyleColor) // Color Style
		{
			style = mStyleColor; // Color Style
			jewelSprite = new JewelCell((col * CELL_WIDTH),
					(row * CELL_HEIGHT), mJewelColorTextureRegion, CELL_WIDTH,
					CELL_HEIGHT, PADDINGX, PADDINGY,
					this.mEngine.getVertexBufferObjectManager());

			// jewelSprite = new JewelSprite(row, col, mJewelColorTextureRegion,
			// CELL_WIDTH, CELL_HEIGHT, PADDINGX, PADDINGY, state);
			mJewelColor = mJewelColor - 1;
		} else if (style == mStyleLight) // Lightning Style
		{
			style = mStyleLight; // Lightning Style
			jewelSprite = new JewelCell((col * CELL_WIDTH),
					(row * CELL_HEIGHT), mJewelLightTextureRegion, CELL_WIDTH,
					CELL_HEIGHT, PADDINGX, PADDINGY,
					this.mEngine.getVertexBufferObjectManager());

			// new JewelSprite(row, col, mJewelLightTextureRegion,
			// CELL_WIDTH, CELL_HEIGHT, PADDINGX, PADDINGY, state);
			mJewelLightning = mJewelLightning - 1;
		} else // Normal jewel
		{
			style = MathUtils.random(0, mJewelTextureRegion.length - 1);
			jewelSprite = new JewelCell((col * CELL_WIDTH),
					(row * CELL_HEIGHT), mJewelTextureRegion[style],
					CELL_WIDTH, CELL_HEIGHT, PADDINGX, PADDINGY,
					this.mEngine.getVertexBufferObjectManager());

			// new JewelSprite(row, col, mJewelTextureRegion[style],
			// CELL_WIDTH, CELL_HEIGHT, PADDINGX, PADDINGY, state);
		}

		// Add to the list
		if (this.mSpriteList.size() <= getSpriteArrayPos(row, col))
			this.mSpriteList.add(getSpriteArrayPos(row, col), jewelSprite);
		else
			this.mSpriteList.set(getSpriteArrayPos(row, col), jewelSprite);

		this.mSpriteState[row][col] = state;
		this.mSpriteStyle[row][col] = style;
	}

	/**
	 * Create Random Style Jewel and add it to the matrix array
	 * 
	 * @param row
	 * @param col
	 * @param state
	 */
	private void addBrick(int row, int col, int style) {
		Sprite brickSprite = null;
		// ArrayList<Integer> randomStyle = new ArrayList<Integer>();
		// for (int i = 0; i < mBrickTextureRegion.length; i++)
		// randomStyle.add(i);

		// Get the random jewel style
		// style = randomStyle.get(MathUtils.random(0, randomStyle.size() - 1));

		// style = MathUtils.random(0, mBrickTextureRegion.length - 1);
		brickSprite = new JewelCell((col * CELL_WIDTH), (row * CELL_HEIGHT),
				mBrickTextureRegion[style], CELL_WIDTH, CELL_HEIGHT, PADDINGX,
				PADDINGY, this.mEngine.getVertexBufferObjectManager());

		// Add to the list
		if (this.mBrickList.size() <= getSpriteArrayPos(row, col))
			this.mBrickList.add(getSpriteArrayPos(row, col), brickSprite);
		else
			this.mBrickList.set(getSpriteArrayPos(row, col), brickSprite);

		// this.mBrickState[row][col] = state;
		this.mBrickStyle[row][col] = style;
	}

	/**
	 * check if the current jewel is 3+ horizontaly
	 * 
	 * @param jewel
	 * @return int
	 */
	synchronized private int checkHorizontal(int row, int col) {
		ArrayList<Sprite> deadArrayList = new ArrayList<Sprite>();
		Sprite jewel = this.mSpriteList.get(getSpriteArrayPos(row, col));
		int ret = 0;
		int curStyle = -1;
		int style = -1;
		if (jewel != null) {
			final int curRow = getRow(jewel);
			int curCol = getCol(jewel);
			curStyle = mSpriteStyle[row][col]; // Get the Jewel Style
			style = curStyle;

			if (curStyle == mStyleBomb) {
				mFlagBomb = true;
				mBombActiveCol = curCol;
				mBombActiveRow = curRow;
				ret = 3;
			}
			// Check if the jewel has "Lightning" style, and disregard checking
			if (curStyle == mStyleLight) {
				mFlagLight = true;
				mLightActiveCol = curCol;
				mLightActiveRow = curRow;
				ret = 3;
			}
			// Check if the jewel has "Color" style, and disregard checking
			if (curStyle == mStyleColor) {
				mFlagColor = true;
				mColorActiveCol = curCol;
				mColorActiveRow = curRow;
			}

			//
			while ((curCol - 1) >= 0) {
				// if (this.mSpriteList.get(getSpriteArrayPos(curRow, curCol -
				// 1)) != null) {
				if (this.mSpriteList.size() > getSpriteArrayPos(row, curCol - 1)
						&& this.mSpriteList.get(getSpriteArrayPos(curRow,
								curCol - 1)) != null) {
					if (this.mSpriteStyle[curRow][curCol - 1] < 10) {
						if (curStyle == this.mSpriteStyle[curRow][curCol - 1]) {
							deadArrayList
									.add(this.mSpriteList
											.get(getSpriteArrayPos(curRow,
													curCol - 1)));
						} else if (mFlagColor && curStyle == mStyleColor) // Enter
																			// only
																			// once
						{
							deadArrayList
									.add(this.mSpriteList
											.get(getSpriteArrayPos(curRow,
													curCol - 1)));
							curStyle = this.mSpriteStyle[curRow][curCol - 1];
						} else
							curCol = 0;
					} else
						curCol = 0;
				}
				curCol -= 1;
			}
			// reset the row
			curCol = getCol(jewel);

			// Check if something is already added to the list
			if (mFlagColor && style == mStyleColor) {
				// Check if there are 2 or more jewels on the left detected
				// and if the style on the first jewel on the right is equal to
				// the left one
				// If NOT reset the 'Color' Jewel and clear the list, prepare
				// for right check
				if (deadArrayList.size() < 2
						&& (curCol >= (CELLS_HORIZONTAL - 1) || curStyle != this.mSpriteStyle[curRow][curCol + 1])) {
					deadArrayList.clear();
					curStyle = mStyleColor;
				}
			}

			deadArrayList.add(this.mSpriteList.get(getSpriteArrayPos(curRow,
					curCol)));

			//
			while ((curCol + 1) < CELLS_HORIZONTAL) {
				// if (this.mSpriteList.get(getSpriteArrayPos(curRow, curCol+1))
				// != null) {
				if (this.mSpriteList.size() > getSpriteArrayPos(row, curCol + 1)
						&& this.mSpriteList.get(getSpriteArrayPos(curRow,
								curCol + 1)) != null) {
					if (this.mSpriteStyle[curRow][curCol + 1] < 10) {
						if (curStyle == this.mSpriteStyle[curRow][curCol + 1]) {
							deadArrayList
									.add(this.mSpriteList
											.get(getSpriteArrayPos(curRow,
													curCol + 1)));
						} else if (mFlagColor && curStyle == mStyleColor
								&& deadArrayList.size() < 2) // Enter only once
						{
							deadArrayList
									.add(this.mSpriteList
											.get(getSpriteArrayPos(curRow,
													curCol + 1)));
							curStyle = this.mSpriteStyle[curRow][curCol + 1];
						} else
							curCol = CELLS_HORIZONTAL;
					} else
						curCol = CELLS_HORIZONTAL;
				}
				curCol += 1;
			}
		}

		// Change the style of the 'color' jewel so it can be removed
		if (mFlagColor && deadArrayList.size() > 2) {
			this.mSpriteStyle[mColorActiveRow][mColorActiveCol] = curStyle;
		}

		if (ret > 0)
			return ret;
		return deadArrayList.size();
	}

	/**
	 * 
	 * @param jewel
	 * @return int
	 */
	synchronized private int checkVertical(int row, int col) {
		ArrayList<Sprite> deadArrayList = new ArrayList<Sprite>();
		Sprite jewel = this.mSpriteList.get(getSpriteArrayPos(row, col));
		int ret = 0;
		int curStyle = -1;
		int style = -1;

		if (jewel != null) {
			int curRow = getRow(jewel);
			int curCol = getCol(jewel);
			curStyle = mSpriteStyle[row][col]; // Get the Jewel Style
			style = curStyle;

			//
			while ((curRow - 1) >= 0) {
				// if (this.mSpriteList.get(getSpriteArrayPos(curRow -
				// 1,curCol)) != null) {
				if (this.mSpriteList.size() > getSpriteArrayPos(curRow - 1,
						curCol)
						&& this.mSpriteList.get(getSpriteArrayPos(curRow - 1,
								curCol)) != null) {
					if (this.mSpriteStyle[curRow - 1][curCol] < 10) {
						if (curStyle == this.mSpriteStyle[curRow - 1][curCol]) {
							deadArrayList
									.add(this.mSpriteList
											.get(getSpriteArrayPos(curRow - 1,
													curCol)));
						} else if (mFlagColor && curStyle == mStyleColor) // Enter
																			// only
																			// once
						{
							deadArrayList
									.add(this.mSpriteList
											.get(getSpriteArrayPos(curRow - 1,
													curCol)));
							curStyle = this.mSpriteStyle[curRow - 1][curCol];
						} else
							curRow = 0;
					} else
						curRow = 0;
				}
				curRow -= 1;
			}

			// reset the row
			curRow = getRow(jewel); //

			// Check if something is already added to the list
			if (mFlagColor && style == mStyleColor) {
				// Check if there are 2 or more jewels on the left detected
				// and if the style on the first jewel on the right is equal to
				// the left one
				// If NOT reset the 'Color' Jewel and clear the list, prepare
				// for right check
				if (deadArrayList.size() < 2
						&& (curRow >= (CELLS_VERTICAL - 1) || curStyle != this.mSpriteStyle[curRow + 1][curCol])) {
					deadArrayList.clear();
					curStyle = mStyleColor;
				}
			}

			deadArrayList.add(this.mSpriteList.get(getSpriteArrayPos(curRow,
					curCol)));

			while ((curRow + 1) < CELLS_VERTICAL) {//
				// if (this.mSpriteList.get(getSpriteArrayPos(curRow+1,curCol))
				// != null) {
				if (this.mSpriteList.size() > getSpriteArrayPos(curRow + 1,
						curCol)
						&& this.mSpriteList.get(getSpriteArrayPos(curRow + 1,
								curCol)) != null) {
					if (this.mSpriteStyle[curRow + 1][curCol] < 10) {
						if (curStyle == this.mSpriteStyle[curRow + 1][curCol]) {
							deadArrayList
									.add(this.mSpriteList
											.get(getSpriteArrayPos(curRow + 1,
													curCol)));
						} else if (mFlagColor && curStyle == mStyleColor
								&& deadArrayList.size() < 2) // Enter only once
						{
							deadArrayList
									.add(this.mSpriteList
											.get(getSpriteArrayPos(curRow + 1,
													curCol)));
							curStyle = this.mSpriteStyle[curRow + 1][curCol];
						} else
							curRow = CELLS_VERTICAL;
					} else
						curRow = CELLS_VERTICAL;
				}
				curRow += 1;
			}
		}
		// Change the style of the 'color' jewel so it can be removed
		if (mFlagColor && deadArrayList.size() > 2) {
			this.mSpriteStyle[mColorActiveRow][mColorActiveCol] = curStyle;
		}

		if (ret > 0)
			return ret;
		return deadArrayList.size();
	}

	/**
	 * Check if jewels Swap is successfull horizontally or verticaly
	 * 
	 * @return ture/false(/)
	 */
	private boolean isSwapFall() {
		int count = 0;

		// Check if the horizontal (mCur) of one of the jewels is horizontal 3+
		// same style
		if (checkHorizontal(mCurRow, mCurCol) >= 3) {
			count += 1;
		}

		// Check if the horizontal (mLast) of one of the jewels is horizontal 3+
		// same style
		if (checkHorizontal(mLastRow, mLastCol) >= 3) {
			count += 1;
		}
		// Check if the vertical (mCur) of one of the jewels is horizontal 3+
		// same style
		if (checkVertical(mCurRow, mCurCol) >= 3) {
			count += 1;
		}

		// Check if the vertical (mLast) of one of the jewels is horizontal 3+
		// same style
		if (checkVertical(mLastRow, mLastCol) >= 3) {
			count += 1;
		}

		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Set the move direction, when the
	 */
	private void setMoveDirection() {
		if (this.mLastRow == this.mCurRow && this.mLastCol > this.mCurCol) {
			this.STATE = this.MOVE_LEFT;
		}
		if (this.mLastRow == this.mCurRow && this.mLastCol < this.mCurCol) {
			this.STATE = this.MOVE_RIGHT;
		}
		if (this.mLastRow > this.mCurRow && this.mLastCol == this.mCurCol) {
			this.STATE = this.MOVE_UP;
		}
		if (this.mLastRow < this.mCurRow && this.mLastCol == this.mCurCol) {
			this.STATE = this.MOVE_DOWN;
		}
	}

	/**
	 * 
	 * @return true
	 * @return false
	 */
	private boolean isNext() {
		if ((Math.abs(this.mCurRow - this.mLastRow) == 1 && this.mCurCol == this.mLastCol)//
				|| (Math.abs(this.mCurCol - this.mLastCol) == 1 && this.mCurRow == this.mLastRow)) {//
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private String getKey(final int row, final int col) {
		return String.valueOf(row) + ":" + String.valueOf(col);
	}

	/**
	 * 
	 */
	private void addProgress() {
		if (_appPrefs.getSound())
			this.mRemoveSound.play();
		if ((mProgress.getWidth() + mChapterStep) < CAMERA_WIDTH)
			this.mProgress.setWidth(this.mProgress.getWidth() + mChapterStep);
	}

	/**
	 * 
	 * @param fallCount
	 */
	private void addScore(int fallCount) {
		switch (fallCount) {
		case 3:
			this.mScore += 20;
			this.mTotalScore += 20;
			break;
		case 4:
			this.mScore += 40;
			this.mTotalScore += 40;
			break;
		case 5:
			this.mScore += 60;
			this.mTotalScore += 60;
			break;
		case 6:
			this.mScore += 80;
			this.mTotalScore += 80;
			break;
		case 7:
			this.mScore += 100;
			this.mTotalScore += 100;
			break;
		case 8:
			this.mScore += 120;
			this.mTotalScore += 120;
			break;
		case 9: // Bomb
			this.mScore += 120;
			this.mTotalScore += 120;
			break;
		default:
			break;
		}

		// Eliminate 20 jewels continuous can win 1 lighting.
		// Each touch, resets the mJewelContinuousCount;
		mJewelContinuousCount = mJewelContinuousCount + fallCount;
		if (mJewelContinuousCount > 20)
			mJewelLightning = mJewelLightning + 1;

		// Match 5 jewels in 1 line can win color-changing jewels and 2
		// lightings.
		if (fallCount > 4) {
			if (mBombExecution) {
				mBombExecution = false;
			} else if (mLightExecution) {
				mLightExecution = false;
			} else {
				mJewelLightning = mJewelLightning + 2;
				mJewelColor = mJewelColor + 1;
				mJewelContinuousCount = 0;
			}

			// Match 4 jewels in 1 line can win the jewel's bomb and 1 lighting.
		} else if (fallCount > 3) {
			// if (mJewelLightning == 0)
			// mJewelLightning = mJewelLightning + 1;
			// if (mJewelBombs <= 1)
			// mJewelBombs = mJewelBombs + 1;
			//
			if (mBombExecution) {
				mBombExecution = false;
			} else if (mLightExecution) {
				mLightExecution = false;
			} else {
				mJewelLightning = mJewelLightning + 1;
				mJewelBombs = mJewelBombs + 1;
				mJewelContinuousCount = 0;
			}
			// combo 1 : match 6 jewels win 1 bomb
		} else if (mJewelContinuousCount > 5) {
			if (mBombExecution) {
				mBombExecution = false;
			} else if (mLightExecution) {
				mLightExecution = false;
			} else {
				mJewelBombs = mJewelBombs + 1;
			}
		}

		// if (this.mTotalScore < 9999999) {
		this.mScoreValText.setText(String.valueOf(this.mScore));

		// this.mTotalScoreValText.setText(String.valueOf(this.mTotalScore));
		// } else {
		// Toast.makeText(getApplicationContext(), "Restart from 0!!!",
		// Toast.LENGTH_LONG).show();
		// this.mScoreValText.setText(String.valueOf(this.mScore));
		// this.mTotalScoreValText.setText(String.valueOf(this.mTotalScore));
		// }
		// Start New Chapter
		// TODO tam
		// if (mScore >= mGoalScore)

	}

	/**
	 * 
	 */
	private void startNewChapter() {

		if (mChapter >= 12) {
			toMenuView(false);
		}
		JewelsArcade.this.mGameRunning = false;
		try {
			JewelsArcade.this.mMainScene
					.unregisterUpdateHandler(mTimerHandlerProgress);
			JewelsArcade.this.mMainScene
					.unregisterUpdateHandler(mTimerHandlerHint);
		} catch (Exception e) {
		}

		/*
		 * Intent intent = new Intent();
		 * intent.setClass(getApplicationContext(), NextLevel.class);
		 * startActivity(intent); JewelsStar.this.finish();
		 */
	    
		JewelsArcade.this.runOnUiThread(new Runnable()
		{
		    public void run()
		    {
		         // Create ad request
			    AdRequest adRequest = new AdRequest();
		    	
    		    // Begin loading your interstitial
    		    interstitial.loadAd(adRequest);
    	
    		    // Set Ad Listener to use the callbacks below
    		    interstitial.setAdListener(JewelsArcade.this);
		    }
		});
		
		mMainScene.setChildScene(nextLevelScene(), false, true, true);
		
		

	}

	private GotLeaderboardCB callback = new GotLeaderboardCB() {

		public void gotLeaderboard(SwarmLeaderboard leaderboard) {

			// If Swarm returns valid SwarmLeaderboard data
			if (leaderboard != null) {

				leaderboard.getTopScores(DateRange.MONTH, gotScoresCB);

			} 
		}
	};

	private GotScoresCB gotScoresCB = new GotScoresCB() {

		@Override
		public void gotScores(int arg0, List<SwarmLeaderboardScore> list) {

			if (list != null) {
				SwarmLeaderboardScore score = list.get(0);
				mGoalScore = (int) score.score;
				JewelsArcade.this.mGoalScoreValText.setText(String
						.valueOf(JewelsArcade.this.mGoalScore));
			}
		}
	};

	/**
	 * Check for "hints" and add to mDeadArrList with instruction for
	 * up,down,left,right. The acutal checking is done in checkDead method.
	 */
	private void checkStarAnimation() {
		for (int i = 0; i < CELLS_HORIZONTAL; i++) {
			for (int j = 0; j < CELLS_VERTICAL; j++) {
				if (this.mSpriteStyle[i][j] == mStyleBomb) {
					mFlagBombAnimated = true;
					displayAnimatedStar(i, j);
				}

			}
		}
	}

	/**
	 * Check for "hints" and add to mDeadArrList with instruction for
	 * up,down,left,right. The acutal checking is done in checkDead method.
	 */
	private void checkFlowerAnimation() {
		for (int i = 0; i < CELLS_HORIZONTAL; i++) {
			for (int j = 0; j < CELLS_VERTICAL; j++) {
				if (this.mSpriteStyle[i][j] == mStyleColor) {
					mFlagFlowerAnimated = true;
					displayAnimatedFlower(i, j);
				}

			}
		}
	}

	/**
	 * Check for "hints" and add to mDeadArrList with instruction for
	 * up,down,left,right. The acutal checking is done in checkDead method.
	 */
	private void checkRuaAnimation() {
		for (int i = 0; i < CELLS_HORIZONTAL; i++) {
			for (int j = 0; j < CELLS_VERTICAL; j++) {
				if (this.mSpriteStyle[i][j] == mStyleLight) {
					mFlagRuaAnimated = true;
					displayAnimatedRua(i, j);
				}

			}
		}
	}

	/**
	 * Check for "hints" and add to mDeadArrList with instruction for
	 * up,down,left,right. The acutal checking is done in checkDead method.
	 */
	private void checkMapDead() {
		int count = 0;
		for (int i = 0; i < CELLS_HORIZONTAL; i++) {
			for (int j = 0; j < CELLS_VERTICAL; j++) {
				if (this.mSpriteStyle[i][j] == mStyleBomb) {
					displayAnimatedStar(i, j);
				}

				if (this.mSpriteState[j][i] == STATE_NORMAL) {
					count++;
				}

			}
		}

		int deadVal = 0;
		if (count == (CELLS_HORIZONTAL * CELLS_VERTICAL)) {
			//
			if (this.mDeadArrList.size() == 0) {
				int i = 0;
				while (i < CELLS_HORIZONTAL) {
					int j = 0;
					while (j < CELLS_VERTICAL) {
						// Get the "hint" value
						deadVal = checkDead(j, i);
						switch (deadVal) {
						case 1:
							this.mDeadArrList.add(getKey(i, j) + "1");
							break;
						case 2:
							this.mDeadArrList.add(getKey(i, j) + "2");
							break;
						case 3:
							this.mDeadArrList.add(getKey(i, j) + "3");
							break;
						case 4:
							this.mDeadArrList.add(getKey(i, j) + "4");
							break;
						default:
							break;
						}
						j += 1;
					}
					i += 1;
				}
				// if(this.mDeadArrList.size() == 0){
				// STATE = DEAD;
				// }
			}
		}
	}

	/**
	 * Check for combinations of 3 or more, for hint
	 */
	private int checkDead(int row, int col) {
		int flag = 0;
		// LEFT
		if ((col - 1) >= 0) {
			// swapJewels(row, col, row, col - 1);
			int v = 0;
			for (v = 1; col - 1 - v >= 0
					&& this.mSpriteStyle[row][col - 1] == this.mSpriteStyle[row][col
							- 1 - v]
					&& this.mSpriteState[row][col - 1] == this.mSpriteState[row][col
							- 1 - v]; v++)
				;
			if (v >= 3) {
				flag = 1;
			}
			// swapJewels(row, col - 1, row, col);
		}
		// RIGHT
		if ((col + 1) < CELLS_HORIZONTAL) {
			// swapJewels(row, col, row, col + 1);
			int v1 = 0;
			for (v1 = 1; col + 1 + v1 < CELLS_HORIZONTAL
					&& this.mSpriteStyle[row][col + 1] == this.mSpriteStyle[row][col
							+ 1 + v1]
					&& this.mSpriteState[row][col + 1] == this.mSpriteState[row][col
							+ 1 + v1]; v1++)
				;
			if (v1 >= 3) {
				flag = 2;
			}
			// swapJewels(row, col + 1, row, col);
		}
		// UP
		if ((row - 1) >= 0) {
			// swapJewels(row, col, row - 1, col);
			int v2 = 0;
			for (v2 = 1; row - 1 - v2 >= 0
					&& this.mSpriteStyle[row - 1][col] == this.mSpriteStyle[row
							- 1 - v2][col]
					&& this.mSpriteState[row - 1][col] == this.mSpriteState[row
							- 1 - v2][col]; v2++)
				;
			if (v2 >= 3) {
				flag = 3;
			}
			// swapJewels(row - 1, col, row, col);
		}
		// DOWN
		if ((row + 1) < CELLS_VERTICAL) {
			// swapJewels(row, col, row + 1, col);
			int v3 = 0;
			for (v3 = 1; row + 1 + v3 < CELLS_VERTICAL
					&& this.mSpriteStyle[row + 1][col] == this.mSpriteStyle[row
							+ 1 + v3][col]
					&& this.mSpriteState[row + 1][col] == this.mSpriteState[row
							+ 1 + v3][col]; v3++)
				;
			if (v3 >= 3) {
				flag = 4;
			}
			// swapJewels(row + 1, col, row, col);
		}
		return flag;
	}

	/**
	 * The Bomb jewel has been "activated" so destroy all jewels around it
	 */
	private void removeBomb() {
		// These values should be set when setting the bomb flag = true;
		int i = mBombActiveRow;
		int j = mBombActiveCol;
		int count = 0;
		if (this.mSpriteState[i][j] == STATE_NORMAL
				&& this.mSpriteStyle[i][j] == mStyleBomb) {

			// this.mSpriteState[i][j] = STATE_SCALEINT;

			for (int n = i - 1; n <= i + 1; n++) {
				for (int m = j - 1; m <= j + 1; m++) {
					if (n >= 0 && n < CELLS_VERTICAL && m >= 0
							&& m < CELLS_HORIZONTAL) {
						this.mSpriteState[n][m] = STATE_SCALEINT;
						count++;
					}
				}
			}
//			this.addProgress();
			this.addScore(count);
		}
	}

	/**
	 * Create the animation listener
	 */
	IAnimationListener mAnimateLightningListener = new IAnimationListener() {

		@Override
		public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
				int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
				int pOldFrameIndex, int pNewFrameIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
				int pRemainingLoopCount, int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
			mLightSprite.setVisible(false);
			removeLightning();
		}
	};

	/**
	 * Create the animation listener
	 */
	IAnimationListener mAnimateBombListener = new IAnimationListener() {

		@Override
		public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
				int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
				int pOldFrameIndex, int pNewFrameIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
				int pRemainingLoopCount, int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
			mBombSprite.setVisible(false);
			removeBomb();
		}
	};

	/**
	 * Create the animation listener
	 */
	IAnimationListener mAnimateJewelDestroyListener = new IAnimationListener() {

		@Override
		public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
				int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
				int pOldFrameIndex, int pNewFrameIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
				int pRemainingLoopCount, int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
			pAnimatedSprite.setVisible(false);
			// mJewelDestroySprite.setVisible(false);
			// fillEmpty();
		}
	};

	/**
	 * Create the animation listener
	 */
	IAnimationListener mAnimateBombAnimatedListener = new IAnimationListener() {

		@Override
		public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
				int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
				int pOldFrameIndex, int pNewFrameIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
				int pRemainingLoopCount, int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
			pAnimatedSprite.setVisible(false);
			mFlagBombAnimated = false;
		}
	};

	/**
	 * Create the animation listener
	 */
	IAnimationListener mAnimateFlowerAnimatedListener = new IAnimationListener() {

		@Override
		public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
				int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
				int pOldFrameIndex, int pNewFrameIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
				int pRemainingLoopCount, int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
			pAnimatedSprite.setVisible(false);
			mFlagFlowerAnimated = false;
		}
	};

	/**
	 * Create the animation listener
	 */
	IAnimationListener mAnimateRuaAnimatedListener = new IAnimationListener() {

		@Override
		public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
				int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
				int pOldFrameIndex, int pNewFrameIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
				int pRemainingLoopCount, int pInitialLoopCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
			pAnimatedSprite.setVisible(false);
			mFlagRuaAnimated = false;
		}
	};

	/**
	 * Execute Animated Star: 1. Run the animation 2. Remove the jewels
	 */
	private void displayAnimatedStar(int row, int col) {

		AnimatedSprite mAnimatedStarSprite = new AnimatedSprite(0, 0,
				CELL_WIDTH, CELL_HEIGHT, mAnimatedStarRegion.deepCopy(),
				this.mEngine.getVertexBufferObjectManager());
		mHUD.attachChild(mAnimatedStarSprite);

		mAnimatedStarSprite.setPosition(PADDINGX + col * CELL_WIDTH, PADDINGY
				+ row * CELL_HEIGHT);
		mAnimatedStarSprite.animate(new long[] { 100, 100, 100, 100, 100, 100,
				100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
				100, 100}, false, mAnimateBombAnimatedListener);
	}

	/**
	 * Execute Animated Flower: 1. Run the animation 2. Remove the jewels
	 */
	private void displayAnimatedFlower(int row, int col) {

		AnimatedSprite mAnimatedFlowerSprite = new AnimatedSprite(0, 0,
				CELL_WIDTH, CELL_HEIGHT, mAnimatedFlowerRegion.deepCopy(),
				this.mEngine.getVertexBufferObjectManager());
		mHUD.attachChild(mAnimatedFlowerSprite);

		mAnimatedFlowerSprite.setPosition(PADDINGX + col * CELL_WIDTH, PADDINGY
				+ row * CELL_HEIGHT);
		mAnimatedFlowerSprite.animate(new long[] { 100, 100, 100, 100, 100,
				100, 100, 100, 100, 100, 100, 100, 100, 100, 100, }, false,
				mAnimateFlowerAnimatedListener);
	}

	/**
	 * Execute Animated Flower: 1. Run the animation 2. Remove the jewels
	 */
	private void displayAnimatedRua(int row, int col) {

		AnimatedSprite mAnimatedFlowerSprite = new AnimatedSprite(0, 0,
				CELL_WIDTH, CELL_HEIGHT, mAnimatedRuaRegion.deepCopy(),
				this.mEngine.getVertexBufferObjectManager());
		mHUD.attachChild(mAnimatedFlowerSprite);

		mAnimatedFlowerSprite.setPosition(PADDINGX + col * CELL_WIDTH, PADDINGY
				+ row * CELL_HEIGHT);
		mAnimatedFlowerSprite.animate(new long[] { 100, 100, 100, 100, 100,
				100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
				100, 100, 100, }, false, mAnimateRuaAnimatedListener);
	}

	/**
	 * Execute Jewel Destroy: 1. Run the animation 2. Remove the jewels
	 */
	private void executeJewelDestroyAnimation(int row, int col) {
		// // if (mJewelDestroySprite == null) {
		// mJewelDestroySprite = new AnimatedSprite(0, 0, CELL_WIDTH,
		// CELL_HEIGHT, mJewelDestroyTextureRegion,
		// this.mEngine.getVertexBufferObjectManager());
		// mJewelDestroySprite.setVisible(false);
		// // this.mMainScene.attachChild(mLightSprite);
		// mHUD.attachChild(mJewelDestroySprite);
		// // }
		//
		// mJewelDestroySprite.setPosition(PADDINGX + col * CELL_WIDTH, PADDINGY
		// + row
		// * CELL_HEIGHT);
		// mJewelDestroySprite.animate(10);
		// mJewelDestroySprite.animate(new long[] { 25, 25, 25, 25, 25, 25, 25,
		// 25 }, 0,
		// 7, 1, mAnimateJewelDestroyListener);
		// mJewelDestroySprite.setVisible(true);

		// if (mJewelDestroySprite == null) {
		AnimatedSprite mJewelDestroySprite = new AnimatedSprite(0, 0,
				CELL_WIDTH, CELL_HEIGHT, mJewelDestroyTextureRegion.deepCopy(),
				this.mEngine.getVertexBufferObjectManager());
		// mJewelDestroySprite.setVisible(false);
		// this.mMainScene.attachChild(mLightSprite);
		mHUD.attachChild(mJewelDestroySprite);
		// }

		mJewelDestroySprite.setPosition(PADDINGX + col * CELL_WIDTH, PADDINGY
				+ row * CELL_HEIGHT);
		mJewelDestroySprite.animate(new long[] { 25, 25, 25, 25, 25, 25, 25,
				25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25}, false,
				mAnimateJewelDestroyListener);
		// mJewelDestroySprite.setVisible(true);
	}

	/**
	 * Execute Bombing: 1. Run the animation 2. Remove the jewels
	 */
	private void executeBombAnimation() {
		if (mBombSprite == null) {
			mBombSprite = new AnimatedSprite(0, 0, CELL_WIDTH * 3,
					CELL_HEIGHT * 3, mBombTextureRegion,
					this.mEngine.getVertexBufferObjectManager());
			mBombSprite.setVisible(false);
			// this.mMainScene.attachChild(mLightSprite);
			mHUD.attachChild(mBombSprite);
		}

		mBombSprite.setPosition(PADDINGX + (mBombActiveCol - 1) * CELL_WIDTH,
				PADDINGY + (mBombActiveRow - 1) * CELL_HEIGHT);
		mBombSprite.animate(new long[] { 25, 25, 25, 25, 25, 25, 25, 25, 25,
				25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25}, false, mAnimateBombListener);
		mBombSprite.setVisible(true);
	}

	/**
	 * Execute Lightning: 1. Run the animation 2. Remove the jewels
	 */
	private void executeLightning() {
		if (mLightSprite == null) {
			mLightSprite = new AnimatedSprite(0, 0, CAMERA_WIDTH - PADDINGX,
					CELL_HEIGHT, mLightTextureRegion,
					this.mEngine.getVertexBufferObjectManager());
			mLightSprite.setVisible(false);
			// this.mMainScene.attachChild(mLightSprite);
			mHUD.attachChild(mLightSprite);
		}

		mLightSprite.setPosition(PADDINGX, PADDINGY + mLightActiveRow
				* CELL_HEIGHT);
		mLightSprite.animate(new long[] { 100, 100, 100, 100, 100, 100, 100,
				100 }, 0, 7, 1, mAnimateLightningListener);
		mLightSprite.setVisible(true);
	}

	/**
	 * The Lightning jewel has been "activated" so destroy all jewels in a row
	 */
	private void removeLightning() {
		// These values should be set when setting the lightning flag = true;
		int i = mLightActiveRow;
		int j = mLightActiveCol;

		if (this.mSpriteState[i][j] == STATE_NORMAL
				&& this.mSpriteStyle[i][j] == mStyleLight) {
			for (int n = 0; n < CELLS_HORIZONTAL; n++) {
				this.mSpriteState[i][n] = STATE_SCALEINT;
			}
//			this.addProgress();
			this.addScore(CELLS_HORIZONTAL);
		}
		mLightActiveCol = -2;
		mLightActiveRow = -2;
	}

	/**
	 * Check the Horizontal jewels 3+ of the same style
	 */
	private void removeHorizontal() {
		int k = 0;
		for (int i = 0; i < CELLS_VERTICAL; i++) {
			for (int j = 0; j < CELLS_HORIZONTAL - 2; j++) {
				if (this.mSpriteState[i][j] == STATE_NORMAL) {
					for (k = 1; j + k < CELLS_HORIZONTAL
							&& this.mSpriteStyle[i][j] == this.mSpriteStyle[i][j
									+ k]
							&& this.mSpriteState[i][j] == this.mSpriteState[i][j
									+ k]; k++)
						;
					if (k >= 3) {
//						this.addProgress();
						this.addScore(k);
						removeVrtical(); // T()
						for (int n = 0; n < k; n++) {
							this.mSpriteState[i][j++] = STATE_SCALEINT;
						}
					}
				}
			}
		}
	}

	/**
	 * Check the Vertical jewels 3+ of the same style
	 */
	private void removeVrtical() {
		int k = 0;
		for (int i = 0; i < CELLS_HORIZONTAL; i++) {
			for (int j = 0; j < CELLS_VERTICAL - 2; j++) {
				if (this.mSpriteState[j][i] == STATE_NORMAL) {
					for (k = 1; j + k < CELLS_VERTICAL
							&& this.mSpriteStyle[j][i] == this.mSpriteStyle[j
									+ k][i]
							&& this.mSpriteState[j][i] == this.mSpriteState[j
									+ k][i]; k++)
						;
					if (k >= 3) {
//						this.addProgress();
						this.addScore(k);
						for (int n = 0; n < k; n++) {
							this.mSpriteState[j++][i] = STATE_SCALEINT;
						}
					}
				}
			}
		}
	}

	/**
* 
*/
	private void changeState() {
		int fallCount = 0;
		for (int i = 0; i < CELLS_HORIZONTAL; i++) {
			for (int j = 0; j < CELLS_VERTICAL; j++) {
				if (this.mSpriteState[j][i] == STATE_SCALEINT) {
					fallCount++;
				}
			}
		}
		if (fallCount > 0) {
			STATE = FALL;
		}
	}

	synchronized private void refreshScale() {
		for (int i = 0; i < CELLS_HORIZONTAL; i++) {
			for (int j = 0; j < CELLS_VERTICAL; j++) {
				if (this.mSpriteState[j][i] == STATE_SCALEINT) {
					// doScale(j, i);
					this.mSpriteList.get(getSpriteArrayPos(j, i))
							.setScale(0.7f);
					this.mSpriteList.get(getSpriteArrayPos(j, i))
							.setAlpha(0.5f);
					this.mSpriteState[j][i] = STATE_DEAD;
				}

				if (this.mSpriteState[j][i] == STATE_DEAD) {

					if (this.mBrickStyle[j][i] == 0) {
						mBrickList.get(getSpriteArrayPos(j, i)).detachSelf();

						int temp = this.mBrickStyle[j][i];
						temp--;
						this.mBrickStyle[j][i] = temp;

						mBrickCount--;
					}

					executeJewelDestroyAnimation(j, i);
					if (mSpriteEntityPos[j][i] != -1) {
						mSpriteEntityPos[j][i] = -1;
					}
					mSpriteList.get(getSpriteArrayPos(j, i)).detachSelf();

				}
			}
		}
	}

	synchronized private void fillEmpty() {
		for (int i = 0; i < CELLS_HORIZONTAL; i++) {
			for (int j = 0; j < CELLS_VERTICAL; j++) {
				if (this.mSpriteState[j][i] == STATE_DEAD) {

					int p = j;
					while ((p - 1) >= 0
							&& this.mSpriteState[p - 1][i] != STATE_DEAD) {
						swapJewels(p - 1, i, p, i);
						p--;
					}

					// After all rows have been shifted down, add new jewel on
					// top
					addRandomJewel(0, i, STATE_NORMAL);
					// setSpritePosition(0,i);

					// Add to the Scene
					this.mMainScene.attachChild(this.mSpriteList
							.get(getSpriteArrayPos(0, i)));

					// Add Brick to Scene
					if (this.mBrickStyle[j][i] > 0) {
						mBrickList.get(getSpriteArrayPos(j, i)).detachSelf();
						int temp = this.mBrickStyle[j][i];
						temp--;
						this.mBrickStyle[j][i] = temp;
						addBrick(j, i, temp);
						this.mBrickList.get(getSpriteArrayPos(j, i)).setZIndex(
								-99);
						this.mMainScene.attachChild(this.mBrickList
								.get(getSpriteArrayPos(j, i)));
						this.mMainScene.sortChildren();

					}
				}
			}
		}
		STATE = CHECK;
	}

	IAnimationListener mAnimateHintListener = new IAnimationListener() {

		@Override
		public void onAnimationFinished(AnimatedSprite arg0) {
			mHintSprite.setVisible(false);
		}

		@Override
		public void onAnimationFrameChanged(AnimatedSprite arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onAnimationLoopFinished(AnimatedSprite arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onAnimationStarted(AnimatedSprite arg0, int arg1) {
			// TODO Auto-generated method stub
		}
	};

	/**
	 * Checl for tips and show spark
	 */
	private void doTips() {

		if (mHintSprite == null) {
			mHintSprite = new AnimatedSprite(0, 0, mHintTextureRegion,
					this.mEngine.getVertexBufferObjectManager());
			mHintSprite.setVisible(false);
			mHUD.attachChild(mHintSprite);
		}
		if (mDeadArrList.size() > 0) {
			String key = mDeadArrList.get(MathUtils.random(0,
					mDeadArrList.size() - 1));
			if (key.substring(3, 4).equals("1")) // LEFT
			{
				mHintSprite.setPosition(
						PADDINGX + Integer.parseInt(key.substring(0, 1))
								* CELL_WIDTH - 16,
						PADDINGY + Integer.parseInt(key.substring(2, 3))
								* CELL_HEIGHT + 16);
				mHintSprite.animate(new long[] { 250, 250, 250, 250 }, 4, 7, 4,
						mAnimateHintListener);
				mHintSprite.setVisible(true);
			} else if (key.substring(3, 4).equals("2")) // RIGHT
			{
				mHintSprite.setPosition(
						PADDINGX + Integer.parseInt(key.substring(0, 1))
								* CELL_WIDTH + CELL_WIDTH - 16, PADDINGY
								+ Integer.parseInt(key.substring(2, 3))
								* CELL_HEIGHT + 16);
				mHintSprite.animate(new long[] { 250, 250, 250, 250 }, 8, 11,
						4, mAnimateHintListener);
				mHintSprite.setVisible(true);
			} else if (key.substring(3, 4).equals("3")) // UP
			{
				mHintSprite.setPosition(
						PADDINGX + Integer.parseInt(key.substring(0, 1))
								* CELL_WIDTH + 16,
						PADDINGY + Integer.parseInt(key.substring(2, 3))
								* CELL_HEIGHT);
				mHintSprite.animate(new long[] { 250, 250, 250, 250 }, 12, 15,
						4, mAnimateHintListener);
				mHintSprite.setVisible(true);
			} else if (key.substring(3, 4).equals("4")) // DOWN
			{
				mHintSprite.setPosition(
						PADDINGX + Integer.parseInt(key.substring(0, 1))
								* CELL_WIDTH + 16,
						PADDINGY + Integer.parseInt(key.substring(2, 3))
								* CELL_HEIGHT + CELL_HEIGHT - 16);
				mHintSprite.animate(new long[] { 250, 250, 250, 250 }, 0, 3, 4,
						mAnimateHintListener);
				mHintSprite.setVisible(true);
			}

		} else {
			checkMapDead();
		}
	}
	
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
