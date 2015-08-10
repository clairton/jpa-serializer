package br.eti.clairton.jpa.serializer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Tagable<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Return the tag name for single object serialization/deserialization.
	 * 
	 * @param src
	 *            instance of the object
	 * @return String with tag name
	 */
	public String getRootTag(final T src) {
		final Class<?> type = src.getClass();
		final String name = type.getSimpleName();
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	/**
	 * Return the tag name for collection object serialization/deserialization
	 * 
	 * @param collection
	 *            collection of objects
	 * @return String with a tag name
	 */
	public String getRootTagCollection(final Collection<T> collection) {
		final T src;
		try{
			src = getFirst(collection);
		}catch(final NoSuchElementException e){
			return "";
		}
		final String tag = getRootTag(src);
		final String collectionTag = pluralize(tag);
		return collectionTag;
	}
	
	protected String pluralize(final String tag){
		return String.format("%s%s", tag, "s");
	}

	protected T getFirst(final Collection<T> collection) {
		final Iterator<T> iterator = collection.iterator();
		final T src = iterator.next();
		return src;
	}
}
