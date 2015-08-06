package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.jpa.serializer.Operation;
import br.eti.clairton.jpa.serializer.model.OutroModel;

public class OutroModelSerializer extends JpaSerializer<OutroModel>{

	public OutroModelSerializer(EntityManager em) {
		super(em);
		ignore("nome", Operation.SERIALIZE);
	}
}
