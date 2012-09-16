package com.my;

import org.databene.model.consumer.AbstractConsumer;
import org.databene.model.data.Entity;

public class ConsoleConsumer extends AbstractConsumer<Entity> {

	public void startConsuming(Entity entity) {
		System.out.println("Transaction #" + entity.get("id") + " charged $" 
				+ entity.get("amount") + " from credit card " + entity.get("creditcard") 
				+ " of " + entity.get("owner"));
	};
}
