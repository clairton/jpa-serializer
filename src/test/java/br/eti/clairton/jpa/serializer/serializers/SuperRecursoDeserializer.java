package br.eti.clairton.jpa.serializer.serializers;

import java.lang.reflect.Type;

import javax.persistence.EntityManager;

import br.eti.clairton.jpa.serializer.JpaDeserializer;
import br.eti.clairton.jpa.serializer.Mode;
import br.eti.clairton.jpa.serializer.model.SuperRecurso;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class SuperRecursoDeserializer extends JpaDeserializer<SuperRecurso>{

	public SuperRecursoDeserializer(final EntityManager em) {
		super(em);
		nodes().put("aplicacao", Mode.RELOAD);
	}

	@Override
	public SuperRecurso deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
		return super.deserialize(json, type, context);
	}
}
