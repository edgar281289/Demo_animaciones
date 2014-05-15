package base;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.image.TextureLoader;
import java.awt.*;
import javax.swing.*;
import javax.vecmath.*;
import java.util.ArrayList;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Box;
import figuras.FiguraMDL;
import java.util.Enumeration;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

/**
 * 
 * @author Edgar Pérez Ferrando
 */
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
    public PickTool explorador;
    BranchGroup TGelefante;
    BranchGroup BGcaja_0;
    BranchGroup BGcaja_1;
    BranchGroup BGsonido;
    public boolean caja1 = false;
    public boolean caja2 = false;

    public Juego() {
        conjunto.setUserData("conjunto");
        //this.primeraPersona = true;
        Container GranPanel = getContentPane();
        Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        zonaDibujo.setPreferredSize(new Dimension(800, 600));
        GranPanel.add(zonaDibujo, BorderLayout.CENTER);
        universo = new SimpleUniverse(zonaDibujo);

        zonaDibujo.getView().setMinimumFrameCycleTime(30L);

        OrbitBehavior B = new OrbitBehavior(zonaDibujo);
        B.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        universo.getViewingPlatform().setViewPlatformBehavior(B);

        BranchGroup escena = crearEscena();
        escena.setUserData("escena");
        escena.compile();
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);
        hebra.start();
    }

    BranchGroup crearEscena() {
        
        BranchGroup objRoot = new BranchGroup();

        explorador = new PickTool(conjunto);
        explorador.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

        objRoot.addChild(conjunto);

        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        conjunto.setCapability(BranchGroup.ALLOW_DETACH);

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

        try {
            PhysicalEnvironment pe = universo.getViewer().getPhysicalEnvironment();
            JavaSoundMixer objetoMezcladorSonidos = new JavaSoundMixer(pe);
            pe.setAudioDevice(objetoMezcladorSonidos);
            objetoMezcladorSonidos.initialize();
            universo.getViewer().getView().setPhysicalEnvironment(pe);
        } catch (Exception e) {
            System.out.println("problema de audio");
        }
        String ficheroSonido = rutaCarpetaProyecto + "/sonidos/choco.mid";
        BGsonido = new BranchGroup();
        BGsonido.setCapability(BranchGroup.ALLOW_DETACH);
        anadirSonidoARama(BGsonido, ficheroSonido);
        conjunto.addChild(BGsonido);
        
        //Creando el personaje del juego, controlado por teclado. Tambien se pudo haber creado en CrearEscena()
        float radio = 1f, posX = 0f, posY = 0.8f, posZ = 0f;
        personaje = new FiguraMDL(0.4f, 3.0f, "objetosMDL/Iron_Golem.mdl", radio, conjunto, listaObjetosFisicos, this, true);
        personaje.crearPropiedades(posX, posY, posZ);

        radio = 1f;
        posX = 0.0f;
        posY = 0.8f;
        posZ = 15.0f;
        perseguidor1 = new FiguraMDL(0.4f, 3.0f, "objetosMDL/Iron_Golem.mdl", radio, conjunto, listaObjetosFisicos, this, false);
        perseguidor1.crearPropiedades(posX, posY, posZ);

        TGelefante = crearElefante();
        conjunto.addChild(TGelefante);

        BGcaja_0 = new BranchGroup();
        BGcaja_0.setUserData("Caja_0");
        BGcaja_0.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        BGcaja_0.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        BGcaja_0.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        BGcaja_0.setCapability(BranchGroup.ALLOW_DETACH);

        BGcaja_1 = new BranchGroup();
        BGcaja_1.setUserData("Caja_1");
        BGcaja_1.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        BGcaja_1.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        BGcaja_1.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        BGcaja_1.setCapability(BranchGroup.ALLOW_DETACH);

        BGcaja_0.addChild(crearCaja(-15f, 1.5f, 20.0f, 0));
        BGcaja_1.addChild(crearCaja(-21f, 1.5f, 25.0f, 1));
        conjunto.addChild(BGcaja_0);
        conjunto.addChild(BGcaja_1);

        CollisionDetector2 colisionesPersonaje = new CollisionDetector2(personaje.ramaVisible, personaje, this);
        colisionesPersonaje.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0));
        conjunto.addChild(colisionesPersonaje);

        CollisionDetector2 colisionesPerseguidor = new CollisionDetector2(personaje.ramaVisible, perseguidor1, this);
        colisionesPerseguidor.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0));
        conjunto.addChild(colisionesPerseguidor);
        
        //Creacion de un Terreno Simple (no es una figura, no es movil, tiene masa 0)
        utilidades.TerrenoSimple terreno = new utilidades.TerrenoSimple(1000, 1000, -25, -0.1f, -12, "unaTextura_Desabilitada", conjunto);
        
        return objRoot;
    }

    /**
     *
     * @return
     */
    BranchGroup crearCaja(float pX, float pY, float pZ, int identificador) {

        Appearance apariencia = new Appearance();
        Texture tex = new TextureLoader(rutaCarpetaProyecto + "Texturas/madera.jpg", this).getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        Box caja = new Box(2f, 2f, 2f, Box.GENERATE_TEXTURE_COORDS, apariencia);

        Enumeration childrens = caja.getAllChildren();
        while (childrens.hasMoreElements()) {
            Object o = childrens.nextElement();
            if (o instanceof Shape3D) {
                Shape3D shape = (Shape3D) o;
                shape.setUserData("figura_caja_" + identificador);
                PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);
                shape.setPickable(true);
            }
        }

        Transform3D trasladarCaja = new Transform3D();
        trasladarCaja.set(new Vector3d(pX, pY, pZ));
        TransformGroup TGcaja = new TransformGroup(trasladarCaja);
        TGcaja.addChild(caja);

        BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bg.setCapability(BranchGroup.ALLOW_DETACH);
        bg.addChild(TGcaja);
        bg.setUserData("BG_figura_caja_" + identificador);

        return bg;
    }

    BranchGroup crearElefante() {
        //Creando un elefenta con luz direccional
        ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
        Scene scene = null;
        try {
            scene = file.load(rutaCarpetaProyecto + "elephav.obj");
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        BranchGroup elefante = scene.getSceneGroup();

        Transform3D scala = new Transform3D();
        scala.setScale(7);

        Transform3D posicionInicial = new Transform3D();
        posicionInicial.set(new Vector3f(15.0f, 3.5f, 20.0f));

        TransformGroup TGelefanteScala = new TransformGroup(scala);
        TransformGroup TGelefantePos = new TransformGroup(posicionInicial);
        TransformGroup TGelefante = new TransformGroup();

        TGelefanteScala.addChild(elefante);
        TGelefante.addChild(TGelefantePos);
        TGelefantePos.addChild(TGelefanteScala);

        PickTool.setCapabilities(elefante.getChild(0), PickTool.INTERSECT_FULL);
        elefante.setPickable(true);
        elefante.getChild(0).setUserData("figura_elefante");

        BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bg.setCapability(BranchGroup.ALLOW_DETACH);
        bg.addChild(TGelefante);
        bg.setUserData("BG_figura_elefante");
        return bg;
    }

    void actualizar() {
        //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS  (ej. para que cada figura pueda persiguir su objetivo)
        for (int i = 0; i < this.listaObjetosFisicos.size(); i++) {
            listaObjetosFisicos.get(i).actualizar();
        }

        Vector3d direccion = personaje.conseguirDireccionFrontal();
        float cercania = 1.5f;

        if (!primeraPersona) {

            colocarCamara(universo,
                    new Point3d(personaje.posiciones[0] - direccion.getX() * cercania, personaje.posiciones[1] - direccion.getY() + personaje.altura, personaje.posiciones[2] - direccion.getZ() * cercania),
                    new Point3d(personaje.posiciones[0] + direccion.getX(), personaje.posiciones[1] + direccion.getY() + personaje.altura, personaje.posiciones[2] + direccion.getZ())
            );
        } else {
            colocarCamara(universo,
                    new Point3d(personaje.posiciones[0] + 0.5f * cercania, personaje.posiciones[1] + personaje.altura, personaje.posiciones[2] + 0.5f * cercania),
                    new Point3d(personaje.posiciones[0] + direccion.getX(), personaje.posiciones[1] + direccion.getY() + personaje.altura, personaje.posiciones[2] + direccion.getZ())
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
                actualizar();
            } catch (Exception e) {
                //System.out.println("Error durante actualizar. Estado del juego "+estadoJuego);
                e.printStackTrace();
            }
            try {
                Thread.sleep(tiempoDeEspera);
            } catch (Exception e) {
            }
        }
        mensaje_fin_del_juego();
    }

    protected void anadirSonidoARama(BranchGroup b, String soundFile) {
        //Create a media container to load the file
        MediaContainer droneContainer = new MediaContainer("file:" + soundFile);
        //Create the background sound from the media container
        BackgroundSound drone = new BackgroundSound(droneContainer, 1.0f);
        //Activate the sound
        drone.setSchedulingBounds(limites);
        drone.setEnable(true);
        //Set the sound to loop forever
        drone.setLoop(BackgroundSound.INFINITE_LOOPS);
        b.addChild(drone);
    }
    
    void mensaje_fin_del_juego() {
        
        Font3D font3d = new Font3D(new Font("Helvetica", Font.ITALIC, 2), 5, new FontExtrusion());
        Text3D textGeom = new Text3D(font3d, new String("Fin del juego"), new Point3f(0,0,0));
        Shape3D textShape = new Shape3D(textGeom);
        Transform3D rotateText = new Transform3D();
        rotateText.rotY(3.14f);
        
        Transform3D despText = new Transform3D();
        despText.set(new Vector3d(-5.0f,0.0f,-10.0f));
        rotateText.mul(despText);
        
        TransformGroup TGtexto = new TransformGroup(rotateText);
        TGtexto.addChild(textShape);
        BranchGroup bg = new BranchGroup();
        bg.addChild(TGtexto);
        conjunto.addChild(bg);
        
        colocarCamara(universo,
                new Point3d(5.0f, 1.0f, -20.0f),
                new Point3d(-5.0f, 1.0f, 20.0f)
        );
        
        BranchGroup BGfanfare = new BranchGroup();
        conjunto.removeChild(BGsonido);
        anadirSonidoARama(BGfanfare, rutaCarpetaProyecto + "/sonidos/fanfare.mid");
        conjunto.addChild(BGfanfare);
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
