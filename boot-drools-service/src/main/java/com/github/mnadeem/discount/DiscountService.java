package com.github.mnadeem.discount;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.mnadeem.discount.model.Sale;

@Service
public class DiscountService {

    private static final String SALE_IDENTIFIER = "Sale";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountService.class);

    @Value("${kie.containerId}")
	private String containerId;
    @Value("${kie.server.user}")
    private String user;
    @Value("${kie.server.password}")
    private String password;
    @Value("${kie.server.url}")
    private String url;

	public void applyDiscount(Sale sale) {

		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, user, password, 60000);
        config.setMarshallingFormat(MarshallingFormat.JSON);

		RuleServicesClient client = KieServicesFactory.newKieServicesClient(config).getServicesClient(RuleServicesClient.class);
		BatchExecutionCommand batchExecutionCommand = batchCommand(sale);
		ServiceResponse<ExecutionResults> response = client.executeCommandsWithResults(containerId, batchExecutionCommand);
 
		if (response.getType() == ServiceResponse.ResponseType.SUCCESS) {
			LOGGER.info("Commands executed with success! Response: ");
			LOGGER.info("{}", response.getResult());
			Sale saleUpdated = (Sale) response.getResult().getValue(SALE_IDENTIFIER);
			sale.setDiscount(saleUpdated.getDiscount());
			LOGGER.info("{}", saleUpdated);
		} else {
			LOGGER.error("Error executing rules. Message: {}", response.getMsg());
		}
	}

	private BatchExecutionCommand batchCommand(Sale sale) {
		List<Command<?>> cmds = buildCommands(sale);

		BatchExecutionCommand batchExecutionCommand = CommandFactory.newBatchExecution(cmds);
		return batchExecutionCommand;
	}

	private List<Command<?>> buildCommands(Sale sale) {
		List<Command<?>> cmds = new ArrayList<Command<?>>();
		KieCommands commands = KieServices.Factory.get().getCommands();

		cmds.add(commands.newInsert(sale, SALE_IDENTIFIER));
		cmds.add(commands.newFireAllRules());
		return cmds;
	}
}
