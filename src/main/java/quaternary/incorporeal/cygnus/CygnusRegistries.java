package quaternary.incorporeal.cygnus;

import quaternary.incorporeal.api.cygnus.ICygnusDatatype;
import quaternary.incorporeal.api.cygnus.ICygnusStack;
import quaternary.incorporeal.etc.SimpleRegistry;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CygnusRegistries {
	private CygnusRegistries() {}
	
	public static final SimpleRegistry<Consumer<ICygnusStack>> ACTIONS = new SimpleRegistry<>();
	public static final SimpleRegistry<Predicate<ICygnusStack>> CONDITIONS = new SimpleRegistry<>();
	public static final SimpleRegistry<ICygnusDatatype<?>> DATATYPES = new SimpleRegistry<>();
	
	public static void freezeRegistries() {
		ACTIONS.freeze();
		CONDITIONS.freeze();
		DATATYPES.freeze();
	}
}
