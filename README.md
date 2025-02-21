1- Descargar dependecias:
-
- Usar Intellij IDEA Community edition https://www.jetbrains.com/idea/download
- Para este pequeño proyecto se esta usando maven como manejador de dependencias
  por lo que una vez descargado el proyecto debes usar el comando:
  ``` mvn clean install ```
- pero antes debe deshabilitar los test, para que no de error la compilacion o correr el proyecto.

2- Usar Docker -[ Docker-compose]
-
Lo primero es usar el comando
```bash
   docker-compose up -d  
 ``` 
 para descargar las imagenes de docker y crear los contenedores
 si quiere bajar los contenedores usar el comando:   
```bash
   docker-compose down  
 ``` 

