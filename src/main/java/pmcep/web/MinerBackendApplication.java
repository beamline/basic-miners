package pmcep.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MinerBackendApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MinerBackendApplication.class, args);
	}
}

@RestController
@CrossOrigin
class FileServer {
	
	@GetMapping("/files/{fileId}")
	public ResponseEntity<Resource> instanceStart(@PathVariable("fileId") String fileId) throws FileNotFoundException {
		File f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileId);
		if (!f.exists()) {
			return ResponseEntity.notFound().build();
		}
		InputStreamResource resource = new InputStreamResource(new FileInputStream(f));
		
		return ResponseEntity
				.ok()
				.contentLength(f.length())
				.body(resource);
	}
}

