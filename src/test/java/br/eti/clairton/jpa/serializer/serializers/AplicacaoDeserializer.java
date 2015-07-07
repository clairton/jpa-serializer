package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.JpaDeserializer;
import br.eti.clairton.jpa.serializer.model.Aplicacao;

public class AplicacaoDeserializer extends JpaDeserializer<Aplicacao> {

	public AplicacaoDeserializer(final EntityManager entityManager) {
		super(entityManager);
	}

}
