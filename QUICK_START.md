# ⚡ Guía de Inicio Rápido - TechTrend

¡Bienvenido a **TechTrend**! Esta guía te ayudará a configurar y ejecutar el proyecto en menos de 5 minutos.

## 🚀 Configuración Rápida

### 1. Prerrequisitos
```bash
# Verificar Java 17
java -version

# Verificar Maven
mvn -version

# Verificar Git
git --version
```

### 2. Clonar y Configurar
```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/techtrend-microservice.git
cd techtrend-microservice

# Configurar GitFlow automáticamente
./scripts/setup-gitflow.sh
# O en Windows: scripts\setup-gitflow.bat
```

### 3. Ejecutar el Proyecto
```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Iniciar aplicación
mvn spring-boot:run
```

### 4. Verificar Funcionamiento
- **API Principal**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

## 🔧 Comandos Esenciales

### Desarrollo
```bash
# Compilar proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar con cobertura
mvn test jacoco:report

# Ejecutar aplicación
mvn spring-boot:run

# Ejecutar en modo debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### GitFlow
```bash
# Crear feature
git flow feature start mi-feature

# Hacer commit
git commit -m "feat(auth): agregar validación de email"

# Finalizar feature
git flow feature finish mi-feature

# Crear release
git flow release start 1.1.0

# Finalizar release
git flow release finish 1.1.0

# Crear hotfix
git flow hotfix start correccion-urgente

# Finalizar hotfix
git flow hotfix finish correccion-urgente
```

### Testing
```bash
# Ejecutar todos los tests
mvn test

# Ejecutar test específico
mvn test -Dtest=AuthenticationServiceTest

# Ejecutar tests con reporte
mvn test jacoco:report

# Ver cobertura en navegador
open target/site/jacoco/index.html
```

### Base de Datos
```bash
# Acceder a H2 Console
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Usuario: sa
# Contraseña: (vacío)

# Ver datos en consola
curl http://localhost:8080/api/catalog/products
```

### API Testing
```bash
# Health check
curl http://localhost:8080/health

# Registrar usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@techtrend.com","password":"password123","role":"CLIENT"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@techtrend.com","password":"password123"}'

# Obtener productos
curl http://localhost:8080/api/catalog/products
```

## 🛠️ Herramientas de Desarrollo

### IDE Setup
```bash
# IntelliJ IDEA
# Importar como proyecto Maven

# Eclipse
# Import > Existing Maven Projects

# VS Code
# Instalar extensiones: Java Extension Pack, Spring Boot Extension Pack
```

### Debugging
```bash
# Puerto debug: 5005
# Configurar en IDE: localhost:5005

# Logs en tiempo real
tail -f logs/application.log

# Métricas
curl http://localhost:8080/actuator/metrics
```

### Monitoreo
```bash
# Health check
curl http://localhost:8080/actuator/health

# Info de la aplicación
curl http://localhost:8080/actuator/info

# Métricas
curl http://localhost:8080/actuator/metrics

# Variables de entorno
curl http://localhost:8080/actuator/env
```

## 📊 Métricas y Reportes

### Cobertura de Código
```bash
# Generar reporte
mvn test jacoco:report

# Ver en navegador
open target/site/jacoco/index.html
```

### Análisis de Código
```bash
# SonarQube (si está configurado)
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=techtrend-microservice \
  -Dsonar.host.url=$SONAR_HOST_URL \
  -Dsonar.login=$SONAR_TOKEN
```

### Dependencias
```bash
# Verificar dependencias
mvn dependency:tree

# Escanear vulnerabilidades
mvn dependency:check

# Actualizar dependencias
mvn versions:display-dependency-updates
```

## 🔍 Troubleshooting

### Problemas Comunes

#### Puerto 8080 en uso
```bash
# Cambiar puerto
mvn spring-boot:run -Dserver.port=8081
```

#### Error de base de datos
```bash
# Limpiar y recompilar
mvn clean compile

# Verificar configuración en application.yml
```

#### Tests fallando
```bash
# Ejecutar tests específicos
mvn test -Dtest=AuthenticationServiceTest

# Ver logs detallados
mvn test -X
```

#### Problemas de GitFlow
```bash
# Reinicializar GitFlow
git flow init -d

# Verificar configuración
git config --list | grep gitflow
```

## 📚 Recursos Adicionales

### Documentación
- [README.md](./README.md) - Documentación completa
- [CONTRIBUTING.md](./CONTRIBUTING.md) - Guía de contribución
- [CHANGELOG.md](./CHANGELOG.md) - Historial de cambios
- [SECURITY.md](./SECURITY.md) - Política de seguridad

### Plantillas
- [Pull Request Template](.github/pull_request_template.md)
- [Issue Templates](.github/ISSUE_TEMPLATE/)
- [Commit Template](.gitmessage)

### Scripts
- [setup-gitflow.sh](./scripts/setup-gitflow.sh) - Configuración GitFlow
- [setup-gitflow.bat](./scripts/setup-gitflow.bat) - Configuración GitFlow (Windows)

## 🎯 Próximos Pasos

1. **Explorar la API** en Swagger UI
2. **Ejecutar tests** para verificar funcionamiento
3. **Crear tu primera feature** con GitFlow
4. **Revisar documentación** completa
5. **Contribuir** al proyecto

## 📞 Soporte

- **Issues**: [GitHub Issues](https://github.com/tu-usuario/techtrend-microservice/issues)
- **Email**: soporte@techtrend.com
- **Documentación**: [Wiki del proyecto]

---

**¡Disfruta explorando TechTrend!** 🚀
