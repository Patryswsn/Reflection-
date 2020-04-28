import javassist.*;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JarLoad {

    public int xyz;
    private HashMap<String, String> classNames;//mapa nazw prostych na złożone
    private String jarPath;
    //private String qwe;
    private String choosenClass;
    private Loader classLoader;
    private ClassPool cp;
    private HashMap<String, HashMap<String, Class[]>> addedMethods = new HashMap<String, HashMap<String, Class[]>>();//nazwa klasy->(nazwa metody-> typy argumentow)


    public void chooseClass(String choosenClass) {//wybor klasy
        this.choosenClass = classNames.get(choosenClass);
    }


    public void setJarPath(String jarPath) throws NotFoundException, IOException {
        this.jarPath = jarPath;
        this.cp = ClassPool.getDefault();
        this.cp.appendClassPath(jarPath);
        this.classLoader = new Loader(cp);
        getClasses();//ta metoda wczytuje klasy kotre umieszczone sa w pliku jar
    }


    public HashMap<String, String> getClasses() throws IOException {//wypisuje klasy z pliku jar

        if (this.classNames != null)
            return this.classNames;

        JarInputStream jis = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry;
        HashMap<String, String> classesNames = new HashMap<String, String>();
        String ss1;
        String ss2;

        while (true) {
            entry = jis.getNextJarEntry();

            if (entry == null)
                break;


            if (entry.getName().endsWith(".class")) {
                ss1 = entry.getName().substring(entry.getName().lastIndexOf("/") + 1, entry.getName().length() - ".class".length());
                ss2 = entry.getName().replaceAll("/", ".");
                ss2 = ss2.substring(0, ss2.length() - ".class".length());
                classesNames.put(ss1, ss2);


            }

        }

        this.classNames = classesNames;
        return this.classNames;
    }


    public Class getClass(String str) throws ClassNotFoundException {


        return this.classLoader.loadClass(this.classNames.get(str));//rzutuje nazwe prosta na złożona

    }

    public String printAllPossibleClasses() throws ClassNotFoundException {//wypisuje wszystkie klasy
        Object[] str = this.classNames.keySet().toArray();
        String build = "";

        for (Object x : str) {
            build += getClassInfo(this.classLoader.loadClass(this.classNames.get(x.toString())),true) + "\n\n";
        }
        return build;
    }


    public String getClassInfo(Class cl, boolean git) throws ClassNotFoundException {//przedstawia metody, ich parametry itp. dla wybranej klasy

        //parametr git zapewnia wypisanie klasy "rodzica" z której dziedziczy wybrana klasa.
        Class loadedClass = cl;
        Method[] methods = loadedClass.getDeclaredMethods();
        String build = "";



        if(git==true) {
            build += loadedClass.getSuperclass().getName() + "\n";
            build += loadedClass.getName() + "\n";
        }
        if(!loadedClass.getSuperclass().equals(Object.class))
            build+=getClassInfo(classLoader.loadClass(cl.getSuperclass().getName()),false);


        for (Field f : loadedClass.getDeclaredFields()) {

            build += Modifier.toString(f.getModifiers()) + " " + f.getType() + " " + f.getName() + "\n";
        }


        for (Method m : methods) {

            build += Modifier.toString(m.getModifiers()) + " " + m.getName() + "(";

            for (Class c : m.getParameterTypes())
                build += c.getName() + ",";

            if (m.getParameterTypes().length != 0)
                build = build.subSequence(0, build.length() - 1).toString();
            build += ")\n";

        }
        if(git==true)
        build += "\n\n";
        return build;


    }


    public void addMethod(String src) throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {


        String zxc = src;
        for (int i = 0; i < zxc.length(); i++)//oddzielanie nagłowka metody od ciała
                if (zxc.toCharArray()[i] == '{') {
                zxc = zxc.substring(0, i);
                break;
            }

        ArrayList<String> q = new ArrayList<String>();
        q.add(zxc);
        q = regexChecker("\\(.+\\)", q);
        q = regexChecker("((\\()(\\s+)?[A-Za-z]+\\s)|(,(\\s+)?)[A-Za-z]+\\s", q);
        q = regexChecker("[A-Za-z]+\\s", q);//efektem koncowym sa Stringi wyrażające typy parametrów


        String y = null;
        for (int i = 0; i < q.size(); i++) {//regexy zostawiaja na koncu kazdego argumentu spcej ktorej sie tu pozmywam
            y = q.get(i);
            y = y.substring(0, y.length() - 1);
            q.set(i, y);
        }




    CtClass ctClass = cp.getCtClass(this.choosenClass);
    ctClass.defrost();
    CtMethod ctMethod = CtMethod.make(src,ctClass);


        Class[] methodParameters = new Class[q.size()];

        int i = 0;


        for (String x : q) {//tutaj stringi sa konwertowane na odpowiadajace im klasy, program interpretuje tylko typy podane ponizej

            if (x.equals("int")) {
                methodParameters[i] = int.class;
            } else if (x.equals("double")) {
                methodParameters[i] = double.class;

            } else if (x.equals("float")) {

                methodParameters[i] = float.class;

            } else if (x.equals("String")) {

                methodParameters[i] = String.class;
            }

            i++;

        }

        for(Class c :methodParameters)
        {
            System.out.println(c.getCanonicalName());
        }


        if(methodParameters.length!=0) {//dodaje tylko metody z parametrami
            HashMap<String, Class[]> a = new HashMap<String, Class[]>();
            a.put(ctMethod.getName(), methodParameters);//do nazwy metody dodaje jej parametry
            this.addedMethods.put(this.choosenClass, a);//do nazwy klasy dodaje hashmape z nazwa metody i jej parametrami
        }



    ctClass.addMethod(ctMethod);
    ctClass.writeFile();
this.classLoader = new Loader(cp);

}


        public void editMethodTotally (String code, String methodName) throws
        NotFoundException, CannotCompileException, IOException {//ustawia nowe cialo dla danej metody

            CtClass ctClass = cp.get(this.choosenClass);
            ctClass.defrost();
            Ret ret = coreToInvoke(methodName);


            if(ret.cc==null)//jezeli jest to metoda bez parametrow
            {
                CtMethod ctMethod = ctClass.getDeclaredMethod(ret.name);
                ctMethod.setBody(code);
                ctClass.writeFile();
                this.classLoader = new Loader(this.cp);

            }else//jezeli jest to metoda z parametraim
            {
                CtClass[] args = new CtClass[ret.cc.length];

                for(int i=0;i<args.length;i++)// tworzenie klas ctClass na podstawie Class
                    args[i]=this.cp.get(ret.cc[i].getCanonicalName());

                CtMethod ctMethod = ctClass.getDeclaredMethod(ret.name,args);
                ctMethod.setBody(code);
                ctClass.writeFile();
                this.classLoader = new Loader(this.cp);


            }

        }


        public void editMethodPre(String code, String name) throws NotFoundException, CannotCompileException, IOException {

            CtClass ctClass = cp.get(this.choosenClass);
            ctClass.defrost();
            Ret ret = coreToInvoke(name);

            if(ret.cc==null)
            {
                CtMethod ctMethod = ctClass.getDeclaredMethod(ret.name);
                ctMethod.insertBefore(code);
                ctClass.writeFile();
                this.classLoader = new Loader(this.cp);

            }else
            {
                CtClass[] args = new CtClass[ret.cc.length];

                for(int i=0;i<args.length;i++)
                    args[i]=this.cp.get(ret.cc[i].getCanonicalName());

                CtMethod ctMethod = ctClass.getDeclaredMethod(ret.name,args);
                ctMethod.insertBefore(code);
                ctClass.writeFile();
                this.classLoader = new Loader(this.cp);


            }


        }


    public void editMethodPost(String code, String name) throws NotFoundException, CannotCompileException, IOException {

        CtClass ctClass = cp.get(this.choosenClass);
        ctClass.defrost();
        Ret ret = coreToInvoke(name);

        if(ret.cc==null)
        {
            CtMethod ctMethod = ctClass.getDeclaredMethod(ret.name);
            ctMethod.insertAfter(code);
            ctClass.writeFile();
            this.classLoader = new Loader(this.cp);

        }else
        {
            CtClass[] args = new CtClass[ret.cc.length];

            for(int i=0;i<args.length;i++)
                args[i]=this.cp.get(ret.cc[i].getCanonicalName());

            CtMethod ctMethod = ctClass.getDeclaredMethod(ret.name,args);
            ctMethod.insertAfter(code);
            ctClass.writeFile();
            this.classLoader = new Loader(this.cp);


        }

    }





    public static ArrayList<String> regexChecker (String theRegex, ArrayList < String > check){
        //uzywany do tego aby dostac typy parametrow oraz wartosc argumentow
        //zwraca liste Stringow reprezentujacych typy parametrów badź wartości argumentów


            String str2Check = "";
            for (String x : check)
                str2Check += x;

            Pattern checkRegex = Pattern.compile(theRegex);

            Matcher regexMatcher = checkRegex.matcher(str2Check);
            String ss = "";
            ArrayList<String> sqs = new ArrayList<String>();
            int start;
            int end;
            while (regexMatcher.find()) {
                if (regexMatcher.group().length() != 0) {

                    start = regexMatcher.start();
                    end = regexMatcher.end();
                    ss = str2Check.substring(start, end);
                    sqs.add(ss);

                }
            }

            return sqs;
        }












    private Ret coreToInvoke(String methodName)
    {//ta metoda na podstawie samego stringa umozliwa nam wywolanie odpowiedniej metody np. qwe(12.3,"asd",22) - taki ciag znakow wystarczy aby odczytac typy parametrow etc.


        ArrayList<String> zx = new ArrayList<String>();
        String qq = methodName;
        String params = "";
        String name = "";
        for (int i = 0; i < qq.length(); i++) {
            if (qq.toCharArray()[i] == '(') {
                name = qq.substring(0, i);
                params = qq.substring(i, qq.length());
            }
        }


        Class[]paramsTypes=null;
        if(this.addedMethods.get(choosenClass)!=null)
            paramsTypes=this.addedMethods.get(choosenClass).get(name);//tutaj dostaje typy parametrów

        if(paramsTypes!=null) {
            zx.add(params);
            zx = regexChecker("((-)?[0-9]+\\.[0-9]+(F|f))|((-)?[0-9]+\\.[0-9]+)|((-)?[0-9]+)|(\".+\")", zx);
            for (int i=0;i<zx.size();i++) {//tutaj dostaje string reprezentujace typy parametrów
                if(zx.get(i).toCharArray()[0]=='"')
                    zx.set(i,zx.get(i).substring(1,zx.get(i).length()-1));
            }


            Object[] arg = new Object[paramsTypes.length];

            for(int i=0;i<paramsTypes.length;i++) {//tutaj nastepuje konwersja stringów reprezentujacych wartości argumentów na ich prawdziwe wartości

                if (paramsTypes[i] == int.class) {
                    arg[i]=Integer.parseInt(zx.get(i));
                } else if (paramsTypes[i] == double.class) {
                    arg[i]=Double.parseDouble(zx.get(i));
                } else if (paramsTypes[i] == String.class) {
                    arg[i]=zx.get(i);
                } else if (paramsTypes[i] == float.class)
                {
                    arg[i]=Float.parseFloat(zx.get(i));
                }

            }
            return new Ret(paramsTypes,arg,name);

        }else
        {
            return new Ret(null,null,methodName);

        }

    }













        public void invokeMethod (String methodName) throws ClassNotFoundException,
                NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {


            Class cl = this.classLoader.loadClass(this.choosenClass);
            Object obj = cl.newInstance();
            Ret ret = coreToInvoke(methodName);

            if(ret.cc==null)//jezeli brak parametrów
            {
                Method method = cl.getDeclaredMethod(ret.name);
                if(Modifier.toString(method.getModifiers())=="public")
                method.invoke(obj);
            }else//jezeli sa parametry w metodzie
            {
                Method method = cl.getDeclaredMethod(ret.name,ret.cc);
                if(Modifier.toString(method.getModifiers())=="public")
                    method.invoke(obj,ret.arg);
            }

        }






        public void changeHierarchy (String clz, String clzParent ) throws
        NotFoundException, CannotCompileException, ClassNotFoundException, IOException {//zmaina hierarchii klas

            CtClass clzzParent = this.cp.get(this.classNames.get(clzParent));
            CtClass clzz = this.cp.get(this.classNames.get(clz));

            clzz.defrost();
            clzzParent.defrost();

            clzzParent.setSuperclass(clzz.getSuperclass());
            clzz.setSuperclass(clzzParent);

            clzz.writeFile();
            clzzParent.writeFile();
            this.classLoader = new Loader(this.cp);


            CtClass cc = this.cp.get(this.classNames.get(clz));



        }



}
















