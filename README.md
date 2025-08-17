# 🚀 TechTrend - Plataforma de E-commerce

## 📋 Descripción

**TechTrend** es una plataforma de e-commerce moderna construida con arquitectura de microservicios usando Spring Boot 3.2.0. La aplicación incluye funcionalidades completas de autenticación, catálogo de productos, carrito de compras y procesamiento de pagos.

## 🏗️ Arquitectura de Microservicios

### 🔐 Microservicio de Autenticación
- **Funcionalidades**: Registro de usuarios, login, validación JWT, bloqueo de cuentas
- **Entidad**: `User` con roles CLIENT/ADMIN
- **Endpoints**: `/auth/register`, `/auth/login`, `/auth/validate`, `/auth/user-info`

### 📦 Microservicio de Catálogo
- **Funcionalidades**: Gestión de productos, control de stock, búsquedas, estadísticas
- **Entidad**: `Product` con gestión de inventario
- **Endpoints**: `/catalog/products`, `/catalog/products/{id}`, `/catalog/search`

### 🛒 Microservicio de Carrito
- **Funcionalidades**: Gestión de carrito, agregar/eliminar items, cálculos de totales
- **Entidad**: `CartItem` con gestión de cantidades
- **Endpoints**: `/cart/items`, `/cart/total`, `/cart/check-product`

### 💳 Microservicio de Pagos
- **Funcionalidades**: Procesamiento de pagos, validación de montos, simulación
- **Entidad**: `Payment` con estados PENDING/COMPLETED/FAILED
- **Endpoints**: `/payments/process`, `/payments/{id}/status`

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Seguridad y autenticación
- **JWT** - Tokens de autenticación
- **BCrypt** - Encriptación de contraseñas
- **H2 Database** - Base de datos en memoria (desarrollo)
- **PostgreSQL** - Base de datos de producción

### Testing
- **JUnit 5** - Framework de pruebas
- **Mockito** - Mocking de dependencias
- **JaCoCo** - Cobertura de código

### Herramientas
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de código boilerplate
- **Jackson** - Serialización JSON

## 📁 Estructura del Proyecto

```
techtrend-microservice/
├── src/
│   ├── main/
│   │   ├── java/com/techtrend/
│   │   │   ├── TechTrendApplication.java
│   │   │   ├── authentication/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   ├── catalog/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   ├── cart/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   ├── payment/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   └── common/
│   │   │       └── exception/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-test.yml
│   └── test/
│       └── java/com/techtrend/
│           ├── authentication/service/
│           ├── catalog/service/
│           ├── cart/service/
│           └── payment/service/
├── pom.xml
└── README.md
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- **Java 17** o superior
- **Maven 3.6** o superior
- **Git**

### **⚡ Inicio Rápido**
Para una configuración rápida, consulta: **[QUICK_START.md](./QUICK_START.md)**

### **🔧 Comandos de Configuración:**

#### **1. Configurar GitFlow:**
```bash
# Configuración automática
./scripts/setup-gitflow.sh
# O en Windows: scripts\setup-gitflow.bat

# Configuración manual
git flow init -d
git config commit.template .gitmessage
chmod +x .git/hooks/pre-commit
```

#### **2. Compilar y Ejecutar:**
```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar con cobertura
mvn test jacoco:report

# Ejecutar la aplicación
mvn spring-boot:run

# Ejecutar en modo debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

#### **3. Verificar Funcionamiento:**
```bash
# Health check
curl http://localhost:8080/health

# API principal
curl http://localhost:8080/api

# Swagger UI
open http://localhost:8080/swagger-ui.html
```

### **🌐 URLs de Acceso:**
- **API Principal**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **Actuator**: http://localhost:8080/actuator

### **🗄️ Base de Datos H2:**
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: (vacío)

## 🧪 Pruebas Unitarias

### Ejecutar Todas las Pruebas
```bash
mvn test
```

### Ver Cobertura de Código
```bash
mvn test jacoco:report
```

### Resultados de Pruebas
- **Total de pruebas**: 72
- **Autenticación**: 16 pruebas
- **Catálogo**: 20 pruebas  
- **Carrito**: 18 pruebas
- **Pagos**: 18 pruebas

### Estructura de Pruebas
Los tests están organizados de manera simple y legible:

```java
@Test
@DisplayName("✅ Registro exitoso con datos válidos")
void registroExitoso() {
    // PREPARAR
    // Configurar datos de prueba
    
    // EJECUTAR
    // Llamar al método que se está probando
    
    // VERIFICAR
    // Comprobar que el resultado es correcto
}
```

## 🌐 Endpoints de la API

