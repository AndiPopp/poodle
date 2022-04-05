package de.andipopp.poodle.data.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import com.vaadin.flow.spring.annotation.SpringComponent;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.service.ConfigService;

@SpringComponent
public class ConfigLoader {

	public static ConfigService configService;
	
	@Bean("configLoaderRunner")
	@Order(55)
	public CommandLineRunner loadData(ConfigService configService) {
		return args -> {
			Logger logger = LoggerFactory.getLogger(getClass());
			ConfigLoader.configService = configService;
			logger.info("Config service hooked up. Has "+configService.count()+" configs");
			Config.setCurrentConfig(configService.getDefaultConfig());
		};
	}
}
