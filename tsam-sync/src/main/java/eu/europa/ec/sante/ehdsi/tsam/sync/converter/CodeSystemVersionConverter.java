package eu.europa.ec.sante.ehdsi.tsam.sync.converter;

import eu.europa.ec.sante.ehdsi.termservice.web.rest.model.sync.CodeSystemVersionModel;
import eu.europa.ec.sante.ehdsi.tsam.sync.domain.CodeSystemVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CodeSystemVersionConverter implements Converter<CodeSystemVersionModel, CodeSystemVersion> {

    private Map<String, CodeSystemVersion> cache = new HashMap<>();

    private final CodeSystemConverter codeSystemConverter;

    @Autowired
    public CodeSystemVersionConverter(CodeSystemConverter codeSystemConverter) {
        Assert.notNull(codeSystemConverter, "codeSystemConverter must not be null");
        this.codeSystemConverter = codeSystemConverter;
    }

    @Override
    public CodeSystemVersion convert(CodeSystemVersionModel source) {
        if (source == null) {
            return null;
        }

        String versionKey = source.getCodeSystem().getId() + "-" + source.getVersionId();
        if (cache.containsKey(versionKey)) {
            return cache.get(versionKey);
        }

        CodeSystemVersion target = new CodeSystemVersion();
        target.setFullName(source.getFullName());
        target.setLocalName(source.getLocalName());
        target.setEffectiveDate(source.getEffectiveDate() == null ? null : LocalDateTime.parse(source.getEffectiveDate()));
        target.setReleaseDate(source.getReleaseDate() == null ? null : LocalDateTime.parse(source.getReleaseDate()));
        target.setStatus(source.getStatus());
        target.setStatusDate(source.getStatusDate() == null ? null : LocalDateTime.parse(source.getStatusDate()));
        target.setDescription(source.getDescription());
        target.setCopyright(source.getCopyright());
        target.setSource(source.getSource());
        target.setCodeSystem(codeSystemConverter.convert(source.getCodeSystem()));

        cache.put(versionKey, target);

        return target;
    }
}
