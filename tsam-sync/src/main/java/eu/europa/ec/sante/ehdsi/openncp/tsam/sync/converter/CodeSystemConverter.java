package eu.europa.ec.sante.ehdsi.openncp.tsam.sync.converter;

import eu.europa.ec.sante.ehdsi.openncp.tsam.sync.domain.CodeSystem;
import eu.europa.ec.sante.ehdsi.termservice.web.rest.model.sync.CodeSystemModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CodeSystemConverter implements Converter<CodeSystemModel, CodeSystem> {

    private Map<String, CodeSystem> cache = new HashMap<>();

    @Override
    public CodeSystem convert(CodeSystemModel source) {
        if (source == null) {
            return null;
        }
        if (cache.containsKey(source.getId())) {
            return cache.get(source.getId());
        }

        CodeSystem target = new CodeSystem();
        target.setOid(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());

        cache.put(source.getId(), target);

        return target;
    }
}
