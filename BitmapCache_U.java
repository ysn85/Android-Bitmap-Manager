
package com.byecity.baselib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

//import com.android.volley.toolbox.ImageLoader.ImageCache;

/******************************************
 * 类描述： 图片LRU缓存工具类 类名称：BitmapCache_U
 * 
 * @version: 1.0
 * @author: shaoningYang
 * @time: 2014-6-4 上午11:13
 ******************************************/

public class BitmapCache_U /*implements ImageCache */{

	private static final int DEFAULT_CACHE_SIZE = 4;

	private int realSize = DEFAULT_CACHE_SIZE;
	private LruCache<String, Bitmap> mCache;
	private Context mContext;

	/** 是否要将图片保存在本地true为是，false为否 */
	public boolean bSaveLocal;
	/** 保存图片至本地磁盘的路径 */
	public String savePath;

	public BitmapCache_U(Context context) {
		mContext = context;
		int memClass = ((ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

		/** 占用系统分配给应用内存的10% (0-50%) */
		realSize = calculateCacheSize(memClass, 10);
		initCache();
	}

	/**
	 * 清空当前LRUCache中的缓存数据
	 * 
	 * @return void
	 */

	public void clearCache() {
		if (mCache != null && mCache.size() > 0) {
			mCache.evictAll();
			System.gc();
		}
	}

	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		synchronized (mCache) {
			bitmap = mCache.get(url);
		}
		/*
		if (bSaveLocal && bitmap == null && !TextUtils.isEmpty(savePath)) {
			bitmap = File_U.getImageFile(savePath, url);
		}*/
		return bitmap;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		synchronized (mCache) {
			if (mCache.get(url) == null) {
				mCache.put(url, bitmap);
			}
		}

		if (!bSaveLocal || TextUtils.isEmpty(savePath)) {
			return;
		}
		final String fUrl = url;
		final Bitmap fBitmap = bitmap;
		/*
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File_U.saveImageFile(savePath, fUrl, fBitmap);
			}
		}).start();*/
	}

	private void initCache() {
		mCache = new LruCache<String, Bitmap>(realSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	private int calculateCacheSize(int memClass, int percentageOfMemoryForCache) {
		if (memClass == 0) {
			memClass = DEFAULT_CACHE_SIZE;
		}

		if (percentageOfMemoryForCache < 0) {
			percentageOfMemoryForCache = 0;
		} else if (percentageOfMemoryForCache > 51) {
			percentageOfMemoryForCache = 50;
		}

		int capacity = (int) ((memClass * percentageOfMemoryForCache * 1024L * 1024L) / 100L);
		if (capacity <= 0) {
			capacity = 1024 * 1024 * DEFAULT_CACHE_SIZE;
		}

		return capacity;
	}
}