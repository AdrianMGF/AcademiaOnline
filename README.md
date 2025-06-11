# AcademiaOnline
Proyecto para asignatura Ingenieria Web

En este repositorio se incluye el proyecto para la asignatura de Ingenieria Web, basado en los frameworks de Java Spring y Vaadin. Se utiliza una base de datos relacional Mysql. 
Para el funcionamiento de envios de correos SMTP se utiliza un sandbox para el envio y recibo de estos correos. 

Si se quiere otro entorno SMTP mas adelante te muestro como cambiar la configuracion para logarlo.

# Funcionamiento de la plataforma

La plataforma permite registrarse como usuario (con rol de alumno) e iniciar sesion (independientemente del rol).
Para el registro se enviara un correo para confirmar el registro el cual enviara un enlace con un token de verificacion.
Incluye creacion, publicacion y edicion de cursos y modulos.
Subida y consulta de material didactico (archivos).
Creacion de tests de evaluacion con correccion automatica.
Inscripcion y seguimiento de cursos por parte del alumnado.
Realizacion de test por parte del alumnado de los distintos modulos de cada curso.
Panel de administracion para control de usuarios y roles.
Notificaciones via SMTP.

El sistema esta diseñado de forma segura y con interfaz intutitiva para el usuario.

# Diagramas de clases y contenedores

Incluyo archivos en aplicacion draw.io para mejor visualizacion de estos diagramas. Tambien se podran editar.
Estos archivos podraas descargarlos y con la aplicacion o en la web https://draw.io/ podras visualizarlos.

  - [Diagrama de Clases](Diagramas/containers.drawio)
  - [Diagrama de Contenedores](Diagramas/Dragramas.drawio)

Igualmente dejo capturas de los diagramas:

![dragramaclases](https://github.com/user-attachments/assets/982f4cb4-4cbc-4a32-9942-ccd6c48399f8)
![clasesUsuariodiagrama](https://github.com/user-attachments/assets/b438c21a-44e1-4718-801e-5c2f9975ed78)
![cursomodulodiagrama](https://github.com/user-attachments/assets/816d3ac3-4aeb-4bb5-b289-562567f39d79)
![preghunta respuestadiag](https://github.com/user-attachments/assets/0e4f2b5e-2274-424c-8850-c7c136edb293)
![archivodiag](https://github.com/user-attachments/assets/a8acfc87-f420-4983-b7a4-1edc962eda90)


# Configuración de la aplicación

Abra application.properties y modifique las siguientes propiedades del servidor de base de datos (por ejemplo, MySQL):

  - spring.datasource.url
  - spring.datasource.username
  - spring.datasource.password

Abra application.properties y modifique las siguientes propiedades del servidor de correo (por ejemplo, Gmail):

  - spring.mail.host
  - spring.mail.port
  - spring.mail.username
  - spring.mail.password


Para este caso he usado un SMTP sandbox como ya he mencionado anteriormente. Dejo las cedenciales aqui para su uso:

  - https://mailtrap.io/
  - Ususario: icuentaiw@gmail.com
  - Contraseña: Icuentaiw@gmail.com1

Con estas credenciales podras usar el servicio se SMTP ya configurado en el proyecto. Navegando a la pestaña de sandbox y seleccionando el box con nombre 'My Inbox'.

# Ejecutar la aplicación

El proyecto es un proyecto Maven estándar. Para ejecutarlo desde la línea de comandos, escriba mvnw(Windows) o ./mvnw(Mac y Linux) y abra http://localhost:8080 en su navegador.

También puedes importar el proyecto al IDE que prefieras, como lo harías con cualquier proyecto Maven. Obtén más información sobre cómo importar proyectos Vaadin a diferentes IDE (Eclipse, IntelliJ IDEA, NetBeans y VS Code).


# Implementación en producción

En mi caso he usado la herramienta ngrok para exponer mi servidor local.

En caso de querer crear una compilacion de produccion:
Llame a mvnw clean package -Pproduction(Windows) o ./mvnw clean package -Pproduction(Mac y Linux). Esto generará un archivo JAR con todas las dependencias y recursos frontend, listo para implementar. El archivo se encuentra en la targetcarpeta una vez completada la compilación.

Una vez creado el archivo JAR, puedes ejecutarlo usando java -jar target/fullstackwebapp-1.0.0.jar


