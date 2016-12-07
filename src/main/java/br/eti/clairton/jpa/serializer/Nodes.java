package br.eti.clairton.jpa.serializer;

import static br.eti.clairton.jpa.serializer.Mode.ID;
import static br.eti.clairton.jpa.serializer.Mode.ID_POLYMORPHIC;
import static br.eti.clairton.jpa.serializer.Mode.IGNORE;
import static br.eti.clairton.jpa.serializer.Mode.RECORD;
import static br.eti.clairton.jpa.serializer.Mode.RELOAD;
import static br.eti.clairton.jpa.serializer.Operation.DESERIALIZE;
import static br.eti.clairton.jpa.serializer.Operation.SERIALIZE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Nodes extends HashMap<String, Node> implements Map<String, Node>, Iterable<Node> {
	{
		put("serialVersionUID", IGNORE);
		put("MIRROR", IGNORE);
		put("logger", IGNORE);
		put("STYLE", IGNORE);
	}
	private static final long serialVersionUID = 1L;
	private final Node defaultNode = new Node(ID);

	@Override
	public Node get(final Object key) {
		final Node node = super.get(key);
		return node == null ? defaultNode : node;
	}

	public Node get(final Object key, final Operation operation) {
		final Node node = super.get(key);
		if (node == null || !node.getOperations().contains(operation)) {
			return defaultNode;
		} else {
			return node;
		}
	}

	public void put(final String key, final Mode mode) {
		put(key, new Node(mode));
	}

	public void put(final String key, final Mode mode, final Operation operation) {
		put(key, new Node(mode, operation));
	}

	public Boolean isReload(final Object key) {
		return is(key, RELOAD);
	}

	public Boolean isRecord(final Object key) {
		return is(key, RECORD);
	}

	public Boolean isId(final Object key) {
		return is(key, ID) || isIdPolymorphic(key);
	}

	public Boolean isIdPolymorphic(final Object key) {
		return is(key, ID_POLYMORPHIC);
	}
	
	public Boolean isIdPolymorphic(final Object key, final Operation operation) {
		return is(key, ID_POLYMORPHIC, operation);
	}

	public Boolean isIgnore(final Object key) {
		return is(key, IGNORE);
	}

	public Boolean is(final Object key, final Mode mode) {
		final Node node = get(key);
		return node.getMode().equals(mode) && 
					node.getOperations().contains(DESERIALIZE) &&
						node.getOperations().contains(SERIALIZE);
	}

	public Boolean is(final Object key, final Mode mode, final Operation operation) {
		final Node node = get(key);
		return node.getMode().equals(mode) && 
					node.getOperations().contains(operation);
	}

	public Boolean isReload(final Object key, final Operation operation) {
		return is(key, RELOAD, operation);
	}

	public Boolean isRecord(final Object key, final Operation operation) {
		return is(key, RECORD, operation);
	}

	public Boolean isId(final Object key, final Operation operation) {
		return is(key, ID, operation) || isIdPolymorphic(key, operation);
	}

	public Boolean isIgnore(final Object key, final Operation operation) {
		return is(key, IGNORE, operation);
	}

	@Override
	public Iterator<Node> iterator() {
		return this.values().iterator();
	}
}
