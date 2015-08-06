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
		return get(key).getMode().equals(Mode.RELOAD);
	}

	public Boolean isRecord(final Object key) {
		return get(key).getMode().equals(Mode.RECORD);
	}

	public Boolean isId(final Object key) {
		return get(key).getMode().equals(Mode.ID);
	}

	public Boolean isIgnore(final Object key) {
		return get(key).getMode().equals(Mode.IGNORE);
	}

	public Boolean isReload(final Object key, final Operation operation) {
		final Node node = get(key);
		return node.getMode().equals(Mode.RELOAD) && node.getOperations().contains(operation);
	}

	public Boolean isRecord(final Object key, final Operation operation) {
		final Node node = get(key);
		return node.getMode().equals(Mode.RECORD) && node.getOperations().contains(operation);
	}

	public Boolean isId(final Object key, final Operation operation) {
		final Node node = get(key);
		return node.getMode().equals(Mode.ID) && node.getOperations().contains(operation);
	}

	public Boolean isIgnore(final Object key, final Operation operation) {
		final Node node = get(key);
		return node.getMode().equals(Mode.IGNORE) && node.getOperations().contains(operation);
	}
}
