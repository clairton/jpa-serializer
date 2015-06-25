package br.eti.clairton.jpa.serializer;

import br.eti.clairton.jpa.serializer.model.ModelWithEmbedded;

public class ModelWithEmbeddedSerializer extends JpaSerializer<ModelWithEmbedded> {

	public ModelWithEmbeddedSerializer() {
		nodes().put("aplicacao", Mode.RECORD);
		nodes().put("recurso", Mode.RECORD);
	}
}
