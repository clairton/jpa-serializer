package br.eti.clairton.jpa.serializer;

import static java.util.Arrays.asList;
import static javax.persistence.Persistence.createEntityManagerFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.eti.clairton.jpa.serializer.model.Aplicacao;
import br.eti.clairton.jpa.serializer.model.ModelManyToMany;
import br.eti.clairton.jpa.serializer.model.ModelOneToOne;
import br.eti.clairton.jpa.serializer.model.OutroModel;
import br.eti.clairton.jpa.serializer.model.Recurso;
import br.eti.clairton.jpa.serializer.serializers.OutroModelSerializer;
import net.vidageek.mirror.dsl.Mirror;

public class JpaSerializerTest {
	private final Mirror mirror = new Mirror();
	private Gson gson;
	private GsonJpaSerializer<OutroModel> outroModelSerializer;

	@Before
	public void init() {
		final GsonBuilder builder = new GsonBuilder();
		final EntityManagerFactory emf = createEntityManagerFactory("default");
		final EntityManager em = emf.createEntityManager();
		outroModelSerializer = new OutroModelSerializer(em);
		builder.registerTypeAdapter(Aplicacao.class, new GsonJpaSerializer<Aplicacao>(em));
		builder.registerTypeAdapter(Recurso.class, new GsonJpaSerializer<Recurso>(em));
		builder.registerTypeAdapter(OutroModel.class, outroModelSerializer);
		builder.registerTypeAdapter(ModelManyToMany.class, new GsonJpaSerializer<ModelManyToMany>(em));
		builder.registerTypeAdapter(ModelOneToOne.class, new GsonJpaSerializer<ModelOneToOne>(em));
		gson = builder.create();
	}

	@Test
	public void testOneToMany() {
		final Aplicacao object = new Aplicacao("Teste");
		final Recurso recurso = new Recurso(object, "Teste");
		final Recurso recurso2 = new Recurso(object, "Outro");
		mirror.on(object).set().field("id").withValue(0l);
		mirror.on(recurso).set().field("id").withValue(1l);
		mirror.on(recurso2).set().field("id").withValue(2l);
		final List<Recurso> recursos = Arrays.asList(recurso, recurso2);
		Collections.sort(recursos, new Comparator<Recurso>() {

			@Override
			public int compare(final Recurso o1, final Recurso o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		object.adicionar(recursos);
		final String json = gson.toJson(object);
		final Map<?, ?> resultado = gson.fromJson(json, HashMap.class);
		final List<?> list = (List<?>) resultado.get("recursos");
		assertEquals(2, list.size());
		assertEquals("Teste", resultado.get("nome"));
		assertEquals(0.0, resultado.get("id"));
	}

	@Test
	public void testAddIgnoreField() {
		assertEquals(Mode.IGNORE, outroModelSerializer.nodes().get("nome").getMode());
		final Long idAplicacao = 1000l;
		final OutroModel outroModel = new OutroModel("teste");
		mirror.on(outroModel).set().field("id").withValue(idAplicacao);
		final String json = gson.toJson(outroModel, OutroModel.class);
		final Map<?, ?> resultado = gson.fromJson(json, HashMap.class);
		assertEquals("PSADGKSADGLDSLÇ", resultado.get("outroValor"));
		assertEquals(1000.0, resultado.get("id"));
		assertFalse(resultado.containsKey("nome"));
	}

	@Test
	public void testManyToOne() {
		final Long idAplicacao = 1000l;
		final Aplicacao aplicacao = new Aplicacao("Teste");
		mirror.on(aplicacao).set().field("id").withValue(idAplicacao);
		final Recurso object = new Recurso(aplicacao, "teste");
		final Long idRecurso = 2000l;
		mirror.on(object).set().field("id").withValue(idRecurso);
		final String json = gson.toJson(object, Recurso.class);
		final Map<?, ?> resultado = gson.fromJson(json, HashMap.class);
		assertEquals("teste", resultado.get("nome"));
		assertEquals(2000.0, resultado.get("id"));
		assertEquals(1000.0, resultado.get("aplicacao"));
	}

	@Test
	public void testOneToOne() {
		final ModelOneToOne model = new ModelOneToOne();
		mirror.on(model).set().field("id").withValue(1l);
		final String json = gson.toJson(model, ModelOneToOne.class);
		final Map<?, ?> resultado = gson.fromJson(json, HashMap.class);
		assertEquals(100.0, resultado.get("aplicacao"));
	}

	@Test
	public void testManyToMany() {
		final ModelManyToMany model = new ModelManyToMany();
		mirror.on(model).set().field("id").withValue(1l);
		final String json = gson.toJson(model, ModelManyToMany.class);
		final Map<?, ?> resultado = gson.fromJson(json, HashMap.class);
		assertEquals(asList(100.0, 200.0), resultado.get("aplicacoes"));
	}
}
