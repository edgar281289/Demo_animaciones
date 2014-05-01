package base;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;

public class Figura {

    float dt, radio, altura;

    public int identificadorFigura;
    public float[] velocidades = new float[3];
    public float[] posiciones = new float[3];
    public BranchGroup ramaVisible = new BranchGroup();
    public TransformGroup desplazamientoFigura = new TransformGroup();

    public boolean adelante, atras, izquierda, derecha, caminando, corriendo, quieto;
    public boolean esMDL;
    public ArrayList<base.Figura> listaObjetosFisicos;
    public BranchGroup conjunto;
    public Matrix3f matrizRotacionPersonaje = new Matrix3f();
    public Juego juego;

    //atributos opcionales para dotar a la figura de cierta inteligencia
    public Vector3f localizacionObjetivo;
    public int estadoFigura;                    //Dependiendo del estado de la figura, su entorno, y del juego, la figura tiene un comportamiento dado.
    public Figura objetivo;                      //El objetivo puede ser: localizar otra figura,
    //Si adem‡s, hubiera que realizar uan accion particular (ej. Dispararle, darle alimento) se necesitaria otro atributo (ej. TareaObjetivo)

    public Figura(float radio_, float altura_, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego) {
        this.listaObjetosFisicos = listaObjetosFisicos;
        this.conjunto = conjunto;
        this.juego = juego;
        radio = radio_;
        altura = altura_;
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        ramaVisible.setCapability(BranchGroup.ALLOW_DETACH);
    }

    public void crearPropiedades(float posX, float posY, float posZ) {
        //A–adiendo objetoVisual asociado al grafo de escea y a la lista de objetos fisicos visibles y situandolo
        conjunto.addChild(ramaVisible);
        this.listaObjetosFisicos.add(this);
        identificadorFigura = listaObjetosFisicos.size() - 1;
        //Presentaci—n inicial de la  figura visual asociada al cuerpo rigido
        Transform3D inip = new Transform3D();
        inip.set(new Vector3f(posX, posY, posZ));
        desplazamientoFigura.setTransform(inip);
        //Actualizacion de posicion. La rotacion se empezar‡ a actualizar en el primer movimiento (ver final del metodo mostrar(rigidBody))
        this.posiciones[0] = posX;
        this.posiciones[1] = posY;
        this.posiciones[2] = posZ;
        matrizRotacionPersonaje.set(new AxisAngle4d(0, 1, 0, 0));
    }

    public void mostrar() {
        //Actualizar los datos de localizacion de figuras no fisicas. Las fig fisicas tienen como parametro rigidBody.
        Transform3D inip = new Transform3D(matrizRotacionPersonaje, new Vector3f(posiciones[0], posiciones[1], posiciones[2]), 1f);
        desplazamientoFigura.setTransform(inip);
    }

    public void actualizarLocalizacion(float dt) {
     //Actualizar los datos de localizacion no JBullet
        // ...
    }

    public void remover() {

        try {
            conjunto.removeChild(this.identificadorFigura);
            for (int i = this.identificadorFigura + 1; i < this.listaObjetosFisicos.size(); i++) {
                listaObjetosFisicos.get(i).identificadorFigura = listaObjetosFisicos.get(i).identificadorFigura - 1;
            }
            listaObjetosFisicos.remove(this.identificadorFigura);
        } catch (Exception e) {
            System.out.println("Ya eliminado");
        }
    }

    public void actualizar(float dt) {
        Transform3D t3dPersonaje = new Transform3D();
        desplazamientoFigura.getTransform(t3dPersonaje);
        Transform3D copiat3dPersonaje = new Transform3D(t3dPersonaje);

        if (identificadorFigura != 0) {
            for (int p = 0; p < 3; p++) {
                posiciones[p] = posiciones[p] + dt * velocidades[p];
                t3dPersonaje.get(matrizRotacionPersonaje);
            }
        } else {
            if (juego.personaje.derecha || juego.personaje.izquierda || juego.personaje.adelante || juego.personaje.atras) {
         //Si se presiona una tecla se da valor a un delta de velocidad hacia adelante y un delta de Angulo

                float deltaVel = 0;
                float deltaAngulo = 0;
                if (juego.personaje.derecha) {
                    deltaAngulo = -0.05f;
                }
                if (juego.personaje.izquierda) {
                    deltaAngulo = 0.05f;
                }
                if (juego.personaje.adelante) {
                    deltaVel = 0.05f;
                }
                if (juego.personaje.atras) {
                    deltaVel = -0.05f;
                }

                //Se calcula el control con respecto al suelo  (del objeto controlado).
                float distAlsuelo = radio;
                float subirBajarPersonaje = controlarAlturaSuelo(t3dPersonaje, juego.explorador, distAlsuelo);
                
                System.out.println("Distancia al suelo: " + distAlsuelo);
                System.out.println("SubirBajarPersonaje: " + 0);
                
                //Se crean un Transform3D con los micro-desplazamientos/rotaciones. 
                Transform3D t3dNueva = new Transform3D();
                t3dNueva.set(new Vector3d(0.0d, 0.0f, deltaVel));
                t3dNueva.setRotation(new AxisAngle4f(0, 1f, 0, deltaAngulo));
                t3dPersonaje.mul(t3dNueva);

                //Se actualiza la posicion del personaje y de la matriz de rotación
                Vector3d posPersonaje = new Vector3d(0, 0, 0);
                t3dPersonaje.get(matrizRotacionPersonaje, posPersonaje);
                posiciones[0] = (float) posPersonaje.x;
                posiciones[1] = (float) posPersonaje.y;
                posiciones[2] = (float) posPersonaje.z;

                //SONAR:   se lanza desde el centro del personaje con dirección Sonar.  Se presentan los objetos encontrados (por sus nombres)
                Vector3d posSonar = new Vector3d(0, 0, 0);
                Transform3D t3dSonar = new Transform3D(matrizRotacionPersonaje, new Vector3f(0.0f, subirBajarPersonaje, deltaVel + 1f), 1f);
                copiat3dPersonaje.mul(t3dSonar);
                copiat3dPersonaje.get(posSonar);

                //Point3d posActual = new Point3d(juego.personaje.posiciones[0], juego.personaje.posiciones[1], juego.personaje.posiciones[2]);
                Vector3d direccion = new Vector3d(posSonar.x - posPersonaje.x, posSonar.y - posPersonaje.y, posSonar.z - posPersonaje.z);
                //Vector3d direccion = new Vector3d(juego.personaje.posiciones[0], -20, juego.personaje.posiciones[2]);
                juego.explorador.setShapeRay(new Point3d(posPersonaje.x, posPersonaje.y, posPersonaje.z), direccion);
                //juego.explorador.setShapeRay(posActual, direccion);
                
                /**
                 * Ahora mismo estamos lanzando el rayo hacia delante
                 */
                PickResult[] objMasCercano = juego.explorador.pickAllSorted();
                if(objMasCercano != null){
                    for(PickResult current : objMasCercano){
                    Node nd = current.getObject();
                    //System.out.println("A la vista esta... " + nd.getUserData());
                }
                }else{
                    //System.out.println("....nada a la vista");
                }
                
            }
        }
    }

