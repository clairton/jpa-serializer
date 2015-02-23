package br.eti.clairton.jpa.serializer;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.Logger;

public class AplicacaoDeserializer extends JpaDeserializer<Aplicacao> {

	public AplicacaoDeserializer(final @NotNull EntityManager entityManager,
			final @NotNull Mirror mirror, final @NotNull Logger logger) {
		super(entityManager, mirror, logger);
	}

}
