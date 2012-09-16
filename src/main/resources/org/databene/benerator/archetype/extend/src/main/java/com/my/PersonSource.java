package com.my;

import java.util.Arrays;
import java.util.List;

import org.databene.commons.HeavyweightIterator;
import org.databene.commons.iterator.HeavyweightIteratorAdapter;
import org.databene.commons.iterator.HeavyweightIteratorProxy;
import org.databene.model.data.AbstractEntitySource;
import org.databene.model.data.Entity;

public class PersonSource extends AbstractEntitySource {
	
	private List<Entity> list;
	
	public PersonSource() {
		list = Arrays.asList(
			new Entity("Person", "firstname", "Alice"),
			new Entity("Person", "firstname", "Bob"),
			new Entity("Person", "firstname", "Charly")
		);
	}

	public HeavyweightIterator<Entity> iterator() {
		return new HeavyweightIteratorProxy<Entity>(list.iterator());
	}

}
