package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ximalaya.service.track.exception.TrackServiceException;
import com.ximalaya.service.track.service.IRemoteTrackFacadeService;
import com.ximalaya.service.track.thrift.ExtensionTrackInfo;
import com.ximalaya.service.track.thrift.TrackForm;

@ContextConfiguration(locations = { "classpath:application-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class TestTrackService {

	@Autowired
	private IRemoteTrackFacadeService trackClient;
	
	@Test
	public void testGetIntro(){
		long trackId=314272L;
		ExtensionTrackInfo queryExtensionTrackInfo = trackClient.queryExtensionTrackInfo(trackId);
		System.out.println(queryExtensionTrackInfo);
		TrackForm trackForm=new TrackForm();
		trackForm.setId(queryExtensionTrackInfo.getTrackId());
		trackForm.setUid(queryExtensionTrackInfo.getUid());
		trackForm.setRichIntro(queryExtensionTrackInfo.getRichIntro()+"--0");
		try {
			trackClient.updateTrack(trackForm);
			ExtensionTrackInfo queryExtensionTrackInfo1 = trackClient.queryExtensionTrackInfo(trackId);
			System.out.println(queryExtensionTrackInfo1);
		} catch (TrackServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
