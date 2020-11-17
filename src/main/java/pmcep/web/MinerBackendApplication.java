package pmcep.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;
import java.io.PrintWriter;

@SpringBootApplication
public class MinerBackendApplication {

	public static void main(String[] args) throws InterruptedException {
		Console console = System.console();
		if(console != null) {
			PrintWriter pw = console.writer();
			pw.println("Console class writer() method example");
		}else {
			System.out.println("Console is null");
		}
		SpringApplication.run(MinerBackendApplication.class, args);
	}
}
