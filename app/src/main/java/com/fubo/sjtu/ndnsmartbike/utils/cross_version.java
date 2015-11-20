package com.fubo.sjtu.ndnsmartbike.utils;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build;

//提供api的跨版本调用方案
public abstract class cross_version {

	@SuppressWarnings("deprecation")
	public static Drawable getDrawable (Resources res, int id, Theme theme)
	{
		if (Build.VERSION.SDK_INT >= 22)
		{
			return res.getDrawable(id, theme);
		}
		else
		{
			return res.getDrawable(id);
		}
	}
}
