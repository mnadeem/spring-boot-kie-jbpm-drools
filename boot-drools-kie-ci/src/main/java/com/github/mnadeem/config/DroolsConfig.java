package com.github.mnadeem.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.scanner.KieModuleMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(DroolsConfig.class);

	@Bean
	public KieContainer kieContainer() {

		KieServices ks = kieServices();
		KieContainer kContainer = ks.newKieContainer(discountsKJar(ks));
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

		KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(discountsKJar(ks));

		kieModuleMetaData.getPackages().stream().map((pkg) -> {
			LOGGER.info(" >> Package Loaded:  {}", pkg);
			return pkg;
		}).forEach((pkg) -> {
			kieModuleMetaData.getRuleNamesInPackage(pkg).stream().forEach((rule) -> {
				LOGGER.info("\t >> Contain Rule:  {}", rule);
			});
		});

		KieScanner scanner = ks.newKieScanner(kContainer);
		//Configure the scanner to check for new versions every 10 seconds
		scanner.start(10_000);

		return kContainer;
	}

	private ReleaseId discountsKJar(KieServices ks) {
		return ks.newReleaseId("com.github.mnadeem", "drools-discount-kjar", "0.0.1-RELEASE");
	}

	private KieServices kieServices() {
		return KieServices.Factory.get();
	}
}