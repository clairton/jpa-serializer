package br.eti.clairton.jpa.serializer;

import static javax.persistence.Persistence.createEntityManagerFactory;
import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Test;

import br.eti.clairton.jpa.serializer.model.Aplicacao;
import br.eti.clairton.jpa.serializer.model.ModelWithEmbedded;
import br.eti.clairton.jpa.serializer.model.Recurso;
import br.eti.clairton.jpa.serializer.serializers.ModelWithEmbeddedDeserializer;
import br.eti.clairton.jpa.serializer.serializers.ModelWithEmbeddedSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JpaSerializatorEmbeddedTest {
	private Gson gson;
	private final String json = "{\"aplicacao\":{\"recursos\":[456],\"nome\":\"Teste\",\"id\":100},\"a\":\"b\",\"recursos\":[{\"nome\":\"inserir\",\"id\":456}]}";
	private ModelWithEmbedded object = new ModelWithEmbedded();
	private final Class<ModelWithEmbedded> t = ModelWithEmbedded.class;

	@Before
	public void init() {
		final GsonBuilder builder = new GsonBuilder();
		final EntityManagerFactory emf = createEntityManagerFactory("default");
		final EntityManager em = emf.createEntityManager();
		builder.registerTypeAdapter(t, new ModelWithEmbeddedSerializer());
		builder.registerTypeAdapter(t, new ModelWithEmbeddedDeserializer(em));
		builder.registerTypeAdapter(Aplicacao.class, new JpaDeserializer<Aplicacao>(em));
		builder.registerTypeAdapter(Recurso.class, new JpaDeserializer<Recurso>(em));
		gson = builder.create();
	}

	@Test
	public void testSerialize() {
		final String toCompare = "{\"aplicacao\":{\"recursos\":[{\"nome\":\"inserir\",\"id\":456}],\"nome\":\"Teste\",\"id\":100},\"a\":\"b\",\"recursos\":[456]}";
		final String json = gson.toJson(object, t);
		assertEquals(toCompare, json);
	}

	@Test
	public void testDeserialize() {
		final ModelWithEmbedded object = gson.fromJson(json, t);
		assertEquals(this.object.getAplicacao().getId(), object.getAplicacao().getId());
		assertEquals(this.object.getA(), object.getA());
		assertEquals(this.object.getRecursos().get(0).getId(), object.getRecursos().get(0).getId());
		assertEquals(object, object.getRecursos().get(0).getEmbedded());
		assertEquals(this.object.getAplicacao().getNome(), object.getAplicacao().getNome());
		assertEquals(this.object.getAplicacao().getRecursos().size(), object.getAplicacao().getRecursos().size());
		assertEquals(this.object.getAplicacao().getRecursos().get(0).getId(), object.getAplicacao().getRecursos().get(0).getId());
	}
}
