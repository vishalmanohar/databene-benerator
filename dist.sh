cd ../commons
mvn clean install assembly:assembly
cd ../webdecs
mvn clean install assembly:assembly
cd ../gui
mvn clean install assembly:assembly
cd ../benerator
mvn clean install assembly:assembly
cd ../maven-benerator-plugin
mvn clean install assembly:assembly
