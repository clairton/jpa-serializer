package br.eti.clairton.jpa.serializer;

import static br.eti.clairton.jpa.serializer.Operation.values;
import static java.util.Arrays.asList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Node implements Serializable{
	private static final long serialVersionUID = 1L;
	private final Mode mode;
	private final List<Operation> operations;

	public Node(final Mode mode, final List<Operation> operations) {
		super();
		this.mode = mode;
		this.operations = operations;
	}

	public Node(final Mode mode) {
		this(mode, Arrays.asList(values()));
	}

	public Node(final Mode mode, final Operation operation) {
		this(mode, asList(operation));
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public Mode getMode() {
		return mode;
	}
}
