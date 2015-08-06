package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.jpa.serializer.model.Aplicacao;

public class AplicacaoDeserializer extends JpaSerializer<Aplicacao> {

	public AplicacaoDeserializer(final EntityManager entityManager) {
		super(entityManager);
	}

}
