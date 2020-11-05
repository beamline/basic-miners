package pmcep.web.miner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/")
public class InstancesController {

	@Autowired
	private MinerController minerController;
	@Autowired
	private StreamController streamController;
	
	@PostMapping("/instances/{streamId}/{minerId}")
	public ResponseEntity<Void> createInstance(@PathVariable("streamId") String streamId, @PathVariable("minerId") String minerId) {
		if (!minerController.minerExists(minerId)) {
			return ResponseEntity.notFound().build();
		}
		if (!streamController.streamExists(streamId)) {
			return ResponseEntity.notFound().build();
		}
		
		System.out.println(streamId);
		System.out.println(minerId);
		
		return ResponseEntity.ok().build();
	}
}
