/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package captortest;

/**
 *
 * @author ayush
 */
import javax.swing.SwingUtilities;

public abstract class CaptureThread {
    //GLOBAL
    private Object value;
    private Thread thread;
    
    
    private static class ThreadVar{
        private Thread thread;
        ThreadVar (Thread t) { thread = t;}
        synchronized Thread get(){return thread;}
        synchronized void clear(){thread = null;}
    }
    
    private ThreadVar threadVar;
    
    //Access Methods
    protected synchronized Object getValue() { return value; }
    private synchronized void setValue(Object x) { value = x; }
    
    //Compute value to be returned
    public abstract Object construct();
    //Called on event dispatching thread not worker thread
    public void finished(){}
    //interputing worker thread //stopping worker
    public void interrupt(){
        Thread t = threadVar.get();
        if(t!=null){
            t.interrupt();
        }
        threadVar.clear();
    }
    //------------------------------
    public Object get(){
        while(true){
            Thread t = threadVar.get();
            if(t == null){
                return getValue();
            }
            try{
                t.join();
            }
            catch(InterruptedException e ){
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }
    //Starting a thread that will call constructor and exit
    public CaptureThread(){
        final Runnable doFinished = new Runnable(){
            public void run(){finished();}
        };
        
        Runnable doConstruct = new Runnable(){
          public void run(){
              try{
                  setValue(construct());
              }
              finally{
                  threadVar.clear();
              }
              SwingUtilities.invokeLater(doFinished);
          }  
        };
        
        Thread t=new Thread(doConstruct);
        threadVar= new ThreadVar(t);
    }
    
    //-------------------------------------------
    public void start(){
        Thread t = threadVar.get();
        if(t!= null){
            t.start();
        }
    }
    
    
    
}
