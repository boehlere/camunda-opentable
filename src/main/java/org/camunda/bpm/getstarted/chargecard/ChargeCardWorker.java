package org.camunda.bpm.getstarted.chargecard;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.getstarted.exception.BpmnException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.awt.Desktop;
import java.net.URI;

public class ChargeCardWorker {
    private final static Logger LOGGER = Logger.getLogger(ChargeCardWorker.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // long polling timeout
                .build();

        // subscribe to an external task topic as specified in the process
        client.subscribe("charge-card")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Put your business logic here

                    // Get a process variable
                    try {
                        String item = (String) externalTask.getVariable("item");
                        Long amount = (Long) externalTask.getVariable("amount");
                        Long saved = (Long) externalTask.getVariable("saved");

                        LOGGER.info("Charging credit card with an amount of '" + amount + "'€ for the item '" + item + "'...");

                        if (saved < amount) {
                            throw new BpmnException("NO-M");
                        }

                        // Complete the task

                        externalTaskService.complete(externalTask);
                    } catch (BpmnException e) {
                        externalTaskService.handleBpmnError(externalTask, e.getErrorCode());
                    }
                })
                .open();
        // subscribe to an external task topic as specified in the process
        client.subscribe("save-money")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Put your business logic here

                    // Get a process variable
                    Long amount = (Long) externalTask.getVariable("amountToSave");

                    LOGGER.info("You successfully saved " + amount + "€!");
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("saved", amount);
                    // Complete the task
                    externalTaskService.complete(externalTask, variables);
                })
                .open();
        client.subscribe("done")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Put your business logic here

                    LOGGER.info("Done");

                    try {
                        Desktop.getDesktop().browse(new URI("https://www.memesmonkey.com/images/memesmonkey/c2/c20f0cf8513d915a77bf06c35276c7e5.jpeg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
        client.subscribe("error")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Put your business logic here

                    LOGGER.info("Not enough Money");

                    try {
                        Desktop.getDesktop().browse(new URI("https://www.memesmonkey.com/images/memesmonkey/e4/e4133a6cf828bba265fb923f04dad8c0.jpeg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}
