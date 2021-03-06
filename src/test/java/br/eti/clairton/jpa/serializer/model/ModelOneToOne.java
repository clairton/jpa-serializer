package br.eti.clairton.jpa.serializer.model;

import javax.persistence.OneToOne;

import net.vidageek.mirror.dsl.Mirror;

public class ModelOneToOne extends Model {
	private static final long serialVersionUID = 6016230217349046379L;

	@OneToOne
	private final Aplicacao aplicacao;

	public ModelOneToOne() {
		aplicacao = new Aplicacao("Teste");
		new Mirror().on(aplicacao).set().field("id").withValue(100l);
	}

	public Aplicacao getAplicacao() {
		return aplicacao;
	}
}