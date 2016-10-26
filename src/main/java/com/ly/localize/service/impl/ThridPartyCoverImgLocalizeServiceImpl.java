package com.ly.localize.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ly.localize.service.ImageLocalService;

@Repository("thridPartyCoverImgLocalizeService")
public class ThridPartyCoverImgLocalizeServiceImpl extends BaseImageLocalServiceImpl implements ImageLocalService {

	@Override
	public List<HashMap<String, String>> imageLocalize(String fileStr,int type) {

		return null;
	}

	@Override
	public void postHandler(long trackId,int type){
		// TODO Auto-generated method stub

	}
}
