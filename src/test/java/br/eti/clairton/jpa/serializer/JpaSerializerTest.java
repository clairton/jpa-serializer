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
			
			idPolymorphic("idPolymorphic");
		}
	};

	@Test
	public void testRecord() {
		assertTrue(serializer.isRecord(null, "recordAlways"));

		assertTrue(serializer.isRecord(null, "recordSerialize", Operation.SERIALIZE));
		assertFalse(serializer.isRecord(null, "recordSerialize", Operation.DESERIALIZE));
		assertFalse(serializer.isRecord(null, "recordSerialize"));

		assertFalse(serializer.isRecord(null, "recordDeserialize", Operation.SERIALIZE));
		assertTrue(serializer.isRecord(null, "recordDeserialize", Operation.DESERIALIZE));
		assertFalse(serializer.isRecord(null, "recordDeserialize"));
	}

	@Test
	public void testReload() {
		assertTrue(serializer.isReload(null, "reloadAlways"));

		assertTrue(serializer.isReload(null, "reloadSerialize", Operation.SERIALIZE));
		assertFalse(serializer.isReload(null, "reloadSerialize", Operation.DESERIALIZE));
		assertFalse(serializer.isReload(null, "reloadSerialize"));

		assertFalse(serializer.isReload(null, "reloadDeserialize", Operation.SERIALIZE));
		assertTrue(serializer.isReload(null, "reloadDeserialize", Operation.DESERIALIZE));
		assertFalse(serializer.isReload(null, "reloadDeserialize"));
	}

	@Test
	public void testId() {
		assertTrue(serializer.isId(null, "idAlways"));

		assertTrue(serializer.isId(null, "idSerialize", Operation.SERIALIZE));
		assertFalse(serializer.isId(null, "idSerialize", Operation.DESERIALIZE));
		assertFalse(serializer.isId(null, "idSerialize"));

		assertFalse(serializer.isId(null, "idDeserialize", Operation.SERIALIZE));
		assertTrue(serializer.isId(null, "idDeserialize", Operation.DESERIALIZE));
		assertFalse(serializer.isId(null, "idDeserialize"));
	}
	
	@Test
	public void testIdPolymorphic() {
		assertTrue(serializer.isId(null, "idPolymorphic", Operation.SERIALIZE));
		assertTrue(serializer.isId(null, "idPolymorphic", Operation.DESERIALIZE));
		assertTrue(serializer.isId(null, "idPolymorphic"));
		assertTrue(serializer.isIdPolymorphic(null, "idPolymorphic", Operation.DESERIALIZE));
		assertTrue(serializer.isIdPolymorphic(null, "idPolymorphic", Operation.SERIALIZE));
		assertTrue(serializer.isIdPolymorphic(null, "idPolymorphic"));
		assertFalse(serializer.isIdPolymorphic(null, "idNotExist"));
	}
	
	@Test
	public void testIgnore() {
		assertTrue(serializer.isIgnore(null, "ignoreAlways"));
		
		assertTrue(serializer.isIgnore(null, "ignoreSerialize", Operation.SERIALIZE));
		assertFalse(serializer.isIgnore(null, "ignoreSerialize", Operation.DESERIALIZE));
		assertFalse(serializer.isIgnore(null, "ignoreSerialize"));
		
		assertFalse(serializer.isIgnore(null, "ignoreDeserialize", Operation.SERIALIZE));
		assertTrue(serializer.isIgnore(null, "ignoreDeserialize", Operation.DESERIALIZE));
		assertFalse(serializer.isIgnore(null, "ignoreDeserialize"));
	}
}
