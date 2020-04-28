import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {





    public static void main(String[] args) throws IOException, ClassNotFoundException, NotFoundException, CannotCompileException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {



//        //String jarPath =  "C:\\Users\\Patryk Wiśniewski\\Downloads\\testclassloader.jar";
//        String jarPath =  "D:\\Java\\JFK_3\\JarLoader\\NewLabJar.jar";
//        //String jarPath =  "D:\\Java\\JFK_3\\JarLoader\\ToLoad.jar";
//


        Path jarFile = Paths.get(Paths.get(".").toString(),"\\NewLabJar.jar");
        System.out.println(jarFile.toAbsolutePath());


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JFrame frame = new MainFrame("JarEditor");
                frame.setSize(800,700);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setVisible(true);
            }
        });


















    }








}
