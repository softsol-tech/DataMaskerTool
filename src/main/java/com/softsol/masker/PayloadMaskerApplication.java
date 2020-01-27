package com.softsol.masker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    MaskConfigProperties.class
})
public class PayloadMaskerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayloadMaskerApplication.class, args);
	}

}
