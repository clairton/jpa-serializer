package br.eti.clairton.jpa.serializer;

import static br.eti.clairton.jpa.serializer.Mode.ID;
import static br.eti.clairton.jpa.serializer.Mode.ID_POLYMORPHIC;
import static br.eti.clairton.jpa.serializer.Mode.IGNORE;
import static br.eti.clairton.jpa.serializer.Mode.RECORD;
import static br.eti.clairton.jpa.serializer.Mode.RELOAD;
import static br.eti.clairton.jpa.serializer.Operation.DESERIALIZE;
import static br.eti.clairton.jpa.serializer.Operation.SERIALIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NodesProgramaticTest {
	private Nodes node = new NodesProgramatic() {
		private static final long serialVersionUID = -6502172538546602569L;
		{
			put("ignore", IGNORE);
			put("record", RECORD);
			put("reload", RELOAD);
			put("reloadOnlySerialize", RELOAD, SERIALIZE);
			put("idPolymorphic", ID_POLYMORPHIC);
			put("id", ID);
		}
	};

	@Test
	public void testGetWhenNotExistOperationOther() {
		assertEquals(ID, node.get("reloadOnlySerialize", DESERIALIZE).getMode());
	}

	@Test
	public void testGetWhenExistOperationOther() {
		assertEquals(RELOAD, node.get("reloadOnlySerialize", SERIALIZE).getMode());
	}

	@Test
	public void testGetWhenNotExistOperation() {
		assertEquals(ID, node.get("xpto", SERIALIZE).getMode());
	}

	@Test
	public void testGetWhenExistOperation() {
		assertEquals(RELOAD, node.get("reload", SERIALIZE).getMode());
	}

	@Test
	public void testGetWhenNotExist() {
		assertEquals(ID, node.get("xpto").getMode());
	}

	@Test
	public void testGetWhenExist() {
		assertEquals(RELOAD, node.get("reload").getMode());
	}

	@Test
	public void testIsReload() {
		assertTrue(node.isReload("reload"));
	}

	@Test
	public void testIsRecord() {
		assertTrue(node.isRecord("record"));
	}

	@Test
	public void testIsId() {
		assertTrue(node.isId("id"));
	}

	@Test
	public void testIsIdPolymorphic() {
		assertTrue(node.isId("idPolymorphic"));
		assertTrue(node.isIdPolymorphic("idPolymorphic"));
	}

	@Test
	public void testIsIgnore() {
		assertTrue(node.isIgnore("ignore"));
	}

	@Test
	public void testIsntReload() {
		assertFalse(node.isReload("id"));
	}

	@Test
	public void testIsntRecord() {
		assertFalse(node.isRecord("reload"));
	}

	@Test
	public void testIsntId() {
		assertFalse(node.isId("ignore"));
	}

	@Test
	public void testIsntIgnore() {
		assertFalse(node.isIgnore("record"));
	}
}
