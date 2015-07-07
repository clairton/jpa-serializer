package br.eti.clairton.jpa.serializer.serializers;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.jpa.serializer.Mode;
import br.eti.clairton.jpa.serializer.model.ModelWithEmbedded;

public class ModelWithEmbeddedSerializer extends JpaSerializer<ModelWithEmbedded> {

	public ModelWithEmbeddedSerializer() {
		nodes().put("aplicacao", Mode.RECORD);
		nodes().put("recurso", Mode.RECORD);
	}
}