### 🔐 Autenticación

#### Registrar Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "usuario@techtrend.com",
  "password": "password123",
  "role": "CLIENT"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@techtrend.com",
  "password": "password123"
}
```

#### Validar Token
```http
GET /api/auth/validate
Authorization: Bearer <token>
```

### 📦 Catálogo

#### Listar Productos
```http
GET /api/catalog/products?page=0&size=20
```

#### Obtener Producto por ID
```http
GET /api/catalog/products/1
```

#### Buscar Productos
```http
GET /api/catalog/products/search?name=laptop
```

#### Verificar Stock
```http
GET /api/catalog/products/1/stock?quantity=5
```

### 🛒 Carrito

#### Agregar Producto al Carrito
```http
POST /api/cart/items
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

#### Obtener Items del Carrito
```http
GET /api/cart/items
```

#### Actualizar Cantidad
```http
PUT /api/cart/items/1
Content-Type: application/json

{
  "quantity": 3
}
```

#### Calcular Total
```http
GET /api/cart/total
```

### 💳 Pagos

#### Procesar Pago
```http
POST /api/payments/process
Content-Type: application/json

{
  "orderId": "ORD-001",
  "amount": 1500.00,
  "paymentMethod": "CREDIT_CARD"
}
```

#### Obtener Estado del Pago
```http
GET /api/payments/1/status
```

## 🔧 Configuración

### Archivo de Configuración Principal (`application.yml`)

```yaml
spring:
  application:
    name: techtrend-microservice
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  
  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8080
  servlet:
    context-path: /api

# Configuraciones personalizadas
techtrend:
  auth:
    max-login-attempts: 3
    lockout-duration: 300000
  catalog:
    default-page-size: 20
  payment:
    max-amount: 100000.00
```

### Configuración de Pruebas (`application-test.yml`)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
  h2:
    console:
      enabled: false
  jpa:
    show-sql: false

logging:
  level:
    com.techtrend: WARN
```

## 🗄️ Base de Datos

### H2 Console (Desarrollo)
- **URL**: http://localhost:8080/api/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: (vacío)

### Entidades Principales

#### User
```java
@Entity
public class User {
    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private boolean isActive;
    private int loginAttempts;
    private LocalDateTime lockedUntil;
}
```

#### Product
```java
@Entity
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;
    private String category;
    private String brand;
    private String sku;
    private boolean isActive;
}
```

#### CartItem
```java
@Entity
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private boolean isActive;
}
```

#### Payment
```java
@Entity
public class Payment {
    private Long id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionId;
    private Long userId;
}
```

## 🔒 Seguridad

### Autenticación JWT
- **Algoritmo**: HS256
- **Expiración**: 24 horas
- **Claims**: email, role, exp, iat

### Encriptación de Contraseñas
- **Algoritmo**: BCrypt
- **Strength**: 12 (configurable)

### Bloqueo de Cuentas
- **Máximo intentos**: 3
- **Duración del bloqueo**: 5 minutos

## 📊 Características Destacadas

### ✅ Funcionalidades Implementadas
- [x] Registro y autenticación de usuarios
- [x] Gestión de catálogo de productos
- [x] Carrito de compras completo
- [x] Procesamiento de pagos
- [x] Validaciones robustas
- [x] Manejo global de excepciones
- [x] Logging detallado
- [x] Pruebas unitarias completas
- [x] Cobertura de código > 90%

### 🎯 Buenas Prácticas Aplicadas
- **Clean Architecture** con separación de capas
- **SOLID Principles** implementados
- **DTOs** para transferencia de datos
- **Validaciones** con Bean Validation
- **Logging estructurado** para debugging
- **Tests descriptivos** y legibles
- **Documentación completa** de APIs

## 🐛 Solución de Problemas

### Error: Cannot load driver class: org.h2.Driver
**Solución**: El driver H2 ya está incluido en las dependencias. Si persiste el error:
1. Limpiar y recompilar: `mvn clean compile`
2. Verificar que H2 no tenga scope `test` en `pom.xml`

### Error: Puerto 8080 en uso
**Solución**: Cambiar el puerto en `application.yml`:
```yaml
server:
  port: 8081
```

### Error: Base de datos no encontrada
**Solución**: Verificar la configuración de la base de datos en `application.yml`

## 📈 Métricas y Monitoreo

### Health Checks
- **URL**: http://localhost:8080/api/actuator/health
- **Estado**: UP/DOWN con detalles de componentes

### Logs
- **Nivel**: INFO para operaciones normales
- **Nivel**: WARN para situaciones de atención
- **Nivel**: ERROR para errores críticos

## 🌿 GitFlow y Buenas Prácticas

### **📋 Configuración Inicial de GitFlow:**

```bash
# Inicializar GitFlow
git flow init

