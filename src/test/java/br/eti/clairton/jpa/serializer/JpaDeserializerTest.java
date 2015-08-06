package br.eti.clairton.jpa.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import br.eti.clairton.jpa.serializer.model.Aplicacao;
import br.eti.clairton.jpa.serializer.model.ModelManyToMany;
import br.eti.clairton.jpa.serializer.model.ModelOneToOne;
import br.eti.clairton.jpa.serializer.model.OutroModel;
import br.eti.clairton.jpa.serializer.model.Recurso;
import br.eti.clairton.jpa.serializer.model.SuperAplicacao;
import br.eti.clairton.jpa.serializer.model.SuperRecurso;
import br.eti.clairton.jpa.serializer.serializers.AplicacaoDeserializer;
import br.eti.clairton.jpa.serializer.serializers.SuperAplicacaoDeserializer;
import br.eti.clairton.jpa.serializer.serializers.SuperRecursoDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JpaDeserializerTest {
	private final Mirror mirror = new Mirror();
	final Logger logger = LogManager.getLogger(JpaDeserializerTest.class);
	private Gson gson;
	private Aplicacao aplicacao;
	private Recurso recurso;

	@Before
	public void init() {
		final GsonBuilder builder = new GsonBuilder();
		final EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("default");
		final EntityManager em = emf.createEntityManager();
		builder.registerTypeAdapter(Aplicacao.class, new AplicacaoDeserializer(em));
		builder.registerTypeAdapter(Recurso.class, new GsonJpaSerializer<Recurso>(em));
		builder.registerTypeAdapter(OutroModel.class, new GsonJpaSerializer<OutroModel>(em));
		builder.registerTypeAdapter(ModelManyToMany.class, new GsonJpaSerializer<ModelManyToMany>(em));
		builder.registerTypeAdapter(ModelOneToOne.class,new GsonJpaSerializer<ModelOneToOne>(em));
		builder.registerTypeAdapter(SuperRecurso.class,new SuperRecursoDeserializer(em));
		builder.registerTypeAdapter(SuperAplicacao.class,new SuperAplicacaoDeserializer(em));

		em.getTransaction().begin();
		aplicacao = new Aplicacao("Teste"+ new Date().getTime());
		em.persist(aplicacao);
		recurso = new Recurso(aplicacao, "Teste"+ new Date().getTime());
		em.persist(recurso);
		em.flush();
		em.clear();
		em.getTransaction().commit();

		gson = builder.create();
	}

	@Test
	public void testManyToOne() {
		final Long idAplicacao = 1000l;
		final Aplicacao aplicacao = new Aplicacao("Teste");
		mirror.on(aplicacao).set().field("id").withValue(idAplicacao);
		mirror.on(aplicacao).set().field("nome").withValue(null);
		final Recurso object = new Recurso(aplicacao, "teste");
		final Long idRecurso = 2000l;
		mirror.on(object).set().field("id").withValue(idRecurso);
		final String json = "{id:'2000',nome:'Teste',aplicacao:'1000'}";
		final Recurso result = gson.fromJson(json, Recurso.class);
		assertEquals("Teste", result.getNome());
		assertEquals(idRecurso, result.getId());
		assertEquals(idAplicacao, aplicacao.getId());
		assertNull(aplicacao.getNome());
	}

	@Test
	public void testReload() {
		final String json = "{id:'2000',nome:'Teste',aplicacao:'"+aplicacao.getId()+"'}";
		final SuperRecurso result = gson.fromJson(json, SuperRecurso.class);
		assertEquals("Teste", result.getNome());
		assertEquals(Long.valueOf(2000l), result.getId());
		assertEquals(aplicacao.getId(), result.getAplicacao().getId());
		assertEquals(aplicacao.getNome(), result.getAplicacao().getNome());
	}

	@Test
	public void testReloadCollection() {
		final String json = "{\"recursos\":["+recurso.getId()+"],\"nome\":\"Teste\",\"id\":"+aplicacao.getId()+"}";
		final SuperAplicacao result = gson.fromJson(json, SuperAplicacao.class);
		assertEquals(aplicacao.getId(), result.getId());
		assertEquals(recurso.getId(), result.getRecursos().get(0).getId());
		assertEquals(recurso.getNome(), result.getRecursos().get(0).getNome());
	}

	@Test
	public void testOneToMany() {
		final Aplicacao object = new Aplicacao("Teste");
		final String json = "{\"recursos\":[1,2],\"nome\":\"Teste\",\"id\":0}";
		final Recurso recurso = new Recurso(object, "Teste");
		final Recurso recurso2 = new Recurso(object, "Outro");
		mirror.on(object).set().field("id").withValue(0l);
		mirror.on(recurso).set().field("id").withValue(1l);
		mirror.on(recurso2).set().field("id").withValue(2l);
		object.adicionar(Arrays.asList(recurso, recurso2));
		final Aplicacao result = gson.fromJson(json, Aplicacao.class);
		assertEquals("Teste", result.getNome());
		assertEquals("0", result.getId().toString());
		assertEquals(2, result.getRecursos().size());
	}

	@Test
	public void testManyToOneWithNull() {
		final String json = "{'nome': 'Teste', 'aplicacao': null}";
		final Recurso result = gson.fromJson(json, Recurso.class);
		assertEquals("Teste", result.getNome());
		assertNull(result.getId());
		assertNull(result.getAplicacao());
	}

	@Test
	public void testOneToOneWithNull() {
		final String json = "{'id': '1111', 'aplicacao': [444]}";
		final ModelOneToOne result = gson.fromJson(json, ModelOneToOne.class);
		assertEquals(Long.valueOf(1111l), result.getId());
		assertEquals(Long.valueOf(444l), result.getAplicacao().getId());
	}

	@Test
	public void testManyToMany() {
		final ModelManyToMany object = new ModelManyToMany();
		final String json = "{'id': '1111', 'aplicacoes': [444,555]}";
		mirror.on(object).set().field("id").withValue(0l);
		final ModelManyToMany result = gson.fromJson(json, ModelManyToMany.class);
		assertEquals(Long.valueOf(444l), result.getAplicacoes().get(0).getId());
		assertEquals(Long.valueOf(555l), result.getAplicacoes().get(1).getId());
	}
}
