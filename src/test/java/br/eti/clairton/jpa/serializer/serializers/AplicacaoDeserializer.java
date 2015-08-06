package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.GsonJpaSerializer;
import br.eti.clairton.jpa.serializer.model.Aplicacao;

public class AplicacaoDeserializer extends GsonJpaSerializer<Aplicacao> {
	private static final long serialVersionUID = 1L;

	public AplicacaoDeserializer(final EntityManager entityManager) {
		super(entityManager);
	}

}
