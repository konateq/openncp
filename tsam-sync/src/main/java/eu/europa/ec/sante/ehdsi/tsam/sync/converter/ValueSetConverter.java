package eu.europa.ec.sante.ehdsi.tsam.sync.converter;

import eu.europa.ec.sante.ehdsi.termservice.web.rest.model.sync.ValueSetModel;
import eu.europa.ec.sante.ehdsi.tsam.sync.domain.ValueSet;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ValueSetConverter implements Converter<ValueSetModel, ValueSet> {

    @Override
    public ValueSet convert(ValueSetModel source) {
        if (source == null) {
            return null;
        }

        ValueSet target = new ValueSet();
        target.setOid(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        return target;
    }
}
