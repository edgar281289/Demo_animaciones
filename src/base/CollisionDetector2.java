/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.media.j3d.WakeupOnCollisionMovement;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3d;

/**
 *
 * @author Edgar Pérez Ferrando
 */
public class CollisionDetector2 extends Behavior {

    int contadorColisiones;
    Figura figura;
    Juego juego;
    protected Shape3D ObjReferencia;
    protected BranchGroup BranchGroupReferencia;
    protected WakeupCriterion[] Criterios;
    protected WakeupOr CriterioUnificador; /* El resultad de 'OR' de los criterios por separado */
    
    /*
    public CollisionDetector2(Shape3D _ObjetoReferencia, Figura p) {
        figura = p;
        ObjReferencia = _ObjetoReferencia;
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    }
    */
    
    public CollisionDetector2(BranchGroup _BranchGroupReferencia, Figura p, Juego juego) {
        this.juego = juego;
        figura = p;
        BranchGroupReferencia = _BranchGroupReferencia;
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    }

    public void initialize() {
        Criterios = new WakeupCriterion[3];
        WakeupOnCollisionEntry inicia = new WakeupOnCollisionEntry(BranchGroupReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);
        WakeupOnCollisionExit finaliza = new WakeupOnCollisionExit(BranchGroupReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);
        WakeupOnCollisionMovement mueve = new WakeupOnCollisionMovement(BranchGroupReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);
        Criterios[0] = inicia; // la colision se produce, se inicia
        Criterios[1] = finaliza; // la colision finaliza. Los objetos dejan de colisionar
        Criterios[2] = mueve; //el objeto colisionado se mueve dentro de la colision
        CriterioUnificador = new WakeupOr(Criterios);
        wakeupOn(CriterioUnificador);
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
            if (theCriterion instanceof WakeupOnCollisionEntry) {
                Node theLeaf = ((WakeupOnCollisionEntry) theCriterion).getTriggeringPath().getObject();
                //System.out.println("El " + BranchGroupReferencia.getClass().getName() + " golpeo con " + theLeaf.getUserData() + " de tipo: " + theLeaf.getClass());
                
                //System.out.println("juego.personaje.atacando " + juego.personaje.atacando);
                if(theLeaf.getUserData().equals("figura_caja_0") && juego.personaje.atacando){
                    juego.conjunto.removeChild(juego.BGcaja_0);
                    juego.personaje.atacando = false;
                    juego.caja1 = true;
                    if(juego.caja1 && juego.caja2) juego.estadoJuego = -1;
                }else if(theLeaf.getUserData().equals("figura_caja_1") && juego.personaje.atacando){
                    juego.conjunto.removeChild(juego.BGcaja_1);
                    juego.personaje.atacando = false;
                    juego.caja2 = true;
                    if(juego.caja1 && juego.caja2) juego.estadoJuego = -1;
                }else if((theLeaf.getUserData().equals("figura_0") && figura.identificadorFigura == 1) || 
                        (theLeaf.getUserData().equals("figura_1") && figura.identificadorFigura == 0)){
                    
                    if(figura.atacando) juego.estadoJuego = -1;
                    figura.guerra = true;
                    figura.quieto = true;
                    figura.caminando = false;
                    figura.buscando = false;
                    // El juego finaliza cuando colisionan los dos golems
                }
                
                
                //contadorColisiones++;
            } else if (theCriterion instanceof WakeupOnCollisionExit) {
                figura.colisionDelante = false;
                figura.colisionAtras = false;
                figura.colisionIzquierda = false;
                figura.colisionDerecha = false;
            } else {
                figura.colisionIzquierda = true;
                figura.colisionDerecha = true;
                if (figura.adelante && !figura.colisionAtras) {
                    figura.colisionDelante = true;
                } else if (figura.atras && !figura.colisionDelante) {
                    figura.colisionAtras = true;
                }
                //System.out.println(" El objeto colisionado se movio miestras colisionaba " + figura.colisionAtras);
            }
        }
        wakeupOn(CriterioUnificador);
    }

}
