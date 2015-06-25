package br.eti.clairton.jpa.serializer.model;

import javax.persistence.OneToOne;

import net.vidageek.mirror.dsl.Mirror;

public class ModelWithEmbedded extends Model {
	private static final long serialVersionUID = 6016230217349046379L;

	@OneToOne
	private final Aplicacao aplicacao;

	private String a = "b";

	public ModelWithEmbedded() {
		aplicacao = new Aplicacao("Teste");
		Mirror mirror = new Mirror();
		final Recurso recurso = new Recurso(null, "inserir");
		mirror.on(recurso).set().field("id").withValue(456l);
		aplicacao.adicionar(recurso);
		mirror.on(aplicacao).set().field("id").withValue(100l);
	}

	public Aplicacao getAplicacao() {
		return aplicacao;
	}

	public String getA() {
		return a;
	}
}