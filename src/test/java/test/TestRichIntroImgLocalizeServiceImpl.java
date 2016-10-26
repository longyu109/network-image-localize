package test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ly.localize.service.ImageLocalService;
import com.ximalaya.dtres.constants.UploadType;

@ContextConfiguration(locations = { "classpath:application-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRichIntroImgLocalizeServiceImpl {

	@Autowired
	private ImageLocalService richIntroImgLocalizeService;

	@Test
	public void testGetImageByUrl() throws IOException {
		String urlBefore = "http://mmbiz.qpic.cn/mmbiz/ypAAyXNNryccXK8s517MzZOJtycSgKv7udwFkoswkyhQxukSKtfI8rKs60SZR5P8jcA1qMNmjX8rDTXzxRHh9A/0?tp=webp&wxfrom=5&wx_lazy=1";
//		String url =
//		 "http://read.html5.qq.com/image?src=forum&q=5&r=0&imgflag=7&imageUrl="+urlBefore;
		// System.out.println(url.substring(url.lastIndexOf("?")));
		// int fmtIndex = url.lastIndexOf("?");
		// String fileType = "";
		// if (fmtIndex != 0 && fmtIndex < url.length()) {
		// String[] params = url.substring(fmtIndex + 1).split("&");
		// for (String fmtType : params) {
		// if (fmtType.startsWith("wx_fmt")) {
		// String paris[] = fileType.split("=");
		// if (null != paris && paris.length > 1)
		// fileType = paris[1];
		// }
		// }
		// }

		String imageByUrl = richIntroImgLocalizeService.getImageByUrl(urlBefore);
		System.out.println(imageByUrl);
	}

	@Test
	public void tesImageLocalize() {
		String fileStr = "/Users/nali/Desktop/b5192c051638410bba8d144af658504a.JPEG";
		// int type = 3;

		List<HashMap<String, String>> imageLocalize = richIntroImgLocalizeService.imageLocalize(fileStr, UploadType.attachment.getValue());
		System.out.println(imageLocalize);
	}

	@Test
	public void tesPostHandler() {
		long trackId = 317137;
		int type = 4;
		richIntroImgLocalizeService.postHandler(trackId, type);
	}

}
