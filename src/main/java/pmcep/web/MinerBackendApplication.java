package pmcep.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@SpringBootApplication
public class MinerBackendApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MinerBackendApplication.class, args);
	}
}


@RestController
@CrossOrigin
class S {

	
//	public static String storeFile(String content) throws IOException {
//		File tempFile = File.createTempFile(UUID.randomUUID().toString(), "");
//		tempFile.deleteOnExit();
//		
//		Files.writeString(tempFile.toPath(), content, StandardCharsets.UTF_8);
//		System.out.println(tempFile.getAbsolutePath());
//		return tempFile.getName();
//	}
	
	@GetMapping("/files/{fileId}")
	public ResponseEntity<Resource> instanceStart(@PathVariable("fileId") String fileId) throws FileNotFoundException {
		System.out.println("bla");
		File f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileId);
		System.out.println(f.getAbsolutePath());
		if (!f.exists()) {
			return ResponseEntity.notFound().build();
		}
		InputStreamResource resource = new InputStreamResource(new FileInputStream(f));
		
//		if (!map.containsKey(fileId)) {
//			return ResponseEntity.notFound().build();
//		}
//		File f = map.get(fileId);
//		InputStreamResource resource = new InputStreamResource(new FileInputStream(f));
//
		return ResponseEntity
				.ok()
				.contentLength(f.length())
				.body(resource);
	}
}

