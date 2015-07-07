package br.eti.clairton.jpa.serializer.model;

import javax.persistence.Entity;

@Entity
public class SuperRecurso extends Recurso {
	private static final long serialVersionUID = 1L;

	@Deprecated
	public SuperRecurso() {
		this(null, null);
	}

	public SuperRecurso(final Aplicacao aplicacao, final String nome) {
		super(aplicacao, nome);
	}
}
