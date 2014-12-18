package org.anc.lapps.masc;

//import org.spring

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Keith Suderman
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class SpringEntryPoint
{
	private static final Logger logger = LoggerFactory.getLogger(SpringEntryPoint.class);

	public SpringEntryPoint()
	{

	}

	public static void main(String[] args)
	{
		logger.info("Launching Spring Framework");
		SpringApplication.run(SpringEntryPoint.class);
	}
}
