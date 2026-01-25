# Galería Fútbol - Backend API

API REST para la gestión de una galería de camisetas de fútbol. Proporciona servicios para administrar álbumes de camisetas, categorías, colecciones destacadas y autenticación de usuarios.

## Tecnologías

- **Java 21** con **Spring Boot 3.3**
- **Spring Security** con JWT para autenticación
- **Spring Data JPA** con Hibernate
- **PostgreSQL** (Neon) como base de datos
- **Cloudflare R2** para almacenamiento de imágenes (compatible con S3)
- **Maven** como gestor de dependencias
- **Docker** para containerización

## Arquitectura

El proyecto sigue una arquitectura en capas:

```
┌─────────────────────────────────────┐
│           Controllers               │  ← REST endpoints
├─────────────────────────────────────┤
│            Services                 │  ← Lógica de negocio
├─────────────────────────────────────┤
│           Repositories              │  ← Acceso a datos (JPA)
├─────────────────────────────────────┤
│         PostgreSQL + R2             │  ← Persistencia
└─────────────────────────────────────┘
```

## Módulos principales

- **Álbumes**: Gestión de colecciones de camisetas con metadata (temporada, equipo, tipo)
- **Imágenes**: Almacenamiento y gestión de imágenes en Cloudflare R2
- **Categorías**: Clasificación de álbumes por equipo/liga
- **Colecciones Destacadas**: Curación de contenido para la página principal
- **Autenticación**: Login con JWT, roles ADMIN/EDITOR

## Requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 15+ (o cuenta en Neon)
- Cuenta en Cloudflare R2 (para almacenamiento de imágenes)

## Variables de entorno

```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/database
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=3600000

# Cloudflare R2
AWS_REGION=auto
AWS_ACCESS_KEY_ID=your-r2-access-key
AWS_SECRET_ACCESS_KEY=your-r2-secret-key
R2_BUCKET_NAME=your-bucket
R2_ACCOUNT_ID=your-account-id
```

## Ejecución local

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/api-0.0.1-SNAPSHOT.jar
```

O con Maven directamente:

```bash
mvn spring-boot:run
```

## Ejecución con Docker

Desde la raíz del proyecto:

```bash
docker build -t galeria-futbol-api .
docker run -p 8080:8080 --env-file .env galeria-futbol-api
```

## Documentación API

Una vez ejecutando, accede a Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

## Convenciones REST

- **POST**: Crear recursos nuevos
- **GET**: Obtener recursos (sin efectos secundarios)
- **PUT**: Reemplazo completo del recurso (enviar todos los campos)
- **PATCH**: Actualización parcial (solo campos a modificar)
- **DELETE**: Eliminar recursos

## Despliegue

El proyecto está configurado para desplegarse en **Render** usando el Dockerfile incluido en la raíz del repositorio.
