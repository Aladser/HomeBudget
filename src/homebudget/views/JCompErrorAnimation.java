package homebudget.views;

/**
 * Анимация неправильного ввода в поле ввода сменой фона элемента
 */
public class JCompErrorAnimation {
    public static void execute(javax.swing.JComponent comp){
        new Thread(()->{
            comp.setBackground(java.awt.Color.red);
            try { Thread.sleep(300);
            } catch (InterruptedException ex) {System.err.println("Ошибка");}
            comp.setBackground(java.awt.Color.white);
        }, "Textfield animation").start();        
    }
}
