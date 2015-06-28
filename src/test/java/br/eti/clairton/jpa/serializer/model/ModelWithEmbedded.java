package br.eti.clairton.jpa.serializer.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.vidageek.mirror.dsl.Mirror;

@Entity
public class ModelWithEmbedded extends Model {
	private static final long serialVersionUID = 6016230217349046379L;

	@OneToOne
	private Aplicacao aplicacao;

	private String a = "b";

	@OneToMany(mappedBy="embedded")
	private List<Recurso> recursos = new ArrayList<Recurso>();

	public ModelWithEmbedded() {
		aplicacao = new Aplicacao("Teste");
		Mirror mirror = new Mirror();
		final Recurso recurso = new Recurso(null, "inserir");
		mirror.on(recurso).set().field("id").withValue(456l);
		aplicacao.adicionar(recurso);
		mirror.on(aplicacao).set().field("id").withValue(100l);
		recursos.add(recurso);
	}

	public Aplicacao getAplicacao() {
		return aplicacao;
	}

	public String getA() {
		return a;
	}

	public List<Recurso> getRecursos() {
		return recursos;
	}
}