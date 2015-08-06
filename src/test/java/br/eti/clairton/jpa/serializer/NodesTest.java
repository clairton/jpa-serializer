package br.eti.clairton.jpa.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NodesTest {
	private Nodes node = new Nodes() {
		private static final long serialVersionUID = -6502172538546602569L;

		{
			put("ignore", Mode.IGNORE);
			put("record", Mode.RECORD);
			put("reload", Mode.RELOAD);
			put("reloadOnlySerialize", Mode.RELOAD, Operation.SERIALIZE);
			put("id", Mode.ID);
		}
	};

	@Test
	public void testGetWhenNotExistOperationOther() {
		assertEquals(Mode.ID, node.get("reloadOnlySerialize", Operation.DESERIALIZE).getMode());
	}

	@Test
	public void testGetWhenExistOperationOther() {
		assertEquals(Mode.RELOAD, node.get("reloadOnlySerialize", Operation.SERIALIZE).getMode());
	}

	@Test
	public void testGetWhenNotExistOperation() {
		assertEquals(Mode.ID, node.get("xpto", Operation.SERIALIZE).getMode());
	}

	@Test
	public void testGetWhenExistOperation() {
		assertEquals(Mode.RELOAD, node.get("reload", Operation.SERIALIZE).getMode());
	}

	@Test
	public void testGetWhenNotExist() {
		assertEquals(Mode.ID, node.get("xpto").getMode());
	}

	@Test
	public void testGetWhenExist() {
		assertEquals(Mode.RELOAD, node.get("reload").getMode());
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
