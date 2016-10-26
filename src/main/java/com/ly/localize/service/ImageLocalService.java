package com.ly.localize.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface ImageLocalService {

	/**
	 * 
	 * 根据url从网络中下载图片到本地
	 * @throws IOException 
	 * 
	 */
	public String getImageByUrl(String url) throws IOException;

	/**
	 * 
	 * 将下载的图片上传到本站
	 * 
	 */

	public List<HashMap<String, String>> imageLocalize(String fileStr,int type);
	
	/**
	 * 后续处理
	 * 
	 * */
	public void postHandler(long trackId,int type);

}
