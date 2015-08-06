package br.eti.clairton.jpa.serializer.serializers;

import java.lang.reflect.Type;

import javax.persistence.EntityManager;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.jpa.serializer.Mode;
import br.eti.clairton.jpa.serializer.Operation;
import br.eti.clairton.jpa.serializer.model.SuperAplicacao;

public class SuperAplicacaoDeserializer extends JpaSerializer<SuperAplicacao>{

	public SuperAplicacaoDeserializer(final EntityManager em) {
		super(em);
		nodes().put("recursos", Mode.RELOAD, Operation.DESERIALIZE);
	}

	@Override
	public SuperAplicacao deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
		return super.deserialize(json, type, context);
	}
}
