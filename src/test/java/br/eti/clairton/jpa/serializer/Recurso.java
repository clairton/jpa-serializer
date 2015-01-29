package br.eti.clairton.jpa.serializer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "recursos")
public class Recurso extends Model {
	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.ALL)
	private final Aplicacao aplicacao;

	private final String nome;

	@Deprecated
	public Recurso() {
		this(null, null);
	}

	public Recurso(final Aplicacao aplicacao, final String nome) {
		super();
		this.nome = nome;
		this.aplicacao = aplicacao;
	}

	public String getNome() {
		return nome;
	}

	public Aplicacao getAplicacao() {
		return aplicacao;
	}
}
