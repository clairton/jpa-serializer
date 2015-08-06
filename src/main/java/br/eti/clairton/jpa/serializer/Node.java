package br.eti.clairton.jpa.serializer;

import java.util.Arrays;
import java.util.List;

public class Node {
	private final Mode mode;
	private final List<Operation> operations;

	public Node(final Mode mode, final List<Operation> operations) {
		super();
		this.mode = mode;
		this.operations = operations;
	}

	public Node(final Mode mode) {
		this(mode, Arrays.asList(Operation.values()));
	}

	public Node(final Mode mode, final Operation operation) {
		this(mode, Arrays.asList(operation));
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public Mode getMode() {
		return mode;
	}
}
