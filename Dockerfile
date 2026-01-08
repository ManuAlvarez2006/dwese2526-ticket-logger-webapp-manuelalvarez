# =========
# STAGE 1: build
# =========
# Usamos una imagen oficial de Maven con JDK 21 (Eclipse Temurin) sobre Alpine (ligera).
# Este stage solo se usará para COMPILAR el proyecto, no para ejecutar la app en producción.
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Establecemos el directorio de trabajo dentro del contenedor.
# Todas las rutas relativas a partir de aquí se interpretan desde /app.
# Por eso en las variables de entorno hemos puesto como ruta de subida de imágenes /app/uploads
WORKDIR /app

# Copiamos solo el pom.xml primero para aprovechar la caché de Docker:
# Si el pom no cambia, las dependencias se mantienen cacheadas entre builds.
COPY pom.xml .

# Descargamos todas las dependencias necesarias para compilar en modo "offline".
# Flags:
#   -q → modo silencioso (menos ruido en logs, pero muestra errores).
#   -e → muestra stacktraces completos en caso de error.
#   -B → batch mode (no interactivo, ideal para CI/CD y Docker).
RUN mvn -q -e -B dependency:go-offline

# Ahora copiamos el código fuente completo al contenedor.
# Esto invalida solo esta capa y las siguientes cuando cambiamos código,
# pero mantiene cacheadas las dependencias si el pom no ha cambiado.
COPY src ./src

# Compilamos y generamos el .jar del proyecto.
# Usamos:
# clean package → limpia /target y empaqueta la app.
# -DskipTests → saltamos tests para acelerar el build en desarrollo.
# En un entorno real de CI, lo normal sería NO saltar los tests.
RUN mvn -q -e -B clean package -DskipTests

# =========
# STAGE 2: run
# ==========

# Segunda imagen: solo para EJECUTAR la aplicación.
# Usamos una imagen de Eclipse Temurin con JRE 21 (sin herramientas de desarrollo),
# basada en Ubuntu 22.04 (jammy), más estable que alpine para libs nativas, etc.
FROM eclipse-temurin:21-jre-jammy

# Creamos un usuario no root para ejecutar la aplicación.
# Motivo: buenas prácticas de seguridad -> nunca ejecutar como root si no es necesario.
RUN useradd -ms /bin/bash spring

# Definimos el directorio de trabajo donde estará el .jar y los recursos de la app.
WORKDIR /app
# Copiamos el .jar generado en el stage "builder" al contenedor final.
# --from-builder indica que la copia viene del primer stage.
# De esta forma NO arrastramos Maven, el código fuente ni el directorio target completo,
# solo el artefacto final empaquetado.S

COPY --from=builder /app/target/*.jar app.jar
# Creamos el directorio donde la aplicación guardará los ficheros subidos (uploads)
# y asignamos permisos al usuario 'spring' sobre /app.
# Así evitamos problemas de permisos cuando la app intente escribir en esa ruta.

RUN mkdir -p /app/uploads && chown -R spring:spring /app
# A partir de aquí, todas las instrucciones y el proceso principal se ejecutarán
# con el usuario 'spring' en lugar de root.

USER spring
# Documentamos que el contenedor expone el puerto 8080.
# Esto no abre el puerto en el host, solo indica a otras herramientas qué puerto usa la app.

EXPOSE 8080

# Definimos opciones por defecto de la JVM.
# -Xms256m → memoria mínima del heap (256 MB)
# -Xmx512m → memoria máxima del heap (512 MB)
# Esta variable se puede sobrescribir desde docker-compose o docker run con -e JAVA_OPTS=...

ENV JAVA_OPTS="-Xms256m -Xmx512m"
# Definimos el proceso principal del contenedor.
# Usamos 'sh -c' para que pueda expandir la variable $JAVA_OPTS.
# El comando final que se ejecuta será algo como:
# java -Xms256m -Xmx512m -jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]