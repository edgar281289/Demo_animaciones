package base;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.picking.PickTool;
import java.awt.*;
import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;
import com.sun.j3d.utils.universe.SimpleUniverse;
import figuras.FiguraMDL;

public class Juego extends JFrame implements Runnable {

    public boolean primeraPersona = false;
    int estadoJuego = 0;
    SimpleUniverse universo;
    public BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/";
    Thread hebra = new Thread(this);
    ArrayList<base.Figura> listaObjetosFisicos = new ArrayList<Figura>();
    public BranchGroup conjunto = new BranchGroup();
    // Pesonajes importantes del juego
    public Figura personaje;  //golem;
    Figura perseguidor1;
    Figura perseguidor2;
    public PickTool explorador;
    
    public Juego() {
        //this.primeraPersona = true;
        Container GranPanel = getContentPane();
        Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        zonaDibujo.setPreferredSize(new Dimension(800, 600));
        GranPanel.add(zonaDibujo, BorderLayout.CENTER);
        universo = new SimpleUniverse(zonaDibujo);
        
        zonaDibujo.getView().setMinimumFrameCycleTime(30L);
        
        //OrbitBehavior B = new OrbitBehavior(zonaDibujo);
        //B.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        //universo.getViewingPlatform().setViewPlatformBehavior(B);

        BranchGroup escena = crearEscena();
        escena.compile();
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);
        hebra.start();
    }

    BranchGroup crearEscena() {
        BranchGroup objRoot = new BranchGroup();
        conjunto = new BranchGroup();

        explorador = new PickTool(conjunto);
        explorador.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

        objRoot.addChild(conjunto);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(10f, 10f, 10f), new Vector3f(1f, 0f, -1f));
        BoundingSphere limitesLuz = new BoundingSphere(new Point3d(-15, 10, 15), 100.0); //Localizacion de fuente/paso de luz
        objRoot.addChild(LuzDireccional);
        mostrar.setSchedulingBounds(limites);
        LuzDireccional.setInfluencingBounds(limitesLuz);
        Background bg = new Background();
        bg.setApplicationBounds(limites);
        bg.setColor(new Color3f(135f / 256, 206f / 256f, 250f / 256f));
        objRoot.addChild(bg);
        objRoot.addChild(mostrar);

        //Creando el personaje del juego, controlado por teclado. Tambien se pudo haber creado en CrearEscena()
        float radio = 1f;
        float posX = 0f;
        float posY = 0.3f, posZ = 0f;
        personaje = new FiguraMDL(0.4f, 3.0f, "objetosMDL/Iron_Golem.mdl", radio, conjunto, listaObjetosFisicos, this, true);
        personaje.crearPropiedades(posX, posY, posZ);
        
        radio = 1f; posX = 0.0f; posY = 0.8f; posZ = 15.0f;
        perseguidor1 = new FiguraMDL(0.4f, 3.0f, "objetosMDL/Iron_Golem.mdl", radio, conjunto, listaObjetosFisicos, this, false);
        perseguidor1.crearPropiedades(posX, posY, posZ);

        //Creando un elefenta con luz direccional
        ObjectFile file = new ObjectFile (ObjectFile.RESIZE); Scene scene = null;
        try {scene = file.load(rutaCarpetaProyecto+"elephav.obj");}
        catch (Exception e) { System.err.println(e); System.exit(1); }
        BranchGroup elefante =  scene.getSceneGroup();
       //Imprime la clase del primer hijo
        
        Transform3D scala = new Transform3D();
        scala.setScale(7);
        
        Transform3D posicionInicial = new Transform3D();
        posicionInicial.set(new Vector3f(15.0f,3.5f,20.0f));
        
        TransformGroup TGelefanteScala = new TransformGroup(scala);
        TransformGroup TGelefantePos = new TransformGroup(posicionInicial);
        TransformGroup TGelefante = new TransformGroup();
        
        TGelefanteScala.addChild(elefante);
        TGelefante.addChild(TGelefantePos);
        TGelefantePos.addChild(TGelefanteScala);
        
        //TGelefante.setTransform(scala);
        //TGelefante.setTransform(posicionInicial);
        
        //TGelefante.addChild(TGelefantePos);
        //TGelefante.addChild(elefante);
        
        conjunto.addChild(TGelefante);
        //Permitiendo que el Shape3D del elefante se explore y se localice.  Se le da un nombre.
        PickTool.setCapabilities(elefante.getChild(0), PickTool.INTERSECT_FULL);
        elefante.setPickable(true);
        elefante.getChild(0).setUserData("Un elefante");
        
        //perseguidor1.velocidades[0]=2.0f;
        //perseguidor1.velocidades[1]=0.0f;
        //perseguidor1.velocidades[0]=1.0f;
        
        /*
        radio = 1f; posX = 8.0f; posY = 0f; posZ = 6.0f;
        perseguidor2 = new FiguraMDL(0.4f, 3.0f, "objetosMDL/Iron_Golem.mdl", radio, conjunto, listaObjetosFisicos, this, false);
        perseguidor2.crearPropiedades(posX, posY, posZ);
        */
        
        /*
        FiguraMDL personajeMDL = (FiguraMDL) personaje;
        Colisiones colisiones = new Colisiones(personajeMDL.RamaMDL, personaje);
        colisiones.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0));
        conjunto.addChild(colisiones);
        */
        
        /*
        DeteccionControlPersonaje mueve = new DeteccionControlPersonaje(personaje);
        mueve.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        conjunto.addChild(mueve);
        */
        
        //perseguidor = new Esfera (radio, "texturas//bosques2.jpg", conjunto, listaObjetosFisicos, this);
        //perseguidor.crearPropiedades( 3, 0, 0);
        //perseguidor.asignarObjetivo(personaje,15f);   //Este objetivo de perseguir DEBE actualizado para que persiga la nueva posicion del personaje
        //Creacion de un Terreno Simple (no es una figura, no es movil, tiene masa 0)
        utilidades.TerrenoSimple terreno = new utilidades.TerrenoSimple(100, 100, -25, -0.1f, -12, "unaTextura_Desabilitada", conjunto);
        
        return objRoot;
    }

    void actualizar(float dt) {
        //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS  (ej. para que cada figura pueda persiguir su objetivo)
        for (int i = 0; i < this.listaObjetosFisicos.size(); i++) {
            listaObjetosFisicos.get(i).actualizar(dt);
        }
        
        Vector3d direccion = personaje.conseguirDireccionFrontal();
        if(!primeraPersona){
            colocarCamara(universo,
                    new Point3d( personaje.posiciones[0] - direccion.getX(), personaje.posiciones[1] - direccion.getY() + personaje.altura, personaje.posiciones[2] - direccion.getZ()),
                    new Point3d( personaje.posiciones[0] + direccion.getX(), personaje.posiciones[1] + direccion.getY() + personaje.altura, personaje.posiciones[2] + direccion.getZ())
            );
        }else{
            colocarCamara(universo,
                    new Point3d( personaje.posiciones[0] + 0.5f, personaje.posiciones[1]  + personaje.altura, personaje.posiciones[2] + 0.5f ),
                    new Point3d( personaje.posiciones[0] + direccion.getX(), personaje.posiciones[1] + direccion.getY() + personaje.altura, personaje.posiciones[2] + direccion.getZ())
            );            
        }
        
        //System.out.println("Posicion npc: " + perseguidor1.posiciones[0] + ", " + perseguidor1.posiciones[1] + ", " + perseguidor1.posiciones[2]);
        
    }

    void mostrar() throws Exception {
        //MOSTRAR FIGURAS FISICAS (muestra el componente visual de la figura, con base en los datos de localizacion del componente fisico)
        for (int idFigura = 0; idFigura <= this.listaObjetosFisicos.size() - 1; idFigura++) // Actualizar posiciones fisicas y graficas de los objetos.
        {
            listaObjetosFisicos.get(idFigura).mostrar();
        }
    }

    public void run() {
        //float dt = 3f/100f;
        float dt = 0.01f;
        int tiempoDeEspera = (int) (dt * 1000);
        while (estadoJuego != -1) {
            try {
                actualizar(dt);
            } catch (Exception e) {
                //System.out.println("Error durante actualizar. Estado del juego "+estadoJuego);
                e.printStackTrace();
            }
            try {
                Thread.sleep(tiempoDeEspera);
            } catch (Exception e) {
            }
        }
    }

    void colocarCamara(SimpleUniverse universo, Point3d posicionCamara, Point3d objetivoCamara) {
        posicionCamara = new Point3d(posicionCamara.x + 0.001, posicionCamara.y + 0.001d, posicionCamara.z + 0.001);
        Transform3D datosConfiguracionCamara = new Transform3D();
        datosConfiguracionCamara.lookAt(posicionCamara, objetivoCamara, new Vector3d(0.001, 1.001, 0.001));
        try {
            datosConfiguracionCamara.invert();
            TransformGroup TGcamara = universo.getViewingPlatform().getViewPlatformTransform();
            TGcamara.setTransform(datosConfiguracionCamara);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        Juego x = new Juego();
        x.setTitle("Juego");
        //x.setSize(1000, 800);
        x.setExtendedState(JFrame.MAXIMIZED_BOTH);
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //x.colocarCamara(x.universo, new Point3d(-3, 8f, 22f), new Point3d(3, 0, 0));
    }
}
