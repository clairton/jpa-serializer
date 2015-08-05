package br.eti.clairton.jpa.serializer;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.eti.clairton.jpa.serializer.model.Aplicacao;
import br.eti.clairton.jpa.serializer.model.Recurso;
import br.eti.clairton.jpa.serializer.serializers.AplicacaoAutoRelationDeserializer;

public class JpaDeserializerAutoRelationTest {
	final Logger logger = LogManager.getLogger(JpaDeserializerAutoRelationTest.class);
	private Gson gson;

	@Before
	public void init() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Aplicacao.class, new AplicacaoAutoRelationDeserializer(null));
		builder.registerTypeAdapter(Recurso.class, new JpaDeserializer<Recurso>(null));
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