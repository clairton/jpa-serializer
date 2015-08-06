package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.GsonJpaSerializer;
import br.eti.clairton.jpa.serializer.model.ModelWithEmbedded;

public class ModelWithEmbeddedSerializer extends GsonJpaSerializer<ModelWithEmbedded> {
	private static final long serialVersionUID = 1L;

	public ModelWithEmbeddedSerializer(EntityManager em) {
		super(em);
		record("aplicacao");
		record("recursos");
	}
}