    public void asignarObjetivo(Figura Objetivo, float aceleracionMuscular) {
        this.objetivo = Objetivo;
        this.localizacionObjetivo = new Vector3f(this.objetivo.posiciones[0], this.objetivo.posiciones[1], this.objetivo.posiciones[2]);
    }

    public void asignarObjetivo(Vector3f localizacionObjetivo, float aceleracionMuscular) {
        this.localizacionObjetivo = localizacionObjetivo;
    }

    public Vector3d conseguirDireccionFrontal() {
        Transform3D t3dPersonaje = new Transform3D();
        this.desplazamientoFigura.getTransform(t3dPersonaje);
        Transform3D copiat3dPersonaje = new Transform3D(t3dPersonaje);
        Transform3D t3dSonar = new Transform3D();
        t3dSonar.set(new Vector3f(10.0f, 0, 10f));
        copiat3dPersonaje.mul(t3dSonar);
        Vector3d posSonar = new Vector3d(0, 0, 0);
        copiat3dPersonaje.get(posSonar);
        Vector3f posPersonaje = new Vector3f(0, 0, 0);
        t3dPersonaje.get(posPersonaje);
        return new Vector3d(posSonar.x - posPersonaje.x, posSonar.y - posPersonaje.y, posSonar.z - posPersonaje.z);
    }

    float controlarAlturaSuelo(Transform3D t3dPersonaje, PickTool localizador, float objAlSuelo) {
        Vector3d posicionActual = new Vector3d(0, 0, 0);
        t3dPersonaje.get(posicionActual);
        Point3d posActual = new Point3d(posicionActual.x, posicionActual.y, posicionActual.z);
        float subirBajarPersonaje = 0;
        localizador.setShapeRay(posActual, new Vector3d(posActual.x, -20, posActual.z));
        PickResult[] lista = localizador.pickAllSorted();
        boolean enc = false;
        if (lista != null) {
            for (PickResult objMasCercano : lista) {
                //System.out.println(objMasCercano.getObject().getUserData());
                if ((objMasCercano != null) && (!objMasCercano.getObject().getUserData().equals("figura_" + identificadorFigura))) {
                    Node nd = objMasCercano.getObject();
                    System.out.println("A la vista esta... " + nd.getUserData());
                    float distanciaSuelo = (float) objMasCercano.getClosestIntersection(posActual).getDistance();
                    subirBajarPersonaje = objAlSuelo + distanciaSuelo;     //System.out.println("... distancia hacia arriba="+distanciaSuelo);
                    enc = true;
                    break;
                }else{
                    System.out.println("No encontramos nada... ");
                }
            }
        }
        if (!enc) {
            //System.out.println("enc = false");
            localizador.setShapeRay(posActual, new Vector3d(posActual.x, 20, posActual.z));
            lista = localizador.pickAllSorted();
            if (lista != null) {
                for (PickResult objMasCercano : lista) {
                    if ((objMasCercano != null) && 
                            (!objMasCercano.getObject().getUserData().equals("figura_" + identificadorFigura))) {
                        Node nd = objMasCercano.getObject();
                        System.out.println("A la vista esta... " + nd.getUserData());
                        float distanciaSuelo = (float) objMasCercano.getClosestIntersection(posActual).getDistance();
                        subirBajarPersonaje = objAlSuelo - distanciaSuelo;     //System.out.println("... distancia hacia abajo="+distanciaSuelo);
                        enc = true;
                        break;
                    }else {
                        System.out.println("No encontramos nada... ");
                    }
                }
            }
        }
        return subirBajarPersonaje * 0.5f;
    }

}
