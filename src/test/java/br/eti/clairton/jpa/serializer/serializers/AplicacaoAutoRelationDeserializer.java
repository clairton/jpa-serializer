package br.eti.clairton.jpa.serializer.serializers;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.jpa.serializer.Mode;
import br.eti.clairton.jpa.serializer.Operation;
import br.eti.clairton.jpa.serializer.model.Aplicacao;

public class AplicacaoAutoRelationDeserializer extends JpaSerializer<Aplicacao> {

	public AplicacaoAutoRelationDeserializer(final EntityManager entityManager) {
		super(entityManager);
		nodes().put("aplicacao", Mode.RECORD, Operation.SERIALIZE);
		nodes().put("recursos", Mode.RECORD, Operation.SERIALIZE);
	}
}
