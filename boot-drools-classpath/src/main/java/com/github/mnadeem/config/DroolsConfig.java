package com.github.mnadeem.config;

import java.io.IOException;

import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(DroolsConfig.class);

	@Bean
	public KieContainer kieContainer() throws IOException {

		KieContainer kContainer = kieServices().newKieClasspathContainer();
		// Let's verify that all the resources are loaded correctly
		Results results = kContainer.verify();
		results.getMessages().stream().forEach((message) -> {
			LOGGER.info(">> Message ( {} ): {}", message.getLevel(), message.getText());
		});
		// If there is an Error we need to stop and correct it
		boolean hasError = results.hasMessages(Message.Level.ERROR);
		LOGGER.info("Any Error : {}", hasError);
		if (hasError) {
			throw new UnsupportedOperationException();
		}
		// Here we make sure that all the KieBases and KieSessions
		// that we are expecting are loaded.
		kContainer.getKieBaseNames().stream().map((kieBase) -> {
			LOGGER.info(">> Loading KieBase: {}", kieBase);
			return kieBase;
		}).forEach((kieBase) -> {
			kContainer.getKieSessionNamesInKieBase(kieBase).stream().forEach((kieSession) -> {
				LOGGER.info("\t >> Containing KieSession: {}", kieSession);
			});
		});

		return kContainer;
	}

	private KieServices kieServices() {
		return KieServices.Factory.get();
	}
}