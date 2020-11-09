package pmcep.web;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import pmcep.logger.Logger;
import pmcep.web.miner.controllers.InstanceController;
import pmcep.web.miner.controllers.MinerController;
import pmcep.web.miner.models.Miner;
import pmcep.web.miner.models.MinerInstanceConfiguration;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.Stream;

@SpringBootApplication
public class MinerBackendApplication {

	@Autowired
	InstanceController ic;
	@Autowired
	MinerController mc;
	
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MinerBackendApplication.class, args);
	}
	
//	@PostConstruct
//	public void init() {
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				Logger.instance().debug("Starting simulation of instances");
//				Miner m = mc.getMiners().getBody().iterator().next();
//				while(true) {
//					Stream s = new Stream("Process-" + new Random().nextInt(5), "broker.host.com", "pmcep");
//					MinerInstanceConfiguration mic = new MinerInstanceConfiguration(s, new ArrayList<MinerParameterValue>());
//					ic.createInstance(m.getId(), mic);
//					
//					try {
//						Thread.sleep(10* 1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
//		
//	}
}
