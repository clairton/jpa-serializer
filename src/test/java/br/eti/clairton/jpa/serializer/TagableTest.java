package br.eti.clairton.jpa.serializer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import br.eti.clairton.jpa.serializer.model.Aplicacao;

public class TagableTest {
	final Tagable<Aplicacao> tagable = new GsonJpaSerializer<Aplicacao>(null);
	final Aplicacao single = new Aplicacao();
	final Collection<Aplicacao> collection = Arrays.asList(single);

	@Test
	public void getRootTagTest() {
		assertEquals("aplicacao", tagable.getRootTag(single));
	}

	@Test
	public void getRootTagCollectionTest() {
		assertEquals("aplicacaos", tagable.getRootTagCollection(collection));
	}
}
