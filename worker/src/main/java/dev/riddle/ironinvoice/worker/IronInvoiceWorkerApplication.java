package dev.riddle.ironinvoice.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EntityScan(basePackages = {
	"dev.riddle.ironinvoice.shared",
	"dev.riddle.ironinvoice.worker"
})
public class IronInvoiceWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IronInvoiceWorkerApplication.class, args);
	}

}
