package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nali.common.serialization.json.IJsonSerializer;
import com.nali.common.serialization.json.JsonGenerationException;

@ContextConfiguration(locations = { "classpath:application-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class TestMq {
	
	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@Autowired
	private IJsonSerializer jacksonSerializer;
	
	@Test
	public void testSendMq() throws InterruptedException{
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("trackId", 315546);
		m.put("type", 4);
		try {
			System.out.println(jacksonSerializer.toString(m));
			rabbitTemplate.convertAndSend("network.image.localize.queue", jacksonSerializer.toString(m));
			
			System.out.println("------");
			Thread.sleep(900000);
		} catch (AmqpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
