package pmcep.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;
import java.io.PrintWriter;

@SpringBootApplication
public class MinerBackendApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MinerBackendApplication.class, args);
	}
}
