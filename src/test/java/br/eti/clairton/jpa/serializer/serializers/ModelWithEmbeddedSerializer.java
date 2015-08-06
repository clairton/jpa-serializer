package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.jpa.serializer.model.ModelWithEmbedded;

public class ModelWithEmbeddedSerializer extends JpaSerializer<ModelWithEmbedded> {

	public ModelWithEmbeddedSerializer(EntityManager em) {
		super(em);
		record("aplicacao");
		record("recursos");
	}
}
