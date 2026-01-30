# Distribuzione OpenJDK ufficiale versione 21.
FROM eclipse-temurin:21-jdk

# Imposta una variabile per il file jar, uesto parametro punta 
# alla cartella dove Maven salva il risultato della compilazione.
ARG JAR_FILE=target/*.jar

# Copia il file .jar generato da Maven lo rinominiamo "app.jar" dentro il container.
COPY ${JAR_FILE} app.jar

# Avvia la Java Virtual Machine ed esegui il nostro file jar.
ENTRYPOINT ["java", "-jar", "/app.jar"]