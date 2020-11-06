package pmcep.web.miner.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pmcep.logger.Logger;
import pmcep.miners.type.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.miner.models.Miner;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin
public class MinerController {

	private Map<String, Miner> miners = new HashMap<String, Miner>();
	
	@PostConstruct
	public void init() {
		Class<AbstractMiner> minerSuperClazz = AbstractMiner.class;
		Reflections reflections = new Reflections("pmcep");
		for (Class<?> clazz : reflections.getTypesAnnotatedWith(ExposedMiner.class)) {
			if (minerSuperClazz.isAssignableFrom(clazz)) {
				ExposedMiner em = clazz.getAnnotation(ExposedMiner.class);
				@SuppressWarnings("unchecked")
				Miner m = new Miner(em, (Class<AbstractMiner>) clazz);
				miners.put(m.getId(), m);
			} else {
				Logger.instance().debug("Class " + clazz + " annotated identified but missed proper inheritance (" + minerSuperClazz.getCanonicalName() + ")");
			}
		}
	}
	
	@GetMapping(
		value = "/miners",
		produces = { "application/json" })
	public ResponseEntity<Collection<Miner>> getMiners() {
		return new ResponseEntity<Collection<Miner>>(miners.values(), HttpStatus.OK);
	}
	
	public boolean minerExists(String id) {
		return miners.containsKey(id);
	}
	
	public Miner getById(String id) {
		return miners.get(id);
	}
}
