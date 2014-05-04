package base;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.media.j3d.*;

public class DeteccionControlPersonaje extends javax.media.j3d.Behavior {
    Figura personaje;
    WakeupOnAWTEvent    presionada = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupOnAWTEvent    liberada = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    WakeupOnAWTEvent    click = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED); // Escuchamos el raton
    WakeupCondition     keepUpCondition = null;
    WakeupCriterion[]   continueArray = new WakeupCriterion[3];

 public DeteccionControlPersonaje(Figura _personaje ) {
        personaje = _personaje;
        continueArray[0]=liberada;
        continueArray[1]=presionada;
        continueArray[2]=click; //Raton
        keepUpCondition = new WakeupOr(continueArray);
    }

 public void initialize()    {
            wakeupOn(keepUpCondition);
    }

 public void processStimulus(Enumeration criteria) {
      while (criteria.hasMoreElements()){
      WakeupCriterion ster=(WakeupCriterion) criteria.nextElement();
       if (ster instanceof WakeupOnAWTEvent)   {
           AWTEvent[] events = ( (WakeupOnAWTEvent) ster).getAWTEvent();
           for (int n=0;n<events.length;n++){
              if( events[n] instanceof MouseEvent){
                  MouseEvent eM = (MouseEvent) events[n];
                  //System.out.println("Raton presionada: " + eM.getButton());
                  if( eM.getID() == MouseEvent.MOUSE_CLICKED){
                      if( eM.getButton() == 1){
                          personaje.guerra = true;                          
                      }
                  }
              }
               if (events[n]  instanceof KeyEvent){
                KeyEvent ek = (KeyEvent) events[n] ;
                //System.out.println("Tecla presionada: " + ek.getKeyChar());
                
                char caracter = Character.toLowerCase(ek.getKeyChar());
                if (ek.getID() == KeyEvent.KEY_PRESSED) {
                                        
                    if (ek.getKeyCode() == 16 ) {
                        personaje.corriendo = true;
                    }
                    
                    if (caracter == 'w') {
                        personaje.adelante= true;
                        personaje.caminando = true;
                        personaje.quieto = false;
                    }
                    else if (caracter == 'a') personaje.izquierda=true;
                    else if (caracter == 'd') personaje.derecha=true;
                    else if (caracter == 's') personaje.atras=true;
                    else if (caracter == 'p') personaje.primeraPersona=true;
                    else if (caracter == 't') personaje.primeraPersona=false;
                }
                else if (ek.getID() == KeyEvent.KEY_RELEASED)   {
                    
                    if (ek.getKeyCode() == 16 ) {
                        personaje.corriendo = false;
                    }
                    
                    if (caracter== 'w'){
                        personaje.adelante=false;
                        personaje.caminando= false;
                        personaje.quieto = true;
                    }
                    else if (caracter == 'a') personaje.izquierda=false;
                    else if (caracter == 'd') personaje.derecha=false;
                    else if (caracter == 's')personaje.atras=false;
                }
          }
        }
}}
 wakeupOn(keepUpCondition);
  }
}
