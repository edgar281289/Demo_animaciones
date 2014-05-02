/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
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
 * @author Sobremesa
 */
public class Colisiones extends Behavior {

    int contadorColisiones;
    Figura figura;
    protected Shape3D ObjReferencia;
    protected WakeupCriterion[] Criterios;
    protected WakeupOr CriterioUnificador; /* El resultad de 'OR' de los criterios por separado */


    public Colisiones(Shape3D _ObjetoReferencia, Figura p) {
        figura = p;
        ObjReferencia = _ObjetoReferencia;
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    }

    public void initialize() {
        Criterios = new WakeupCriterion[3];
        WakeupOnCollisionEntry inicia = new WakeupOnCollisionEntry(ObjReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);
        WakeupOnCollisionExit finaliza = new WakeupOnCollisionExit(ObjReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);
        WakeupOnCollisionMovement mueve = new WakeupOnCollisionMovement(ObjReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);
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
                System.out.println("El " + ObjReferencia.getClass().getName() + " golpeo con " + theLeaf.getUserData());
                contadorColisiones++;
            } else if (theCriterion instanceof WakeupOnCollisionExit) { /*.. codigo si la sale de la colision ...*/

                System.out.println("No hay Nadie Cabesa");
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
                System.out.println(" El objeto colisionado se movio miestras colisionaba " + figura.colisionAtras);
            }
        }
        wakeupOn(CriterioUnificador);
    }

}
