package com.ly.localize.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.ly.localize.service.ImageLocalService;
import com.ly.localize.util.FilePathUtils;
import com.ximalaya.dtres.constants.UploadType;
import com.ximalaya.dtres.model.FileBasic;
import com.ximalaya.dtres.service.IFileUploadService;
import com.ximalaya.rich.text.format.RichTextUtils;
import com.ximalaya.service.track.exception.TrackServiceException;
import com.ximalaya.service.track.service.IRemoteTrackFacadeService;
import com.ximalaya.service.track.thrift.ExtensionTrackInfo;
import com.ximalaya.service.track.thrift.TrackForm;

@Repository("richIntroImgLocalizeService")
public class RichIntroImgLocalizeServiceImpl extends BaseImageLocalServiceImpl implements ImageLocalService {

	@Autowired
	private IFileUploadService fileUploadService;

	@Autowired
	private IRemoteTrackFacadeService trackClient;

	@Value("${picture.cover.temp_path}")
	private String tempPath;

	@Value("${image.base.url}")
	private String imgBaseUrl;

	private final static Logger logger = LoggerFactory.getLogger(RichIntroImgLocalizeServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<HashMap<String, String>> imageLocalize(String fileStr, int uploadImgType) {
		if (StringUtils.isEmpty(fileStr)) {
			return Collections.emptyList();
		}
		Map<String, Object> uploadCoverFromExistPic = uploadCoverFromExistPic(fileStr, uploadImgType);
		System.out.println(uploadCoverFromExistPic);
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("data-preview",
				RichTextUtils.handlerImgFilePaths(imgBaseUrl, uploadCoverFromExistPic.get("140n").toString()));
		map.put("data-preview-height", uploadCoverFromExistPic.get("140n_height").toString());
		map.put("data-preview-width", uploadCoverFromExistPic.get("140n_width").toString());

		map.put("data-large",
				RichTextUtils.handlerImgFilePaths(imgBaseUrl, uploadCoverFromExistPic.get("750n").toString()));
		map.put("data-large-height", uploadCoverFromExistPic.get("750n_height").toString());
		map.put("data-large-width", uploadCoverFromExistPic.get("750n_width").toString());

		map.put("data-origin",
				RichTextUtils.handlerImgFilePaths(imgBaseUrl, uploadCoverFromExistPic.get("origin").toString()));

		map.put("src", RichTextUtils.handlerImgFilePaths(imgBaseUrl, uploadCoverFromExistPic.get("750n").toString()));
		list.add(map);
		return (List<HashMap<String, String>>) (list.size() == 0 ? Collections.emptyList() : list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postHandler(long trackId, int type) {

		ExtensionTrackInfo queryExtensionTrackInfo = trackClient.queryExtensionTrackInfo(trackId);
		if (null == queryExtensionTrackInfo) {
			// 声音不存在
			logger.warn("queryExtensionTrackInfo trackId {} not exist", trackId);
			return;
		}
		// 根据trackId查询track信息，拿到富文本信息intro
		String richIntro = queryExtensionTrackInfo.getRichIntro();
		// 根据richIntro拿到图片的urls
		String[] urls = RichTextUtils.getImgUrlsFormRichIntro(richIntro);
		if (urls == null || urls.length < 1) {
			logger.warn("getImgUrlsFormRichIntro return urls is null");
			return;
		}
		if (!RichTextUtils.checkImgFromNetworkUrls(imgBaseUrl, urls)) {
			logger.warn("checkImgFromNetworkUrls do not pass,trackId {},imgBaseUrl {}", trackId, imgBaseUrl);
			return;
		}
		int urlsLength = urls.length;
		// String[] imgFilePaths = new String[urlsLength];
		List<List<HashMap<String, String>>> imgFilePaths = new ArrayList<List<HashMap<String, String>>>();
		for (int i = 0; i < urlsLength; i++) {
			if (RichTextUtils.checkImgFromNetworkUrl(imgBaseUrl, urls[i])) {
				String localImgPath;
				try {
					localImgPath = getImageByUrl(urls[i]);
				} catch (IOException e) {
					logger.warn("getImageByUrl error,trackId {},url {}", trackId, urls[i]);
					return;
				}
				// 地址转换（加上域名）
				imgFilePaths.add(imageLocalize(localImgPath, type));
			} else {
				imgFilePaths.add(Collections.EMPTY_LIST);
			}
		}
		// 遍历intro，拿到intro中的image对象,替换所有的图片URL(怎么保证顺序)
		richIntro = RichTextUtils.replaceNetworkUrlWithLocalUrl4List(richIntro, imgFilePaths, imgBaseUrl);
		// 更新track
		ExtensionTrackInfo queryExtensionTrackInfo2 = trackClient.queryExtensionTrackInfo(trackId);
		if (null == queryExtensionTrackInfo2) {
			// 声音不存在
			logger.warn("queryExtensionTrackInfo trackId {} not exist", trackId);
			return;
		}
		if(null!=queryExtensionTrackInfo2.getRichIntro()&&queryExtensionTrackInfo2.getRichIntro().equals(richIntro)){
			logger.info("richIntro does not changed", trackId);
			return;
		}
		if (queryExtensionTrackInfo.getRichIntro().length() != queryExtensionTrackInfo2.getRichIntro().length()
				|| !queryExtensionTrackInfo.getRichIntro().equals(queryExtensionTrackInfo2.getRichIntro())) {
			// 富文本已经修改
			logger.warn("richIntro {} already update,trackId {}", richIntro, trackId);
			return;
		}
		TrackForm trackForm = new TrackForm();
		if(null!=queryExtensionTrackInfo.getTitle()){
			trackForm.setTitle(queryExtensionTrackInfo.getTitle());
		}
		trackForm.setId(queryExtensionTrackInfo.getTrackId());
		trackForm.setUid(queryExtensionTrackInfo.getUid());
		trackForm.setRichIntro(richIntro);
		try {
			trackClient.updateTrackForBackend(trackForm);
		} catch (TrackServiceException e) {
			// 更新失败
			logger.error("updateTrack error trackForm {}", trackForm, e);
		}

	}

	private Map<String, Object> uploadCoverFromExistPic(String fileStr, int type) {
		if (StringUtils.isEmpty(fileStr)) {
			return Collections.emptyMap();
		}
		String downloadPath = null;
		FileInputStream in = null;
		try {
			UploadType uploadType = UploadType.getSource(type);
			String ext = FilePathUtils.getFileExtName(fileStr);
			FileBasic fileBasic = new FileBasic(uploadType.getDisName() + ext, null, uploadType);
			downloadPath = fileStr;
			if (StringUtils.isEmpty(downloadPath) || StringUtils.isEmpty(ext)) {
				return Collections.emptyMap();
			}
			File file = new File(downloadPath);
			if (file.length() > 0) {
				fileBasic.setFileExtName(ext);
				fileBasic.setFileSize(file.length());
				in = new FileInputStream(file);
				fileBasic = fileUploadService.saveFile(null, fileBasic, in);
				return fileBasic.getProcessResult();
			}
			return Collections.emptyMap();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			return Collections.emptyMap();
		} finally {
			if (StringUtils.isNotEmpty(downloadPath)) {
				File file = new File(downloadPath);
				file.delete();
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

	}

}
