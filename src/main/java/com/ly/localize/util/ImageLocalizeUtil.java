package com.ly.localize.util;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;


public class ImageLocalizeUtil {

	public String getRandomFileNameTempDir() {
		String tempDirName = UUID.randomUUID().toString().replaceAll("-", "");
		// String tempFile = assembleLocalFilePath(getTempPath(), tempFileName);
		return tempDirName;
	}

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
