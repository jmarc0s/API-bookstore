# Inici a imagem com uma imagem base com maven e jdk 17 e o rotula de 'build'
FROM maven:3.8.4-openjdk-17-slim AS build

# Copia o arquivo pom.xml para o diretório de trabalho atual da imagem Docker.
COPY pom.xml .

# Copia a pasta src para a pasta src que será criada no diretorio de trabalho atual da imagem docker
COPY src src


# Build the application ( Isso usa o Maven para compilar e empacotar a aplicação. O argumento -DskipTests indica ao Maven para pular a execução dos testes unitários durante a compilação.)
RUN mvn package -DskipTests

# Configurando o ambiente jdk-17 para rodar a aplicação.
FROM openjdk:17-slim


# ste comando copia o arquivo JAR resultante da compilação da etapa anterior (a etapa rotulada como "build") 
#para o diretório de trabalho atual no contêiner Docker que está sendo construído. 
#O arquivo JAR é renomeado para bookstore-1.0.0.jar no contêiner.
COPY --from=build target/bookstore-1.0.0.jar bookstore-1.0.0.jar

# Especificação da porta, comando de execução, etc.
EXPOSE 8080


CMD ["java", "-jar", "bookstore-1.0.0.jar"]
