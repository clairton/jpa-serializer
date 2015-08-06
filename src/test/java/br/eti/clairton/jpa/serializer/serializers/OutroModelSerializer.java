package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.GsonJpaSerializer;
import br.eti.clairton.jpa.serializer.Operation;
import br.eti.clairton.jpa.serializer.model.OutroModel;

public class OutroModelSerializer extends GsonJpaSerializer<OutroModel>{
	private static final long serialVersionUID = 1L;

	public OutroModelSerializer(EntityManager em) {
		super(em);
		ignore("nome", Operation.SERIALIZE);
	}
}
