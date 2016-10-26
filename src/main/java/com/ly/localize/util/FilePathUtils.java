package com.ly.localize.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.UUID;

/**
 * 文件路径处理
 * 
 * @author rick
 * @version 2012-6-12
 */
public class FilePathUtils {

	public static String HTTP_HEADER = "http://";
	public static String HTTP_SEPARATOR = "/";

	public static String[] separators = { "/", "\\" };

	public static String getPathSeparator(String filePath) {
		for (String at : separators) {
			if (filePath.indexOf(at) > 0)
				return at;
		}
		return File.separator;
	}

	public static void main(String[] args) {
		String randomFileName = getRandomFileName(
				"http://mmbiz.qpic.cn/mmbiz_jpg/bNTXIuRgC8H0zQGqKxZiaYRm2vKhibfI5rYOSiaYtL1tqPgWNeTicvRMVaYmXCZibLOE9wATocIkibib6lhaTlSzWmMkA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1");
		System.out.println(randomFileName);
	}

	public static String getRandomFileName(String filePath) {
		return getRandomFileNameWithExt(getFileExtName(filePath));
	}

	public static String getRandomFileNameWithExt(String fileExtName) {
		StringBuilder sb = new StringBuilder();
		sb.append(UUID.randomUUID().toString().replaceAll("-", ""));
		sb.append(".").append(fileExtName);
		return sb.toString();
	}
	
	public static String getRandomFileNameWithOutExt() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	public static String getFileType(String url) {
		int fmtIndex = url.lastIndexOf("?");
		String fileType = ".jpg";
		if (fmtIndex != 0 && fmtIndex < url.length()) {
			String[] params = url.substring(fmtIndex + 1).split("&");
			for (String fmtType : params) {
				if (fmtType.startsWith("wx_fmt")) {
					String paris[] = fmtType.split("=");
					if (null != paris && paris.length > 1)
						fileType = "."+paris[1];
				}
			}
		}
		return fileType;
	}

	public static boolean isImgFromWx(String url) {
		if (StringUtils.isEmpty(url)) {
			return false;
		}
		// int fmtIndex = url.lastIndexOf("?");
		// if (fmtIndex != 0 && fmtIndex < url.length()) {
		// String[] params = url.substring(fmtIndex + 1).split("&");
		// for (String fmtType : params) {
		// if (fmtType.startsWith("wx_fmt")||fmtType.startsWith("wxfrom")) {
		// String paris[] = fmtType.split("=");
		// if (null != paris && paris.length > 1) {
		// return true;
		// }
		// }
		// }
		// }
		String urlLowerCase=url.toLowerCase();
		if(urlLowerCase.contains("qq")||urlLowerCase.contains("wx")||urlLowerCase.contains("weixin")){
			return true;
		}
		return false;
	}

	/**
	 * 截取原始文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		int index = filePath.lastIndexOf(getPathSeparator(filePath));
		if (index < 0)
			return filePath;
		else
			return filePath.substring(index + 1);
	}

	public static String getFileExtName(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return "";
		}
		int index = filePath.lastIndexOf(".");
		if (index < 0)
			return "";
		else
			return filePath.substring(index + 1).toLowerCase();
	}

	/**
	 * 截取文件所在文件夹路径
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileDirectory(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		int index = filePath.lastIndexOf(getPathSeparator(filePath));
		if (index < 0)
			return null;
		else
			return filePath.substring(0, index + 1);
	}

	/**
	 * 拼接本地文件路径
	 * 
	 * @param dirn
	 * @return
	 */
	public static String assembleLocalFilePath(String... dirn) {
		if (null == dirn) {
			return null;
		}
		return assembleFilePath(File.separator, dirn);
	}

	public static String assembleFilePath(String fileSeparator, String... dirn) {
		StringBuilder sb = new StringBuilder();
		if (null != dirn) {
			for (int i = 0; i < dirn.length; i++) {
				String dir = dirn[i];
				if (StringUtils.isEmpty(dir))
					continue;
				// 除第一级外，下级目录去掉前缀分割符 var/ --> var/
				if (i > 0) {
					if (dir.indexOf(fileSeparator) == 0) {
						dir = dir.substring(1);
					}
				}
				// 除最后一级目录外，上级目录加上后缀分隔符 var --> var/
				if (i < dirn.length - 1) {
					if (dir.lastIndexOf(fileSeparator) != dir.length() - 1) {
						dir = dir + fileSeparator;
					}
				}
				sb.append(dir);
			}
		}
		return sb.toString();
	}

}
