package br.eti.clairton.jpa.serializer;

import static javax.persistence.Persistence.createEntityManagerFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.eti.clairton.jpa.serializer.model.Aplicacao;
import br.eti.clairton.jpa.serializer.model.Recurso;
import net.vidageek.mirror.dsl.Mirror;

public class JpaSerializerRecordModeTest {
	private EntityManager em;

	@Before
	public void init() {
		final EntityManagerFactory emf = createEntityManagerFactory("default");
		em = emf.createEntityManager();
	}

	@Test
	public void testManyToOneDeserialize() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Aplicacao.class, new GsonJpaSerializer<Aplicacao>(em) {
			private static final long serialVersionUID = 1L;

			{
				record("aplicacao", Operation.DESERIALIZE);
			}
		});
		final Gson gson = builder.create();
		final Aplicacao toSerialize =  new Aplicacao(new Aplicacao("simulador"));
		new Mirror().on(toSerialize.getAplicacao()).set().field("id").withValue(1001l);
		assertEquals("{\"aplicacao\":1001,\"aplicacoes\":[],\"recursos\":[]}", gson.toJson(toSerialize));
		final Aplicacao deserialized = gson.fromJson("{\"aplicacao\":{\"nome\":\"simulador\"}}", Aplicacao.class);
		assertNull(deserialized.getAplicacao().getId());
		assertEquals("simulador", deserialized.getAplicacao().getNome());
	}

	@Test
	public void testManyToOneSerialize() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Aplicacao.class, new GsonJpaSerializer<Aplicacao>(em) {
			private static final long serialVersionUID = 1L;

			{
				record("aplicacao", Operation.SERIALIZE);
			}
		});
		final Gson gson = builder.create();
		final Aplicacao toSerialize =  new Aplicacao(new Aplicacao("simulador"));
		assertEquals("{\"aplicacao\":{\"aplicacoes\":[],\"recursos\":[],\"nome\":\"simulador\"},\"aplicacoes\":[],\"recursos\":[]}", gson.toJson(toSerialize));
		final Aplicacao deserialized = gson.fromJson("{\"aplicacao\":1001}", Aplicacao.class);
		assertNull(deserialized.getAplicacao().getNome());
		assertEquals(Long.valueOf("1001"), deserialized.getAplicacao().getId());
	}

	@Test
	public void testOneToManySerialize() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Aplicacao.class, new GsonJpaSerializer<Aplicacao>(em) {
			private static final long serialVersionUID = 1L;

			{
				record("recursos", Operation.SERIALIZE);
			}
		});
		final Gson gson = builder.create();
		final Aplicacao toSerialize = new Aplicacao(null, new Recurso("index"));
		assertEquals("{\"aplicacoes\":[],\"recursos\":[{\"nome\":\"index\"}]}", gson.toJson(toSerialize));
		final Aplicacao deserialized = gson.fromJson("{\"recursos\":[1001]}", Aplicacao.class);
		assertEquals(1, deserialized.getRecursos().size());
		assertEquals(Long.valueOf("1001"), deserialized.getRecursos().get(0).getId());
		assertNull(deserialized.getRecursos().get(0).getNome());
	}

	@Test
	public void testOneToManyDeserialize() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Aplicacao.class, new GsonJpaSerializer<Aplicacao>(em) {
			private static final long serialVersionUID = 1L;

			{
				record("recursos", Operation.DESERIALIZE);
			}
		});
		final Gson gson = builder.create();
		final Aplicacao toSerialize = new Aplicacao(null, new Recurso("index"));
		new Mirror().on(toSerialize.getRecursos().get(0)).set().field("id").withValue(1001l);
		assertEquals("{\"aplicacoes\":[],\"recursos\":[1001]}", gson.toJson(toSerialize));
		final Aplicacao deserialized = gson.fromJson("{\"recursos\":[{\"nome\":\"index\"}]}", Aplicacao.class);
		assertEquals(1, deserialized.getRecursos().size());
		assertEquals("index", deserialized.getRecursos().get(0).getNome());
	}
}
