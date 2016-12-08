package br.eti.clairton.jpa.serializer;

import java.util.Iterator;
import java.util.Map;

public interface Nodes extends Map<String, Node>, Iterable<Node> {

	Node get(final Object key, final Operation operation);

	void put(final String key, final Mode mode);

	void put(final String key, final Mode mode, final Operation operation);

	Boolean isReload(final Object key);

	Boolean isRecord(final Object key);

	Boolean isId(final Object key);

	Boolean isIdPolymorphic(final Object key);

	Boolean isIdPolymorphic(final Object key, final Operation operation);

	Boolean isIgnore(final Object key);

	Boolean is(final Object key, final Mode mode);

	Boolean is(final Object key, final Mode mode, final Operation operation);

	Boolean isReload(final Object key, final Operation operation);

	Boolean isRecord(final Object key, final Operation operation);

	Boolean isId(final Object key, final Operation operation);

	Boolean isIgnore(final Object key, final Operation operation);

	Iterator<Node> iterator();
}
