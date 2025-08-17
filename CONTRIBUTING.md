# 🤝 Guía de Contribución

¡Gracias por tu interés en contribuir a **TechTrend**! 🚀

Este documento proporciona las pautas necesarias para contribuir al proyecto de manera efectiva.

## 📋 Tabla de Contenidos

- [🚀 Comenzando](#-comenzando)
- [🌿 GitFlow Workflow](#-gitflow-workflow)
- [📝 Convenciones de Commits](#-convenciones-de-commits)
- [🔍 Pull Request Process](#-pull-request-process)
- [🧪 Testing](#-testing)
- [📚 Documentación](#-documentación)
- [🎯 Estándares de Código](#-estándares-de-código)
- [🐛 Reportando Bugs](#-reportando-bugs)
- [💡 Sugiriendo Mejoras](#-sugiriendo-mejoras)

## 🚀 Comenzando

### Prerrequisitos

- **Java 17** o superior
- **Maven 3.6** o superior
- **Git** con GitFlow instalado
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)

### Configuración Inicial

1. **Fork del repositorio**
   ```bash
   # Clonar tu fork
   git clone https://github.com/tu-usuario/techtrend-microservice.git
   cd techtrend-microservice
   ```

2. **Configurar GitFlow**
   ```bash
   # Ejecutar script de configuración
   ./scripts/setup-gitflow.sh
   # O en Windows:
   scripts\setup-gitflow.bat
   ```

3. **Verificar la instalación**
   ```bash
   # Compilar el proyecto
   mvn clean compile
   
   # Ejecutar tests
   mvn test
   
   # Ejecutar la aplicación
   mvn spring-boot:run
   ```

## 🌿 GitFlow Workflow

### Estructura de Ramas

```
main (producción)
├── develop (desarrollo)
├── feature/* (nuevas características)
├── release/* (preparación de releases)
├── hotfix/* (correcciones urgentes)
└── support/* (soporte para versiones antiguas)
```

### Flujo de Trabajo

#### 1. Desarrollo de Features

```bash
# Crear nueva feature
git flow feature start nombre-de-la-feature

# Hacer cambios y commits
git add .
git commit -m "feat(auth): agregar validación de email"

# Finalizar feature
git flow feature finish nombre-de-la-feature
```

#### 2. Preparación de Release

```bash
# Crear rama release
git flow release start 1.1.0

# Hacer ajustes finales
git commit -m "chore: actualizar versión a 1.1.0"

# Finalizar release
git flow release finish 1.1.0
```

#### 3. Hotfixes

```bash
# Crear hotfix
git flow hotfix start correccion-urgente

# Corregir el problema
git commit -m "fix(cart): corregir cálculo de total"

# Finalizar hotfix
git flow hotfix finish correccion-urgente
```

## 📝 Convenciones de Commits

### Formato

```
<tipo>(<alcance>): <descripción corta>

<descripción detallada opcional>

<notas de pie opcionales>
```

### Tipos de Commit

- `feat` - Nueva característica
- `fix` - Corrección de bug
- `docs` - Documentación
- `style` - Formato (no afecta código)
- `refactor` - Refactorización
- `test` - Pruebas
- `chore` - Tareas de build/configuración

### Alcances

- `auth` - Autenticación
- `catalog` - Catálogo
- `cart` - Carrito
- `payment` - Pagos
- `config` - Configuración
- `test` - Pruebas
- `docs` - Documentación

### Ejemplos

```bash
git commit -m "feat(auth): agregar validación de email en registro"
git commit -m "fix(cart): corregir cálculo de total con descuentos"
git commit -m "docs: actualizar README con instrucciones de GitFlow"
git commit -m "test(payment): agregar pruebas para procesamiento de pagos"
git commit -m "refactor(catalog): simplificar lógica de búsqueda"
git commit -m "chore: actualizar dependencias de Maven"
```

## 🔍 Pull Request Process

### 1. Crear Pull Request

1. **Usar la plantilla** en `.github/pull_request_template.md`
2. **Asignar reviewers** apropiados
3. **Agregar etiquetas** relevantes
4. **Referenciar issues** relacionados

### 2. Checklist de PR

- [ ] Código sigue convenciones de estilo
- [ ] Pruebas unitarias pasando
- [ ] Pruebas de integración pasando
- [ ] Documentación actualizada
- [ ] No hay warnings de compilación
- [ ] Código revisado por el desarrollador
- [ ] Todos los checks de CI pasando

### 3. Review Process

- **Al menos 1 aprobación** requerida
- **Todos los checks de CI** deben pasar
- **Resolver comentarios** de review
- **Squash commits** si es necesario

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=AuthenticationServiceTest

# Con cobertura
mvn test jacoco:report

# Tests de integración
mvn verify
```

### Estructura de Tests

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

### Cobertura Mínima

- **Cobertura total**: 80%
- **Cobertura de líneas**: 85%
- **Cobertura de ramas**: 75%

## 📚 Documentación

### Mantener Actualizada

- [ ] README.md actualizado
- [ ] Documentación de API con Swagger
- [ ] Comentarios en código crítico
- [ ] CHANGELOG.md actualizado

### Plantillas Disponibles

- [Pull Request Template](.github/pull_request_template.md)
- [Bug Report Template](.github/ISSUE_TEMPLATE/bug_report.md)
- [Feature Request Template](.github/ISSUE_TEMPLATE/feature_request.md)
- [Commit Message Template](.gitmessage)

## 🎯 Estándares de Código

### Java

- **Versión**: Java 17
- **Formato**: Usar el formateador del IDE
- **Convenciones**: Seguir Java Code Conventions
- **Documentación**: Javadoc para métodos públicos

### Spring Boot

- **Versión**: 3.2.0
- **Arquitectura**: Clean Architecture
- **Patrones**: SOLID Principles
- **Configuración**: Externalizada en YAML

### Testing

- **Framework**: JUnit 5
- **Mocking**: Mockito
- **Cobertura**: JaCoCo
- **Naming**: `should_ExpectedBehavior_when_StateUnderTest`

## 🐛 Reportando Bugs

### Usar la Plantilla

1. Ir a [Issues](https://github.com/tu-usuario/techtrend-microservice/issues)
2. Seleccionar "Bug Report"
3. Completar toda la información requerida

### Información Requerida

- **Descripción clara** del bug
- **Pasos para reproducir**
- **Comportamiento esperado**
- **Información del sistema**
- **Logs de error** (si aplica)

## 💡 Sugiriendo Mejoras

### Usar la Plantilla

1. Ir a [Issues](https://github.com/tu-usuario/techtrend-microservice/issues)
2. Seleccionar "Feature Request"
3. Completar toda la información requerida

### Información Requerida

- **Descripción del problema**
- **Solución propuesta**
- **Alternativas consideradas**
- **Criterios de aceptación**

## 🔧 Herramientas y Scripts

### Scripts Disponibles

```bash
# Configurar GitFlow
./scripts/setup-gitflow.sh

# Limpiar ramas mergeadas
git cleanup

# Ver logs formateados
git lg

# Ver todos los logs
git ll
```

### Hooks de Git

- **Pre-commit**: Validaciones automáticas
- **Commit-msg**: Validación de formato
- **Post-commit**: Notificaciones

## 📞 Contacto

Para preguntas o soporte:

- **Issues**: [GitHub Issues](https://github.com/tu-usuario/techtrend-microservice/issues)
- **Discussions**: [GitHub Discussions](https://github.com/tu-usuario/techtrend-microservice/discussions)
- **Email**: soporte@techtrend.com

## 📄 Licencia

Al contribuir, aceptas que tus contribuciones serán licenciadas bajo la [MIT License](LICENSE).

---

**¡Gracias por contribuir a TechTrend!** 🎉

Tu contribución hace que este proyecto sea mejor para todos.
