package br.eti.clairton.jpa.serializer.serializers;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.jpa.serializer.model.OutroModel;

public class OutroModelSerializer extends JpaSerializer<OutroModel>{

	public OutroModelSerializer() {
		addIgnoredField("nome");
	}
}
