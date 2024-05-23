package eu.europa.ec.sante.openncp.common.immutables;

import org.immutables.value.Value;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Value.Style(depluralize = true, stagedBuilder = true, allParameters = true, jdkOnly = true, get = { "is*", "get*" }, deepImmutablesDetection = true)
public @interface Domain {}