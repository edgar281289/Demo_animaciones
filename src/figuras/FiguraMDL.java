package figuras;

import base.Juego;
import base.Figura;
import base.DeteccionControlPersonaje;
import utilidades.CapabilitiesMDL;
import com.sun.j3d.loaders.Scene;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.AnimationBehavior;
import net.sf.nwn.loader.NWNLoader;

public class FiguraMDL extends Figura {

    public Scene escenaPersonaje1;
    public AnimationBehavior ab = null;
    public String nombreAnimacionCorriendo, nombreAnimacionCaminando, nombreAnimacionQuieto, nombreAnimacionLuchando;
    Vector3d direccion = new Vector3d(0, 0, 10);
    public float radio, alturaP, alturaDeOjos;
    boolean esPersonaje;
    boolean animacion_caminando_lanzada = false;
    boolean animacion_corriendo_lanzada = false;
    public BranchGroup RamaMDL;
    private boolean moviendoLaEspada;

    public FiguraMDL(float radio_, float altura_, String ficheroMDL, float radio, BranchGroup conjunto, ArrayList<Figura> listaObjetos, Juego juego, boolean esPersonaje) {
        super(radio_, altura_, conjunto, listaObjetos, juego);
        esMDL = true;
        this.esPersonaje = esPersonaje;
        //Creando una apariencia
        Appearance apariencia = new Appearance();
        this.radio = radio;
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        TransformGroup figuraVisual = crearObjetoMDL(ficheroMDL, radio * 2);

        ramaVisible.addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(figuraVisual);

        
        if (esPersonaje) {
            DeteccionControlPersonaje mueve = new DeteccionControlPersonaje(this);
            mueve.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
            ramaVisible.addChild(mueve);
        }
        
    }

    TransformGroup crearObjetoMDL(String archivo, float multiplicadorEscala) {
        RamaMDL = new BranchGroup();
        RamaMDL.setUserData("BG_figura_" + this.identificadorFigura);
        float rotacionX = 0;
        float rotacionY = 0;
        float rotacionZ = 0;
        float escalaTamano = 1f;
        float desplazamientoY = 0;
        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            escenaPersonaje1 = nwn2.load(new URL("file://localhost/" + System.getProperty("user.dir") + "/" + archivo));
            RamaMDL = escenaPersonaje1.getSceneGroup();
            //Recorrido por los- objetos para darle capacidades a sus Shapes3D
            //System.out.println("ID: " + this.identificadorFigura);
            CapabilitiesMDL.setCapabilities(RamaMDL, this.identificadorFigura);
            CapabilitiesMDL.etiquetarTodo(RamaMDL, estadoFigura, juego, "figura_" + identificadorFigura);
            //Para cada Objeto MDL dar nombre las animaciones de la figura. Dar rotaciones a la figuraMDL (suelen venir giradas)
            ab = (AnimationBehavior) escenaPersonaje1.getNamedObjects().get("AnimationBehavior");

            if (archivo.equals("objetosMDL/Iron_Golem.mdl")) {
                nombreAnimacionCorriendo = "iron_golem:crun";
                nombreAnimacionCaminando = "iron_golem:cwalk";
                nombreAnimacionQuieto = "iron_golem:cpause1";
                nombreAnimacionLuchando = "iron_golem:run";//"iron_golem:ca1slashl";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                escalaTamano = 0.65f;
                desplazamientoY = -1f;
                alturaP = (float) 3f * escalaTamano;
                alturaDeOjos = alturaP;
                java.util.List anims = new Vector<String>();
                anims.add("iron_golem:cpause1");
                ab.setDefaultAnimations(anims);
                ab.playDefaultAnimation();
            }
            if (archivo.equals("objetosMDL/Doomknight.mdl")) {
                nombreAnimacionCaminando = "Doomknight:crun";
                nombreAnimacionCaminando = "Doomknight:cwalk";
                nombreAnimacionQuieto = "Doomknight:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 2f;
                escalaTamano = 0.8f;
                alturaDeOjos = 1.5f * escalaTamano;
                desplazamientoY = -1f;
            }
            if (archivo.equals("objetosMDL/Dire_Cat.mdl")) {
                nombreAnimacionCaminando = "dire_cat:crun";
                nombreAnimacionCaminando = "dire_cat:cwalk";
                nombreAnimacionQuieto = "dire_cat:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 2f;
                escalaTamano = 1f;
                alturaDeOjos = alturaP * escalaTamano;
            }
            if (archivo.equals("objetosMDL/pixie.mdl")) {
                nombreAnimacionCaminando = "pixie:crun";
                nombreAnimacionCaminando = "pixie:cwalk";
                nombreAnimacionQuieto = "pixie:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 1f;
                escalaTamano = 6.0f;
                desplazamientoY = -6.5f;
                alturaDeOjos = alturaP * escalaTamano;
            }
        } catch (Exception exc) {
            exc.printStackTrace();
           // System.out.println("Error during load Dire_Cat.mdl");
        }

        //Ajustando rotacion inicial de la figura MLD y aplicando tamano
        Transform3D rotacionCombinada = new Transform3D();
        rotacionCombinada.set(new Vector3f(0, desplazamientoY, 0));
        Transform3D correcionTemp = new Transform3D();
        correcionTemp.rotX(rotacionX);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotZ(rotacionZ);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotY(rotacionY);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.setScale(escalaTamano * multiplicadorEscala);
        rotacionCombinada.mul(correcionTemp);
        TransformGroup rotadorDeFIguraMDL = new TransformGroup(rotacionCombinada);
        rotadorDeFIguraMDL.addChild(RamaMDL);
        return rotadorDeFIguraMDL;
    }

    public void actualizar(float dt) {
        super.actualizar(dt);

        if (this.esPersonaje) {

            if(this.guerra){
                if (!moviendoLaEspada) {   // la animacion solo se activa una vez.  Luego se desactiva.  No tiene sentido activar varias veces
                    //La animacion ca1slashr es para atacar con la espada una vez. Si la bander es true, es continua
                    ab.playAnimation("iron_golem:ca1slashr", false);
                    moviendoLaEspada = true;
                    atacando = true;
                    guerra = false;
                }
            } else {
                moviendoLaEspada = false;
                if (juego.personaje.corriendo && animacion_corriendo_lanzada == false) {
                    ab.playAnimation(nombreAnimacionCorriendo, true);
                    animacion_corriendo_lanzada = true;
                } else if (juego.personaje.caminando && (animacion_caminando_lanzada == false)) {
                    ab.playAnimation(nombreAnimacionCaminando, true);
                    animacion_caminando_lanzada = true;
                    animacion_corriendo_lanzada = false;
                } else if ((juego.personaje.quieto && animacion_caminando_lanzada == true) || juego.personaje.quieto && animacion_corriendo_lanzada == true) {
                    ab.playAnimation(nombreAnimacionQuieto, true);
                    animacion_corriendo_lanzada = false;
                    animacion_caminando_lanzada = false;
                }
            }
        }
    }
}
