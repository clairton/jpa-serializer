package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.JpaDeserializer;
import br.eti.clairton.jpa.serializer.Mode;
import br.eti.clairton.jpa.serializer.model.ModelWithEmbedded;

public class ModelWithEmbeddedDeserializer extends JpaDeserializer<ModelWithEmbedded> {

	public ModelWithEmbeddedDeserializer(final EntityManager entityManager) {
		super(entityManager);
		nodes().put("aplicacao", Mode.RECORD);
		nodes().put("recursos", Mode.RECORD);
	}
}
