/*-
 * #%L
 * hapi-fhir-spring-boot-autoconfigure
 * %%
 * Copyright (C) 2014 - 2023 Smile CDR, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.europa.ec.sante.openncp.api.common.configuration;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hapi.fhir")
public class FhirProperties {

    private FhirVersionEnum version = FhirVersionEnum.R4;
    private Server server = new Server();
    private Validation validation = new Validation();

    public FhirVersionEnum getVersion() {
        return version;
    }

    public void setVersion(final FhirVersionEnum version) {
        this.version = version;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(final Server server) {
        this.server = server;
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(final Validation validation) {
        this.validation = validation;
    }

    public static class Server {

        private String url;

        private String path = "/fhir/";

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getBasePath() {
            return path;
        }

        public String getServletPath() {
            if (StringUtils.isBlank(path)) {
                return "/*";
            } else if (path.endsWith("/")){
                return path + "*";
            } else {
                return path + "/*";
            }

        }

        public void setPath(final String path) {
            this.path = path;
        }
    }

    public static class Validation {

        private boolean enabled = true;

        private boolean requestOnly = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isRequestOnly() {
            return requestOnly;
        }

        public void setRequestOnly(final boolean requestOnly) {
            this.requestOnly = requestOnly;
        }
    }
}
