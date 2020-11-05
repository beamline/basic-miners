package pmcep.web.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface ExposedMiner {

	String name();
	
	String description();
	
	ExposedMinerParameter[] configurationParameters();
	
	ExposedMinerParameter[] viewParameters();
}

