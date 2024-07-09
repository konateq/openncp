#openNCP_version=7.1.0-SNAPSHOT
openNCP_version=7.1.1-SNAPSHOT
#openNCP_version=6.5.1
#openNCP_version=6.3.1

echo
echo "NCP-B  release $openNCP_version deploying..."

###########################################################
echo "remove old artifacts..."
rm /opt/Tomcat9-NCP-B/webapps/openncp-client-connector.war &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/openncp-client-connector &> null 

rm /opt/Tomcat9-NCP-B/webapps/openatna-web.war &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/openatna-web &> null 

rm /opt/Tomcat9-NCP-B/webapps/openncp-openatna-service.war &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/openncp-openatna-service &> null 

rm /opt/Tomcat9-NCP-B/webapps/ehealth-portal.war &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/ehealth-portal &> null 

rm /opt/Tomcat9-NCP-B/webapps/ehealth-portal-server.war &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/ehealth-portal-server &> null 

rm /opt/Tomcat9-NCP-B/webapps/translations-and-mappings-ws.war &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/translations-and-mappings-ws &> null 

rm /opt/Tomcat9-NCP-B/webapps/openatna-web.war &> null 
rm /opt/Tomcat9-NCP-B/webapps/openncp-gateway.war &> null 
rm /opt/Tomcat9-NCP-B/webapps/openncp-gateway-backend.war &> null 
rm /opt/Tomcat9-NCP-B/webapps/openncp-ws-server.war &> null 
rm /opt/Tomcat9-NCP-B/webapps/TRC-STS.war &> null 

rm -rf /opt/Tomcat9-NCP-B/webapps/open* &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/TRC-STS* &> null 

rm /opt/Tomcat9-NCP-B/webapps/ehealth-portal.war &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/ehealth-portal &> null 

rm /opt/Tomcat9-NCP-B/webapps/ehealth-portal-server.war &> null 
rm -rf /opt/Tomcat9-NCP-B/webapps/ehealth-portal-server &> null 

###########################################################
echo "remove logs..."
rm -rf /opt/Tomcat9-NCP-B/temp/*
rm -rf /opt/Tomcat9-NCP-B/logs/*
rm -rf /opt/Tomcat9-NCP-B/work/Catalina/localhost/open*
rm -rf /opt/Tomcat9-NCP-B/work/Catalina/localhost/TRC-STS*
rm -rf /opt/Tomcat9-NCP-B/work/Catalina/localhost/translation-and-mappings-ws*

###########################################################
echo "remove validations and obligations..."
rm -rf /opt/openncp-configuration/validation/NCP-B/*

###########################################################
echo "Removing audits..."
rm -rf /opt/openncp-configuration/audits/*.xml
echo "Removing audit-backup..."
rm -rf /opt/openncp-configuration/audit-backup/*.xml

###########################################################
echo "copying new artifacts..."

##### Translation and Mappings ######
cp ./translations-and-mappings-ws/target/translations-and-mappings-ws-$openNCP_version.war /opt/Tomcat9-NCP-B/webapps/translations-and-mappings-ws.war &> null
if [ "$?" = "0" ]; then
  echo "  ./translations-and-mappings-ws/target/translations-and-mappings-ws-$openNCP_version.war      -> /opt/Tomcat9-NCP-B/webapps/translations-and-mappings-ws.war" 1>&2
else
  echo "  ./translations-and-mappings-ws/target/translations-and-mappings-ws-$openNCP_version.war CANNOT be copied!" 1>&2
fi

##### OpenATNA ######
cp ./openatna/web/target/openatna-web-$openNCP_version.war /opt/Tomcat9-NCP-B/webapps/openatna-web.war &> null
if [ "$?" = "0" ]; then
  echo "  ./openatna/web/target/openatna-web-$openNCP_version.war                                                                 -> /opt/Tomcat9-NCP-B/webapps/openatna-web.war" 1>&2
else
  echo "  ./openatna/web/target/openatna-web-$openNCP_version.war CANNOT be copied!" 1>&2
fi

##### ClientConnector ######
cp ./protocol-terminators/epsos-ncp-client/epsos-client-connector/target/openncp-client-connector-$openNCP_version.war /opt/Tomcat9-NCP-B/webapps/openncp-client-connector.war &> null 
if [ "$?" = "0" ]; then
  echo "  ./protocol-terminators/epsos-ncp-client/epsos-client-connector/target/openncp-client-connector-$openNCP_version.war     -> /opt/Tomcat9-NCP-B/webapps/openncp-client-connector.war" 1>&2 
else
  echo "  ./protocol-terminators/epsos-ncp-client/epsos-client-connector/target/openncp-client-connector-$openNCP_version.war CANNOT be copied!" 1>&2
fi

cp ./openncp-gateway/openncp-gateway-backend/target/openncp-gateway-backend-$openNCP_version.war /opt/Tomcat9-NCP-B/webapps/openncp-gateway-backend.war &> null
if [ "$?" = "0" ]; then
  echo "  ./openncp-gateway/openncp-gateway-backend/target/openncp-gateway-backend-$openNCP_version.war                             -> /opt/Tomcat9-NCP-B/webapps/openncp-gateway-backend.war" 1>&2
else
  echo "  ./openncp-gateway/openncp-gateway-backend/target/openncp-gateway-backend-$openNCP_version.war CANNOT be copied!" 1>&2
fi

cp ./trc-sts/target/openncp-trc-sts-$openNCP_version.war /opt/Tomcat9-NCP-B/webapps/TRC-STS.war &> null
if [ "$?" = "0" ]; then
  echo "  ./trc-sts/target/openncp-trc-sts-$openNCP_version.war                                                                   -> /opt/Tomcat9-NCP-B/webapps/TRC-STS.war" 1>&2
else
  echo "  ./trc-sts/target/openncp-trc-sts-$openNCP_version.war CANNOT be copied!" 1>&2
fi

rm null

###########################################################
echo "NCP-B  release $openNCP_version deployed!"
