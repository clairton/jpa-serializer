package br.eti.clairton.jpa.serializer;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.eti.clairton.jpa.serializer.model.Aplicacao;

public class JpaDeserializerAutoRelationTest {
	final Logger logger = LogManager.getLogger(JpaDeserializerAutoRelationTest.class);
	private Gson gson;

	@Before
	public void init() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Aplicacao.class, new GsonJpaSerializer<Aplicacao>(null) {
			private static final long serialVersionUID = 1L;

			{
				nodes(null).put("aplicacao", Mode.RECORD, Operation.DESERIALIZE);
				nodes(null).put("recursos", Mode.RECORD, Operation.DESERIALIZE);
			}
		});
		gson = builder.create();
	}

	@Test
	public void test() {
		final String json = "{nome:'a',aplicacao:{nome:'b',aplicacao:{nome:'c'}},recursos:[{nome:'d'}]}";
		final Aplicacao aplicacao = gson.fromJson(json, Aplicacao.class);
		assertEquals("a", aplicacao.getNome());
		assertEquals("d", aplicacao.getRecursos().get(0).getNome());
		assertEquals(aplicacao, aplicacao.getRecursos().get(0).getAplicacao());
		assertEquals("b", aplicacao.getAplicacao().getNome());
		assertEquals("c", aplicacao.getAplicacao().getAplicacao().getNome());
	}
}
