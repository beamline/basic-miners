package beamline.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ComponentScan({"beamline.core"})
public class MinerBackendApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MinerBackendApplication.class, args);
	}
}


@RestController
@CrossOrigin
class FileServer {
	
	@GetMapping("/files/{fileType}/{fileId}")
	public ResponseEntity<Resource> instanceStart(@PathVariable("fileType") String fileType, @PathVariable("fileId") String fileId) throws FileNotFoundException {
		File f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileId);
		if (!f.exists()) {
			return ResponseEntity.notFound().build();
		}
		InputStreamResource resource = new InputStreamResource(new FileInputStream(f));
		
		return ResponseEntity
				.ok()
				.contentLength(f.length())
				.contentType(MediaType.APPLICATION_XML)
				.body(resource);
	}
}