# Configurar commit template
git config commit.template .gitmessage

# Hacer el hook ejecutable (Linux/Mac)
chmod +x .git/hooks/pre-commit
```

### **🔄 Flujo de Trabajo con GitFlow:**

#### **1. Desarrollo de Nuevas Características:**
```bash
# Crear nueva rama feature
git flow feature start nombre-de-la-feature

# Hacer cambios y commits
git add .
git commit -m "feat(auth): agregar validación de email"

# Finalizar feature
git flow feature finish nombre-de-la-feature
```

#### **2. Preparación de Release:**
```bash
# Crear rama release
git flow release start 1.1.0

# Hacer ajustes finales
git commit -m "chore: actualizar versión a 1.1.0"

# Finalizar release
git flow release finish 1.1.0
```

#### **3. Hotfixes:**
```bash
# Crear hotfix
git flow hotfix start nombre-del-hotfix

# Corregir el problema
git commit -m "fix(cart): corregir cálculo de total"

# Finalizar hotfix
git flow hotfix finish nombre-del-hotfix
```

### **📝 Convenciones de Commits:**

#### **Formato:**
```
<tipo>(<alcance>): <descripción corta>

<descripción detallada opcional>

<notas de pie opcionales>
```

#### **Tipos de Commit:**
- `feat` - Nueva característica
- `fix` - Corrección de bug
- `docs` - Documentación
- `style` - Formato (no afecta código)
- `refactor` - Refactorización
- `test` - Pruebas
- `chore` - Tareas de build/configuración

#### **Alcances:**
- `auth` - Autenticación
- `catalog` - Catálogo
- `cart` - Carrito
- `payment` - Pagos
- `config` - Configuración
- `test` - Pruebas
- `docs` - Documentación

#### **Ejemplos:**
```bash
git commit -m "feat(auth): agregar validación de email en registro"
git commit -m "fix(cart): corregir cálculo de total con descuentos"
git commit -m "docs: actualizar README con instrucciones de GitFlow"
git commit -m "test(payment): agregar pruebas para procesamiento de pagos"
git commit -m "refactor(catalog): simplificar lógica de búsqueda"
git commit -m "chore: actualizar dependencias de Maven"
```

### **🔍 Pull Request Process:**

#### **1. Crear Pull Request:**
- Usar la plantilla de PR en `.github/pull_request_template.md`
- Asignar reviewers apropiados
- Agregar etiquetas relevantes

#### **2. Checklist de PR:**
- [ ] Código sigue convenciones de estilo
- [ ] Pruebas unitarias pasando
- [ ] Pruebas de integración pasando
- [ ] Documentación actualizada
- [ ] No hay warnings de compilación
- [ ] Código revisado por el desarrollador

#### **3. Review Process:**
- Al menos 1 aprobación requerida
- Todos los checks de CI deben pasar
- Resolver comentarios de review

### **🏷️ Etiquetas de Issues:**

#### **Tipos:**
- `bug` - Reporte de bug
- `enhancement` - Nueva característica
- `documentation` - Mejoras en documentación
- `good first issue` - Para nuevos contribuidores

#### **Microservicios:**
- `auth` - Autenticación
- `catalog` - Catálogo
- `cart` - Carrito
- `payment` - Pagos

#### **Prioridades:**
- `priority: high` - Alta prioridad
- `priority: medium` - Prioridad media
- `priority: low` - Baja prioridad

### **🚀 CI/CD Pipeline:**

#### **Workflows Automatizados:**
- **Tests**: Ejecuta pruebas unitarias e integración
- **Security**: Escaneo de vulnerabilidades
- **Quality**: Análisis de código con SonarQube
- **Build**: Compilación y empaquetado

#### **Triggers:**
- Push a `main` y `develop`
- Pull Requests a `main` y `develop`

### **📊 Métricas de Calidad:**

#### **Cobertura de Código:**
- Mínimo 80% de cobertura
- Reportes generados con JaCoCo
- Integración con Codecov

#### **Análisis de Código:**
- SonarQube para análisis estático
- Detección de code smells
- Métricas de complejidad ciclomática

### **🔒 Seguridad:**

#### **Escaneo de Dependencias:**
- OWASP Dependency Check
- Verificación de vulnerabilidades conocidas
- Actualización automática de dependencias

#### **Buenas Prácticas:**
- No committear credenciales
- Usar variables de entorno
- Validar inputs de usuario
- Implementar rate limiting

### **📚 Documentación:**

#### **Mantenimiento:**
- README actualizado
- Documentación de API con Swagger
- Comentarios en código crítico
- Changelog mantenido

#### **Plantillas:**
- Pull Request template
- Issue templates (bug, feature)
- Commit message template

## 🤝 Contribución

### Guías de Contribución
1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

### Estándares de Código
- **Java**: Java 17
- **Formato**: Usar el formateador de IDE
- **Tests**: Mantener cobertura > 90%
- **Documentación**: Comentar métodos públicos

## 📚 Documentación Completa

### **📋 Archivos de Documentación:**

- **[QUICK_START.md](./QUICK_START.md)** - ⚡ Guía de inicio rápido con todos los comandos
- **[CONTRIBUTING.md](./CONTRIBUTING.md)** - 🤝 Guía completa de contribución
- **[CHANGELOG.md](./CHANGELOG.md)** - 📋 Historial de cambios y versiones
- **[SECURITY.md](./SECURITY.md)** - 🔒 Política de seguridad y reporte de vulnerabilidades

### **🔧 Scripts y Herramientas:**

- **[scripts/setup-gitflow.sh](./scripts/setup-gitflow.sh)** - 🐧 Script de configuración GitFlow (Linux/Mac)
- **[scripts/setup-gitflow.bat](./scripts/setup-gitflow.bat)** - 🪟 Script de configuración GitFlow (Windows)

### **📝 Plantillas y Configuración:**

- **[.github/pull_request_template.md](.github/pull_request_template.md)** - 📋 Plantilla para Pull Requests
- **[.github/ISSUE_TEMPLATE/](.github/ISSUE_TEMPLATE/)** - 🐛 Plantillas para Issues (Bug/Feature)
- **[.gitmessage](.gitmessage)** - 💬 Plantilla para mensajes de commit
- **[.gitflow](.gitflow)** - 🌿 Configuración de GitFlow
- **[.gitattributes](.gitattributes)** - 📁 Configuración de archivos Git

### **🚀 CI/CD y Workflows:**

- **[.github/workflows/ci.yml](.github/workflows/ci.yml)** - 🔄 Pipeline de CI/CD automatizado

## 🛠️ Comandos Útiles

### **🔧 Desarrollo:**
```bash
# Configurar GitFlow
./scripts/setup-gitflow.sh

