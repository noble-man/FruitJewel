package com.fruitjewel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
	public static final String KEY_PREFS_SOUND = "key_prefs_sound";
	public static final String KEY_PREFS_MUSIC = "key_prefs_music";
	public static final String KEY_PREFS_CHAPTER = "key_prefs_chapter";
	public static final String KEY_PREFS_TOTAL = "key_prefs_total";
	public static final String KEY_PREFS_UNLOCK_LEVEL = "key_prefs_unlock";

	private static final String APP_SHARED_PREFS = "com.fruitjewel";
	private SharedPreferences _sharedPrefs;
	private Editor _prefsEditor;

	public AppPreferences(Context context) {
		this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
				Activity.MODE_PRIVATE);
		this._prefsEditor = _sharedPrefs.edit();
	}

	public int getTotal() {
		return _sharedPrefs.getInt(KEY_PREFS_TOTAL, 0);
	}

	public void setTotal(int val) {
		_prefsEditor.putInt(KEY_PREFS_TOTAL, val);
		_prefsEditor.commit();
	}
	
	public int getChapter() {
		return _sharedPrefs.getInt(KEY_PREFS_CHAPTER, 1);
	}

	public void setChapter(int val) {
		_prefsEditor.putInt(KEY_PREFS_CHAPTER, val);
		_prefsEditor.commit();
	}
	
	public int getUnlockLevels() {
		return _sharedPrefs.getInt(KEY_PREFS_UNLOCK_LEVEL, 1);
	}

	public void setUnlockLevels(int val) {
		_prefsEditor.putInt(KEY_PREFS_UNLOCK_LEVEL, val);
		_prefsEditor.commit();
	}
	
	public boolean getSound() {
		return _sharedPrefs.getBoolean(KEY_PREFS_SOUND, true);
	}

	public void setSound(boolean val) {
		_prefsEditor.putBoolean(KEY_PREFS_SOUND, val);
		_prefsEditor.commit();
	}
	
	public boolean getMusic() {
		return _sharedPrefs.getBoolean(KEY_PREFS_MUSIC, true);
	}

	public void setMusic(boolean val) {
		_prefsEditor.putBoolean(KEY_PREFS_MUSIC, val);
		_prefsEditor.commit();
	}
}
