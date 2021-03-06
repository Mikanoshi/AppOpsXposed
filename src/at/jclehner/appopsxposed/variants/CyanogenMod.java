/*
 * AppOpsXposed - AppOps for Android 4.3+
 * Copyright (C) 2013 Joseph C. Lehner
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.jclehner.appopsxposed.variants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import at.jclehner.appopsxposed.util.Util;

public class CyanogenMod extends AOSP
{
	private static final String CM_VERSION = getCmVersion();

	@Override
	protected boolean onMatch(ApplicationInfo appInfo, ClassChecker classChecker) {
		return CM_VERSION.length() != 0;
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable
	{
		// for now...
		super.handleLoadPackage(lpparam);
		log("ro.cm.version=" + CM_VERSION);
	}

	@Override
	public boolean canUseLayoutFix() {
		return !isCm11After20140128();
	}

	private static boolean isCm11After20140128()
	{
		if(CM_VERSION.length() == 0)
			return false;
		else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
		{
			XposedBridge.log("Detected CyanogenMod running post-KitKat; assuming it's affected");
			return true;
		}

		final Matcher m = Pattern.compile("([0-9]+)-([0-9]{8})-.*").matcher(CM_VERSION);
		if(!m.matches())
		{
			XposedBridge.log("Failed to match ro.cm.version");
			return false;
		}

		final int cmMajor = Integer.parseInt(m.group(1));
		final int cmDate = Integer.parseInt(m.group(2));

		return cmMajor >= 11 && cmDate >= 20140128;
	}

	private static String getCmVersion() {
		return Util.getSystemProperty("ro.cm.version", "");
	}
}
