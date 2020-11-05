package pmcep.web.miner.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pmcep.web.miner.models.Stream;

@RestController
@RequestMapping("/api/v1/")
public class StreamController {

	private Map<String, Stream> streams = new HashMap<String, Stream>();
	
	@GetMapping(
		value = "/streams",
		produces = { "application/json" })
	public ResponseEntity<Collection<Stream>> getStreams() {
		return ResponseEntity.ok(streams.values());
	}
	
	@PostMapping("/streams")
	public ResponseEntity<Void> addStream(@RequestBody Stream stream) {
		Stream s = Stream.copy(stream);
		streams.put(s.getId(), s);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/streams/{streamId}")
	public ResponseEntity<Void> deleteStream(@PathVariable("streamId") String streamId) {
		if (streams.containsKey(streamId)) {
			streams.remove(streamId);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

	public boolean streamExists(String streamId) {
		return streams.containsKey(streamId);
	}
}