# Compilar y testear
mvn clean compile test

# Ejecutar con cobertura
mvn test jacoco:report

# Ejecutar aplicación
mvn spring-boot:run

# Debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### **🌿 GitFlow:**
```bash
# Crear feature
git flow feature start mi-feature

# Commit con convenciones
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

### **🧪 Testing:**
```bash
# Ejecutar todos los tests
mvn test

# Test específico
mvn test -Dtest=AuthenticationServiceTest

# Con reporte de cobertura
mvn test jacoco:report

# Ver cobertura en navegador
open target/site/jacoco/index.html
```

### **🔍 Monitoreo:**
```bash
# Health check
curl http://localhost:8080/health

# Métricas
curl http://localhost:8080/actuator/metrics

# Info de la aplicación
curl http://localhost:8080/actuator/info

# Variables de entorno
curl http://localhost:8080/actuator/env
```

### **🗄️ Base de Datos:**
```bash
# Acceder a H2 Console
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Usuario: sa
# Contraseña: (vacío)

# Ver productos
curl http://localhost:8080/api/catalog/products
```

### **🔒 Seguridad:**
```bash
# Escanear dependencias
mvn dependency:check

# Verificar vulnerabilidades
mvn dependency:tree

# Actualizar dependencias
mvn versions:display-dependency-updates
```

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo **[LICENSE](./LICENSE)** para más detalles.

## 👥 Equipo

- **Desarrollador Principal**: TechTrend Team
- **Arquitecto**: TechTrend Team
- **QA**: TechTrend Team

## 📞 Soporte

Para soporte técnico o preguntas:
- **Email**: soporte@techtrend.com
- **Issues**: [GitHub Issues](https://github.com/tu-usuario/techtrend-microservice/issues)
- **Discussions**: [GitHub Discussions](https://github.com/tu-usuario/techtrend-microservice/discussions)
- **Seguridad**: security@techtrend.com

---

## 🎉 ¡TechTrend está listo para usar!

La aplicación se ejecuta en: **http://localhost:8080/api**

**¡Disfruta explorando la plataforma de e-commerce más moderna!** 🚀

---

**📚 Documentación**: [QUICK_START.md](./QUICK_START.md) | [CONTRIBUTING.md](./CONTRIBUTING.md) | [CHANGELOG.md](./CHANGELOG.md) | [SECURITY.md](./SECURITY.md)
