package eu.europa.ec.sante.openncp.webmanager.backend.module.smp;

import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerException;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.service.DynamicDiscoveryService;
import eu.europa.ec.sante.openncp.webmanager.backend.module.smp.util.DateTimeUtil;
import eu.europa.ec.sante.openncp.webmanager.backend.service.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class DynamicDiscoveryController {

    private final Logger logger = LoggerFactory.getLogger(DynamicDiscoveryController.class);
    private final PropertyService propertyService;
    private final DynamicDiscoveryService dynamicDiscoveryService;

    public DynamicDiscoveryController(final PropertyService propertyService, final DynamicDiscoveryService dynamicDiscoveryService) {
        this.propertyService = Validate.notNull(propertyService, "PropertyService must not be null");
        this.dynamicDiscoveryService = Validate.notNull(dynamicDiscoveryService, "DynamicDiscoveryService must not be null");
    }

    @GetMapping(path = "/dynamicdiscovery/syncsearchmask")
    public ResponseEntity<List<String>> synchronizeSearchMask() {

        if (logger.isInfoEnabled()) {
            logger.info("[Gateway] Synchronize Search Mask at ('{}')", DateTimeUtil.formatTimeInMillis(System.currentTimeMillis()));
        }

        final String countryList = propertyService.getPropertyValueMandatory("ncp.countries");
        String[] countries = StringUtils.split(countryList, ",");
        countries = StringUtils.stripAll(countries);
        final List<String> synchronizedCountry = new ArrayList<>();

        for (final String countryCode : countries) {
            try {
                dynamicDiscoveryService.fetchInternationalSearchMask(countryCode);
                synchronizedCountry.add(countryCode);

            } catch (final ConfigurationManagerException e) {
                logger.error("ConfigurationManagerException: '{}'", e.getMessage());
                synchronizedCountry.add(e.getLocalizedMessage());
            }
        }

        return ResponseEntity.ok(synchronizedCountry);
    }
}
