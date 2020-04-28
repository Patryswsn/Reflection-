import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DetailsPanel extends JPanel {


    private JarLoad jl = new JarLoad();
    private EventListenerList listenerList = new EventListenerList();


    public DetailsPanel()
    {
        Dimension size = getPreferredSize();
        size.width = 250;
        setPreferredSize(size);

        setBorder(BorderFactory.createTitledBorder("Menu"));
        ArrayList<Component> comp = new ArrayList<Component>();
        Path jarFile = Paths.get(Paths.get(".").toString(),"\\NewLabJar.jar");

        //elementy do wczytywania jarki
        JLabel jarPathLabel = new JLabel("set Jar Path:");
        JTextField jarPathField = new JTextField(jarFile.toAbsolutePath().toString(),10);
        JButton confirmJarPath = new JButton("confirm");
        JButton listClasses = new JButton("print out Classes from jar");//po podaniu sciezki do pliku jar bedzie dzialac dopiero
        comp.add(listClasses);

        //elementy po wczytaniu pliku jar




        //wybor klasy
        JLabel chooseClass = new JLabel("Choose Class");
        JTextField classChoose = new JTextField("nazwa klasy",10);
        JButton classButton = new JButton("confirm");
        comp.add(chooseClass);
        comp.add(classChoose);
        comp.add(classButton);

        //edycja metody calkowita

        JLabel editMethod = new JLabel("Edit method body");
        JTextField methodName = new JTextField("*method name*",11);
        JTextField methodBody = new JTextField("{code}",11);
        JButton emb = new JButton("confirm");
        comp.add(editMethod);
        comp.add(methodName);
        comp.add(methodBody);
        comp.add(emb);


        JLabel editMethodpre = new JLabel("Edit method body pre");
        JTextField methodNamepre = new JTextField("*method name*",11);
        JTextField methodBodypre = new JTextField("{code}",11);
        JButton embpre = new JButton("confirm");
        comp.add(editMethodpre);
        comp.add(methodNamepre);
        comp.add(methodBodypre);
        comp.add(embpre);

        JLabel editMethodpost = new JLabel("Edit method body post");
        JTextField methodNamepost = new JTextField("*method name*",11);
        JTextField methodBodypost = new JTextField("{code}",11);
        JButton embpost = new JButton("confirm");

        comp.add(editMethodpost);
        comp.add(methodNamepost);
        comp.add(methodBodypost);
        comp.add(embpost);



        //uruchamianie metody
        JLabel invokeMethod = new JLabel("Invoke method");
        JTextField invokeMethodName = new JTextField("*method name*",10);
       // JTextField invokeMethodBody = new JTextField("{code}",10);
        JButton imb = new JButton("confirm");
        comp.add(invokeMethod);
        comp.add(invokeMethodName);
        //comp.add(invokeMethodBody);
        comp.add(imb);


        //dodawnie nowej metody
        JLabel addMethod = new JLabel("Add new method");
        JTextField newMethodBody = new JTextField("example: public void qwe(){*code*}",10);
        JButton amb = new JButton("confirm");
        comp.add(addMethod);
        comp.add(newMethodBody);
        comp.add(amb);

        //zmiana hierarchii klasy
        JLabel changeClassHierarchy = new JLabel("Change Class Hierarchy");
        JTextField parentClass = new JTextField("Parent Class",10);
        JTextField childClass = new JTextField("child Class",10);
        JButton confirmhierarchy = new JButton("confirm");
        comp.add(changeClassHierarchy);
        comp.add(parentClass);
        comp.add(childClass);
        comp.add(confirmhierarchy);





        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();




        gc.gridx=0;
        gc.gridy=0;
        add(jarPathLabel,gc);
        gc.gridx=1;
        add(jarPathField,gc);


        gc.weighty=10;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.gridx=0;
        gc.gridy=1;
        add(confirmJarPath,gc);




        JPanel jp = this;
        confirmJarPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(jarPathField);
                remove(jarPathLabel);
                remove(confirmJarPath);
                setComponents(comp,gc);
                jp.revalidate();
                jp.repaint();

                try {
                    jl.setJarPath(jarPathField.getText());
                    //jl.getClasses();
                } catch (NotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });


        listClasses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fireDetailEvent(new DetailEvent(this,jl.printAllPossibleClasses()));
                    //fireDetailEvent(new DetailEvent(this,));
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

            }
        });

        //classButton/emb/imb/amb/confirmHierarchy


        classButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str =classChoose.getText();
                jl.chooseClass(str);
                try {
                    fireDetailEvent(new DetailEvent(this,jl.getClassInfo(jl.getClass(str),true)));

                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });

        emb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                try {
                    jl.editMethodTotally(methodBody.getText(),methodName.getText());
                } catch (NotFoundException e1) {
                    e1.printStackTrace();
                } catch (CannotCompileException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });



        imb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    jl.invokeMethod(invokeMethodName.getText());
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }


            }
        });


        amb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    jl.addMethod(newMethodBody.getText());
                } catch (NotFoundException e1) {
                    e1.printStackTrace();
                } catch (CannotCompileException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        });


        //zmiana hierarchii klasy


        confirmhierarchy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    jl.changeHierarchy(childClass.getText(),parentClass.getText());
                } catch (NotFoundException e1) {
                    e1.printStackTrace();
                } catch (CannotCompileException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });




        embpre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    jl.editMethodPre(methodBodypre.getText(),methodNamepre.getText());
                } catch (NotFoundException e1) {
                    e1.printStackTrace();
                } catch (CannotCompileException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


        embpost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    jl.editMethodPost(methodBodypost.getText(),methodNamepost.getText());
                } catch (NotFoundException e1) {
                    e1.printStackTrace();
                } catch (CannotCompileException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });



    }


    public void fireDetailEvent(DetailEvent event)
    {
        Object[] listeners = listenerList.getListenerList();

        for(int i=0;i<listeners.length;i+=2)
        {

            if(listeners[i]==DetailListener.class)
                ((DetailListener)listeners[i+1]).detailEventOccured(event);

        }

    }


    public void addDetailListener(DetailListener detailListener) {
        listenerList.add(DetailListener.class,detailListener);
    }

    public void removeDetailListener(DetailListener detailListener) {
        listenerList.remove(DetailListener.class,detailListener);
    }


public void setComponents(ArrayList<Component> comp, GridBagConstraints gc)
{

    int y=2;
    gc.gridx=0;
    for(Component z : comp)
    {
        gc.gridy=y;
        add(z,gc);
        y+=1;
    }

}


}
