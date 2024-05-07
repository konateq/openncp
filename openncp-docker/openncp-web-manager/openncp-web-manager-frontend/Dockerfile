FROM tomcat:9-jdk11-temurin

COPY /target/classes/webapp/openncp-gateway-backend.war /usr/local/tomcat/webapps/
COPY target/classes/lib /usr/local/tomcat/lib/

RUN mkdir /app

COPY ../openncp-web-manager/openncp-web-manager-frontend /web-manager/frontend

ENV NODE_VERSION=16.13.0
RUN apt install -y curl
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
ENV NVM_DIR=/root/.nvm
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"
RUN node --version
RUN npm --version

COPY ../openncp-web-manager/openncp-web-manager-frontend/package*.json /web-manager/frontend
RUN cd /web-manager/frontend && npm install
COPY ../openncp-web-manager/openncp-web-manager-frontend /web-manager/frontend
COPY ../openncp-web-manager/openncp-web-manager-frontend /web-manager/frontend
RUN cd /web-manager/frontend && npm run local
# Start the tomcat service
CMD ["catalina.sh", "run"]

FROM tomcat:9-jdk11-temurin

# Copy the webapps
COPY target/classes/webapp/openncp-ws-server.war /usr/local/tomcat/webapps/
COPY target/classes/lib /usr/local/tomcat/lib/

COPY ./ncp_a/tomcat-config-entrypoint.sh /usr/local/tomcat/bin/
COPY ./ncp_a/config /usr/local/tomcat/conf/

COPY ./openncp-configuration-utility/ /opt/ehealth-openncp/openncp-configuration-utility/

# needed because else we get the error:
# failed to create shim task: OCI runtime create failed: runc create failed: unable to start container process:
# exec: "tomcat-config-entrypoint.sh": executable file not found in $PATH: unknown
RUN chmod 777 /usr/local/tomcat/bin/tomcat-config-entrypoint.sh
RUN chmod 777 -R /opt/ehealth-openncp/openncp-configuration-utility/

RUN groupadd -g 1000 tomcat && \
  useradd -r -u 1000 -g tomcat tomcat && \
  chown -R tomcat:tomcat /usr/local/tomcat/

RUN apt-get update && apt-get -qq -y install gettext socat

ENV EPSOS_PROPS_PATH=/opt/openncp-configuration/

ENTRYPOINT ["tomcat-config-entrypoint.sh"]

USER tomcat

# Start the tomcat service
CMD ["catalina.sh", "run"]

