package br.eti.clairton.jpa.serializer;

public class NodesProgramatic extends AbstractNodes implements Nodes {
	private static final long serialVersionUID = 1L;

	@Override
	public Node get(final Object key) {
		final Node node = super.get(key);
		return node == null ? getDefaulNode() : node;
	}
}
