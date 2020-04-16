package com.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class FileUtils {
	public static String cachePath;
	public static String downloadPath;
	public static String imagePath;


	public static void initSDCard(Context context) {
		File f = getDiskCacheDir(context, "engineer51");
		if (!f.exists()) {
			f.mkdirs();
		}
		String cache = f.getPath() + File.separator;

		File file = new File(cache + "download"
				+ File.separator);
		if (!file.exists()) {
			file.mkdirs();
		}
		downloadPath = file.getPath() + File.separator ;

		File file2 = new File(cache + "cache"
				+ File.separator);
		if (!file2.exists()) {
			file2.mkdirs();
		}
		cachePath = file2.getPath() + File.separator;

		File file3 = new File(cache + "image"
				+ File.separator);
		if (!file3.exists()) {
			file3.mkdirs();
		}
		imagePath = file3.getPath() + File.separator;
		LogUtils.d("initSDCard cachePath:", cache);


	}

	/**
	 * 获取可以使用的缓存目录
	 * 
	 * @param context
	 * @param uniqueName
	 *            目录名称
	 * @return
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取程序外部的缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static File getExternalCacheDir(Context context) {
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	public static void copyAssetsFile(Context context, String filename,
			String dstpath) {
		File dstfile = new File(dstpath + filename);
		if (!dstfile.exists()) {
			AssetManager am = context.getAssets();
			try {
				InputStream is = am.open(filename);
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				makesureFileExist2(dstpath + filename);
				FileOutputStream fos = new FileOutputStream(dstfile, false);
				fos.write(buffer);
				fos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void saveDataAsFile(Object d, String fileName) {
		File file = new File(cachePath + fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(d);
			oos.close();
		} catch (FileNotFoundException e) {
			LogUtils.PST(e);
		} catch (IOException e) {
			Log.e("saveDataAsFile", cachePath + fileName);
			e.printStackTrace();
		}
	}

	// public static BaseData readCacheDataFromFile(String name) {
	// return readDataFromFile("data", name);
	// }

	public static Object readDataFromFile(String path, String name) {
		String filename = path + File.separator + name;

		File file = new File(cachePath + filename);
		LogUtils.d("readDataFromFile", cachePath + filename);
		if (!file.exists()) {
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			Object data =  ois.readObject();
			ois.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			if (file.exists()) {
				// 有时候莫名其妙的转换错误。。。
				file.delete();
				return null;
			}
		}
		return null;
		// return readDataFromFile(filename);
	}

	public static Object readBaseDataFromFile(String fileName) {
		File file = new File(cachePath + fileName);
		LogUtils.d("readDataFromFile", cachePath + fileName);
		if (!file.exists()) {
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			Object data =  ois.readObject();
			ois.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			if (file.exists()) {
				// 有时候莫名其妙的转换错误。。。
				file.delete();
				return null;
			}
		}
		return null;
	}

	public static String readDataFromFile(String fileName) {
		String res = null;
		try {
			File f = new File(cachePath + fileName);
			if (!f.exists()) {
				return null;
			}
			FileInputStream fin = new FileInputStream(f);
			int length = fin.available();
			if (length > 0) {
				byte[] buffer = new byte[length];
				fin.read(buffer);
				res = new String(buffer, "utf-8");
			}
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.PST(e);
		}
		return res;
	}

	public static void saveAsFile(String data, String fileName) {
		try {
			saveAsFile(data.getBytes("utf-8"), fileName);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 数组保存为文件
	public static void saveAsFile(byte[] data, String fileName) {
		try {
			InputStream is = new ByteArrayInputStream(data);
			FileUtils.saveAsFile(is, cachePath + fileName);
			is.close();
		} catch (IOException e) {
			LogUtils.PST(e);
		}
	}

	// 输入流保存为文件
	public static void saveAsFile(InputStream inputStream, String fileName) {
		try {
			if (TextUtils.isEmpty(fileName)) {
				return;
			}
			// make sure this file exist
			makesureFileExist2(fileName);
			LogUtils.d("FileUitls","fileutils save:" + fileName);
			OutputStream os = new FileOutputStream(fileName, false);
			byte[] buf = new byte[255];
			int len = 0;
			while ((len = inputStream.read(buf)) != -1) {
				os.write(buf, 0, len);
			}
			inputStream.close();
			os.flush();
			os.close();
		} catch (IOException e) {
			LogUtils.PST(e);
		}
	}

	public static String convertFilenameFromUrl(String urlstr) {
		return String.valueOf(urlstr.hashCode());
	}

	public static void saveFile(String pathDirectory, String fileName,
			Object object) {
		String filePath = pathDirectory + File.separator + fileName;
		makesureFileExist(filePath);
		saveDataAsFile(object, filePath);
	}

	// 确定指定文件是否存在，如果不存在，则创建空文件
	public static void makesureFileExist(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return;
		}
		// file path
		fileName = cachePath + fileName;
		int index = fileName.lastIndexOf("/");
		File file = null;
		if (index != -1) {
			String filePath = fileName.substring(0, index);
			file = new File(filePath);
			if (!file.exists()) {
				boolean ret = file.mkdirs();
				LogUtils.d("FileUtils","mkdirs " + ret + " " + filePath);
			}
		}
		file = new File(fileName);
		if (!file.exists()) {// 确保文件存在
			try {
				file.createNewFile();
			} catch (Exception e) {
				Log.e("FileUtils", fileName);
				LogUtils.PST(e);
			}
		}
	}

	public static void makesureFileExist2(String filename) {
		if (TextUtils.isEmpty(filename)) {
			return;
		}
		if (filename.endsWith("/")) {
			File folder = new File(filename);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		} else {
			int index = filename.lastIndexOf("/");
			String filePath = filename.substring(0, index);
			File folder = new File(filePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File file = new File(filename);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	// 获取缓存文件大小
	public static String getCacheSize(Context context) {
		File cacheFile = new File(cachePath);
		File save = new File(cachePath + "bin/");
		long cacheSize = getCacheLegth(cacheFile) - getCacheLegth(save) ;
		return formatFileSize(cacheSize);
	}

	 public static String formatFileSize(long length) {
		String result = null;
		int sub_string = 0;
		if (length >= 1073741824) {
			sub_string = String.valueOf((float) length / 1073741824).indexOf(
					".");
			result = ((float) length / 1073741824 + "000").substring(0,
					sub_string + 3) + "GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3) + "MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3) + "KB";
		} else if (length < 1024)
			result = Long.toString(length) + "B";
		return result;
	 }
	public static long getCacheLegth(File f) {
		long length = 0;
		if (f != null && f.exists()) {
			File[] tempList = f.listFiles();
			if (tempList != null) {
				for (int i = 0; i < tempList.length; i++) {
					if (tempList[i].isFile()) {
						// 文件
						File tempFile = tempList[i];
						length += tempFile.length();
					}
					if (tempList[i].isDirectory()) {
						// 文件夹
						length += getCacheLegth(tempList[i]);
					}
				}
			}
		}
		return length;
	}

	public static void clearCache(Context context) {
		deleteFileOrPath(cachePath);
		deleteFileOrPath(context.getCacheDir());
		// deleteFileOrPath("/data/data/"+context.getPackageName() +
		// "/databases");
		cachePath = null;
	}

	// 递归删除文件及文件夹
	public static void deleteFileOrPath(String path) {
		File file = new File(path);
		deleteFileOrPath(file);
	}

	// 递归删除文件及文件夹
	public static void deleteFileOrPath(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			if (file.getName().equals("bin")) {
				return;
			}
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteFileOrPath(childFiles[i]);
			}
			// final File to = new File(file.getAbsolutePath() +
			// System.currentTimeMillis());
			// file.renameTo(to);
			// to.delete();
		}
	}

	public static void copyFile(File res, File des) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new BufferedInputStream(new FileInputStream(res));
			os = new BufferedOutputStream(new FileOutputStream(des));
			byte[] buffer = new byte[1024];
			int i;
			while ((i = is.read(buffer)) != -1) {
				os.write(buffer, 0, i);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
