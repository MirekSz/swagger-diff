
package com.deepoove.swagger.diff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SwaggerDiffApplication {

	private static Logger logger = LoggerFactory.getLogger(SwaggerDiffApplication.class);

	public static void main(final String[] args) {

		SpringApplication.run(SwaggerDiffApplication.class, args);
	}

}
