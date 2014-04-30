package figuras;
import base.Juego;
import base.Figura;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.Sphere;
import java.util.ArrayList;
import javax.media.j3d.*;

public class Esfera extends Figura {

    public Esfera(float radio_, float altura_, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego) {
        super(radio_, altura_, conjunto, listaObjetosFisicos, juego);
    }
/*
  public  Esfera(float radio, String textura, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego) {
      
      super(conjunto, listaObjetosFisicos, juego);   //Si se desea programar una clase Esfera, su constrctor tendr’a esta linea

        //Creando una apariencia
        Appearance apariencia = new Appearance();
        Texture tex = new TextureLoader(System.getProperty("user.dir") + "//" + textura, juego).getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        //Creacion de formas visuales y fisicas
        Sphere figuraVisual = new Sphere(radio, Sphere.GENERATE_TEXTURE_COORDS, 60, apariencia);
        ramaVisible.addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(figuraVisual);
        this.conjunto = conjunto;
 }
    */

}
