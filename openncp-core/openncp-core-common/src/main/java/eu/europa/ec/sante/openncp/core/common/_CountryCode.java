package eu.europa.ec.sante.openncp.core.common;

import eu.europa.ec.sante.openncp.common.immutables.Wrapped;
import eu.europa.ec.sante.openncp.common.immutables.Wrapper;
import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import java.util.Locale;
import java.util.Set;

@Wrapped
abstract class _CountryCode extends Wrapper<String> {
    private static final Set<String> ISO3166_1_ALPHA_2_COUNTRIES = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2);

    @Value.Check
    protected void check() {
        Validate.notBlank(value(), "CountryCode cannot be empty and must be a valid ISO 3166-1 alpha-2 code");
        Validate.isTrue(ISO3166_1_ALPHA_2_COUNTRIES.contains(value()), "The CountryCode provided is not a valid ISO 3166-1 alpha-2 code");
    }
}
