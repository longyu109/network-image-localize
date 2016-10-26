package com.ly.localize.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.ly.localize.listener.ImageLocalListenerHandler;
import com.ly.localize.service.ImageLocalService;
import com.ly.localize.util.FilePathUtils;
import com.ly.localize.util.HttpClientUtils;
import com.ly.localize.util.ImageUtil;

@Repository("baseImageLocalService")
public class BaseImageLocalServiceImpl implements ImageLocalService {

	@Value("${base.path}")
	private String basePath;

	private final static Logger logger = LoggerFactory.getLogger(ImageLocalListenerHandler.class);

	private final static String WEIXIN_IMG_PROXY = "http://read.html5.qq.com/image?src=forum&q=5&r=0&imgflag=7&imageUrl=";

	public String getImageByUrl(String url) throws IOException {
		if (FilePathUtils.isImgFromWx(url)) {
			url = WEIXIN_IMG_PROXY + url;
		}
		String localFile = basePath + FilePathUtils.getRandomFileNameWithOutExt();
		String fileType = ".jpg";
		String newFilePath = localFile;
		try {
			HttpClientUtils.downloadFile(url, localFile);
			fileType = ImageUtil.getImageFileType(new File(localFile));
			File file = new File(localFile);
			newFilePath = localFile + "." + fileType;
			file.renameTo(new File(newFilePath));
			if (StringUtils.isNotEmpty(fileType) && fileType.equals("gif")) {
				if (file.exists()) {
					file.delete();
				}
				newFilePath = "";
			}
		} catch (IOException e) {
			logger.error("downloadFile error,url {},errorMsg {}", url, e.getMessage(), e);
		}
		return newFilePath;
	}

	public List<HashMap<String, String>> imageLocalize(String fileStr, int type) {
		return Collections.emptyList();
	}

	public void postHandler(long trackId, int type) {
		// TODO Auto-generated method stub

	}
}
