package com.ly.localize.util;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nali.common.util.UrlUtil;

public class FdfsFilePathUtil {

	private final static Logger log = LoggerFactory.getLogger(FdfsFilePathUtil.class);

	private static String hostsName = "/fdfs_file_path.properties";

	private static Properties fdfsFilePaths;

	private static char key_separator = '.';

	private static char size_separator = ',';

	private static char value_separator = '|';

	private static String size = "size";

	private static String prefix = "prefix";

	private final static char urlSeparator = '/';

	private final static String emptyString = "";

	private static String fdfsStaticPath, homeDirectHost, downloadDomain, audioDomain;

	private static Map<String, Map<String, String>> paths;

	static {
		try {
			fdfsFilePaths = new Properties();
			InputStream in = FdfsFilePathUtil.class.getResourceAsStream(hostsName);
			if (null == in) {
				in = FdfsFilePathUtil.class.getClassLoader().getResourceAsStream(hostsName);
			}
			if (null != in) {
				fdfsFilePaths.load(in);
				if (log.isDebugEnabled()) {
					log.debug("load file ：{},{}", FdfsFilePathUtil.class.getResource(hostsName).getPath(), hostsName);
				}
			} else {
				throw new RuntimeException("can not load file:" + hostsName);
			}
			fdfsStaticPath = UrlUtil.get("server.fdfs.domain");
			homeDirectHost = UrlUtil.get("server.home.host");
			downloadDomain = UrlUtil.get("server.download.domain");
			audioDomain = UrlUtil.get("server.audio.domain");
			paths = new HashMap<String, Map<String, String>>();
			if (log.isDebugEnabled()) {
				log.debug("load file ：{},{}", FdfsFilePathUtil.class.getResource("/").getPath(), hostsName);
			}
			Enumeration<Object> fileTypes = fdfsFilePaths.keys();
			while (fileTypes.hasMoreElements()) {
				String key = fileTypes.nextElement().toString();
				String[] p = StringUtils.split(key, key_separator);
				if (p.length != 2) {
					log.error("read fdfs_file_path.properties error, error key:{}", key);
				} else {
					if (p[1].equals(size)) {
						String[] sizes = StringUtils.split(fdfsFilePaths.get(key).toString(), value_separator);
						String[] prefixes = StringUtils
								.split(fdfsFilePaths.get(p[0] + key_separator + prefix).toString(), value_separator);
						if (sizes.length != prefixes.length) {
							log.error("read fdfs_file_path.properties error, {}:{},{}:{}", key, fdfsFilePaths.get(key),
									p[0] + key_separator + prefix, fdfsFilePaths.get(p[0] + key_separator + prefix));
						} else {
							Map<String, String> sizeMap = new HashMap<String, String>();
							for (int i = 0; i < sizes.length; i++) {
								String[] ss = StringUtils.split(sizes[i], size_separator);
								String s = ss[0].equals("0") ? "n" : ss[0];
								String s2 = ss[1].equals("0") ? "n" : "";
								sizeMap.put(s + s2, prefixes[i]);
							}
							paths.put(p[0], sizeMap);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("load fdfs_file_path.properties error", e);
		}
	}

	// public static String getAbsoluteFdfsPath(String originPath, String type,
	// String size) {
	// UploadType uploadType = UploadType.valueOf(type);
	// return getAbsoluteFdfsPath(originPath, uploadType, size);
	// }
	//
	// public static Map<String, String> getPictureCutConfigByType(UploadType
	// type) {
	// return paths.get(type.name());
	// }
	//
	// public static String getAbsoluteFdfsPath(String originPath, UploadType
	// type, String size) {
	// if (StringUtils.isEmpty(originPath)) {
	// return emptyString;
	// }
	// if (originPath.startsWith("http://")) {
	// return originPath;
	// }
	// StringBuilder sb = new StringBuilder();
	// sb.append(fdfsStaticPath);
	// if (fdfsStaticPath.charAt(fdfsStaticPath.length() - 1) != urlSeparator
	// && originPath.charAt(0) != urlSeparator) {
	// sb.append(urlSeparator);
	// }
	// if (type.getValue() == UploadType.audio.getValue()) {
	// sb.append(originPath);
	// return sb.toString();
	// }
	// String prefixName = FdfsFilePathUtil.getPrefixName(type, size);
	// int lastDot = originPath.lastIndexOf(key_separator);
	// String ext = emptyString;
	// String fileName = originPath;
	// if (lastDot > 0) {
	// ext = originPath.substring(lastDot);
	// fileName = originPath.substring(0, lastDot);
	// }
	// sb.append(fileName).append("_").append(prefixName).append(ext);
	// return sb.toString();
	// }

	// public static String getSlaveFdfdId(String originPath, UploadType type,
	// String size) {
	// StringBuilder sb = new StringBuilder();
	// String prefixName = FdfsFilePathUtil.getPrefixName(type, size);
	// int lastDot = originPath.lastIndexOf(key_separator);
	// String ext = emptyString;
	// String fileName = originPath;
	// if (lastDot > 0) {
	// ext = originPath.substring(lastDot);
	// fileName = originPath.substring(0, lastDot);
	// }
	// sb.append(fileName).append("_").append(prefixName).append(ext);
	// return sb.toString();
	// }

	// public static String getPrefixName(UploadType type, String size) {
	// if (StringUtils.isEmpty(size) || null == type) {
	// throw new IllegalArgumentException("empty parameter, type=" + type +
	// ",size=" + size + "");
	// }
	// if (null == paths.get(type.name())) {
	// return emptyString;
	// }
	//
	// return paths.get(type.name()).get(size);
	// }

	public static String appendHomeHost(String relativeUrl) {
		return joinPath(homeDirectHost, relativeUrl);
	}

	public static String appendFdfsHost(String relativeUrl) {
		return joinPath(fdfsStaticPath, relativeUrl);
	}

	public static String appendDownloadHost(String downloadUrl) {
		return joinPath(downloadDomain, downloadUrl);
	}

	public static String appendAudioHost(String audioUrl) {
		return joinPath(audioDomain, audioUrl);
	}

	public static String joinPath(String domain, String relativeUrl) {
		if (StringUtils.isEmpty(relativeUrl)) {
			return emptyString;
		}
		if (relativeUrl.startsWith("http://")) {
			return relativeUrl;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(domain);
		if (domain.charAt(domain.length() - 1) != urlSeparator && relativeUrl.charAt(0) != urlSeparator) {
			sb.append(urlSeparator);
		}
		sb.append(relativeUrl);
		return sb.toString();
	}
}
