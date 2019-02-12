Java. Sphero
Se hizo uso de openCV para reconocer el robot.
Por medio del panel de video se reconoce el objeto desde donde se traza la ruta que pretendemos darle al robot.

El archivo señalado en la siguiente linea:
CascadeClassifier spheroDetector = new CascadeClassifier("C:\\OpenCV\\3.1.0\\opencv\\build\\x64\\vc12\\bin\\data8_Bueno\\cascade.xml");
Ubicado en el archivo Panel_video.java, es un archivo generado a partir de OpenCV.
OpenCV cuenta con dos herramientas para generar el archivo *.xml, al instalarlo normalmente se encuentra en 
opencv\build\'x64 | x86' desde el directorio opencv, los programas usados son "opencv_createsamples" y 
"opencv_traincascade", como se muestra en el link: https://docs.opencv.org/2.4/doc/user_guide/ug_traincascade.html


Se usaron aproximadamente 1000 imagenes, cabe resaltar que para reconocer el objeto al ser esférico se tuvo demasiado cuidado con los ejemplos.
El entrenamiento tarda mucho tiempo, dependiendo de la computadora en la que se haga.
Mi lap era de 500gb DD, 8gb de RAM, tardo más o menos medio dia en generar un archivo aveces más y a veces menos... genere varios archivos *.xml, 
probando hasta que el resultado del entrenamiento fuera bueno y reconociera al robot en  diferentes contextos.


