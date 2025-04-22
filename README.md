# Guía de Instalación y Configuración del Proyecto

Este documento proporciona las instrucciones necesarias para configurar el entorno de desarrollo y ejecutar la aplicación.

## 📋 Requisitos Previos

Asegúrate de tener instalado lo siguiente antes de comenzar:

-   **IDE Recomendado**: IntelliJ IDEA Community Edition
-   **JDK**: OpenJDK 17.0.14
-   **Maven**: Apache Maven 3.9.9
-   **Spring Boot**: 3.4.4 (gestionado por Maven a través del `pom.xml`)
-   **Base de datos**: SQLite

## 🛠 Instalación y Configuración

Sigue estos pasos para configurar el entorno y el proyecto:

### 1. Instalación de JDK

Instala el JDK recomendado (OpenJDK 17.0.14). Puedes hacerlo directamente desde IntelliJ IDEA o descargándolo manualmente.

Una vez instalado, configura las variables de entorno `JAVA_HOME` y `PATH`. En Windows, puedes usar los siguientes comandos en el Símbolo del sistema o PowerShell (ajusta la ruta si es necesario):

```bash
# Establece JAVA_HOME apuntando a tu directorio de instalación del JDK
setx JAVA_HOME "C:\Users\example\.jdks\ms-17.0.14"

# Agrega el directorio bin del JDK al PATH del sistema
setx PATH "%PATH%;%JAVA_HOME%\bin"
```
*Nota: Es posible que necesites reiniciar tu terminal o el sistema para que los cambios en PATH surtan efecto.*

### 2. Instalación de Maven

Se recomienda usar [Chocolatey](https://chocolatey.org/) (un administrador de paquetes para Windows) para instalar Maven.

1.  Abre **PowerShell como Administrador**.
2.  Ejecuta el siguiente comando para instalar Chocolatey (si aún no lo tienes):
    ```powershell
    Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('[https://community.chocolatey.org/install.ps1](https://community.chocolatey.org/install.ps1)'))
    ```
3.  Una vez instalado Chocolatey, instala Maven ejecutando:
    ```bash
    choco install maven
    ```
4.  Cierra y vuelve a abrir tu terminal para asegurar que Maven esté en el PATH.

### 3. Configuración del Proyecto

1.  Clona este repositorio en tu máquina local (si aún no lo has hecho).
2.  Abre una terminal o Símbolo del sistema en la **carpeta raíz del proyecto**.
3.  Ejecuta el siguiente comando para descargar dependencias y construir el proyecto:
    ```bash
    mvn clean install
    ```
4.  Para iniciar la aplicación Spring Boot:
    ```bash
    mvn spring-boot:run
    ```

## 🌐 Verificación

Puedes verificar que la configuración sea correcta de las siguientes maneras:

1.  **Verificar versiones de JDK y Maven**:
    Abre una terminal y ejecuta:
    ```bash
    mvn -v
    ```
    Confirma que las versiones de Java y Maven coinciden (o son compatibles) con las especificadas en los requisitos previos.

2.  **Verificar la aplicación en ejecución**:
    Una vez que hayas iniciado la aplicación con `mvn spring-boot:run`, abre tu navegador web y accede a:
    ```
    http://localhost:7070/test/index
    ```
    Deberías ver el mensaje: `"Test Index Controller"` en la página, lo que confirma que el backend está funcionando correctamente.

## 🗃 Base de Datos

-   **Sistema**: SQLite
-   **Ventajas**:
    -   Configuración sencilla (no requiere servidor externo).
    -   Bajo requerimiento de recursos.
    -   La base de datos se almacena como un archivo local dentro del proyecto.
