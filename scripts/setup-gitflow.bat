@echo off
REM Script de configuración de GitFlow para TechTrend (Windows)
REM Este script configura GitFlow y todas las herramientas necesarias

echo 🚀 Configurando GitFlow para TechTrend...

REM Verificar que Git esté instalado
git --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Git no está instalado. Por favor, instala Git primero.
    echo Descarga Git desde: https://git-scm.com/download/win
    pause
    exit /b 1
)

REM Verificar que GitFlow esté instalado
git flow version >nul 2>&1
if errorlevel 1 (
    echo ⚠️  GitFlow no está instalado.
    echo Por favor, instala GitFlow manualmente:
    echo   1. Descarga desde: https://github.com/nvie/gitflow/releases
    echo   2. O usa: winget install GitFlow.GitFlow
    echo   3. O usa: chocolatey install gitflow
    pause
    exit /b 1
)

echo ✅ GitFlow instalado correctamente

REM Inicializar GitFlow
echo 🔧 Inicializando GitFlow...
git flow init -d

REM Configurar commit template
echo 📝 Configurando commit template...
git config commit.template .gitmessage

REM Configurar alias útiles
echo ⚡ Configurando alias de Git...
git config alias.feature "flow feature"
git config alias.release "flow release"
git config alias.hotfix "flow hotfix"
git config alias.support "flow support"

REM Configurar configuración de usuario (si no está configurada)
git config user.name >nul 2>&1
if errorlevel 1 (
    echo 👤 Configurando usuario de Git...
    set /p username="Ingresa tu nombre: "
    set /p email="Ingresa tu email: "
    git config user.name "%username%"
    git config user.email "%email%"
)

REM Configurar configuración de línea de comandos
echo 🔧 Configurando configuración de Git...
git config core.autocrlf true
git config core.eol crlf
git config pull.rebase false

echo ✅ Configuración de GitFlow completada exitosamente!
echo.
echo 📋 Comandos útiles:
echo   git flow feature start ^<nombre^>     - Crear nueva feature
echo   git flow feature finish ^<nombre^>    - Finalizar feature
echo   git flow release start ^<version^>    - Crear release
echo   git flow release finish ^<version^>   - Finalizar release
echo   git flow hotfix start ^<nombre^>      - Crear hotfix
echo   git flow hotfix finish ^<nombre^>     - Finalizar hotfix
echo.
echo 🎯 Próximos pasos:
echo   1. Crear tu primera feature: git flow feature start mi-primera-feature
echo   2. Hacer cambios y commits siguiendo las convenciones
echo   3. Finalizar la feature: git flow feature finish mi-primera-feature
echo.
echo 📚 Para más información, consulta el README.md
pause
