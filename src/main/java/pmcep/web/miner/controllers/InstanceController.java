package pmcep.web.miner.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pmcep.logger.Logger;
import pmcep.miners.type.AbstractMiner;
import pmcep.web.miner.models.Miner;
import pmcep.web.miner.models.MinerInstance;
import pmcep.web.miner.models.MinerInstanceConfiguration;


@RestController
@RequestMapping("/api/v1/")
public class InstanceController {

	@Autowired
	private MinerController minerController;
	private Map<String, MinerInstance> instances = new HashMap<String, MinerInstance>();
	
	@GetMapping(
		value = "/instances",
		produces = { "application/json" })
	public ResponseEntity<Collection<MinerInstance>> getInstances() {
		return new ResponseEntity<Collection<MinerInstance>>(instances.values(), HttpStatus.OK);
	}
	
	@PostMapping("/instances/{minerId}")
	public ResponseEntity<Void> createInstance(@PathVariable("minerId") String minerId, @RequestBody MinerInstanceConfiguration configuration) {
		if (!minerController.minerExists(minerId)) {
			return ResponseEntity.notFound().build();
		}
		
		// create an instance of the miner
		try {
			Miner miner = minerController.getById(minerId);
			Class<AbstractMiner> clazz = miner.getMinerClass();
			AbstractMiner minerObject = clazz.getDeclaredConstructor().newInstance();
			minerObject.setStream(configuration.getStream());
			minerObject.configure(configuration.getParameterValues());
			
			MinerInstance mi = new MinerInstance(miner, configuration);
			mi.setMinerObject(minerObject);
			
			instances.put(mi.getId(), mi);
			
		} catch (Exception e) {
			Logger.instance().error(e);
		}
		
		return ResponseEntity.ok().build();
	}
}
