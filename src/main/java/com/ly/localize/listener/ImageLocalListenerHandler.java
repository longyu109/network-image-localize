package com.ly.localize.listener;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.ly.localize.service.ImageLocalService;
import com.ximalaya.dtres.constants.UploadType;
import com.ximalaya.serialization.json.JsonParser;

/**
 * 
 * 绑定消息队列，专门获取网络图片本地化的消息
 * 
 * @author moon
 * @since 2016.9.7
 * 
 */
public class ImageLocalListenerHandler implements MessageListener {

	// 根据不同的type来区分图片的类型，从而确定业务逻辑

	private final static Logger log = LoggerFactory.getLogger(ImageLocalListenerHandler.class);

	@Autowired
	private ImageLocalService richIntroImgLocalizeService;

	@Autowired
	protected JsonParser jsonParser;

	public void onMessage(Message message) {
		if (null == message) {
			return;
		}
		long trackId = 0;
		try {
			String msg = new String(message.getBody(), "UTF-8");
			Map<String, Object> map = jsonParser.toMap(msg);
			trackId = (Integer) map.get("id");
			if (log.isDebugEnabled()) {
				log.debug("receive a message，trackId : {}", trackId);
			}
			int type = UploadType.attachment.getValue();
			try {
				// 图片本地化后续处理
				richIntroImgLocalizeService.postHandler(trackId, type);
				log.debug("postHandler success,trackid {},type {}", trackId, type);
				// else if (UploadType.cover.getValue() ==
				// Integer.parseInt(type)) {
				// trackId = null == messageMap.get("trackId") ? null :
				// messageMap.get("trackId").toString();
				// // 图片本地化后续处理
				// richIntroImgLocalizeService.postHandler(Long.parseLong(trackId),
				// Integer.parseInt(type));
				// log.debug("postHandler success,trackid {},type {}",
				// trackId,type);
				// }
			} catch (RuntimeException e) {
				log.error("String parseInt error,type {},trackId {}", type, trackId, e);
			}
		} catch (Throwable e) {
			log.error("update track error, trackId {}", trackId, e);
		}
	}

}
