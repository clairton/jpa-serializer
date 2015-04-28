package br.eti.clairton.jpa.serializer;

import java.lang.reflect.Field;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.Logger;

abstract class AbstractSerializator {
	protected final Mirror mirror;
	protected final Logger logger;

	public AbstractSerializator(final Mirror mirror, final Logger logger) {
		this.mirror = mirror;
		this.logger = logger;
	}

	public abstract Boolean isToMany(final Field field);

	public abstract Boolean isToOne(final Field field);
}