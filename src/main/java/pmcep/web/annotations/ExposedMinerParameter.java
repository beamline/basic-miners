package pmcep.web.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import pmcep.web.miner.models.MinerParameter;

@Retention(RUNTIME)
@Target(TYPE)

public @interface ExposedMinerParameter {
	String name();
	MinerParameter.Type type();
}
