#openNCP_version=7.1.0-SNAPSHOT
openNCP_version=7.1.1-SNAPSHOT
#openNCP_version=6.5.1
#openNCP_version=6.3.1

echo
echo "NCP-A deploying release $openNCP_version..."

###########################################################
echo "remove old artifacts..."
rm /opt/Tomcat9-NCP-A/webapps/openncp-ws-server.war &> null 
rm -rf /opt/Tomcat9-NCP-A/webapps/openncp-ws-server &> null 

rm /opt/Tomcat9-NCP-A/webapps/translations-and-mappings-ws.war &> null 
rm -rf /opt/Tomcat9-NCP-A/webapps/translations-and-mappings-ws &> null 

###########################################################
echo "remove logs..."
rm -rf /opt/Tomcat9-NCP-A/temp/*
rm -rf /opt/Tomcat9-NCP-A/logs/*
rm -rf /opt/Tomcat9-NCP-A/work/Catalina/localhost/openncp-ws-server
rm -rf /opt/Tomcat9-NCP-B/work/Catalina/localhost/translation-and-mappings-ws

###########################################################
echo "remove validations and obligations..."
rm -rf /opt/openncp-configuration/validation/NCP-A/*
rm -rf /opt/openncp-configuration/validation/NCP-B/*
rm -rf /opt/openncp-configuration/obligations/nro/*
rm -rf /opt/openncp-configuration/obligations/nrr/*

###########################################################
echo "Removing audits..."
rm -rvf /opt/openncp-configuration/audits/*.xml
echo "Removing audit-backup..."
rm -rvf /opt/openncp-configuration/audit-backup/*.xml

###########################################################
echo "copying new artifacts..."


##### Translation and Mappings ######
#cp ./translations-and-mappings-ws/target/translations-and-mappings-ws-$openNCP_version.war /opt/Tomcat9-NCP-A/webapps/translations-and-mappings-ws.war &> null
#if [ "$?" = "0" ]; then
#  echo "  ./translations-and-mappings-ws/target/translations-and-mappings-ws-$openNCP_version.war                                                                 -> /opt/Tomcat9-NCP-A/webapps/translations-and-mappings-ws.war" 1>&2
#else
#  echo "  ./translations-and-mappings-ws/target/translations-and-mappings-ws-$openNCP_version.war CANNOT be copied!" 1>&2
#fi

##### OpenNCP WS Server ######
cp ./protocol-terminators/epsos-ncp-server/epsos-ws-server/target/openncp-ws-server-$openNCP_version.war /opt/Tomcat9-NCP-A/webapps/openncp-ws-server.war &> null
if [ "$?" = "0" ]; then
  echo "  ./protocol-terminators/epsos-ncp-server/epsos-ws-server/target/openncp-ws-server-$openNCP_version.war                   -> /opt/Tomcat9-NCP-A/webapps/openncp-ws-server.war" 1>&2
else
  echo "  ./protocol-terminators/epsos-ncp-server/epsos-ws-server/target/openncp-ws-server-$openNCP_version.war CANNOT be copied!" 1>&2
fi

rm null

###########################################################
echo "NCP-A release $openNCP_version deployed!"
