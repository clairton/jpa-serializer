package br.eti.clairton.jpa.serializer;

import java.util.HashMap;
import java.util.Map;

public class Nodes extends HashMap<String, Node>implements Map<String, Node> {
	private static final long serialVersionUID = 1L;
	private final Node defaultNode = new Node(Mode.ID);

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
		return is(key, Mode.RELOAD);
	}

	public Boolean isRecord(final Object key) {
		return is(key, Mode.RECORD);
	}

	public Boolean isId(final Object key) {
		return is(key, Mode.ID);
	}

	public Boolean isIgnore(final Object key) {
		return is(key, Mode.IGNORE);
	}

	public Boolean is(final Object key, final Mode mode) {
		final Node node = get(key);
		return node.getMode().equals(mode) && 
					node.getOperations().contains(Operation.DESERIALIZE) &&
						node.getOperations().contains(Operation.SERIALIZE);
	}

	public Boolean is(final Object key, final Mode mode, final Operation operation) {
		final Node node = get(key);
		return node.getMode().equals(mode) && 
					node.getOperations().contains(operation);
	}

	public Boolean isReload(final Object key, final Operation operation) {
		return is(key, Mode.RELOAD, operation);
	}

	public Boolean isRecord(final Object key, final Operation operation) {
		return is(key, Mode.RECORD, operation);
	}

	public Boolean isId(final Object key, final Operation operation) {
		return is(key, Mode.ID, operation);
	}

	public Boolean isIgnore(final Object key, final Operation operation) {
		return is(key, Mode.IGNORE, operation);
	}
}
