package br.eti.clairton.jpa.serializer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.eti.clairton.jpa.serializer.model.Aplicacao;

public class JpaSerializerTest extends JpaSerializer<Aplicacao> {
	private static final long serialVersionUID = 1L;
	private JpaSerializer<Aplicacao> serializer = new JpaSerializer<Aplicacao>() {
		private static final long serialVersionUID = 1L;
		{
			record("recordAlways");
			record("recordSerialize", Operation.SERIALIZE);
			record("recordDeserialize", Operation.DESERIALIZE);

			reload("reloadAlways");
			reload("reloadSerialize", Operation.SERIALIZE);
			reload("reloadDeserialize", Operation.DESERIALIZE);

			id("idAlways");
			id("idSerialize", Operation.SERIALIZE);
			id("idDeserialize", Operation.DESERIALIZE);

			ignore("ignoreAlways");
			ignore("ignoreSerialize", Operation.SERIALIZE);
			ignore("ignoreDeserialize", Operation.DESERIALIZE);
		}
	};

	@Test
	public void testRecord() {
		assertTrue(serializer.isRecord("recordAlways"));

		assertTrue(serializer.isRecord("recordSerialize", Operation.SERIALIZE));
		assertFalse(serializer.isRecord("recordSerialize", Operation.DESERIALIZE));
		assertFalse(serializer.isRecord("recordSerialize"));

		assertFalse(serializer.isRecord("recordDeserialize", Operation.SERIALIZE));
		assertTrue(serializer.isRecord("recordDeserialize", Operation.DESERIALIZE));
		assertFalse(serializer.isRecord("recordDeserialize"));
	}

	@Test
	public void testReload() {
		assertTrue(serializer.isReload("reloadAlways"));

		assertTrue(serializer.isReload("reloadSerialize", Operation.SERIALIZE));
		assertFalse(serializer.isReload("reloadSerialize", Operation.DESERIALIZE));
		assertFalse(serializer.isReload("reloadSerialize"));

		assertFalse(serializer.isReload("reloadDeserialize", Operation.SERIALIZE));
		assertTrue(serializer.isReload("reloadDeserialize", Operation.DESERIALIZE));
		assertFalse(serializer.isReload("reloadDeserialize"));
	}

	@Test
	public void testId() {
		assertTrue(serializer.isId("idAlways"));

		assertTrue(serializer.isId("idSerialize", Operation.SERIALIZE));
		assertFalse(serializer.isId("idSerialize", Operation.DESERIALIZE));
		assertFalse(serializer.isId("idSerialize"));

		assertFalse(serializer.isId("idDeserialize", Operation.SERIALIZE));
		assertTrue(serializer.isId("idDeserialize", Operation.DESERIALIZE));
		assertFalse(serializer.isId("idDeserialize"));
	}
	
	@Test
	public void testIgnore() {
		assertTrue(serializer.isIgnore("ignoreAlways"));
		
		assertTrue(serializer.isIgnore("ignoreSerialize", Operation.SERIALIZE));
		assertFalse(serializer.isIgnore("ignoreSerialize", Operation.DESERIALIZE));
		assertFalse(serializer.isIgnore("ignoreSerialize"));
		
		assertFalse(serializer.isIgnore("ignoreDeserialize", Operation.SERIALIZE));
		assertTrue(serializer.isIgnore("ignoreDeserialize", Operation.DESERIALIZE));
		assertFalse(serializer.isIgnore("ignoreDeserialize"));
	}
}
