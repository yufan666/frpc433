package com.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static int PERPAGE = 20;
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}
	// 把dip值转变为像素�?
	public static int dip2px(Context context, float dpValue) {
		if (context == null) {
			return 0;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static boolean checkBeforeSubmit(Context context,TextView... tv){
		boolean ref = true;
		for(int i = 0;i < tv.length;i++){
			if("".equals(tv[i].getText().toString())){
				ref = false;
				LogUtils.ShowToast(context, "请完善信息！");
				break;
			}
		}
		return ref;
	}
	public static boolean checkPhoneNumber(String phone){
		String s = "^[1][3578][0-9]{9}$";
		Pattern pattern = Pattern.compile(s);
		Matcher isNum = pattern.matcher(phone);
		return isNum.matches();
	}

	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;

		String expression = "((^(13|15|17|18)[0-9]{9}$)|(^0[1-9]{1}[0-9]{9,10}$))";
		CharSequence inputStr = phoneNumber;

		Pattern pattern = Pattern.compile(expression);

		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;

	}
	public static int getVersionCode(Context context) {
		int versionName = 1;
		try {
			PackageManager pm = context.getPackageManager();
			versionName = pm.getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
			LogUtils.PST(e);
		}
		return versionName;
	}
	public static String getVersionName(Context context){
		PackageManager packageManager=context.getPackageManager();
		PackageInfo packageInfo;
		String versionName="";
		try {
			packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
			versionName=packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	//验证身份证号
	@SuppressLint("WrongConstant")
	public static String IDCardValidate(String IDStr)  {
		String errorInfo = "";// 记录错误信息
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
				"3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
				"9", "10", "5", "8", "4", "2" };
		String Ai = "";
		// ================ 号码的长�? 15位或18�? ================
		if (IDStr.length() != 15 && IDStr.length() != 18) {
			errorInfo = "身份证号码长度应该为15位或18位�??";
			return errorInfo;
		}
		// =======================(end)========================
		// ================ 数字 除最后以为都为数�? ================
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			errorInfo = "身份�?15位号码都应为数字 ; 18位号码除�?后一位外，都应为数字�?";
			return errorInfo;
		}
		// =======================(end)========================
		// ================ 出生年月是否有效 ================
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 月份
		if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
			errorInfo = "身份证生日无效�??";
			return errorInfo;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
					|| (gc.getTime().getTime() - s.parse(
					strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				errorInfo = "身份证生日不在有效范围�??";
				return errorInfo;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			errorInfo = "身份证月份无�?";
			return errorInfo;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			errorInfo = "身份证日期无�?";
			return errorInfo;
		}
		// =====================(end)=====================
		// ================ 地区码时候有�? ================
		Hashtable h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			errorInfo = "身份证地区编码错误�??";
			return errorInfo;
		}
		// ==============================================
		// ================ 判断�?后一位的�? ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi
					+ Integer.parseInt(String.valueOf(Ai.charAt(i)))
					* Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;
		if (IDStr.length() == 18) {
			if (Ai.equals(IDStr) == false) {
				errorInfo = "身份证无效，不是合法的身份证号码";
				return errorInfo;
			}
		} else {
			return "";
		}
		// =====================(end)=====================
		return "";
	}

	private static Hashtable GetAreaCode() {
		Hashtable hashtable = new Hashtable();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙�?");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙�?");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

	public static boolean isDataFormat(String str) {
		boolean flag = false;
		// String
		// regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
		String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		Pattern pattern1 = Pattern.compile(regxStr);
		Matcher isNo = pattern1.matcher(str);
		if (isNo.matches()) {
			flag = true;
		}
		return flag;
	}

	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNoChineseWord(String str) {
		Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmail(String string) {
		Pattern pattern = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
		Matcher isEmail = pattern.matcher(string);
		if (isEmail.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isLetter(String str){
		Pattern pattern = Pattern.compile("[a-zA-Z]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static SpannableString getPrict(String price1,String price2){
		String s = "¥" + price1 + " 原价" + price2;
		int pos = s.indexOf(" ");
		int end = s.length();
		SpannableString spanString = new SpannableString(s);
		ForegroundColorSpan span = new ForegroundColorSpan(0xff999999);
		StrikethroughSpan span2 = new StrikethroughSpan();
		spanString.setSpan(span, pos+1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spanString.setSpan(span2, pos+1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spanString;
	}

	/*public static SpannableString processPrice(Context context,String price){
		SpannableString spanString = new SpannableString("价格：¥" + price);
		ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.text_color_red));
		AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(sp2px(context, 18));
		spanString.setSpan(span, 3, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spanString.setSpan(sizeSpan, 3, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}*/

	public static String formatDate(long cretetime){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(cretetime));
	}

	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return "";
		}
	}

	public static String getRealPath(Context context, Uri uri) {
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor actualimagecursor = context.getContentResolver().query(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		return actualimagecursor.getString(actual_image_column_index);
	}
	public static int MAX_SIZE = 819200;
	public static boolean compressImage(File imageFile, String savePath) {
		if (imageFile.length() > MAX_SIZE && imageFile.exists()) {
			double scale = imageFile.length() / MAX_SIZE;
			Log.d("TAG", "doInBackground: " + scale);
			int degree = readPictureDegree(imageFile.getPath());
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageFile.getPath(), option);
			option.inSampleSize = (int) scale;
			option.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), option);
			if (degree != 0) {
				bitmap = rotateBitmap(bitmap, degree);
			}
			imageFile = new File(FileUtils.imagePath + savePath);
			if (!imageFile.exists()) {
				try {
					imageFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(imageFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}else {
			return false;
		}


	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
		Bitmap mBitmap = null;
		try {
			Matrix m = new Matrix();
			m.setRotate(degrees % 360);
			mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return mBitmap;
	}

	public static boolean checkBeforeSubmit(TextView... tv){
		boolean ref = true;
		for(TextView t : tv){
			if(t.getText().toString().equals("")){
				ref = false;
				break;
			}
		}

		return ref;
	}

	public static String getPath(Context context, Uri uri) {

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		}

		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static boolean isLetter(char c){
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	public static int getStringLength(String s){
		if (s == null)
			return 0;
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}
	private static final int MIN_CLICK_DELAY_TIME = 2000;
	private static long lastClickTime;

	public abstract static class OnMultiClickListener implements View.OnClickListener{
		// 两次点击按钮之间的点击间隔不能少于1000毫秒
		private  final int MIN_CLICK_DELAY_TIME = 8000;
		private  long lastClickTime;

		public abstract void onMultiClick(View v);

		@Override
		public void onClick(View v) {
			long curClickTime = System.currentTimeMillis();
			if((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
				// 超过点击间隔后再将lastClickTime重置为当前点击时间
				lastClickTime = curClickTime;
				onMultiClick(v);
			}
		}
	}
}
