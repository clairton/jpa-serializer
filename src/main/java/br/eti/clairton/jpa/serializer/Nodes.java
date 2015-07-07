package br.eti.clairton.jpa.serializer;

import java.util.HashMap;
import java.util.Map;

public class Nodes extends HashMap<String, Mode> implements Map<String, Mode> {
	private static final long serialVersionUID = 1L;

	@Override
	public Mode get(final Object key) {
		final Mode mode = super.get(key);
		return mode == null ? Mode.ID : mode;
	}

	public Boolean isReload(final Object key) {
		return Mode.RELOAD.equals(get(key));
	}

	public Boolean isRecord(final Object key) {
		return Mode.RECORD.equals(get(key));
	}

	public Boolean isId(final Object key) {
		return Mode.ID.equals(get(key));
	}

	public Boolean isIgnore(final Object key) {
		return Mode.IGNORE.equals(get(key));
	}
}
