package dev.riddle.ironinvoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IroninvoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IroninvoiceApplication.class, args);
	}

}
