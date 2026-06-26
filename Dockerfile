# ---- Etapa de build: compila el JAR con Maven + JDK 21 ----------------------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Cachea dependencias: copia solo lo necesario para resolverlas primero.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline

# Copia el código y empaqueta (sin tests para acelerar el deploy).
COPY src ./src
RUN ./mvnw -B clean package -DskipTests

# ---- Etapa de runtime: solo el JRE + el JAR (imagen más liviana) ------------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/cocina360.jar app.jar

# Heap hasta ~75% de la RAM del contenedor (1.5 GB en la instancia de 2 GB),
# dejando margen para metaspace, threads y memoria off-heap.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
